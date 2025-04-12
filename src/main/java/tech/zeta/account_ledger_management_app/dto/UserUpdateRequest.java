package tech.zeta.account_ledger_management_app.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.zeta.account_ledger_management_app.enums.UserStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateRequest {
    private String name;
    private String username;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
