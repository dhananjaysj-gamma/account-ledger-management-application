package tech.zeta.account_ledger_management_app.exceptions;

public class UserNotRegisteredUnderTenantException extends RuntimeException {
    public UserNotRegisteredUnderTenantException(String message) {
        super(message);
    }
}
