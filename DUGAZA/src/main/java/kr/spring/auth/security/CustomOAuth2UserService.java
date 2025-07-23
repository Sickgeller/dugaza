package kr.spring.auth.security;

import kr.spring.member.dao.MemberMapper;
import kr.spring.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberMapper memberMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId(); // kakao, google 등
        Map<String, Object> attributes = user.getAttributes();

        String email = null;
        Long providerId = null;

        try {
            email = attributes.get("email").toString();
            providerId = Long.valueOf(attributes.get("id").toString());
        } catch (Exception e) {
            throw new OAuth2AuthenticationException("필수 사용자 정보(email, id)를 가져오는데 실패했습니다.");
        }

        // 1) providerId로 회원 조회
        MemberVO member = findByProviderId(registrationId, providerId);

        // 2) 소셜id로 조회한 회원 없으면 이메일로 조회 후 providerId 업데이트
        if (member == null) {
            member = memberMapper.findByEmail(email);
            if (member != null) {
                updateProviderId(member, registrationId, providerId);
            }
        }

        if (member == null) {
            member = new MemberVO();
            updateProviderId(member, registrationId, providerId);
//            memberMapper.insertMemberWithSocial(member);
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

    private void updateProviderId(MemberVO member, String registrationId, Long providerId) {
        if ("kakao".equals(registrationId)) {
            member.setKakaoId(providerId);
        } else if ("google".equals(registrationId)) {
            // member.setGoogleId(providerId);
        }
//        memberMapper.updateSocialId(member); // providerId만 업데이트
    }
}

