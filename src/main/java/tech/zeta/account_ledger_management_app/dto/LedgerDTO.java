package tech.zeta.account_ledger_management_app.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;



@Builder
@Data
@AllArgsConstructor
public class LedgerDTO {

    private Long ledgerId;
    private String legerName;
    private double  ledgerBalance;
}
