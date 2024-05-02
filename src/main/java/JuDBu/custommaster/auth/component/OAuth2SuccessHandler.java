package JuDBu.custommaster.auth.component;

import JuDBu.custommaster.domain.dto.account.CustomAccountDetails;
import JuDBu.custommaster.domain.entity.account.Account;
import JuDBu.custommaster.domain.entity.account.Authority;
import JuDBu.custommaster.auth.jwt.JwtTokenUtils;
import JuDBu.custommaster.domain.repo.account.AccountRepo;
import JuDBu.custommaster.domain.service.account.JpaUserDetailsManger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
// OAuth2UserServiceImpl이 성공적으로 OAuth2 과정을 마무리 했을 때, 넘겨받은 사용자 정보를 바탕으로 JWT를 생성,
// 클라이언트한테 JWT를 전달
public class OAuth2SuccessHandler
        // 인증에 성공했을 때 특정 URL로 리다이렉트 하고 싶은 경우 활용 가능한 SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {
    // JWT 발급을 위해 JwtTokenUtils
    private final JwtTokenUtils tokenUtils;
    // 사용자 정보 등록을 위해 UserDetailsManager
    private final JpaUserDetailsManger userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepo accountRepo;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        // OAuth2UserServiceImpl의 반환값이 할당된다.
        OAuth2User oAuth2User
                = (OAuth2User) authentication.getPrincipal();

        // 넘겨받은 정보를 바탕으로 사용자 정보를 준비
        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");
        String name = oAuth2User.getAttribute("name");
        String username
                = String.format("{%s}%s", provider, email);
        String providerId = oAuth2User.getAttribute("id").toString();
        // 해당 이메일을 가진 유저가 이미 존재하는지
        log.info("naver email: {}",email);

        // 처음으로 이 소셜 로그인으로 로그인을 시도했다.
        if (!userDetailsManager.userExists(username)) {
            // 새 계정을 만들어야 한다.
            userDetailsManager.createUser(CustomAccountDetails.builder()
                    .username(username)
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(providerId))
                    .authority(Authority.ROLE_ACTIVE_USER)
                    .build());
        }

        // 데이터베이스에서 사용자 계정 회수
        UserDetails userDetails
                = userDetailsManager.loadUserByUsername(username);
        // JWT 생성
        String accessToken = tokenUtils.generateToken(userDetails,"accessToken");
        String refreshToken = tokenUtils.generateToken(userDetails,"refreshToken");
        Cookie cookie = new Cookie("CMToken", refreshToken);
        cookie.setMaxAge(24 * 60 * 60 * 2);
        cookie.setPath("/"); // 쿠키의 경로 설정
        cookie.setDomain("localhost");
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        // 어디로 리다이렉트 할지 지정
        String targetUrl = String.format(
                "http://localhost:8080/account/oauth?token=%s",accessToken
        );
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
