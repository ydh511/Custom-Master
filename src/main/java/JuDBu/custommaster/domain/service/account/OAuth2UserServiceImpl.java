package JuDBu.custommaster.domain.service.account;

import JuDBu.custommaster.domain.entity.account.Authority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// 기본적인 OAuth2 인증 과정을 진행해주는 클래스
@Slf4j
@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 어떤 서비스 제공자를 사용했는지
        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();
        // TODO 서비스 제공자에 따라 데이터 처리를 달리 하고 싶을 때

        // OAuth2 제공자로 부터 받은 데이터를 원하는 방식으로 다시 정리하기 위한 Map
        Map<String, Object> attributes = new HashMap<>();
        String nameAttribute = "";

        // Naver 아이디로 로그인
        if (registrationId.equals("naver")) {
            // Naver에서 받아온 정보다.
            attributes.put("provider", "naver");

            Map<String, Object> responseMap
                    // 네이버가 반환한 JSON에서 response를 회수
                    = oAuth2User.getAttribute("response");
            attributes.put("id", responseMap.get("id"));
            attributes.put("email", responseMap.get("email"));
            attributes.put("name", responseMap.get("name"));
            nameAttribute = "email";
        }
        log.info(attributes.toString());
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(Authority.ROLE_ACTIVE_USER.getAuthority())),
                attributes,
                nameAttribute
        );
    }
}
