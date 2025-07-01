package kr.spring.member.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.member.vo.MemberVO;

@Mapper
public interface MemberMapper {
	//회원관리 - 일반회원
	@Select("SELECT MEMBER_SEQ.nextval FROM dual")
	public Long selectMemberId();
	@Insert("INSERT INTO MEMBER (MEMBER_ID, ID, PASSWORD, NAME, EMAIL, PHONE, ADDRESS, ADDRESS_DETAIL,CREATED_AT, UPDATED_AT) " +
			"VALUES (MEMBER_SEQ.NEXTVAL, #{id}, #{password}, #{name}, #{email}, #{phone}, #{address}, #{addressDetail}, SYSDATE, SYSDATE)")
	public void insertMember(MemberVO member);
	public void insertMemberDetail(MemberVO member);
	public MemberVO selectIdCheck(String id);
	public MemberVO selectCheckMember(String id);
	@Select("SELECT * FROM MEMBER WHERE ID=#{id}")
	public MemberVO selectMemberByUsername(String id);
	@Select("SELECT * FROM MEMBER WHERE MEMBER_ID=#{memberId}")
	public MemberVO selectMember(Long memberId);
	@Update("UPDATE MEMBER SET NAME=#{name}, EMAIL=#{email}, PHONE=#{phone}, ADDRESS=#{address}, ADDRESS_DETAIL=#{addressDetail}, UPDATED_AT=SYSDATE WHERE MEMBER_ID=#{memberId}")
	public void updateMember(MemberVO member);
	public void updateMemberDetail(MemberVO member);
	@Update("UPDATE MEMBER SET PASSWORD=#{password}, UPDATED_AT=SYSDATE WHERE MEMBER_ID=#{memberId}")
	public void updatePassword(MemberVO member);
	@Delete("DELETE FROM MEMBER WHERE MEMBER_ID=#{memberId}")
	public void deleteMember(Long memberId);
	//자동 로그인 해제
	@Delete("DELETE FROM persistent_logins WHERE username=#{id}")
	public void deleteRememberMe(String id);
	//비밀번호 찾기
	@Update("UPDATE MEMBER SET PASSWORD=#{password}, UPDATED_AT=SYSDATE WHERE MEMBER_ID=#{memberId}")
	public void updateRadomPassword(MemberVO member);
	//프로필 이미지 업데이트
	@Update("UPDATE MEMBER SET PROFILE_IMAGE=#{profileImage}, UPDATED_AT=SYSDATE WHERE MEMBER_ID=#{memberId}")
	public void updateProfile(MemberVO member);

	//회원관리 - 관리자
	public Integer selectRowCount(Map<String,Object> map);
	public List<MemberVO> selectList(Map<String,Object> map);
	@Update("UPDATE MEMBER SET ROLE=#{role}, UPDATED_AT=SYSDATE WHERE MEMBER_ID=#{memberId}")
	public void updateByAdmin(MemberVO memberVO);
	@Select("SELECT COUNT(*) FROM member WHERE role != 'ADMIN'")
	public Integer selectMemberCount();
}









