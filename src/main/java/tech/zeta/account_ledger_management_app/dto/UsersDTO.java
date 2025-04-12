package tech.zeta.account_ledger_management_app.dto;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import tech.zeta.account_ledger_management_app.enums.UserStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {


    private Long userId;
    private String name;
    private String username;
    private String adhaarNumber;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;



}
