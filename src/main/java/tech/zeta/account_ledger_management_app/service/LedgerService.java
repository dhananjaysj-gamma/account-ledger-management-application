package tech.zeta.account_ledger_management_app.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.zeta.account_ledger_management_app.dto.LedgerDTO;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.exceptions.LedgerNotFoundException;
import tech.zeta.account_ledger_management_app.exceptions.UserNotFoundException;
import tech.zeta.account_ledger_management_app.models.Ledger;
import tech.zeta.account_ledger_management_app.models.Transaction;
import tech.zeta.account_ledger_management_app.models.Users;
import tech.zeta.account_ledger_management_app.repository.LedgerRepository;
import tech.zeta.account_ledger_management_app.repository.TransactionRepository;
import tech.zeta.account_ledger_management_app.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public LedgerService(LedgerRepository ledgerRepository, UserRepository userRepository,TransactionRepository transactionRepository){

        this.ledgerRepository=ledgerRepository;
        this.userRepository=userRepository;
        this.transactionRepository=transactionRepository;

    }

    public LedgerDTO createLedger(Ledger ledger, Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ledger.setUsers(user);
        ledgerRepository.save(ledger);
        return ledgerDetails(ledger);

    }

    public LedgerDTO getLedgerById(Long ledgerId) {

        Ledger ledger = ledgerRepository.findById(ledgerId).orElseThrow(() ->
                new LedgerNotFoundException("The Provided Ledger Id Not Found, Please check the Id:" + ledgerId));

        return ledgerDetails(ledger);
    }

    public List<TransactionResponse> getTransactionHistoryById(Long fromLedgerId) {
        ledgerRepository.findById(fromLedgerId).orElseThrow(() -> new LedgerNotFoundException("There is No Ledger Created with this ID:" + fromLedgerId));

        List<Transaction> transactions = transactionRepository.findAllByFromLedgerId(fromLedgerId);

        if (transactions.isEmpty()) {
            return Collections.emptyList();
        }
        return transactionHistory(transactions);

    }

    //utility methods
    private static LedgerDTO ledgerDetails(Ledger ledger) {
        return LedgerDTO.builder().ledgerId(ledger.getLedgerId())
                .ledgerName(ledger.getLedgerName())
                .ledgerBalance(ledger.getLedgerBalance())
                .build();
    }
    
    private static List<TransactionResponse> transactionHistory(List<Transaction> transactions)
    {
        return transactions.stream()
                .map(txn -> TransactionResponse.builder()
                        .transactionId(txn.getTransactionId())
                        .fromLedgerId(txn.getFromLedgerId())
                        .toLedgerId(txn.getToLedgerId())
                        .transactionAmount(txn.getTransactionAmount())
                        .transactionType(txn.getTransactionType())
                        .transactionDateAndTime(txn.getTransactionDate())
                        .build())
                .toList();
    }

}

