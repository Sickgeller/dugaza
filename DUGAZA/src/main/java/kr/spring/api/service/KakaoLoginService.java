package kr.spring.api.service;

import kr.spring.api.client.KakaoApiClient;
import kr.spring.api.dto.KakaoUserInfoDto;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {
    
    private final KakaoApiClient kakaoApiClient;
    private final MemberService memberService;
    
    /**
     * 카카오 로그인 처리
     */
    public Mono<MemberVO> processKakaoLogin(String code, String redirectUri) {
        return kakaoApiClient.getAccessToken(code, redirectUri)
                .flatMap(tokenResponse -> {
                    String accessToken = (String) tokenResponse.get("access_token");
                    return kakaoApiClient.getUserInfo(accessToken);
                })
                .map(this::convertToKakaoUserInfo)
                .flatMap(this::processUserRegistration);
    }
    
    /**
     * 카카오 사용자 정보를 DTO로 변환
     */
    private KakaoUserInfoDto convertToKakaoUserInfo(Map<String, Object> response) {
        KakaoUserInfoDto userInfo = new KakaoUserInfoDto();
        
        // 카카오 계정 ID
        userInfo.setId(Long.valueOf(response.get("id").toString()));
        
        // 카카오 계정 정보
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
        if (kakaoAccount != null) {
            // 이메일
            Map<String, Object> emailInfo = (Map<String, Object>) kakaoAccount.get("email");
            if (emailInfo != null && (Boolean) emailInfo.get("needs_agreement") == false) {
                userInfo.setEmail((String) emailInfo.get("value"));
            }
            
            // 이름
            Map<String, Object> nameInfo = (Map<String, Object>) kakaoAccount.get("name");
            if (nameInfo != null && (Boolean) nameInfo.get("needs_agreement") == false) {
                userInfo.setName((String) nameInfo.get("value"));
            }
            
            // 전화번호
            Map<String, Object> phoneInfo = (Map<String, Object>) kakaoAccount.get("phone_number");
            if (phoneInfo != null && (Boolean) phoneInfo.get("needs_agreement") == false) {
                userInfo.setPhoneNumber((String) phoneInfo.get("value"));
            }
            
            // 생일
            Map<String, Object> birthdayInfo = (Map<String, Object>) kakaoAccount.get("birthday");
            if (birthdayInfo != null && (Boolean) birthdayInfo.get("needs_agreement") == false) {
                userInfo.setBirthday((String) birthdayInfo.get("value"));
            }
            
            // 성별
            Map<String, Object> genderInfo = (Map<String, Object>) kakaoAccount.get("gender");
            if (genderInfo != null && (Boolean) genderInfo.get("needs_agreement") == false) {
                userInfo.setGender((String) genderInfo.get("value"));
            }
        }
        
        // 프로필 정보
        Map<String, Object> profile = (Map<String, Object>) response.get("properties");
        if (profile != null) {
            userInfo.setNickname((String) profile.get("nickname"));
            userInfo.setProfileImage((String) profile.get("profile_image"));
            userInfo.setThumbnailImage((String) profile.get("thumbnail_image"));
        }
        
        return userInfo;
    }
    
    /**
     * 사용자 등록 처리
     */
    private Mono<MemberVO> processUserRegistration(KakaoUserInfoDto kakaoUserInfo) {
        return Mono.fromCallable(() -> {
            // 1. 카카오 ID로 기존 회원 확인
            MemberVO existingMember = memberService.findByKakaoId(kakaoUserInfo.getId());
            
            if (existingMember != null) {
                // 기존 카카오 회원인 경우 로그인 처리
                log.info("기존 카카오 회원 로그인: {}", kakaoUserInfo.getId());
                return existingMember;
            }
            
            // 2. 이메일로 기존 회원 확인 (통합 로직)
            if (kakaoUserInfo.getEmail() != null) {
                MemberVO emailMember = memberService.findByEmail(kakaoUserInfo.getEmail());
                if (emailMember != null) {
                    // 기존 이메일 회원이 있는 경우 - 카카오 ID 연결
                    log.info("기존 이메일 회원과 카카오 계정 통합: {}", kakaoUserInfo.getEmail());
                    return linkKakaoToExistingMember(emailMember, kakaoUserInfo);
                }
            }
            
            // 3. 신규 회원인 경우 회원가입 처리
            log.info("신규 카카오 회원 가입: {}", kakaoUserInfo.getId());
            return registerNewMember(kakaoUserInfo);
        });
    }
    
    /**
     * 신규 회원 등록
     */
    private MemberVO registerNewMember(KakaoUserInfoDto kakaoUserInfo) {
        MemberVO newMember = new MemberVO();
        
        // 카카오 정보 설정
        newMember.setEmail(kakaoUserInfo.getEmail());
        newMember.setName(kakaoUserInfo.getName() != null ? kakaoUserInfo.getName() : kakaoUserInfo.getNickname());
        newMember.setNickname(kakaoUserInfo.getNickname());
        newMember.setPhone(kakaoUserInfo.getPhoneNumber());
        newMember.setProfileImage(kakaoUserInfo.getProfileImage());
        
        // 카카오 ID 설정 (LOGIN_TYPE 대신 KAKAO_ID로 판단)
        newMember.setKakaoId(kakaoUserInfo.getId());
        
        // 임시 비밀번호 생성 (카카오 로그인 사용자는 비밀번호가 필요 없지만 DB 제약조건을 위해)
        String tempPassword = UUID.randomUUID().toString().substring(0, 12);
        newMember.setPassword(tempPassword);
        
        // 기본 권한 설정
        newMember.setRole("USER");
        
        // 카카오 회원 전용 등록 메서드 사용
        memberService.registerKakaoMember(newMember);
        
        return newMember;
    }
    
    /**
     * 기존 이메일 회원에 카카오 ID 연결
     */
    private MemberVO linkKakaoToExistingMember(MemberVO existingMember, KakaoUserInfoDto kakaoUserInfo) {
        // 카카오 ID 연결
        existingMember.setKakaoId(kakaoUserInfo.getId());
        
        // 추가 정보 업데이트 (프로필 이미지, 닉네임 등)
        if (existingMember.getProfileImage() == null && kakaoUserInfo.getProfileImage() != null) {
            existingMember.setProfileImage(kakaoUserInfo.getProfileImage());
        }
        if (existingMember.getNickname() == null && kakaoUserInfo.getNickname() != null) {
            existingMember.setNickname(kakaoUserInfo.getNickname());
        }
        
        // DB 업데이트
        memberService.updateMember(existingMember);
        
        return existingMember;
    }
} 