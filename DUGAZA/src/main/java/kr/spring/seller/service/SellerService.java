package kr.spring.seller.service;

import kr.spring.seller.vo.SellerDetailVO;
import kr.spring.seller.vo.SellerVO;

public interface SellerService {

    void register(SellerVO sellerVO);
    SellerDetailVO getSellerDetail(SellerVO seller);
}
