package kr.spring.seller.service;

import kr.spring.seller.dao.SellerMapper;
import kr.spring.seller.vo.SellerDetailVO;
import kr.spring.seller.vo.SellerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerMapper sellerMapper;

    @Override
    public void register(SellerVO sellerVO) {
        sellerMapper.insertSeller(sellerVO);
    }

    @Override
    public SellerDetailVO getSellerDetail(SellerVO seller) {
        return null;
    }
}
