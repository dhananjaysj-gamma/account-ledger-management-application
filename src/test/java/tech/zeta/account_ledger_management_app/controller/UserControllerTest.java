package tech.zeta.account_ledger_management_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.zeta.account_ledger_management_app.dto.LoginRequest;
import tech.zeta.account_ledger_management_app.dto.UserAccountInformation;
import tech.zeta.account_ledger_management_app.dto.UsersDTO;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.service.JWTService;
import tech.zeta.account_ledger_management_app.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Users user;
    private UsersDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUserId(1001L);
        user.setUsername("test user");
        user.setPassword("password");

        userDTO = new UsersDTO();
        userDTO.setUserId(1001L);
        userDTO.setUserName("test user");
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        when(userService.addUser(any(Users.class))).thenReturn(userDTO);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1001L));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest("test user", "password");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("test user")).thenReturn("mock-token");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("mock-token"));
    }

    @Test
    void testLogin_Failure_InvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("test user", "wrongpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid Credentials"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    @WithMockUser(username = "test user")
    void testGetUserById_Success() throws Exception {
        UserAccountInformation info = new UserAccountInformation();
        info.setUserId(1001L);
        info.setUsername("test user");


        when(userService.getUserDetailsById(1001L)).thenReturn(info);

        mockMvc.perform(get("/user/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1001L))
                .andExpect(jsonPath("$.username").value("test user"));
    }

    @Test
    @WithMockUser(username = "test user")
    void testUpdateUserAllDetails_Success() throws Exception {
        when(userService.updateUserAllDetails(eq(1001L), any(Users.class))).thenReturn(userDTO);

        mockMvc.perform(put("/user/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1001L));
    }

    @Test
    @WithMockUser(username = "test user")
    void testPatchUpdateUserDetails_Success() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", "new name");

        userDTO.setUserName("new name");
        when(userService.updateDetails(eq(1001L), anyMap())).thenReturn(userDTO);

        mockMvc.perform(patch("/user/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk());

    }
    @Test
    @WithMockUser(username = "test user")
    void testUserNotAuthenticated() throws Exception {

        LoginRequest request = new LoginRequest("test user", "wrongpass");
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }
    @Test
    @WithMockUser(username = "test user")
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/user/10001"))
                .andExpect(status().isOk());
    }
}
