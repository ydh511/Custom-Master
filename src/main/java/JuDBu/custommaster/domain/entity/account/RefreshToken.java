package JuDBu.custommaster.domain.entity.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken{
    @Id
    private String accessToken;

    private String refreshToken;
    private Long accountId;
    private LocalDateTime issuedTime;
}
