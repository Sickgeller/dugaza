package kr.spring.house.dao;

import java.util.List;
import java.util.Map;

import kr.spring.api.dto.HouseDetailApiDto;
import org.apache.ibatis.annotations.Mapper;

import kr.spring.house.vo.HouseVO;
import kr.spring.seller.vo.HouseSellerDetailVO;

@Mapper
public interface HouseMapper {
	// 숙소 총 개수
	public Integer selectRowCount(Map<String,Object> map);
	// 숙소 목록
	public List<HouseVO> selectList(Map<String,Object> map);
	public HouseVO selectHouse(Long id);
    List<HouseVO> selectHousesWithSellerId(Long sellerId);
	void insertHouseDetail(HouseDetailApiDto houseDetailApiDto);
	
	// 숙소 신청용 목록 조회 (회원 정보 없이)
	List<HouseVO> selectListForApply(Map<String,Object> map);
	
	// 숙소 승인 관련 메서드들
	void insertHouseApplication(HouseSellerDetailVO houseSellerDetailVO);
	List<HouseSellerDetailVO> selectPendingHouses();
	void updateHouseStatus(Map<String, Object> params);
	List<HouseSellerDetailVO> selectHousesBySellerAndStatus(Map<String, Object> params);
}
