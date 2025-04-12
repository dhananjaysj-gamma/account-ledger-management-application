package tech.zeta.account_ledger_management_app.models;

import jakarta.persistence.*;
import lombok.*;
import tech.zeta.account_ledger_management_app.enums.UserStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Users {

    @Id
    private Long userId;
    private String name;
    private String username;
    private String password;
    private String aadhaarNumber;

    /// soft delete
    private Boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "users", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<Ledger> ledger = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

}
