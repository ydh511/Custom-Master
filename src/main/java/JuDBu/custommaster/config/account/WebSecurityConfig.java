package JuDBu.custommaster.config.account;

import JuDBu.custommaster.auth.component.OAuth2SuccessHandler;
import JuDBu.custommaster.auth.jwt.JwtExceptionFilter;
import JuDBu.custommaster.auth.jwt.JwtTokenFilter;
import JuDBu.custommaster.auth.jwt.JwtTokenUtils;
import JuDBu.custommaster.domain.entity.account.Authority;
import JuDBu.custommaster.domain.service.account.OAuth2UserServiceImpl;
import JuDBu.custommaster.domain.service.account.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("web");
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                                .permitAll()
                                // form
                                .requestMatchers(
                                        "/account/login",
                                        "/api/account/login",
                                        "/account/register",
                                        "/account/business-register",
                                        "/account/logout",
                                        "/account/profile",
                                        "/api/account/profile",
                                        "/account/update",
                                        "/account/mail-auth"
                                ).permitAll()
                                // 로그인, 회원가입
                                .requestMatchers(
                                        "/api/account/register",
                                        "/api/account/business-register"
                                ).anonymous()
                                // 로그아웃
                                // 프로필, 유저정보 수정
                                .requestMatchers(
                                        "/api/account/logout",
                                        "/api/account/update",
                                        "account/header"
                                ).authenticated()
                                // 메일 인증
                                .requestMatchers(
                                        "/api/account/send-mail",
                                        "/api/account/check-mail"
                                ).hasAnyAuthority(Authority.ROLE_INACTIVE_USER.getAuthority())

                                // 주문 요청 승락/거절
                                .requestMatchers(
                                        "/order-accept/{shopId}/read-all",
                                        "/order-accept/{shopId}/read/{ordId}",
                                        "/order-accept/{shopId}/accept/{ordId}",
                                        "/order-accept/{shopId}/delete/{ordId}"
                                ).hasAnyAuthority(
                                        Authority.ROLE_BUSINESS_USER.getAuthority(),
                                        Authority.ROLE_ADMIN.getAuthority()
                                )
                                // Review CUD
                                .requestMatchers(
                                        "/review/{shopId}/create",
                                        "/review/{shopId}/create-view",
                                        "/review/{shopId}/update/{reviewId}",
                                        "/review/{shopId}/delete/{reviewId}"
                                ).authenticated()
                                // Read Review
                                .requestMatchers(
                                        "/review/{shopId}/read-all",
                                        "/review/{shopId}/read/{reviewId}"
                                ).permitAll()

                                // toss
                                .requestMatchers(
                                        "/toss/confirm-payment/{ordId}"
                                ).authenticated()
                                // 사용자 주문 확인
                                .requestMatchers(
                                        "/profile/ord-list",
                                        "/profile/read/{ordId}"
                                ).authenticated()
                                .requestMatchers(
                                        "/{shopId}/{productId}/request" // GET, POST
                                ).authenticated()
                                // Shop CUD
                                .requestMatchers(
                                        "/shop/create",
                                        "/shop/{shopId}/update",
                                        "/shop/{shopId}/delete"
                                ).authenticated()
                                // Read
                                .requestMatchers(
                                        "/shop",
                                        "/shop/{shopId}"
                                ).permitAll()
                                // Product CUD
                                .requestMatchers(
                                        "/shop/{shopId}/product/create",
                                        "/shop/{shopId}/product/{productId}/update",
                                        "/shop/{shopId}/product/{productId}/delete"
                                ).authenticated()

                                .requestMatchers("/api/account/authenticated")
                                .authenticated()
                                .requestMatchers("/api/account/inactive")
                                .hasRole(Authority.ROLE_INACTIVE_USER.getAuthority())
                                .requestMatchers("/api/account/active")
                                .hasRole(Authority.ROLE_ACTIVE_USER.getAuthority())
                                .requestMatchers("/api/account/business")
                                .hasRole(Authority.ROLE_BUSINESS_USER.getAuthority())
                                .requestMatchers("/api/account/admin")
                                .hasRole(Authority.ROLE_ADMIN.getAuthority())
                                .anyRequest()
                                .permitAll()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/account/login")
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                )
                .logout(logout -> logout
                        .logoutUrl("/api/account/logout")
                        .logoutSuccessUrl("/shop")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "CMToken")
                )
                .addFilterBefore(new JwtTokenFilter(jwtTokenUtils, manager),
                        AuthorizationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(objectMapper, tokenService, manager),
                        JwtTokenFilter.class)

        ;
        return http.build();
    }
}
