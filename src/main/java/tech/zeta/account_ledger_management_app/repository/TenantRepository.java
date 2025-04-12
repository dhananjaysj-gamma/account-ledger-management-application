package tech.zeta.account_ledger_management_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.zeta.account_ledger_management_app.models.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    boolean existsByRegisteredUserIdsContaining(Long userId);

}
