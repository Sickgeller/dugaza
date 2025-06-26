package kr.spring.api.mapper;

import com.project.dugaza.api.dto.EventContentApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventContentMapper {
    
    /**
     * 이벤트 테이블이 없는 경우 생성
     */
    void createEventContentTableIfNotExists();
    
    /**
     * 이벤트 데이터 삽입 또는 업데이트
     * @param eventContent 이벤트 데이터
     * @return 영향받은 행 수
     */
    int insertOrUpdateEventContent(EventContentApiDto eventContent);
    
    /**
     * 이벤트 데이터 개수 조회
     * @return 이벤트 데이터 개수
     */
    int countEventContent();
    
    /**
     * 이벤트 데이터 전체 삭제
     * @return 삭제된 행 수
     */
    int deleteAllEventContent();
} 