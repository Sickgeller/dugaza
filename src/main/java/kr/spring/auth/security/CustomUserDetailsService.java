package kr.spring.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import kr.spring.member.dao.MemberMapper;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.UserRole;
import kr.spring.seller.dao.SellerMapper;
import kr.spring.seller.vo.SellerRole;
import kr.spring.seller.vo.SellerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;
    private final SellerMapper sellerMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 인증 시도: {}", username);

        // 현재 요청에서 userType 파라미터 확인
        String userType = getCurrentUserType();
        log.debug("요청된 사용자 타입: {}", userType);

        if ("seller".equals(userType)) {
            // 판매자 로그인 요청인 경우
            SellerVO seller = sellerMapper.selectSeller(username);
            if (seller != null) {
                log.debug("판매자 로그인: {}", seller.getId());
                return new CustomUserDetails(seller);
            }
            log.warn("판매자를 찾을 수 없음: {}", username);
            throw new UsernameNotFoundException("판매자를 찾을 수 없습니다: " + username);

        } else  if ("member".equals(userType)) {
            // 일반 회원 로그인 요청인 경우 (기본값)
            MemberVO member = memberMapper.selectMemberByUsername(username);
            if (member != null) {
                log.debug("일반 회원 로그인: {}", member.getId());
                return new CustomUserDetails(member);
            }
        }
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

    /**
     * 현재 요청에서 userType 파라미터를 가져오는 메서드
     */
    private String getCurrentUserType() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getParameter("userType");
            }
        } catch (Exception e) {
            log.warn("userType 파라미터 조회 중 오류: {}", e.getMessage());
        }
        return null;
    }
} 