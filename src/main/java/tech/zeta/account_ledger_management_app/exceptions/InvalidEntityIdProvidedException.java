package tech.zeta.account_ledger_management_app.exceptions;

public class InvalidEntityIdProvidedException extends RuntimeException {

    public InvalidEntityIdProvidedException(String message) {
        super(message);
    }
}
