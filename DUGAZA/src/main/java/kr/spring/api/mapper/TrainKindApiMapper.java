package kr.spring.api.mapper;

import com.project.dugaza.api.dto.TrainKindApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainKindApiMapper {

    /**
     * trainKind type dto insert
     * @param trainKindApiDto insert dto
     */
    int insert(TrainKindApiDto trainKindApiDto);

    /**
     * trainKind type dto update
     * @param trainKindApiDto update dto
     */
    int update(TrainKindApiDto trainKindApiDto);

}
