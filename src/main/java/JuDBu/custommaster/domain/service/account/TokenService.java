package JuDBu.custommaster.domain.service.account;

import JuDBu.custommaster.auth.facade.AuthenticationFacade;
import JuDBu.custommaster.auth.jwt.JwtTokenUtils;
import JuDBu.custommaster.auth.jwt.dto.JwtResponseDto;
import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.domain.entity.account.RefreshToken;
import JuDBu.custommaster.domain.repo.account.AccountRepo;
import JuDBu.custommaster.domain.repo.account.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final AccountRepo accountRepo;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenRepo tokenRepo;

    public JwtResponseDto issueAccess(UserDetails userDetails){
        String accessToken = jwtTokenUtils.generateToken(userDetails, "accessToken");
        JwtResponseDto response = JwtResponseDto.builder()
                .accessToken(accessToken)
                .build();
        return response;
    }

    public JwtResponseDto issueRefresh(UserDetails userDetails, String accessToken){
        String refreshToken = jwtTokenUtils.generateToken(userDetails, "refreshToken");
        Account account = accountRepo.findByUsername(userDetails.getUsername()).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        RefreshToken token = RefreshToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accountId(account.getId())
                .issuedTime(LocalDateTime.now())
                .build();
        tokenRepo.save(token);

        JwtResponseDto response = JwtResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return response;
    }

    public String username(String token){
        RefreshToken refreshToken = tokenRepo.findTopByAccessTokenOrderByIssuedTimeDesc(token);
        if (refreshToken == null){
            return null;
        }
        Account account = accountRepo.findById(refreshToken.getAccountId()).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        return account.getUsername();
    }
}
