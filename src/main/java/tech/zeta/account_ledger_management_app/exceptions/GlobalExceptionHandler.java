package tech.zeta.account_ledger_management_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIME_STAMP = "timestamp";
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";

    @ExceptionHandler(InvalidEntityIdProvidedException.class)
    public ResponseEntity<Object> handlerOfInvalidEntityIdProvidedException(InvalidEntityIdProvidedException invalidEntityIdProvidedException) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIME_STAMP, LocalDateTime.now());
        response.put(MESSAGE, invalidEntityIdProvidedException.getMessage());
        response.put(STATUS, HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Object> handlerOfInsufficientBalanceException(InsufficientBalanceException insufficientBalanceException) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIME_STAMP, LocalDateTime.now());
        response.put(MESSAGE, insufficientBalanceException.getMessage());
        response.put(STATUS, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTransactionTypeException.class)
    public ResponseEntity<Object> handlerOfInvalidTransactionTypeException(InvalidTransactionTypeException invalidTransactionTypeException) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIME_STAMP, LocalDateTime.now());
        response.put(MESSAGE, invalidTransactionTypeException.getMessage());
        response.put(STATUS, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LedgerNotFoundException.class)
    public ResponseEntity<Object> handlerOfLedgerNotFoundException(LedgerNotFoundException ledgerNotFoundException) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIME_STAMP, LocalDateTime.now());
        response.put(MESSAGE, ledgerNotFoundException.getMessage());
        response.put(STATUS, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handlerOfUserNotFoundException(UserNotFoundException userNotFoundException) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIME_STAMP, LocalDateTime.now());
        response.put(MESSAGE, userNotFoundException.getMessage());
        response.put(STATUS, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handlerOfBadCredentialsException(BadCredentialsException badCredentialsException) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIME_STAMP,LocalDateTime.now());
        response.put(MESSAGE,badCredentialsException.getMessage());
        response.put(STATUS,HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
    }
}

