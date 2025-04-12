package tech.zeta.account_ledger_management_app.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import tech.zeta.account_ledger_management_app.enums.TransactionType;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transactionId;
    private Long fromLedgerId;
    private Long toLedgerId;
    private double transactionAmount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;
}
