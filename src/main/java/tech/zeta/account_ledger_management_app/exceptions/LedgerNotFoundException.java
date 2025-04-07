package tech.zeta.account_ledger_management_app.exceptions;

public class LedgerNotFoundException extends RuntimeException {
    public LedgerNotFoundException(String message) {
        super(message);
    }
}
