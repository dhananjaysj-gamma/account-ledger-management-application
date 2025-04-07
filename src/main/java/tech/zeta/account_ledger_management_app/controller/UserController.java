package tech.zeta.account_ledger_management_app.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import tech.zeta.account_ledger_management_app.dto.LoginRequest;
import tech.zeta.account_ledger_management_app.dto.UserAccountInformation;
import tech.zeta.account_ledger_management_app.dto.UsersDTO;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.repository.TenantRepository;
import tech.zeta.account_ledger_management_app.service.JWTService;
import tech.zeta.account_ledger_management_app.service.UserService;

import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private TenantRepository tenantRepository;



    @PostMapping("/register")
    public UsersDTO addUser(@RequestBody Users users)
    {

        return userService.addUser(users);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
             if(authentication.isAuthenticated()) {
                 String token = jwtService.generateToken(request.getUsername());
                 return ResponseEntity.ok(token);
             }
             else {
                 throw new BadCredentialsException("Invalid Credentials");
             }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    @GetMapping("/{userId}")
    public UserAccountInformation getUserDetailsById(@PathVariable Long userId)
    {
        return userService.getUserDetailsById(userId);

    }
    @PutMapping("/{userId}")
    public UsersDTO updateUserAllDetails(@PathVariable Long userId,@RequestBody Users users)
    {
        return userService.updateUserAllDetails(userId,users);
    }

    @PatchMapping("/{userId}")
    public UsersDTO updateDetails(@PathVariable Long userId, @RequestBody Map<String, Object> updateDetails)
    {
        return  userService.updateDetails(userId, updateDetails);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId)
    {
        userService.deleteUser(userId);
    }



}
