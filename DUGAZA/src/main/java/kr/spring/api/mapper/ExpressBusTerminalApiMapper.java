package kr.spring.api.mapper;

import kr.spring.api.dto.ExpressBusTerminalApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpressBusTerminalApiMapper {
    int insertTerminal(ExpressBusTerminalApiDto expressBusTerminalApiDto);
    int updateTerminal(ExpressBusTerminalApiDto expressBusTerminalApiDto);
}
