package JuDBu.custommaster.auth.jwt;

import JuDBu.custommaster.auth.jwt.dto.ErrorCode;
import JuDBu.custommaster.auth.jwt.dto.JwtErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;

    private final List<String> skipUrl = Arrays.asList(
            "/api/account/logout",
            "/account/logout");
    private String refreshToken;

    public JwtTokenFilter(
            JwtTokenUtils jwtTokenUtils,
            UserDetailsManager manager
    ) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.manager = manager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("try jwt filter");
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie c : cookies){
                if (c.getName().equals("CMToken")){
                    refreshToken = c.getValue();
                }
            }
        }
        // 1. Authorization 헤더를 회수
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authHeader: {}",authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer "))
        {
            String accessToken = authHeader.split(" ")[1];
            if(skipFilterForUrl(request)){
                log.info("skip");
                filterChain.doFilter(request, response);
                return;
            };
            String token = accessToken;
            log.info("accesstoken");
            log.info("token value: {}",token);
            try{
                jwtTokenUtils.validateAccess(token);

                SecurityContext context = SecurityContextHolder.createEmptyContext();


                String username = jwtTokenUtils
                        .parseClaims(token)
                        .getSubject();

                log.info("username: {}",username);
                UserDetails userDetails = manager.loadUserByUsername(username);
                for (GrantedAuthority authority :userDetails.getAuthorities()) {
                    log.info("authority: {}", authority.getAuthority());
                }

                // 인증 정보 생성
                AbstractAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                token,
                                userDetails.getAuthorities()
                        );
                // 인증 정보 등록
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                log.info("set security context with jwt");
            }
            catch (ExpiredJwtException e){
                throw new JwtException("1");
            }catch (UnsupportedJwtException e){
                throw new JwtException("2");
            }catch (MalformedJwtException e){
                throw new JwtException("3");
            }catch (SignatureException e){
                throw new JwtException("4");
            } catch (IllegalArgumentException e){
                throw new JwtException("5");
            }

        }
        else if(authHeader == null){
            if (refreshToken != null){
                String token = refreshToken;
                log.info("refreshtoken");
                log.info("token value: {}",token);
                try{
                    jwtTokenUtils.validateRefresh(token);

                    SecurityContext context = SecurityContextHolder.createEmptyContext();


                    String username = jwtTokenUtils
                            .parseClaims(token)
                            .getSubject();

                    log.info("username: {}",username);
                    UserDetails userDetails = manager.loadUserByUsername(username);
                    for (GrantedAuthority authority :userDetails.getAuthorities()) {
                        log.info("authority: {}", authority.getAuthority());
                    }

                    // 인증 정보 생성
                    AbstractAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    token,
                                    userDetails.getAuthorities()
                            );
                    // 인증 정보 등록
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                    log.info("set security context with jwt");
                }
                catch (ExpiredJwtException e){
                    throw new JwtException("1");
                }catch (UnsupportedJwtException e){
                    throw new JwtException("2");
                }catch (MalformedJwtException e){
                    throw new JwtException("3");
                }catch (SignatureException e){
                    throw new JwtException("4");
                } catch (IllegalArgumentException e){
                    throw new JwtException("5");
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    public boolean skipFilterForUrl(HttpServletRequest request){
        String url = request.getRequestURI();
        log.info(url);
        return skipUrl.stream().anyMatch(url::equals);
    }

    public void refresh(String token){
        try{
            log.info("refreshtoken");
            jwtTokenUtils.validateRefresh(token);

            SecurityContext context = SecurityContextHolder.createEmptyContext();

            String username = jwtTokenUtils
                    .parseClaims(token)
                    .getSubject();

            UserDetails userDetails = manager.loadUserByUsername(username);
            for (GrantedAuthority authority :userDetails.getAuthorities()) {
                log.info("authority: {}", authority.getAuthority());
            }

            // 인증 정보 생성
            AbstractAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            token,
                            userDetails.getAuthorities()
                    );
            // 인증 정보 등록
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            log.info("set security context with jwt");
        }
        catch (ExpiredJwtException e){
            throw new JwtException("1");
        }catch (UnsupportedJwtException e){
            throw new JwtException("2");
        }catch (MalformedJwtException e){
            throw new JwtException("3");
        }catch (SignatureException e){
            throw new JwtException("4");
        } catch (IllegalArgumentException e){
            throw new JwtException("5");
        }
    }
}