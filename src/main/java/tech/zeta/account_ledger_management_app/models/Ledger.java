package tech.zeta.account_ledger_management_app.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Ledger {

    @Id
    private Long ledgerId;
    private String ledgerName;
    private double ledgerBalance;

    @ManyToOne
    @JoinColumn (name = "user_id",nullable = false)
    private Users users;

    @OneToMany(mappedBy = "ledger", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<Transaction> transaction;

}
