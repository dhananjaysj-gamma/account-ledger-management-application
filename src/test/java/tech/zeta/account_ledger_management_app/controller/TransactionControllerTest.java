package tech.zeta.account_ledger_management_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.enums.TransactionType;
import tech.zeta.account_ledger_management_app.service.TransactionService;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionResponse response;

    @BeforeEach
    void setup() {
        response = new TransactionResponse();

        response.setTransactionId(1L);
        response.setTransactionAmount(500.0);
    }

    @Test
    @WithMockUser(username = "test user")
    void testTransferFunds_Success() throws Exception {
        when(transactionService.processTransaction(10001L, 10002L, 500.0, TransactionType.INTERNAL))
                .thenReturn(response);

        mockMvc.perform(post("/transactions/transfer")
                        .param("fromLedgerId", "10001")
                        .param("toLedgerId", "10002")
                        .param("transactionAmount", "500.0")
                        .param("transactionType", "INTERNAL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.transactionAmount").value(500.0));
    }

    @Test
    @WithMockUser(username = "test user")
    void testTransferFunds_InvalidTransactionType() throws Exception {
        mockMvc.perform(post("/transactions/transfer")
                        .param("fromLedgerId", "10001")
                        .param("toLedgerId", "10002")
                        .param("transactionAmount", "500.0")
                        .param("transactionType", "INVALID_TYPE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
