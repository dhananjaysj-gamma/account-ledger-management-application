package tech.zeta.account_ledger_management_app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.zeta.account_ledger_management_app.models.Ledger;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger,Long> {

}
