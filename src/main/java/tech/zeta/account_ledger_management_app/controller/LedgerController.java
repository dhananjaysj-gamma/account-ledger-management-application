package tech.zeta.account_ledger_management_app.controller;

import org.springframework.web.bind.annotation.*;
import tech.zeta.account_ledger_management_app.dto.LedgerDTO;
import tech.zeta.account_ledger_management_app.dto.TransactionResponse;
import tech.zeta.account_ledger_management_app.models.Ledger;
import tech.zeta.account_ledger_management_app.service.LedgerService;

import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService=ledgerService;
    }

    @PostMapping("/user/{userId}")
    public LedgerDTO createLedger(@RequestBody Ledger ledger, @PathVariable Long userId) {

       return ledgerService.createLedger(ledger,userId);
    }

    @GetMapping("/{ledgerId}")
    public LedgerDTO getLedgerById(@PathVariable Long ledgerId) {
        return ledgerService.getLedgerById(ledgerId);
    }

    @GetMapping("/transaction/history")
    public List<TransactionResponse> getLedgerTransactionHistory(@RequestParam Long fromLedgerId) {
        return  ledgerService.getTransactionHistoryById(fromLedgerId);
    }






}
