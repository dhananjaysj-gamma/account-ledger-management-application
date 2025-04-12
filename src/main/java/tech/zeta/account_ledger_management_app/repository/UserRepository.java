package tech.zeta.account_ledger_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.zeta.account_ledger_management_app.models.Users;

@Repository
public interface UserRepository  extends JpaRepository<Users, Long> {
   Users findByUsername(String username);
}