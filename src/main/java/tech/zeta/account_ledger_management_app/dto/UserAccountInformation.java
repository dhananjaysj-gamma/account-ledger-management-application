package tech.zeta.account_ledger_management_app.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.zeta.account_ledger_management_app.enums.UserStatus;
import tech.zeta.account_ledger_management_app.models.Ledger;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountInformation {

    private Long userId;
    private String name;
    private String username;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private List<Ledger> ledgers;

}
