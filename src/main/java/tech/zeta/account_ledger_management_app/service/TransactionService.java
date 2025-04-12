package tech.zeta.account_ledger_management_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.enums.TransactionType;
import tech.zeta.account_ledger_management_app.exceptions.InsufficientBalanceException;
import tech.zeta.account_ledger_management_app.exceptions.InvalidTransactionTypeException;
import tech.zeta.account_ledger_management_app.exceptions.LedgerNotFoundException;
import tech.zeta.account_ledger_management_app.models.Ledger;
import tech.zeta.account_ledger_management_app.models.Transaction;
import tech.zeta.account_ledger_management_app.repository.LedgerRepository;
import tech.zeta.account_ledger_management_app.repository.TransactionRepository;



@Service
public class TransactionService {
    
    private final LedgerRepository ledgerRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(LedgerRepository ledgerRepository, TransactionRepository transactionRepository) {
        this.ledgerRepository=ledgerRepository;
        this.transactionRepository=transactionRepository;
    }

    @Transactional
    public TransactionResponse processTransaction(Long fromLedgerId, Long toLedgerId, double transactionAmount, TransactionType transactionType) {
        Ledger fromLedger = extractLedger(fromLedgerId);
        Ledger toLedger =  extractLedger(toLedgerId);

        if (fromLedger.getLedgerBalance() < transactionAmount) {
            throw new InsufficientBalanceException("Insufficient balance in the Ledger");
        }

        if (transactionType.equals(TransactionType.INTERNAL) && !fromLedger.getUsers().getUserId().equals(toLedger.getUsers().getUserId())) {
            throw new InvalidTransactionTypeException("Internal transactions must be between ledgers of the same user!");
        } else if (transactionType.equals(TransactionType.EXTERNAL) && fromLedger.getUsers().getUserId().equals(toLedger.getUsers().getUserId())) {
                throw new InvalidTransactionTypeException("External transactions must be between different users!");
        }

        fromLedger.setLedgerBalance(fromLedger.getLedgerBalance() - transactionAmount);
        toLedger.setLedgerBalance(toLedger.getLedgerBalance() + transactionAmount);

        ledgerRepository.save(fromLedger);
        ledgerRepository.save(toLedger);

        Transaction transaction = addTransactionDetails(fromLedgerId, toLedgerId, transactionAmount, transactionType, fromLedger);
        transactionRepository.save(transaction);
        return transactionDetails(transaction);
    }

    private Ledger extractLedger(Long ledgerId) {
      return ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new LedgerNotFoundException("Ledger not found:"+ledgerId));
    }

    public static Transaction addTransactionDetails(Long fromLedgerId, Long toLedgerId, double transactionAmount, TransactionType transactionType,Ledger ledger) {
       Transaction transaction = new Transaction();
       transaction.setFromLedgerId(fromLedgerId);
       transaction.setToLedgerId(toLedgerId);
       transaction.setTransactionAmount(transactionAmount);
       transaction.setTransactionType(transactionType);
       transaction.setLedger(ledger);
       return transaction;
    }

    private static TransactionResponse transactionDetails(Transaction transaction) {
        return TransactionResponse.builder().
                transactionId(transaction.getTransactionId())
                .fromLedgerId(transaction.getFromLedgerId())
                .toLedgerId(transaction.getToLedgerId())
                .transactionAmount(transaction.getTransactionAmount())
                .transactionType(transaction.getTransactionType())
                .transactionDateAndTime(transaction.getTransactionDate())
                .build();
    }
}

