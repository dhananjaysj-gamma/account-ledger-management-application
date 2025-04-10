package tech.zeta.account_ledger_management_app.dto;




import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LedgerDTO {

    private Long ledgerId;
    private String ledgerName;
    private double  ledgerBalance;
}
