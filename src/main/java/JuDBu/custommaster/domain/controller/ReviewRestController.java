package JuDBu.custommaster.domain.controller;

import JuDBu.custommaster.domain.service.ReviewService;
import JuDBu.custommaster.domain.dto.review.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/reviews")
@RequiredArgsConstructor
public class ReviewRestController {
    private final ReviewService reviewService;

    // Create review
    @PostMapping("/{shopId}/create")
    public ReviewDto createReview(
            @PathVariable("shopId") Long shopId,
            @RequestParam("comment") String comment
    ) {
        return reviewService.createReview(shopId, comment);
    }

    // READ ALL review
    @GetMapping("/{shopId}")
    public Page<ReviewDto> readReviewPaged(
            @PathVariable Long shopId,
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return reviewService.readReviewPaged(shopId, pageable);
    }

    // READ ONE Review
    @GetMapping("/{shopId}/read/{reviewId}")
    public ReviewDto readReview(
            @PathVariable("shopId") Long shopId,
            @PathVariable("reviewId") Long reviewId
    ) {
        return reviewService.readReview(shopId, reviewId);
    }

    // 댓글 수정
    @PutMapping("/{shopId}/update/{reviewId}")
    public ReviewDto updateReview(
            @PathVariable("shopId") Long shopId,
            @PathVariable("reviewId") Long reviewId,
            @RequestParam("comment") String comment
    ) {
        return reviewService.updateReview(shopId, reviewId, comment);
    }

    // 댓글 삭제
    @DeleteMapping("/{shopId}/delete/{reviewId}")
    public void deleteReview(
            @PathVariable("shopId") Long shopId,
            @PathVariable("reviewId") Long reviewId
    ) {
        reviewService.deleteReview(shopId, reviewId);
    }
}


