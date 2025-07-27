package kr.spring.house.service;

import java.util.List;
import java.util.Map;

import kr.spring.house.vo.HouseVO;
import kr.spring.seller.vo.HouseSellerDetailVO;
import kr.spring.util.CursorPagingUtil;

public interface HouseService {
	// 숙소 총 개수
	public Integer selectRowCount(Map<String, Object> map);
	// 숙소 목록
	public List<HouseVO> selectList(Map<String, Object> map);
	
	// 커서 기반 페이지네이션을 위한 메서드들
	public List<HouseVO> selectListByCursor(CursorPagingUtil cursorPaging);
	public boolean hasNextPage(CursorPagingUtil cursorPaging);
	
	// 관리자용 숙소 목록 (WISHLIST JOIN 제거)
	public List<HouseVO> selectAdminList(Map<String, Object> map);
	public HouseVO selectHouse(Long id);
	List<HouseVO> selectHousesWithSellerId(Long sellerId);
	void insertWithApi(Long contentId);
	
	// 숙소 신청용 목록 조회 (회원 정보 없이)
	List<HouseVO> selectListForApply(Map<String, Object> map);
	
	// 숙소 승인 관련 메서드들
	void applyHouse(HouseSellerDetailVO houseSellerDetailVO);
	List<HouseSellerDetailVO> getPendingHouses();
	void approveHouse(Long houseId, Long sellerId);
	void rejectHouse(Long houseId, Long sellerId);
	List<HouseSellerDetailVO> getHousesBySellerAndStatus(Long sellerId, String status);
	
	// 신규 메서드 추가
	public List<HouseSellerDetailVO> getHouseApplications(Map<String, Object> params);
	public int getHouseApplicationCount(Map<String, Object> params);
	public boolean cancelHouseApplication(Map<String, Object> params);
}
