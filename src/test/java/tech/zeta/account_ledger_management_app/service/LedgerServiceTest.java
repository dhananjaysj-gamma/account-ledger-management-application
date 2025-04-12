package tech.zeta.account_ledger_management_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.zeta.account_ledger_management_app.dto.LedgerDTO;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.enums.TransactionType;
import tech.zeta.account_ledger_management_app.exceptions.LedgerNotFoundException;
import tech.zeta.account_ledger_management_app.exceptions.UserNotFoundException;
import tech.zeta.account_ledger_management_app.models.Ledger;
import tech.zeta.account_ledger_management_app.models.Transaction;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.repository.LedgerRepository;
import tech.zeta.account_ledger_management_app.repository.TransactionRepository;
import tech.zeta.account_ledger_management_app.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    private LedgerRepository ledgerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private LedgerService ledgerService;

    private Users users;
    private Ledger ledger;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        users = new Users();
        users.setUserId(1L);
        users.setName("test User");
        users.setUsername("test-1");
        users.setPassword("t123");
        users.setAadhaarNumber("12345678");

        ledger = new Ledger();
        ledger.setUsers(users);
        ledger.setLedgerId(1L);
        ledger.setLedgerName("Test Ledger");
        ledger.setLedgerBalance(1000.0);

        transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setFromLedgerId(1L);
        transaction.setToLedgerId(1L);
        transaction.setTransactionAmount(500.0);
        transaction.setTransactionType(TransactionType.INTERNAL);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setLedger(ledger);
    }

    @Test
    void testCreateLedgerSuccess() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(users));
        when(ledgerRepository.save(any(Ledger.class)))
                .thenReturn(ledger);

        LedgerDTO result = ledgerService.createLedger(ledger, 1L);

        assertEquals(ledger.getLedgerId(), result.getLedgerId());
        assertEquals(ledger.getLedgerName(), result.getLedgerName());
        assertEquals(ledger.getLedgerBalance(), result.getLedgerBalance());
    }

    @Test
    void testCreateLedger_UserNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> ledgerService.createLedger(ledger, 1L));
    }

    @Test
    void testGetLedgerByIdSuccess() {
        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.of(ledger));
        LedgerDTO result = ledgerService.getLedgerById(1L);

        assertEquals(1L, result.getLedgerId());
        assertEquals("Test Ledger", result.getLedgerName());
        assertEquals(1000.0, result.getLedgerBalance());
    }

    @Test
    void testGetLedgerById_NotFound() {
        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(LedgerNotFoundException.class, () -> ledgerService.getLedgerById(1L));
    }

    @Test
    void testGetTransactionHistoryByIdSuccess() {
        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.of(ledger));
        when(transactionRepository.findAllByFromLedgerId(1L))
                .thenReturn(List.of(transaction));

        List<TransactionResponse> responses = ledgerService.getTransactionHistoryById(1L);
        assertEquals(1, responses.size());
        assertEquals(transaction.getTransactionId(), responses.get(0).getTransactionId());
    }

    @Test
    void testGetTransactionHistoryById_EmptyTransactionList() {
        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.of(ledger));
        when(transactionRepository.findAllByFromLedgerId(1L))
                .thenReturn(Collections.emptyList());

        List<TransactionResponse> responses = ledgerService.getTransactionHistoryById(1L);
        assertTrue(responses.isEmpty());
    }

    @Test
    void testGetTransactionHistoryById_LedgerNotFound() {
        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(LedgerNotFoundException.class, () -> ledgerService.getTransactionHistoryById(1L));
    }
}
