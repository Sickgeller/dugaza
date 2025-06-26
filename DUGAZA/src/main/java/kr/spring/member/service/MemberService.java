package kr.spring.member.service;

import java.util.List;
import java.util.Map;

import kr.spring.member.vo.MemberVO;

public interface MemberService {
	//회원관리 - 일반회원
	public void insertMember(MemberVO member);
	public MemberVO selectIdAndNickName(Map<String,String> map);
	public MemberVO selectCheckMember(String id);
	public MemberVO selectMember(Long mem_num);
	public void updateMember(MemberVO member);
	public void updatePassword(MemberVO member);
	public void deleteMember(Long mem_num);
	//비밀번호 찾기
	public void updateRadomPassword(MemberVO member);
	//프로필 이미지 업데이트
	public void updateProfile(MemberVO member);

	//회원관리 - 관리자
	public Integer selectRowCount(Map<String,Object> map);
	public List<MemberVO> selectList(Map<String,Object> map);
	public void updateByAdmin(MemberVO memberVO);

}





