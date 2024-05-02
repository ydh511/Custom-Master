package JuDBu.custommaster.domain.repo;

import JuDBu.custommaster.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
}
