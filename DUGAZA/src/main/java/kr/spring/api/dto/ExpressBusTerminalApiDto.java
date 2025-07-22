package kr.spring.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpressBusTerminalApiDto {
    private String terminalId;
    private String terminalName;
    private String address;
    private Long cityCode;
}
