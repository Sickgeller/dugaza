package kr.spring.house.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.content.vo.ContentVO;
import kr.spring.house.dao.HouseMapper;
@Service
@Transactional
public class HouseServiceImpl implements HouseService{

	@Autowired
	private HouseMapper houseMapper;
	
	@Override
	public Integer selectRowCount() {
		return houseMapper.selectRowCount();
	}

	@Override
	public List<ContentVO> selectList(Map<String, Object> map) {
		return houseMapper.selectList(map);
	}

}
