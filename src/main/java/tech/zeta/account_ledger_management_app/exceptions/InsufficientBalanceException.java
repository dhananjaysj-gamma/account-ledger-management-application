package tech.zeta.account_ledger_management_app.exceptions;


public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
