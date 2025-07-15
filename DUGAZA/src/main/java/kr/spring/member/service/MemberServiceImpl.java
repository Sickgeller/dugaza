package kr.spring.member.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.member.dao.MemberMapper;
import kr.spring.member.vo.MemberVO;

@Service
@Transactional
public class MemberServiceImpl implements MemberService{

	@Autowired
	private MemberMapper memberMapper;
	
	@Override
	public void insertMember(MemberVO member) {
		member.setMemberId(memberMapper.selectMemberId());
		memberMapper.insertMember(member);
	}

	@Override
	public MemberVO selectIdCheck(String id) {
		return memberMapper.selectIdCheck(id);
	}

	@Override
	public MemberVO selectCheckMember(String id) {
		return memberMapper.selectCheckMember(id);
	}

	@Override
	public MemberVO selectMember(Long memberId) {
		return memberMapper.selectMember(memberId);
	}

	@Override
	public void updateMember(MemberVO member) {
		memberMapper.updateMember(member);
	}

	@Override
	public void updatePassword(MemberVO member) {
		memberMapper.updatePassword(member);
		// 설정되어 있는 자동로그인 기능 해제(모든 브라우저에 설정된 자동로그인 해제)
		memberMapper.deleteRememberMe(member.getId());
	}

	@Override
	public void deleteMember(Long memberId) {
		memberMapper.deleteMember(memberId);
	}

	@Override
	public void updateRadomPassword(MemberVO member) {
		memberMapper.updateRadomPassword(member);
	}

	@Override
	public void updateProfile(MemberVO member) {
		memberMapper.updateProfile(member);
	}

	@Override
	public Integer selectRowCount(Map<String, Object> map) {
		return memberMapper.selectRowCount(map);
	}

	@Override
	public List<MemberVO> selectList(Map<String, Object> map) {
		return memberMapper.selectList(map);
	}

	@Override
	public void updateByAdmin(MemberVO memberVO) {
		memberMapper.updateByAdmin(memberVO);
	}

	@Override
	public Integer selectMemberCount() {
		return memberMapper.selectMemberCount();
	}

	@Override
	public Integer selectNewMemberCount() {
		return memberMapper.selectNewMemberCount();
	}

	@Override
	public Integer selectWithdrawnMemberCount() {
		return memberMapper.selectWithdrawnMemberCount();
	}

	@Override
	public Integer selectHumanMemberCount() {
		return memberMapper.selectHumanMemberCount();
	}
	
    @Override
    public Long getMemberIdByUsername(String username) {
        return memberMapper.findMemberIdByUsername(username);
    }
    
    @Override
    public MemberVO findByEmail(String email) {
        return memberMapper.findByEmail(email);
    }
    
    @Override
    public MemberVO findByKakaoId(Long kakaoId) {
        return memberMapper.findByKakaoId(kakaoId);
    }
    
    @Override
    public void registerMember(MemberVO member) {
        member.setMemberId(memberMapper.selectMemberId());
        member.setStatus("ACTIVE");
        member.setRole("USER");
        memberMapper.insertMember(member);
    }
    
    @Override
    public void registerKakaoMember(MemberVO member) {
        member.setMemberId(memberMapper.selectMemberId());
        member.setStatus("ACTIVE");
        member.setRole("USER");
        memberMapper.insertKakaoMember(member);
    }
    
    @Override
    public boolean isAccountLinked(Long memberId) {
        MemberVO member = memberMapper.selectMember(memberId);
        return member != null && member.getKakaoId() != null;
    }
    
    @Override
    public void unlinkKakaoAccount(Long memberId) {
        memberMapper.unlinkKakaoAccount(memberId);
    }

}







