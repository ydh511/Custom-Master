package JuDBu.custommaster.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtils {
    // JWT를 만드는 용도의 암호키
    private final Key accessSigningKey;
    private final Key refreshSigningKey;
    // JWT를 해석하는 용도의 객체
    private JwtParser jwtParser;

    public JwtTokenUtils(
            @Value("${jwt.secret}")
            String jwtAccessSecret,
            @Value("${jwt.refresh-secret}")
            String jwtRefreshSecret
    ) {
        this.accessSigningKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.refreshSigningKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    // UserDetails를 받아 JWT로 변환
    public String generateToken(UserDetails userDetails, String type) {
        Long validity = 0L;
        Key tokenKey = null;

        if(type.equals("accessToken")) {
            // 유효기간 30일
            validity = 60 * 60 * 24 * 30L;
            tokenKey = accessSigningKey;
        }
        else{
            // 유효기간 30일
            validity = 60 * 60 * 24 * 30L;
            tokenKey = refreshSigningKey;
        }

        jwtParser = Jwts.parserBuilder()
                .setSigningKey(tokenKey)
                .build();

        Instant now = Instant.now();
        Claims jwtClaims = Jwts.claims()
                // 사용자 이름
                .setSubject(userDetails.getUsername())
                // 발급 시간
                .setIssuedAt(Date.from(now))
                // 로그인 한 후 한시간이 지나면 토큰이 만료됨
                .setExpiration(Date.from(now.plusSeconds((validity))));

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(tokenKey)
                .compact();
    }

    // 정상적인 JWT인지를 판단하는 메서드
    public boolean validate(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("invalid jwt");
        }
        return false;
    }

    // 실제 데이터(Payload)를 반환하는 메서드
    public Claims parseClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    // 정상적인 JWT인지를 판단하는 메서드
    public boolean validateAccess(String token) {
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(accessSigningKey)
                .build();

        jwtParser.parseClaimsJws(token);
        return true;

    }

    // 정상적인 JWT인지를 판단하는 메서드
    public boolean validateRefresh(String token) {
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(refreshSigningKey)
                .build();
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("invalid jwt");
        }
        return false;
    }
}
