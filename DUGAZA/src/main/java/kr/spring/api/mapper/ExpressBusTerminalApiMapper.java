package kr.spring.api.mapper;

import kr.spring.api.dto.ExpressBusTerminalApiDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ExpressBusTerminalApiMapper extends CommonApiMapper{
    int insert(ExpressBusTerminalApiDto expressBusTerminalApiDto);
    int update(ExpressBusTerminalApiDto expressBusTerminalApiDto);
    List<ExpressBusTerminalApiDto> selectAll();
    List<ExpressBusTerminalApiDto> selectByCityCode(Long cityCode);
}
