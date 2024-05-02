package JuDBu.custommaster.domain.controller.account;

import JuDBu.custommaster.auth.facade.AuthenticationFacade;
import JuDBu.custommaster.domain.entity.account.RefreshToken;
import JuDBu.custommaster.domain.repo.account.AccountRepo;
import JuDBu.custommaster.domain.repo.account.RefreshTokenRepo;
import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.auth.jwt.JwtTokenUtils;
import JuDBu.custommaster.auth.jwt.dto.JwtResponseDto;
import JuDBu.custommaster.domain.service.account.TokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenController {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // 액세스 토큰 발급 테스트
    @PostMapping("/issue-access")
    public JwtResponseDto issueAccess(
            @RequestParam
            String username,
            @RequestParam
            String password
    ) {
        // 사용자가 제공한 username(id), password가 저장된 사용자인지 판단
        if (!manager.userExists(username))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        UserDetails userDetails
                = manager.loadUserByUsername(username);

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // JWT 발급
        return tokenService.issueAccess(userDetails);
    }

    // 리프레쉬 토큰 발급 테스트
    @PostMapping("/issue-refresh")
    public JwtResponseDto issueRefresh(
            @RequestParam
            String username,
            @RequestParam
            String token
    ) {
        // 사용자가 제공한 username(id), password가 저장된 사용자인지 판단
        if (!manager.userExists(username))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        UserDetails userDetails
                = manager.loadUserByUsername(username);

        // JWT 발급
        return tokenService.issueRefresh(userDetails, token);
    }
    // 토큰 발급 보기


    // 발급된 토큰이 유효한지 확인 유효하지 않다면 기간이 다 됐는지 확인
    @GetMapping("/validate")
    public Claims validateToken(
            @RequestParam("token")
            String token
    ) {
        if (!jwtTokenUtils.validateAccess(token))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return jwtTokenUtils.parseClaims(token);
    }

    //ㅇㅅㅇ
    @GetMapping("/check-token")
    public String checkToken() {
        return "hi";
    }
}

