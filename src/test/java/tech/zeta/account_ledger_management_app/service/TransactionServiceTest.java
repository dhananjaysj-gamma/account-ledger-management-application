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
        user1.setUserId(1L);
        user2.setUserId(2L);

        fromLedger.setLedgerId(1L);
        fromLedger.setLedgerBalance(1000.0);
        fromLedger.setUsers(user1);
        fromLedger.setLedgerName("From ledger");

        toLedger.setLedgerId(2L);
        toLedger.setLedgerBalance(500.0);
        toLedger.setLedgerName("To Ledger");

    }

    @Test
    void testSuccessfulInternalTransaction() {
        toLedger.setUsers(user1);

        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(2L))
                .thenReturn(Optional.of(toLedger));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.processTransaction(
                1L, 2L, 200.0, TransactionType.INTERNAL);

        assertNotNull(response);
        assertEquals(1L, response.getFromLedgerId());
        assertEquals(2L, response.getToLedgerId());
        assertEquals(200.0, response.getTransactionAmount());

        verify(ledgerRepository, times(2)).save(any(Ledger.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testSuccessfulExternalTransaction() {
        toLedger.setUsers(user2);

        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(2L))
                .thenReturn(Optional.of(toLedger));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.processTransaction(
                1L, 2L, 150.0, TransactionType.EXTERNAL);

        assertNotNull(response);
        assertEquals(150.0, response.getTransactionAmount());
    }

    @Test
    void testFromLedgerNotFound() {
        when(ledgerRepository
                .findById(1L)).thenReturn(Optional.empty());

        assertThrows(LedgerNotFoundException.class, () ->
                transactionService.processTransaction(1L, 2L, 100.0, TransactionType.INTERNAL));
    }

    @Test
    void testToLedgerNotFound() {
        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(LedgerNotFoundException.class, () ->
                transactionService.processTransaction(1L, 2L, 100.0, TransactionType.INTERNAL));
    }

    @Test
    void testInsufficientBalance() {
        fromLedger.setLedgerBalance(50.0);
        toLedger.setUsers(user1);

        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(2L))
                .thenReturn(Optional.of(toLedger));

        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.processTransaction(1L, 2L, 100.0, TransactionType.INTERNAL));
    }

    @Test
    void testInvalidInternalTransactionBetweenDifferentUsers() {
        toLedger.setUsers(user2);

        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(2L))
                .thenReturn(Optional.of(toLedger));

        assertThrows(InvalidTransactionTypeException.class, () ->
                transactionService.processTransaction(1L, 2L, 100.0, TransactionType.INTERNAL));
    }

    @Test
    void testInvalidExternalTransactionBetweenSameUsers() {
        toLedger.setUsers(user1);
        when(ledgerRepository.findById(1L))
                .thenReturn(Optional.of(fromLedger));
        when(ledgerRepository.findById(2L))
                .thenReturn(Optional.of(toLedger));

        assertThrows(InvalidTransactionTypeException.class, () ->
                transactionService.processTransaction(1L, 2L, 100.0, TransactionType.EXTERNAL));
    }
}