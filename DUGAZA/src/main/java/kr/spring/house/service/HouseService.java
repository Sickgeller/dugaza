package kr.spring.house.service;

import java.util.List;
import java.util.Map;

import kr.spring.house.vo.HouseVO;
import kr.spring.seller.vo.HouseSellerDetailVO;

public interface HouseService {
	// 숙소 총 개수
	public Integer selectRowCount(Map<String, Object> map);
	// 숙소 목록
	public List<HouseVO> selectList(Map<String, Object> map);
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
}
