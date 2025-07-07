package kr.spring.seller.dao;

import kr.spring.seller.vo.SellerVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SellerMapper {

    @Insert("INSERT INTO SELLER (SELLER_ID, ID, PASSWORD, NAME, BUSINESS_NAME, EMAIL, LICENSE, SELLER_TYPE, STATUS, ADDRESS, ADDRESS_DETAIL, PHONE, SECONDARY_PHONE, ROLE, CREATED_AT, UPDATED_AT) " +
            "VALUES (SELLER_SEQ.NEXTVAL, #{id,jdbcType=VARCHAR}, #{password,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{businessName,jdbcType=VARCHAR}, #{email,jdbcType=VARCHAR}, #{license,jdbcType=VARCHAR}, #{sellerType,jdbcType=VARCHAR}, #{status,jdbcType=VARCHAR}, #{address,jdbcType=VARCHAR}, #{addressDetail,jdbcType=VARCHAR}, #{phone,jdbcType=VARCHAR}, #{secondaryPhone,jdbcType=VARCHAR}, #{role,jdbcType=VARCHAR}, SYSDATE, SYSDATE)")
    void insertSeller(SellerVO sellerVO);

    @Select("SELECT * FROM SELLER WHERE ID = #{id,jdbcType=VARCHAR}")
    SellerVO selectSeller(String id);
}
