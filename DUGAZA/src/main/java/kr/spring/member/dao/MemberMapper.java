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
	
	// 카카오 사용자 전용 INSERT (ID 필드 포함)
	@Insert("INSERT INTO MEMBER (MEMBER_ID, ID, PASSWORD, NAME, EMAIL, PHONE, ADDRESS, ADDRESS_DETAIL, KAKAO_ID, PROFILE_IMAGE, CREATED_AT, UPDATED_AT) " +
			"VALUES (MEMBER_SEQ.NEXTVAL, #{id,jdbcType=VARCHAR}, #{password,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{email,jdbcType=VARCHAR}, #{phone,jdbcType=VARCHAR}, #{address,jdbcType=VARCHAR}, #{addressDetail,jdbcType=VARCHAR}, #{kakaoId,jdbcType=NUMERIC}, #{profileImage,jdbcType=VARCHAR}, SYSDATE, SYSDATE)")
	public void insertKakaoMember(MemberVO member);
	public void insertMemberDetail(MemberVO member);
	@Select("SELECT * FROM MEMBER WHERE ID = #{id}")
	public MemberVO selectIdCheck(String id);
	public MemberVO selectCheckMember(String id);
//	@Select("SELECT * FROM MEMBER WHERE ID=#{id}")
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
	// 해당 년월의 신규 가입 회원 수
	@Select("SELECT COUNT(*) FROM member WHERE role != 'ADMIN' AND TO_CHAR(created_at, 'MM') = TO_CHAR(SYSDATE, 'MM') AND TO_CHAR(created_at, 'YYYY') = TO_CHAR(SYSDATE, 'YYYY')")
	public Integer selectNewMemberCount();
	// 탈퇴 회원 수
	@Select("SELECT COUNT(*) FROM member WHERE role != 'ADMIN' AND status = 'WITHDRAWN'")
	public Integer selectWithdrawnMemberCount();
	// 휴면 회원 수
	@Select("SELECT COUNT(*) FROM member WHERE role != 'ADMIN' AND status = 'HUMAN'")
	public Integer selectHumanMemberCount();
	
	Long findMemberIdByUsername(String username);
	
	// 카카오 로그인 관련
	@Select("SELECT * FROM MEMBER WHERE EMAIL = #{email}")
	public MemberVO findByEmail(String email);
	
	@Select("SELECT * FROM MEMBER WHERE KAKAO_ID = #{kakaoId}")
	public MemberVO findByKakaoId(Long kakaoId);
	
	// 카카오 계정 연결 해제
	@Update("UPDATE MEMBER SET KAKAO_ID = NULL, UPDATED_AT = SYSDATE WHERE MEMBER_ID = #{memberId}")
	public void unlinkKakaoAccount(Long memberId);
	
	// 카카오 ID 연결
	@Update("UPDATE MEMBER SET KAKAO_ID = #[object Object]kakaoId}, UPDATED_AT = SYSDATE WHERE MEMBER_ID = #{memberId}")
	public void linkKakaoAccount(Long memberId, Long kakaoId);
}









