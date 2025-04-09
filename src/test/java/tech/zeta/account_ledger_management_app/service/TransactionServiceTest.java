package tech.zeta.account_ledger_management_app.service;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.enums.TransactionType;
import tech.zeta.account_ledger_management_app.exceptions.InsufficientBalanceException;
import tech.zeta.account_ledger_management_app.exceptions.InvalidTransactionTypeException;
import tech.zeta.account_ledger_management_app.exceptions.LedgerNotFoundException;
import tech.zeta.account_ledger_management_app.models.Ledger;
import tech.zeta.account_ledger_management_app.models.Transaction;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.repository.LedgerRepository;
import tech.zeta.account_ledger_management_app.repository.TransactionRepository;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class TransactionServiceTest {


    @Mock
    private LedgerRepository ledgerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private final Users user1 = new Users();
    private final Users user2 = new Users();
    private final Ledger fromLedger = new Ledger();
    private final Ledger toLedger = new Ledger();

    @BeforeEach
    void setUp() {
        user1.setUserId(1001L);
        user2.setUserId(1002L);

        fromLedger.setLedgerId(10001L);
        fromLedger.setLedgerBalance(1000.0);
        fromLedger.setUsers(user1);
        fromLedger.setLedgerName("From Ledger");

        toLedger.setLedgerId(10002L);
        toLedger.setLedgerBalance(500.0);
        toLedger.setLedgerName("To Ledger");

    }

    @Test
    void testSuccessfulInternalTransaction() {
        toLedger.setUsers(user1); // same user

        when(ledgerRepository.findById(10001L)).thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(10002L)).thenReturn(Optional.of(toLedger));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.processTransaction(
                10001L, 10002L, 200.0, TransactionType.INTERNAL);

        assertNotNull(response);
        assertEquals(10001L, response.getFromLedgerId());
        assertEquals(10002L, response.getToLedgerId());
        assertEquals(200.0, response.getTransactionAmount());

        verify(ledgerRepository, times(2)).save(any(Ledger.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testSuccessfulExternalTransaction() {
        toLedger.setUsers(user2); // different user

        when(ledgerRepository.findById(10001L)).thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(10002L)).thenReturn(Optional.of(toLedger));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.processTransaction(
                10001L, 10002L, 150.0, TransactionType.EXTERNAL);

        assertNotNull(response);
        assertEquals(150.0, response.getTransactionAmount());
    }

    @Test
    void testFromLedgerNotFound() {
        when(ledgerRepository.findById(10001L)).thenReturn(Optional.empty());

        assertThrows(LedgerNotFoundException.class, () ->
                transactionService.processTransaction(10001L, 10002L, 100.0, TransactionType.INTERNAL));
    }

    @Test
    void testToLedgerNotFound() {
        when(ledgerRepository.findById(10001L)).thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(10002L)).thenReturn(Optional.empty());

        assertThrows(LedgerNotFoundException.class, () ->
                transactionService.processTransaction(10001L, 10002L, 100.0, TransactionType.INTERNAL));
    }

    @Test
    void testInsufficientBalance() {
        fromLedger.setLedgerBalance(50.0);
        toLedger.setUsers(user1);

        when(ledgerRepository.findById(10001L)).thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(10002L)).thenReturn(Optional.of(toLedger));

        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.processTransaction(10001L, 10002L, 100.0, TransactionType.INTERNAL));
    }

    @Test
    void testInvalidInternalTransactionBetweenDifferentUsers() {
        toLedger.setUsers(user2);

        when(ledgerRepository.findById(10001L)).thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(10002L)).thenReturn(Optional.of(toLedger));

        assertThrows(InvalidTransactionTypeException.class, () ->
                transactionService.processTransaction(10001L, 10002L, 100.0, TransactionType.INTERNAL));
    }

    @Test
    void testInvalidExternalTransactionBetweenSameUsers() {
        toLedger.setUsers(user1);
        when(ledgerRepository.findById(10001L)).thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(10002L)).thenReturn(Optional.of(toLedger));

        assertThrows(InvalidTransactionTypeException.class, () ->
                transactionService.processTransaction(10001L, 10002L, 100.0, TransactionType.EXTERNAL));
    }
}