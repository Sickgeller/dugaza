package kr.spring.qnaQuestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import kr.spring.auth.security.CustomUserDetails;
import kr.spring.auth.security.PrincipalDetails;
import kr.spring.member.dao.MemberMapper;
import kr.spring.member.vo.MemberVO;

public class CustomUserDetailsService implements UserDetailsService {
	
	 	@Autowired
	    private MemberMapper memberMapper;

	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        MemberVO member = memberMapper.selectMemberByUsername(username); // mapper에서 조회
	        if (member == null) {
	            throw new UsernameNotFoundException("사용자 없음: " + username);
	        }
	        return new CustomUserDetails(member); // MemberVO를 담아서 반환
	    }
}
