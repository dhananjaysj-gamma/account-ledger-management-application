package tech.zeta.account_ledger_management_app.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.zeta.account_ledger_management_app.dto.LedgerDTO;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.enums.UserStatus;
import tech.zeta.account_ledger_management_app.models.Ledger;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.service.LedgerService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LedgerService ledgerService;

    @Autowired
    private ObjectMapper objectMapper;

    private LedgerDTO ledgerDTO;
    private Ledger ledger;

    @BeforeEach
    void setup() {
        ledgerDTO = new LedgerDTO();
        ledgerDTO.setLedgerId(10001L);
        ledgerDTO.setLedgerName("Cash");
        ledgerDTO.setLedgerBalance(1000.00);

        ledger = new Ledger();
        ledger.setLedgerName("Cash");
        ledger.setLedgerBalance(1000.00);
    }

    @Test
    @WithMockUser(username = "test user")
    void testCreateLedger() throws Exception {
        when(ledgerService.createLedger(any(Ledger.class), eq(10001L))).thenReturn(ledgerDTO);

        mockMvc.perform(post("/ledger/user/10001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ledger)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ledgerId").value(10001L))
                .andExpect(jsonPath("$.ledgerName").value("Cash"))
                .andExpect(jsonPath("$.ledgerBalance").value(1000.00));
    }

    @Test
    @WithMockUser(username = "test user")
    void testGetLedgerById() throws Exception {
        when(ledgerService.getLedgerById(10001L)).thenReturn(ledgerDTO);

        mockMvc.perform(get("/ledger/10001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ledgerId").value(10001L))
                .andExpect(jsonPath("$.ledgerName").value("Cash"));
    }

    @Test
    @WithMockUser(username = "test user")
    void testGetLedgerTransactionHistory() throws Exception {
        TransactionResponse transaction1 = new TransactionResponse();
        transaction1.setTransactionId(1L);
        transaction1.setTransactionAmount(500.0);

        TransactionResponse transaction2 = new TransactionResponse();
        transaction2.setTransactionId(1L);
        transaction2.setTransactionAmount(200.0);

        List<TransactionResponse> transactionList = Arrays.asList(transaction1, transaction2);

        when(ledgerService.getTransactionHistoryById(10001L)).thenReturn(transactionList);

        mockMvc.perform(get("/ledger/transactions/history")
                        .param("fromLedgerId", "10001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].transactionId").value(101L))
                .andExpect(jsonPath("$[1].transactionId").value(102L));
    }
}




