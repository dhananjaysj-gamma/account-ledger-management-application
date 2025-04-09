package tech.zeta.account_ledger_management_app.service;


import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import tech.zeta.account_ledger_management_app.dto.UserAccountInformation;
import tech.zeta.account_ledger_management_app.dto.UsersDTO;
import tech.zeta.account_ledger_management_app.enums.UserStatus;
import tech.zeta.account_ledger_management_app.exceptions.InvalidEntityIdProvidedException;
import tech.zeta.account_ledger_management_app.exceptions.UserNotFoundException;
import tech.zeta.account_ledger_management_app.models.Ledger;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.repository.TenantRepository;
import tech.zeta.account_ledger_management_app.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
 class UserServiceTest {



    @Mock
    private TenantRepository tenantRepository;
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;


    private final Users users = new Users();
    private final Ledger ledger =  new Ledger();
    @BeforeEach
    void setUp() {



        ledger.setUsers(users);
        ledger.setLedgerId(10001L);
        ledger.setLedgerName("test Ledger");
        ledger.setLedgerBalance(1000.0);

        List<Ledger> ledger1 = Collections.singletonList(ledger);

        users.setUserId(1001L);
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
    void testUserIdInvalid()
    {
        users.setUserId(0L);
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.addUser(users));
    }

    @Test
    void testUserIdInvalid2()
    {
        users.setUserId(999L);
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.addUser(users));
    }

    @Test
    void testGetUserIdDetails()
    {

        when(userRepository.findById(users.getUserId())).thenReturn(java.util.Optional.of(users));
        UserAccountInformation userTest = userService.getUserDetailsById(users.getUserId());
        assertEquals("test User",userTest.getName());
    }
    @Test
    void testGetUserIdDetailsWithLedger()
    {
        when(userRepository.findById(users.getUserId())).thenReturn(java.util.Optional.of(users));
        UserAccountInformation userTest = userService.getUserDetailsById(users.getUserId());
        assertEquals("test User",userTest.getName());
        assertEquals("test Ledger",users.getLedger().get(0).getLedgerName());
    }

    @Test
    void testGetUserIdDetailsInvalid()
    {
        users.setUserId(0L);
        Long userId = users.getUserId();
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.getUserDetailsById(userId));
    }
    @Test
    void testGetUserIdDetailsInvalid2()
    {
        users.setUserId(999L);
        Long userId = users.getUserId();
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.getUserDetailsById(userId));
    }
    @Test
    void testGetUserIdDetailsNotFound()
    {
        when(userRepository.findById(users.getUserId())).thenThrow(new UserNotFoundException("User Not Registered With an ID: "+ users.getUserId()));
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
    void getUserDetailsByIdWithNonExistentUser() {
        Long nonExistentUserId = 9999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserDetailsById(nonExistentUserId));
    }


    @Test
    void testUpdateUserAllDetails()
    {
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        when(userRepository.save(users)).thenReturn(users);
        UsersDTO userTest = userService.updateUserAllDetails(users.getUserId(),users);
        assertEquals("test User",userTest.getName());
    }
    @Test
    void testUpdateUserAllDetailsInvalid()
    {
        users.setUserId(0L);
        Long userId = users.getUserId();
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.updateUserAllDetails(userId,users));
    }
    @Test
    void testUpdateUserAllDetailsInvalid2()
    {
        users.setUserId(999L);
        Long userId = users.getUserId();
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.updateUserAllDetails(userId,users));
    }
    @Test
    void testUpdateUserAllDetailsNotFound()
    {
        when(userRepository.findById(users.getUserId())).thenThrow(new UserNotFoundException("User Not Registered With an ID: "+ users.getUserId()));
        Long userId = users.getUserId();
        assertThrows(UserNotFoundException.class, () -> userService.updateUserAllDetails(userId,users));
    }
    @Test
    void updateDetailsWithValidFields() {
        Map<String, Object> updateMap = Map.of(
                "name", "Updated Name",
                "username", "updated-username",
                "status", "INACTIVE"
        );
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        UsersDTO updatedUser = userService.updateDetails(users.getUserId(), updateMap);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated-username", updatedUser.getUserName());
        assertEquals(UserStatus.INACTIVE, updatedUser.getUserStatus());
    }

    @Test
    void updateDetailsWithInvalidField() {
        Map<String, Object> updateMap = Map.of("invalidField", "value");
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        assertThrows(IllegalArgumentException.class, () -> userService.updateDetails(users.getUserId(), updateMap));
    }

    @Test
    void updateDetailsWithEmptyMap() {
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        UsersDTO updatedUser = userService.updateDetails(users.getUserId(), Collections.emptyMap());
        assertEquals("test User", updatedUser.getName());
        assertEquals("test-1", updatedUser.getUserName());
        assertEquals(UserStatus.ACTIVE, updatedUser.getUserStatus());
    }

    @Test
    void updateDetailsWithInvalidUserId() {
        users.setUserId(0L);
        Long userId = users.getUserId();
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.updateDetails(userId, Map.of("name", "New Name")));
    }

    @Test
    void updateDetailsWithNonExistentUser() {
        when(userRepository.findById(users.getUserId())).thenThrow(new UserNotFoundException("User Not Registered With an ID: " + users.getUserId()));
        assertThrows(UserNotFoundException.class, () -> userService.updateDetails(users.getUserId(), Map.of("name", "New Name")));
    }

    @Test
    void updateDetailsWithPasswordField() {
        Map<String, Object> updateMap = Map.of("password", "newPassword123");
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        UsersDTO updatedUser = userService.updateDetails(users.getUserId(), updateMap);
        assertEquals("test User", updatedUser.getName());
        assertEquals("test-1", updatedUser.getUserName());

    }
    @Test
    void testUpdateUserDetailsInvalid()
    {
        users.setUserId(0L);
        Long userId = users.getUserId();
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.updateDetails(userId, Collections.emptyMap()));
    }
    @Test
    void testUpdateUserDetailsInvalid2()
    {
        users.setUserId(999L);
        Long userId = users.getUserId();
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.updateDetails(userId, Collections.emptyMap()));
    }
    @Test
    void testUpdateUserDetailsNotFound()
    {
        when(userRepository.findById(users.getUserId())).thenThrow(new UserNotFoundException("User Not Registered With an ID: "+ users.getUserId()));
        Long userId = users.getUserId();
        assertThrows(UserNotFoundException.class, () -> userService.updateDetails(userId, Collections.emptyMap()));
    }
    @Test
    void testDeleteUser()
    {
        when(userRepository.findById(users.getUserId())).thenReturn(Optional.of(users));
        userService.deleteUser(users.getUserId());
    }
    @Test
    void testDeleteUserInvalid()
    {
        users.setUserId(0L);
        Long userId = users.getUserId();
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.deleteUser(userId));
    }
    @Test
    void testDeleteUserInvalid2()
    {
        users.setUserId(999L);
        Long userId = users.getUserId();
        assertThrows(InvalidEntityIdProvidedException.class, () -> userService.deleteUser(userId));
    }
    @Test
    void testDeleteUserNotFound()
    {
        when(userRepository.findById(users.getUserId())).thenThrow(new UserNotFoundException("User Not Registered With an ID: "+ users.getUserId()));
        Long userId = users.getUserId();
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }
    @Test
    void testDeleteUserNotFound2()
    {
        when(userRepository.findById(users.getUserId())).thenThrow(new UserNotFoundException("User Not Registered With an ID: "+ users.getUserId()));
        Long userId = users.getUserId();
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }
    @Test
    void testDeleteUserNotFound3()
    {
        when(userRepository.findById(users.getUserId())).thenThrow(new UserNotFoundException("User Not Registered With an ID: "+ users.getUserId()));
        Long userId = users.getUserId();
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }





















}
