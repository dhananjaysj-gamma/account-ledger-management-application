package tech.zeta.account_ledger_management_app.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.zeta.account_ledger_management_app.dto.LoginRequest;
import tech.zeta.account_ledger_management_app.dto.UserAccountInformation;
import tech.zeta.account_ledger_management_app.dto.UserUpdateRequest;
import tech.zeta.account_ledger_management_app.dto.UsersDTO;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.service.JWTService;
import tech.zeta.account_ledger_management_app.service.UserService;
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
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUserId(1L);
        user.setUsername("test user");
        user.setPassword("password");

        userDTO = new UsersDTO();
        userDTO.setUserId(1L);
        userDTO.setUsername("test user");
        loginRequest=new LoginRequest("test-user","testpass");
    }

    @Test
    void testUserVerify() throws Exception {
        when(userService.verifyUser(loginRequest)).thenReturn("mock-token");
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
    @Test
    void testRegisterUser_Success() throws Exception {
        when(userService.addUser(any(Users.class))).thenReturn(userDTO);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    @WithMockUser(username = "test user")
    void testGetUserById_Success() throws Exception {
        UserAccountInformation info = new UserAccountInformation();
        info.setUserId(1L);
        info.setUsername("test user");


        when(userService.getUserDetailsById(1L)).thenReturn(info);

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("test user"));
    }


    @Test
    @WithMockUser(username = "test user")
    void testPatchUpdateUserDetails_Success() throws Exception {

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("updated name");
        when(userService.updateDetails(eq(1L),any(UserUpdateRequest.class))).thenReturn(userDTO);

        mockMvc.perform(patch("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "test user")
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isOk());
    }
}
