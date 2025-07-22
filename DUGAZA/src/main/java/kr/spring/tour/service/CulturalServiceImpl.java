package kr.spring.tour.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.tour.dao.CulturalCenterMapper;
import kr.spring.tour.vo.CulturalCenterVO;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Transactional
public class CulturalServiceImpl implements CulturalCenterService{
	@Autowired
	private CulturalCenterMapper centerMapper;

	@Override
	public CulturalCenterVO selectCenter(Long id) {
		return centerMapper.selectCenter(id);
	}
	
}