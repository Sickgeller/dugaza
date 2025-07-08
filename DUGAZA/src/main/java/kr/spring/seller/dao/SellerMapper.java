package kr.spring.seller.dao;

import kr.spring.seller.vo.SellerVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SellerMapper {
    void insertSeller(SellerVO sellerVO);
    SellerVO selectSeller(String id);
}
