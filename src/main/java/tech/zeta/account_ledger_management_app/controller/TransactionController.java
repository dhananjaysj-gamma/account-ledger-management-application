package tech.zeta.account_ledger_management_app.controller;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.enums.TransactionType;
import tech.zeta.account_ledger_management_app.models.Transaction;
import tech.zeta.account_ledger_management_app.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


    @Autowired
    private TransactionService transactionService;


    @PostMapping("/transfer")
    public TransactionResponse transferFunds(
            @RequestParam Long fromLedgerId,
            @RequestParam Long toLedgerId,
            @RequestParam double transactionAmount,
            @RequestParam TransactionType transactionType
            )  {
        return transactionService.processTransaction(fromLedgerId,toLedgerId,transactionAmount,transactionType);
    }
//    @GetMapping("/history")
//    public TransactionHistoryResponse getTransactionHistoryByLedgerId(Long ledgerId)
//    {
//        return transactionService.getTransactionHistoryById(ledgerId);
//    }
}
