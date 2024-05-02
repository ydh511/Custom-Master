package JuDBu.custommaster.domain.repo;

import JuDBu.custommaster.domain.entity.Shop;
import JuDBu.custommaster.domain.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Shop findByAccount(Account account);
}
