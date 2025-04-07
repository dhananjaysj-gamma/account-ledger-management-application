package tech.zeta.account_ledger_management_app.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Tenant {

    @Id
    private Long tenantId;
    private String tenantName;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Users> users = new ArrayList<>();

    @ElementCollection
    private List<Long> registeredUserIds;// Stores user IDs pre-registered by the bank
}
