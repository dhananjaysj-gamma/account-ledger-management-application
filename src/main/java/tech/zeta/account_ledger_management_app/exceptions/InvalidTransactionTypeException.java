package tech.zeta.account_ledger_management_app.exceptions;


public class InvalidTransactionTypeException extends RuntimeException {
    public InvalidTransactionTypeException(String message) {
        super(message);
    }
}
