package JuDBu.custommaster.domain.dto.review;

import JuDBu.custommaster.domain.entity.Review;
import JuDBu.custommaster.domain.entity.Shop;
import JuDBu.custommaster.domain.entity.account.Account;
import lombok.*;


@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private Long id;
    @Setter
    private Long accountId;
    @Setter
    private Long shopId;
    @Setter
    private String comment;
    @Setter
    private Long orderId;

    public static ReviewDto fromEntity(Review entity){
        ReviewDto.ReviewDtoBuilder builder = ReviewDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccount().getId())
                .shopId(entity.getShop().getId())
                .comment(entity.getComment())
                .orderId(entity.getOrderId());

        return builder.build();
    }
}

