package tech.zeta.account_ledger_management_app.dto;


import lombok.Data;

@Data
public class LoginRequest {

    private String name;
    private String username;
    private String password;
}
