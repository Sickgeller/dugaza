package kr.spring.api.mapper;

import kr.spring.api.dto.ExpressBusTerminalApiDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpressBusTerminalApiMapper {
    int insert(ExpressBusTerminalApiDto expressBusTerminalApiDto);
    int update(ExpressBusTerminalApiDto expressBusTerminalApiDto);
}
