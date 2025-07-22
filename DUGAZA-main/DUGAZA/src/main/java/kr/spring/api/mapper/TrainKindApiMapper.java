package kr.spring.api.mapper;

import kr.spring.api.dto.TrainKindApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainKindApiMapper extends CommonApiMapper{

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
