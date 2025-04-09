package tech.zeta.account_ledger_management_app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import tech.zeta.account_ledger_management_app.enums.TransactionType;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionResponse {


    private Long transactionId;
    private Long fromLedgerId;
    private Long toLedgerId;
    private double transactionAmount;
    private TransactionType transactionType;
    private LocalDateTime transactionDateAndTime;

}
