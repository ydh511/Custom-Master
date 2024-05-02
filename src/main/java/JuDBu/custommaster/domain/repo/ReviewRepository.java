package JuDBu.custommaster.domain.repo;


import JuDBu.custommaster.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Page<Review>> findByShop_IdOrderByIdDesc(Long shopId, Pageable pageable);
    Optional<Review> findByShop_IdAndId(Long shopId, Long reviewId);
    Optional<List<Review>> findByShop_Id(Long shopId);
}
