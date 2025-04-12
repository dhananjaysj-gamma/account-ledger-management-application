package tech.zeta.account_ledger_management_app.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import tech.zeta.account_ledger_management_app.dto.LoginRequest;
import tech.zeta.account_ledger_management_app.dto.UserAccountInformation;
import tech.zeta.account_ledger_management_app.dto.UserUpdateRequest;
import tech.zeta.account_ledger_management_app.dto.UsersDTO;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.service.JWTService;
import tech.zeta.account_ledger_management_app.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserService userService;

    public UserController(AuthenticationManager authenticationManager,JWTService jwtService,UserService userService) {
         this.authenticationManager=authenticationManager;
         this.jwtService=jwtService;
         this.userService=userService;
    }

    @PostMapping("/register")
    public UsersDTO addUser(@RequestBody Users users) {
        return  userService.addUser(users);
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestBody LoginRequest loginRequest) {
       return userService.verifyUser(loginRequest);
    }

    @GetMapping("/{userId}")
    public UserAccountInformation getUserDetailsById(@PathVariable Long userId) {
        return userService.getUserDetailsById(userId);

    }

    @PatchMapping("/{userId}")
    public UsersDTO updateDetails(@PathVariable Long userId, @RequestBody UserUpdateRequest updateRequest) {
        return  userService.updateDetails(userId,updateRequest);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }



}
