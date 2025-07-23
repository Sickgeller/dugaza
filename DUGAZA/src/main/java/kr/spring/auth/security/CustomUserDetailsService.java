package kr.spring.auth.security;

import kr.spring.member.dao.MemberMapper;
import kr.spring.member.enums.MemberRole;
import kr.spring.member.vo.MemberVO;
import kr.spring.seller.dao.SellerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final MemberMapper memberMapper;
    private final SellerMapper sellerMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 인증 시도: {}", username);
        MemberVO member = memberMapper.selectMemberByUsername(username);

        if(member == null){
            throw new UsernameNotFoundException("일치하는 아이디가 없습니다." + username);
        }

        if(member.getRole() == MemberRole.SELLER.getValue()){
//            SellerVO seller = sellerMapper.selectSellerByMemberId(member.getMemberId());
//            return new CustomUserDetails(member, seller);
        }

        return new CustomUserDetails(member);
    }

} 