package tech.zeta.account_ledger_management_app.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.zeta.account_ledger_management_app.dto.LedgerDTO;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.exceptions.InvalidEntityIdProvidedException;
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


    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public LedgerDTO createLedger(Ledger ledger, Long userId) {


        // Fetch the user from the DB
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ledger.setUsers(user);
        ledgerRepository.save(ledger);
        return new LedgerDTO(
                ledger.getLedgerId(),
                ledger.getLedgerName(),
                ledger.getLedgerBalance()
        );

    }

    public LedgerDTO getLedgerById(Long ledgerId) {

        if (ledgerId <= 0 || ledgerId < 10000) {
            log.error("Invalid id provided:{}", ledgerId);
            throw new InvalidEntityIdProvidedException("The Ledger Id Must be of length 6");
        }
        Ledger ledger = ledgerRepository.findById(ledgerId).orElseThrow(() ->
                new LedgerNotFoundException("The Provided Ledger Id Not Found, Please check the Id:" + ledgerId));

        return new LedgerDTO(
                ledger.getLedgerId(),
                ledger.getLedgerName(),
                ledger.getLedgerBalance()
        );
    }
    public List<TransactionResponse> getTransactionHistoryById(Long fromLedgerId) {

         ledgerRepository.findById(fromLedgerId).orElseThrow(()-> new LedgerNotFoundException("There is No Ledger Created with this ID:"));

          List<Transaction> transaction = transactionRepository.findAllByFromLedgerId(fromLedgerId);

          if(transaction.isEmpty())
          {
              return Collections.emptyList();
          }

        return transaction.stream()
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

