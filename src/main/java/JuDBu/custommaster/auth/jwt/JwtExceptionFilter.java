package JuDBu.custommaster.auth.jwt;

import JuDBu.custommaster.auth.jwt.dto.ErrorCode;
import JuDBu.custommaster.auth.jwt.dto.JwtErrorResponse;
import JuDBu.custommaster.auth.jwt.dto.JwtResponseDto;
import JuDBu.custommaster.domain.entity.account.RefreshToken;
import JuDBu.custommaster.domain.service.account.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;
    private final UserDetailsManager manager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(req, res); // go to 'JwtAuthenticationFilter'
        } catch (JwtException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, req, res, ex);
        }
    }

    public void setErrorResponse(HttpStatus status,
                                 HttpServletRequest req,
                                 HttpServletResponse res,
                                 Throwable ex) throws IOException {

        res.setStatus(status.value());
        res.setContentType("application/json; charset=UTF-8");
        JwtErrorResponse jwtErrorResponse = new JwtErrorResponse();
        log.info(ex.getMessage());

        if(ex.getMessage().equals("1")){
            String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
            log.info("error authHeader: {}",authHeader);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.split(" ")[1];
                String username = tokenService.username(token);
                if (username != null){
                    UserDetails userDetails = manager.loadUserByUsername(username);
                    JwtResponseDto dto = tokenService.issueAccess(userDetails);
                    jwtErrorResponse.setToken(dto.getAccessToken());
                }
            }
            jwtErrorResponse.setResponse(ErrorCode.EXPIRED_TOKEN);
        }
        if(ex.getMessage().equals("2")){
            jwtErrorResponse.setResponse(ErrorCode.UNSUPPORTED_TOKEN);
        }if(ex.getMessage().equals("3")){
            jwtErrorResponse.setResponse(ErrorCode.JWT_DECODE_FAIL);
        }if(ex.getMessage().equals("4")){
            jwtErrorResponse.setResponse(ErrorCode.JWT_SIGNATURE_FAIL);
        }if(ex.getMessage().equals("5")){
            jwtErrorResponse.setResponse(ErrorCode.JWT_SIGNATURE_FAIL);
        }
        res.getWriter().write(objectMapper.writeValueAsString(jwtErrorResponse));
        res.sendRedirect("/account/logout");
    }
}
