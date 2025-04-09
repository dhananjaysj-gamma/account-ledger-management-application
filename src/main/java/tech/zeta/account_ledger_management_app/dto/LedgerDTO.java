package tech.zeta.account_ledger_management_app.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LedgerDTO {

    private Long ledgerId;
    private String ledgerName;
    private double  ledgerBalance;
}
