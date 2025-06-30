package kr.spring.auth.security;

import kr.spring.member.dao.MemberMapper;
import kr.spring.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    
    private final MemberMapper memberMapper;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 인증 시도: {}", username);
        
        // 1. 일반 회원으로 조회
        MemberVO member = memberMapper.selectMemberByUsername(username);
        if (member != null) {
            log.debug("일반 회원 로그인: {}", member.getId());
            // 회원의 역할 정보도 함께 조회
            if (member.getUserRoles() == null) {
                // 추가 역할 정보 조회 (필요시)
                // member.setUserRoles(memberMapper.selectMemberRoles(member.getMemberId()));
            }
            return new CustomUserDetails(member);
        }
        
        // 2. 판매자로 조회 (SellerMapper가 있다면)
        // TODO: SellerMapper 구현 후 활성화
        /*
        SellerVO seller = sellerMapper.selectSeller(username);
        if (seller != null) {
            log.debug("판매자 로그인: {}", seller.getId());
            return new CustomUserDetails(seller);
        }
        */
        
        log.warn("사용자를 찾을 수 없음: {}", username);
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }
    
    /**
     * 사용자 ID로 사용자 정보 조회 (추가 기능)
     */
    public CustomUserDetails loadUserById(Long userId) {
        log.debug("사용자 ID로 조회: {}", userId);
        
        MemberVO member = memberMapper.selectMember(userId);
        if (member != null) {
            return new CustomUserDetails(member);
        }
        
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
    }
} 