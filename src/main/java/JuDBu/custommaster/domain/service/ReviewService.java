package JuDBu.custommaster.domain.service;

import JuDBu.custommaster.auth.facade.AuthenticationFacade;
import JuDBu.custommaster.domain.dto.account.AccountDto;
import JuDBu.custommaster.domain.entity.Ord;
import JuDBu.custommaster.domain.entity.Review;
import JuDBu.custommaster.domain.dto.review.ReviewDto;
import JuDBu.custommaster.domain.entity.Shop;
import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.domain.entity.account.Authority;
import JuDBu.custommaster.domain.repo.OrdRepo;
import JuDBu.custommaster.domain.repo.ReviewRepository;
import JuDBu.custommaster.domain.repo.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.pagination.LimitOffsetLimitHandler;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final AuthenticationFacade authFacade;
    private final ShopRepository shopRepo;
    private final OrdRepo ordRepo;

    // CREATE review
    public ReviewDto createReview(
            Long shopId,
            String comment
    ) {
        // 어떤 유저가 리뷰를 작성하려 하는지 확인
        Account account = authFacade.getAccount();
        log.info("auth account: {}", account.getUsername());

/*        // 리뷰를 작성하려는 고객이 해당 매장에서 구매 기록이 없는 경우
        if (!ordRepo.findByShop_IdAndAccount_Id(shopId, account.getId())) {
            log.info("매장 구매 고객이 아닙니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }*/

        Shop shop = shopRepo.findById(shopId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        Review review = Review.builder()
                .account(account)
                .shop(shop)
                .comment(comment)
                .build();

        return ReviewDto.fromEntity(reviewRepository.save(review));
    }

    // READ ALL Review
    // 한 매장에 달린 리뷰 전체 불러오기
    public Page<ReviewDto> readReviewPaged(Long shopId, Pageable pageable) {
        // 해당 매장의 리뷰 불러오기
        Page<Review> reviews = reviewRepository.findByShop_IdOrderByIdDesc(shopId, pageable)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return reviews.map(ReviewDto::fromEntity);
    }

    // 리뷰 작성자 이름 불러오기
    public List<String> getAccountName(Long shopId) {
        List<Review> reviewList = reviewRepository.findByShop_Id(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return reviewList.stream()
                .map(Review::getAccount)
                .map(Account::getName)
                .collect(Collectors.toList());
    }

    // READ ONE Review
    public ReviewDto readReview(Long shopId, Long reviewId) {
        Review review = reviewRepository.findByShop_IdAndId(shopId, reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        log.info("review: {}", review);

        return ReviewDto.fromEntity(review);
    }

    // 특정 리뷰 작성자 이름 불러오기
    public AccountDto getReviewName(Long shopId, Long reviewId) {
        Review review = reviewRepository.findByShop_IdAndId(shopId, reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        log.info("review: {}", review);

        return AccountDto.fromEntity(review.getAccount());
    }

    // review 수정
    public ReviewDto updateReview(
            Long shopId,
            Long reviewId,
            String comment
    ) {
        // 계정 불러오기
        Account account = authFacade.getAccount();
        log.info("Account: {}", account.getUsername());

        // 수정하려는 review 불러오기
        Review review = reviewRepository.findByShop_IdAndId(shopId, reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 수정하려는 사람이 review 작성자인지 체크
        if (!review.getAccount().getId().equals(account.getId())) {
            if (!account.getAuthority().equals(Authority.ROLE_ADMIN)) {
                log.error("리뷰 작성자만 수정 가능합니다.");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

        // 리뷰 수정 후 저장
        review.setComment(comment);
        return ReviewDto.fromEntity(reviewRepository.save(review));
    }


    // DELETE Reivew
    public void deleteReview(Long shopId, Long reviewId) {
        // 접근자 확인
        Account account = authFacade.getAccount();
        log.info("auth account: {}", account.getUsername());

        // 삭제하려는 리뷰 불러오기
        Review review = reviewRepository.findByShop_IdAndId(shopId, reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 삭제하려는 사람이 reivew 주인과 일치하지 않으면
        if (!review.getAccount().getId().equals(account.getId())) {
            // 판매자 또는 관리자인지 확인
            if (!checkAuth(account, shopId)) {
                log.error("판매자 또는 리뷰 작성자만 삭제 가능합니다.");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
        // 리뷰 삭제
        reviewRepository.delete(review);
    }

    // 권한이 매장 주인 또는 관리자인 경우 true
    private boolean checkAuth(Account account, Long shopId) {
        if (account.getAuthority().equals(Authority.ROLE_ADMIN)) {
            return true;
        }

        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return shop.getAccount().getId().equals(account.getId());
    }
}