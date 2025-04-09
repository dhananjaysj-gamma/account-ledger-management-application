package tech.zeta.account_ledger_management_app.service;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import tech.zeta.account_ledger_management_app.dto.UserAccountInformation;
import tech.zeta.account_ledger_management_app.dto.UsersDTO;
import tech.zeta.account_ledger_management_app.enums.UserStatus;
import tech.zeta.account_ledger_management_app.exceptions.InvalidEntityIdProvidedException;
import tech.zeta.account_ledger_management_app.exceptions.UserNotFoundException;
import tech.zeta.account_ledger_management_app.models.Tenant;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.repository.TenantRepository;
import tech.zeta.account_ledger_management_app.repository.UserRepository;


import java.util.Collections;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class UserService {



    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;


    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public UserService(UserRepository userRepository, TenantRepository tenantRepository)
    {
        this.userRepository=userRepository;
        this.tenantRepository=tenantRepository;
    }

    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(10);

    public UsersDTO addUser(Users users) {


        if(users.getUserId() <=0  || users.getUserId() < 1000)
        {
            log.error("The Provided Id is not Valid");
            throw new InvalidEntityIdProvidedException("The Id provided is Not Valid, Please re-check the Id:");
        }

        boolean userExists = tenantRepository.existsByRegisteredUserIdsContaining(users.getUserId());

        if(!userExists)
        {
            log.error("The User Id is Not Registered Under the tenant :{}",users.getUserId());
            throw new UserNotFoundException("This Id is Not Registered Under this Bank,  Please check with Bank for the registered Id:");
        }
         users.setPassword(encoder.encode(users.getPassword()));
         log.info("The User is added Successfully:");

         userRepository.save(users);

         return new UsersDTO(
                 users.getUserId(),
                 users.getName(),
                 users.getUsername(),
                 users.getAadhaarNumber(),
                 users.getStatus()
         );

    }


    public UserAccountInformation getUserDetailsById(Long userId){

        if(userId <=0  || userId < 1000 )
        {
            throw new InvalidEntityIdProvidedException("The Id provided is Not Valid, Please check the ID:" + userId);
        }

        Users users = userRepository.findById(userId).orElseThrow(
                        ()->
                         new UserNotFoundException("User Not Registered With an ID: "+ userId)
                );

        boolean hasLedgers=  !users.getLedger().isEmpty();

        return new UserAccountInformation(
                users.getUserId(),
                users.getName(),
                users.getUsername(),
                users.getStatus(),
                hasLedgers ? users.getLedger() : Collections.emptyList()
        );

    }

    public UsersDTO updateUserAllDetails(Long userId, Users updateUser) {

        if(userId <=0  || userId < 1000 )
        {
            throw new InvalidEntityIdProvidedException("The Id provided is Not Valid, Please check the ID:" + userId);
        }
        Users existingUsers = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User Not Registered With the Id:"+userId));

        existingUsers.setUserId(updateUser.getUserId());
        existingUsers.setName(updateUser.getName());
        existingUsers.setUsername(updateUser.getUsername());
        existingUsers.setStatus(updateUser.getStatus());
         userRepository.save(existingUsers);


         return new UsersDTO(
                 existingUsers.getUserId(),
                 existingUsers.getName(),
                 existingUsers.getUsername(),
                 existingUsers.getAadhaarNumber(),
                 existingUsers.getStatus()
         );
    }

    public UsersDTO updateDetails(Long userId, Map<String, Object> updateDetails) {

        if(userId <=0  || userId < 1000 )
        {
            throw new InvalidEntityIdProvidedException("The Id provided is Not Valid, Please check the ID:" + userId);
        }
        Users existingUsers = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User Not Registered With an Id:"+userId));

        updateDetails.forEach((field,value)->
        {
            switch (field)
            {
                case "name" ->  existingUsers.setName((String) value);
                case  "username" ->  existingUsers.setUsername((String) value);
                case  "status"  -> existingUsers.setStatus(UserStatus.valueOf(((String) value).toUpperCase()));
                case  "password" -> existingUsers.setPassword((String) value);
                default -> throw  new IllegalArgumentException("Invalid Field: " + value);
            }
        });
        return new UsersDTO(
                existingUsers.getUserId(),
                existingUsers.getName(),
                existingUsers.getUsername(),
                existingUsers.getAadhaarNumber(),
                existingUsers.getStatus()
        );

    }

   public void deleteUser(Long userId)
   {
       if(userId <=0  || userId < 1000 )
       {
           throw new InvalidEntityIdProvidedException("The Id provided is Not Valid, Please check the ID:" + userId);
       }
       Users user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User Not Registered"));
       user.setStatus(UserStatus.INACTIVE);
       user.setIsDeleted(true);
       userRepository.save(user);
       log.info("The User is  Soft Deleted With Id: {}",userId);
   }
}
