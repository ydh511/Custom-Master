package JuDBu.custommaster.auth.jwt.dto;

import JuDBu.custommaster.domain.entity.account.RefreshToken;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Data
public class JwtRefreshDto {
    private String accessToken;
    private String refreshToken;
    private Long accountId;
    private LocalDateTime issuedTime;

    public static JwtRefreshDto fromEntity(RefreshToken token){
        return JwtRefreshDto.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .accountId(token.getAccountId())
                .issuedTime(token.getIssuedTime())
                .build();
    }
}
