package tech.zeta.account_ledger_management_app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tech.zeta.account_ledger_management_app.dto.LoginRequest;
import tech.zeta.account_ledger_management_app.dto.UserAccountInformation;
import tech.zeta.account_ledger_management_app.dto.UserUpdateRequest;
import tech.zeta.account_ledger_management_app.dto.UsersDTO;
import tech.zeta.account_ledger_management_app.enums.UserStatus;
import tech.zeta.account_ledger_management_app.exceptions.UserNotFoundException;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.repository.TenantRepository;
import tech.zeta.account_ledger_management_app.repository.UserRepository;
import java.util.Collections;

@Slf4j
@Service
public class UserService {


    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public UserService(UserRepository userRepository, TenantRepository tenantRepository,JWTService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository=userRepository;
        this.tenantRepository=tenantRepository;
        this.jwtService=jwtService;
        this.authenticationManager=authenticationManager;
    }
    private final BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(10);

    public UsersDTO addUser(Users users) {

        boolean userExists = tenantRepository.existsByRegisteredUserIdsContaining(users.getUserId());

        if(!userExists) {
            log.error("The user Id not registered under the tenant :{}",users.getUserId());
            throw new UserNotFoundException("This Id is not registered under this bank,  Please check with bank for the registered Id:");
        }
         users.setPassword(encoder.encode(users.getPassword()));
         log.info("The user is added successfully:");
         userRepository.save(users);

         return userDetails(users);
    }

    public String verifyUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(loginRequest.getUsername());
        }
        else {
            throw new BadCredentialsException("Invalid credentials,check username or password!");
        }
    }

    public UserAccountInformation getUserDetailsById(Long userId){

        Users users = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found, Please register with the Id provided by the bank: "+ userId));

        return userAccountInformation(users);
    }

    public UsersDTO updateDetails(Long userId, UserUpdateRequest updateRequest) {

        Users existingUser = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found, Please check the Id provided:"+ userId));

        if(updateRequest.getName()!=null) {
            existingUser.setName(updateRequest.getName());
        }
        if(updateRequest.getUsername()!=null) {
            existingUser.setUsername(updateRequest.getUsername());
        }
        if(updateRequest.getStatus()!=null) {
            existingUser.setStatus(updateRequest.getStatus());
        }
        log.info("The details are updated successfully!");
        userRepository.save(existingUser);

        return  userDetails(existingUser);
    }

   public String deleteUser(Long userId) {

       Users user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found, Please check the Id provided "+ userId));
       user.setStatus(UserStatus.INACTIVE);
       user.setIsDeleted(true);
       userRepository.save(user);
       log.info("The User is  Soft Deleted With Id: {}",userId);
       return "The user deleted successfully!";
   }

   private static UserAccountInformation userAccountInformation(Users users) {
       boolean hasLedgers= !users.getLedger().isEmpty();
       return new UserAccountInformation(
               users.getUserId(),
               users.getName(),
               users.getUsername(),
               users.getStatus(),
               hasLedgers ? users.getLedger() : Collections.emptyList()
       );
   }
   private static UsersDTO userDetails(Users users) {
        return new UsersDTO(
                users.getUserId(),
                users.getName(),
                users.getUsername(),
                users.getAadhaarNumber(),
                users.getStatus()
        );
   }
}
