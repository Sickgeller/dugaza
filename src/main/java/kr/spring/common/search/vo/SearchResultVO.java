package kr.spring.common.search.vo;

import java.util.List;

import kr.spring.tour.vo.TourVO;
import lombok.Data;

@Data
public class SearchResultVO {
	private String name;               // 관광지 / 행사 / 숙소 / 맛집
    private String type;              // URL용 (ex: "tour", "event", etc)
    private List<TourVO> items;  // 최대 6개만
}
