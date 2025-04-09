package tech.zeta.account_ledger_management_app.dto;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import tech.zeta.account_ledger_management_app.enums.UserStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {


    private Long userId;
    private String name;
    private String userName;
    private String adhaarNumber;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;



}
