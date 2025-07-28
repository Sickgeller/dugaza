package kr.spring.auth.security;

import kr.spring.member.dao.MemberMapper;
import kr.spring.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberMapper memberMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        log.info("OAuth2 로그인 시도: {}", request.getClientRegistration().getRegistrationId());
        
        OAuth2User user = super.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId(); // kakao, google 등
        Map<String, Object> attributes = user.getAttributes();
        
        log.info("OAuth2 사용자 정보: {}", attributes);

        String email = null;
        Long providerId = null;
        String name = null;
        String profileImage = null;

        if ("kakao".equals(registrationId)) {
            // 카카오 API 응답 구조에 맞게 처리 - 고유 아이디만 사용
            providerId = Long.valueOf(attributes.get("id").toString());
            
            // 기본값 설정
            name = "카카오사용자";
            email = "kakao_" + providerId + "@kakao.com"; // 임시 이메일 생성
        } else {
            // Google 등 다른 OAuth2 제공자
            try {
                email = attributes.get("email").toString();
                providerId = Long.valueOf(attributes.get("id").toString());
                name = attributes.get("name").toString();
                profileImage = attributes.get("picture").toString();
            } catch (Exception e) {
                throw new OAuth2AuthenticationException("필수 사용자 정보(email, id)를 가져오는데 실패했습니다.");
            }
        }

        // 1) providerId로 회원 조회
        MemberVO member = findByProviderId(registrationId, providerId);

        // 2) 새로운 회원 생성
        if (member == null) {
            log.info("신규 회원 가입 요청: kakaoId={}, email={}, name={}", providerId, email, name);
            // 신규 회원인 경우 통합 페이지로 리다이렉트
            String exceptionMessage = "NEW_ACCOUNT_REQUIRED:" + providerId + ":" + email + ":" + name;
            log.info("신규 가입 요청 예외 발생: {}", exceptionMessage);
            throw new BadCredentialsException(exceptionMessage);
        }
        
        // 3) 기존 카카오 회원인지 확인 (status가 KAKAO인 경우)
        if (member != null && "KAKAO".equals(member.getStatus())) {
            log.info("카카오 미완료 회원 발견: memberId={}, status={}", member.getMemberId(), member.getStatus());
            // 통합 페이지로 리다이렉트
            String exceptionMessage = "INTEGRATION_REQUIRED:" + email + ":" + providerId + ":" + name;
            log.info("통합 요청 예외 발생: {}", exceptionMessage);
            throw new BadCredentialsException(exceptionMessage);
        }
        
        return new CustomOAuth2User(member, attributes);
    }


    private MemberVO findByProviderId(String registrationId, Long providerId) {
        if (registrationId.equals("kakao")) {
            return memberMapper.findByKakaoId(providerId);
        } else if (registrationId.equals("google")) {
//            return memberMapper.findByGoogleId(providerId);
        }
        return null;
    }
}

