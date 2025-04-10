package tech.zeta.account_ledger_management_app.service;


import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import tech.zeta.account_ledger_management_app.dto.LoginRequest;
import tech.zeta.account_ledger_management_app.dto.UserAccountInformation;
import tech.zeta.account_ledger_management_app.dto.UserUpdateRequest;
import tech.zeta.account_ledger_management_app.dto.UsersDTO;
import tech.zeta.account_ledger_management_app.enums.UserStatus;
import tech.zeta.account_ledger_management_app.exceptions.UserNotFoundException;
import tech.zeta.account_ledger_management_app.models.Ledger;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.repository.TenantRepository;
import tech.zeta.account_ledger_management_app.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
 class UserServiceTest {

    @Mock
    private TenantRepository tenantRepository;
    @InjectMocks
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTService jwtService;
    @Mock
    private UserRepository userRepository;

    private final Users users = new Users();
    private final Ledger ledger =  new Ledger();
    @BeforeEach
    void setUp() {

        ledger.setUsers(users);
        ledger.setLedgerId(1L);
        ledger.setLedgerName("test Ledger");
        ledger.setLedgerBalance(1000.0);

        List<Ledger> ledger1 = Collections.singletonList(ledger);

        users.setUserId(1L);
        users.setName("test User");
        users.setUsername("test-1");
        users.setPassword("t123");
        users.setAadhaarNumber("12345678");
        users.setStatus(UserStatus.ACTIVE);
        users.setLedger(ledger1);

    }
    @Test
    void testAddUser()
    {
        when(tenantRepository.existsByRegisteredUserIdsContaining(users.getUserId())).thenReturn(true);
        when(userRepository.save(users)).thenReturn(users);
        UsersDTO userTest = userService.addUser(users);
        assertEquals("test User",userTest.getName());
    }

    @Test
    void userNotRegisteredUnderTenant() {

        when(tenantRepository.existsByRegisteredUserIdsContaining(users.getUserId())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.addUser(users));
    }
    @Test
    void testVerifyUser_whenAuthenticated_shouldReturnToken() {
        LoginRequest loginRequest = new LoginRequest("test", "password123");
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("test")).thenReturn("fake-jwt-token");
        String result = userService.verifyUser(loginRequest);
        assertEquals("fake-jwt-token", result);
    }

    @Test
    void testVerifyUser_whenNotAuthenticated_shouldThrowException() {

        LoginRequest loginRequest = new LoginRequest("john_doe", "wrongPassword");
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> userService.verifyUser(loginRequest));
        assertEquals("Invalid credentials,check username or password!", exception.getMessage());
    }
    @Test
    void testGetUserIdDetails() {

        when(userRepository.findById(users.getUserId())).thenReturn(java.util.Optional.of(users));
        UserAccountInformation userTest = userService.getUserDetailsById(users.getUserId());
        assertEquals("test User", userTest.getName());
    }

    @Test
    void testGetUserIdDetailsWithLedger() {
        when(userRepository.findById(users.getUserId())).thenReturn(java.util.Optional.of(users));
        UserAccountInformation userTest = userService.getUserDetailsById(users.getUserId());
        assertEquals("test User",userTest.getName());
        assertEquals("test Ledger",users.getLedger().get(0).getLedgerName());
    }

    @Test
    void testGetUserIdDetailsNotFound()
    {
        when(userRepository.findById(users.getUserId())).thenThrow(new UserNotFoundException("User not found, Please register with the Id provided by the bank: "+ users.getUserId()));
        Long userId = users.getUserId();
        assertThrows(UserNotFoundException.class, () -> userService.getUserDetailsById(userId));
    }
    @Test
    void getUserDetailsByIdWithEmptyLedger() {
        users.setLedger(Collections.emptyList());
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        UserAccountInformation userDetails = userService.getUserDetailsById(users.getUserId());
        assertEquals(Collections.emptyList(), userDetails.getLedgers());
    }

    @Test
     void testUpdateDetailsUserNotFound() {
        when(userRepository.findById(users.getUserId()))
                .thenThrow(new UserNotFoundException("User not found, Please register with the Id provided by the bank: "+ users.getUserId()));
        assertThrows(UserNotFoundException.class,()-> userService.updateDetails(users.getUserId(), null));
    }

    @Test
    void testUserUpdateRequestName(){
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("updated name");
        users.setName(request.getName());
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        UsersDTO usersDTO = userService.updateDetails(1L,request);
        assertEquals("updated name",usersDTO.getName());

    }

    @Test
    void testUserUpdateRequestUserName(){
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("updated-test");
        users.setUsername(request.getUsername());
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        UsersDTO usersDTO = userService.updateDetails(1L,request);
        assertEquals("updated-test",usersDTO.getUsername());

    }
    @Test
    void testUserUpdateRequestUserStatus(){
        UserUpdateRequest request = new UserUpdateRequest();
        request.setStatus(UserStatus.INACTIVE);
        users.setStatus(request.getStatus());
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        UsersDTO usersDTO = userService.updateDetails(1L,request);
        assertEquals(UserStatus.INACTIVE,usersDTO.getUserStatus());

    }
    @Test
    void testDeleteUser()
    {
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        String response = userService.deleteUser(users.getUserId());
        assertEquals("The user deleted successfully!",response);

    }

    @Test
    void testDeleteUserNotFound()
    {
        when(userRepository.findById(users.getUserId())).thenThrow(new UserNotFoundException("User Not Registered With an ID: "+ users.getUserId()));
        Long userId = users.getUserId();
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }

}
