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

import java.time.LocalDateTime;


@Service
public class TransactionService {


    @Autowired
    private LedgerRepository ledgerRepository;


    @Autowired
    private TransactionRepository transactionRepository;


    @Transactional
    public TransactionResponse processTransaction(Long fromLedgerId, Long toLedgerId, double transactionAmount, TransactionType transactionType) {

        Ledger fromLedger = ledgerRepository.findById(fromLedgerId)
                .orElseThrow(() -> new LedgerNotFoundException("From Ledger not found"));
        Ledger toLedger = ledgerRepository.findById(toLedgerId)
                .orElseThrow(() -> new LedgerNotFoundException("To Ledger not found"));

        if (fromLedger.getLedgerBalance() < transactionAmount) {
            throw new InsufficientBalanceException("Insufficient balance in the Ledger");
        }

        if (transactionType == TransactionType.INTERNAL && !fromLedger.getUsers().getUserId().equals(toLedger.getUsers().getUserId()))
        {
            throw new InvalidTransactionTypeException("Internal transactions must be between ledgers of the same user");
        } else if (transactionType == TransactionType.EXTERNAL && fromLedger.getUsers().getUserId().equals(toLedger.getUsers().getUserId())) {
                throw new InvalidTransactionTypeException("External transactions must be between different users");
            }

        fromLedger.setLedgerBalance(fromLedger.getLedgerBalance() - transactionAmount);
        toLedger.setLedgerBalance(toLedger.getLedgerBalance() + transactionAmount);

        ledgerRepository.save(fromLedger);
        ledgerRepository.save(toLedger);

        Transaction transaction = new Transaction();
        transaction.setFromLedgerId(fromLedgerId);
        transaction.setToLedgerId(toLedgerId);
        transaction.setTransactionAmount(transactionAmount);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setLedger(fromLedger);
        transactionRepository.save(transaction);

        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getFromLedgerId(),
                transaction.getToLedgerId(),
                transaction.getTransactionAmount(),
                transaction.getTransactionType(),
                transaction.getTransactionDate()
        );
    }


}

