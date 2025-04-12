package tech.zeta.account_ledger_management_app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tech.zeta.account_ledger_management_app.models.UserPrincipal;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.repository.UserRepository;
@Slf4j
@Service
public class CustomizedUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomizedUserDetailService(UserRepository userRepository) {
        this.userRepository=userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findByUsername(username);

        if (users == null || Boolean.TRUE.equals(users.getIsDeleted())) {
            log.error("User Name Not Found");
            throw new UsernameNotFoundException("User Name Not Found");
        }
        return new UserPrincipal(users);
    }
}
