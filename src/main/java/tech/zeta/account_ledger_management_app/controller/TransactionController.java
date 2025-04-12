package tech.zeta.account_ledger_management_app.controller;

import org.springframework.web.bind.annotation.*;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.enums.TransactionType;

import tech.zeta.account_ledger_management_app.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {


    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public TransactionResponse transferFunds(
            @RequestParam Long fromLedgerId,
            @RequestParam Long toLedgerId,
            @RequestParam double transactionAmount,
            @RequestParam TransactionType transactionType
            )  {
        return transactionService.processTransaction(fromLedgerId,toLedgerId,transactionAmount,transactionType);
    }
}
