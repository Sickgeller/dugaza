package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.AreaCodeApiClient;
import kr.spring.api.dto.AreaCodeApiDto;
import kr.spring.api.dto.SigunguCodeApiDto;
import kr.spring.api.mapper.AreaCodeMapper;
import kr.spring.api.mapper.SigunguCodeMapper;
import kr.spring.api.service.AreaDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaDataSyncServiceImpl implements AreaDataSyncService {

    private final AreaCodeApiClient areaCodeApiClient;
    private final AreaCodeMapper areaCodeMapper;
    private final SigunguCodeMapper sigunguCodeMapper;

    @Transactional
    @Override
    @LogExecutionTime(category = "AreaSync")
    public List<AreaCodeApiDto> syncAreaCodes() {
        try {
            List<AreaCodeApiDto> siCodes = areaCodeApiClient.getAreaCode();

            // NULL 값이나 빈 값 필터링
            siCodes = siCodes.stream()
                    .filter(code -> code.getAreaCode() != null)
                    .filter(code -> code.getAreaName() != null && !code.getAreaName().isEmpty())
                    .collect(Collectors.toList());

            for (AreaCodeApiDto element : siCodes) {
                try {
                    Long areaCode = element.getAreaCode();
                    String areaName = element.getAreaName();

                    // NULL 체크 추가
                    if (areaCode == null) {
                        continue;
                    }

                    AreaCodeApiDto existing = areaCodeMapper.findByAreaCode(areaCode.toString());

                    if (existing == null) {
                        try {
                            areaCodeMapper.insert(element);
                        } catch (Exception e) {
                            // AOP에서 예외 처리
                        }
                    } else {
                        try {
                            existing.setAreaName(areaName);
                            areaCodeMapper.update(existing);
                        } catch (Exception e) {
                            // AOP에서 예외 처리
                        }
                    }
                } catch (Exception e) {
                    // AOP에서 예외 처리
                }
            }
            return siCodes;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    @LogExecutionTime(category = "AreaSync")
    public void syncSigunguCodes() {
        int siCount = 0;
        int sigunguCodeCount = 0;
        
        try {
            List<AreaCodeApiDto> areaCodeList = areaCodeApiClient.getAreaCode();
            
            // NULL 값이나 빈 값 필터링
            areaCodeList = areaCodeList.stream()
                    .filter(code -> code.getAreaCode() != null)
                    .filter(code -> code.getAreaName() != null && !code.getAreaName().isEmpty())
                    .collect(Collectors.toList());
                    
            for(AreaCodeApiDto element : areaCodeList){
                String areaName = element.getAreaName();
                Long areaCode = element.getAreaCode();
                
                List<SigunguCodeApiDto> sigunguCodeList = areaCodeApiClient.getSigunguCode(areaCode);
                
                // NULL 값이나 빈 값 필터링
                sigunguCodeList = sigunguCodeList.stream()
                    .filter(code -> code.getSigunguCode() != null)
                    .filter(code -> code.getSigunguName() != null && !code.getSigunguName().isEmpty())
                    .collect(Collectors.toList());
                
                // 각 시군구 코드를 SIGUNGU_CODE 테이블에 저장
                for (SigunguCodeApiDto sigunguCode : sigunguCodeList) {
                    try {
                        Long sigunguCodeValue = sigunguCode.getSigunguCode();
                        String sigunguName = sigunguCode.getSigunguName();
                        
                        // NULL 체크
                        if (sigunguCodeValue == null) {
                            continue;
                        }
                        
                        // 부모 코드 설정 (시 코드)
                        sigunguCode.setAreaCode(areaCode);
                        // isActive 기본값 설정
                        sigunguCode.setIsActive(1L);
                        
                        // 이미 존재하는지 확인 (복합키: sigunguCode + areaCode)
                        SigunguCodeApiDto existing = sigunguCodeMapper.findBySigunguCodeAndAreaCode(
                            sigunguCodeValue.toString(),
                            areaCode.toString()
                        );
                        
                        if (existing == null) {
                            try {
                                sigunguCodeMapper.insert(sigunguCode);
                                sigunguCodeCount++;
                            } catch (Exception e) {
                                // AOP에서 예외 처리
                            }
                        } else {
                            try {
                                existing.setSigunguName(sigunguName);
                                sigunguCodeMapper.update(existing);
                                sigunguCodeCount++;
                            } catch (Exception e) {
                                // AOP에서 예외 처리
                            }
                        }
                    } catch (Exception e) {
                        // AOP에서 예외 처리
                    }
                }
                
                siCount++;
            }
        } catch (Exception e) {
            // AOP에서 예외 처리
        }
    }

    @Override
    @LogExecutionTime(category = "AreaSync")
    public void syncSidoCodes() {
        // 구현 필요
    }
}
