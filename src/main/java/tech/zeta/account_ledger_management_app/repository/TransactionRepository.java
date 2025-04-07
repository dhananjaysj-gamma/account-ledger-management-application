package tech.zeta.account_ledger_management_app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.zeta.account_ledger_management_app.models.Transaction;

import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
     List<Transaction> findAllByFromLedgerId(Long fromLedgerId);

}
