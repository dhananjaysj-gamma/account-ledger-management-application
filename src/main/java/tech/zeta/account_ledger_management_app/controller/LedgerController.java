package tech.zeta.account_ledger_management_app.controller;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import tech.zeta.account_ledger_management_app.dto.LedgerDTO;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.models.Ledger;
import tech.zeta.account_ledger_management_app.service.LedgerService;

import java.util.List;


@RestController
@RequestMapping("/ledger")
public class LedgerController {


    @Autowired
    private LedgerService ledgerService;

    @PostMapping("/user/{userId}")
    public LedgerDTO createLedger(@RequestBody Ledger ledger, @PathVariable Long userId)
    {

       return ledgerService.createLedger(ledger,userId);
    }

    @GetMapping("/{ledgerId}")
    public LedgerDTO getLedgerById(@PathVariable Long ledgerId)
    {
        return ledgerService.getLedgerById(ledgerId);
    }

    @GetMapping("/transactions/history")
    public List<TransactionResponse> getLedgerTransactionHistory(@RequestParam Long fromLedgerId)
    {
        return  ledgerService.getTransactionHistoryById(fromLedgerId);
    }




}
