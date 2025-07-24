package kr.spring.tour.service;

import kr.spring.api.mapper.TouristAttractionApiMapper;
import kr.spring.api.service.CommonDataSyncSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.api.client.TouristAttractionApiClient;
import kr.spring.api.dto.TouristAttrationDetailApiDto;
import kr.spring.tour.dao.TouristAttractionMapper;
import kr.spring.tour.vo.TouristAttractionVO;
import kr.spring.tour.vo.TourVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TouristAttractionServiceImpl implements TouristAttractionService{
	private final TouristAttractionMapper touristAttractionMapper;
	private final TouristAttractionApiClient touristAttractionApiClient;
	private final CommonDataSyncSupportService commonDataSyncSupportService;
	private final TouristAttractionApiMapper touristAttractionApiMapper;

	public TouristAttractionVO selectAttraction(Long id) {
		return touristAttractionMapper.selectAttraction(id);
	}

	@Override
	public TouristAttractionVO selectAttractionWithApi(Long contentId) {
		TouristAttractionVO result;
		TouristAttrationDetailApiDto dto = touristAttractionApiClient.getTouristAttrationDetailData(contentId);
		if(dto != null) {
			commonDataSyncSupportService.insertOrUpdate(touristAttractionApiMapper,dto);
			result = selectAttraction(contentId);
		} else {
			return null;
		}
		return result;
	}

}
