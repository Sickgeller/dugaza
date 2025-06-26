package kr.spring.api.service;

import com.project.dugaza.api.dto.AreaCodeApiDto;

import java.util.List;

public interface AreaDataSyncService {
    public List<AreaCodeApiDto> syncAreaCodes();
    public void syncSidoCodes();
    public void syncSigunguCodes();
}
