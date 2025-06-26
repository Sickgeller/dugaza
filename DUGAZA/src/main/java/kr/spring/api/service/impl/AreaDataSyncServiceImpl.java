package kr.spring.api.service.impl;

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
    public List<AreaCodeApiDto> syncAreaCodes() {
        log.info("-----지역코드 동기화 시작 (시) -----");
        try {
            List<AreaCodeApiDto> siCodes = areaCodeApiClient.getAreaCode();

            // NULL 값이나 빈 값 필터링
            siCodes = siCodes.stream()
                    .filter(code -> code.getAreaCode() != null)
                    .filter(code -> code.getAreaName() != null && !code.getAreaName().isEmpty())
                    .collect(Collectors.toList());

            log.info("유효한 지역코드 개수: {}", siCodes.size());

            for (AreaCodeApiDto element : siCodes) {
                try {
                    Long areaCode = element.getAreaCode();
                    String areaName = element.getAreaName();

                    // NULL 체크 추가
                    if (areaCode == null) {
                        log.warn("지역코드가 null입니다. 건너뜁니다: {}", element);
                        continue;
                    }

                    AreaCodeApiDto existing = areaCodeMapper.findByAreaCode(areaCode.toString());

                    if (existing == null) {
                        try {
                            log.debug("삽입 시도 전 DTO 상태: {}", element);
                            areaCodeMapper.insert(element);
                            log.info("시 코드 추가 : {} - {}", areaCode, areaName);
                        } catch (Exception e) {
                            log.error("시 코드 추가 실패 : {} - {}", areaCode, areaName, e);
                        }
                    } else {
                        try {
                            existing.setAreaName(areaName);
                            areaCodeMapper.update(existing);
                            log.info("시도 코드 업데이트: {} - {}", areaCode, areaName);
                        } catch (Exception e) {
                            log.error("시도 코드 업데이트 실패: {} - {}", areaCode, areaName, e);
                        }
                    }
                } catch (Exception e) {
                    log.error("시 코드 처리 실패 : {}", element, e);
                }
            }
            log.info("시 코드 처리 완료 - {}개", siCodes.size());
            return siCodes;
        } catch (Exception e) {
            log.error("시 코드 처리 실패  ", e);
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    public void syncSigunguCodes() {
        int siCount = 0;
        int sigunguCodeCount = 0;
        log.info("-----시군구 코드 동기화 시작 ------");
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
                log.info("-----{}시 동기화 시작 , 지역코드 : {} " , areaName, areaCode);
                
                List<SigunguCodeApiDto> sigunguCodeList = areaCodeApiClient.getSigunguCode(areaCode);
                
                // NULL 값이나 빈 값 필터링
                sigunguCodeList = sigunguCodeList.stream()
                    .filter(code -> code.getSigunguCode() != null)
                    .filter(code -> code.getSigunguName() != null && !code.getSigunguName().isEmpty())
                    .collect(Collectors.toList());
                
                log.info("{}시의 유효한 시군구 코드 개수: {}", areaName, sigunguCodeList.size());
                
                // 각 시군구 코드를 SIGUNGU_CODE 테이블에 저장
                for (SigunguCodeApiDto sigunguCode : sigunguCodeList) {
                    try {
                        Long sigunguCodeValue = sigunguCode.getSigunguCode();
                        String sigunguName = sigunguCode.getSigunguName();
                        
                        // NULL 체크
                        if (sigunguCodeValue == null) {
                            log.warn("시군구 코드가 null입니다. 건너뜁니다: {}", sigunguCode);
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
                                log.debug("시군구 코드 삽입 시도 전 DTO 상태: {}", sigunguCode);
                                sigunguCodeMapper.insert(sigunguCode);
                                log.info("시군구 코드 추가: {} - {} (부모: {})", sigunguCodeValue, sigunguName, areaCode);
                                sigunguCodeCount++;
                            } catch (Exception e) {
                                log.error("시군구 코드 추가 실패: {} - {}", sigunguCodeValue, sigunguName, e);
                            }
                        } else {
                            try {
                                existing.setSigunguName(sigunguName);
                                sigunguCodeMapper.update(existing);
                                log.info("시군구 코드 업데이트: {} - {} (부모: {})", sigunguCodeValue, sigunguName, areaCode);
                                sigunguCodeCount++;
                            } catch (Exception e) {
                                log.error("시군구 코드 업데이트 실패: {} - {}", sigunguCodeValue, sigunguName, e);
                            }
                        }
                    } catch (Exception e) {
                        log.error("시군구 코드 처리 실패: {}", sigunguCode, e);
                    }
                }
                
                log.info("-----{}시 동기화 완료 , 지역코드 : {} " , areaName, areaCode);
                siCount++;
            }
            log.info("시군구 코드 처리 완료 처리된 시 개수: {}, 처리된 시군구 개수: {}", siCount, sigunguCodeCount);
        } catch (Exception e) {
            log.error("시군구 코드 처리 실패 처리된 시 개수: {}, 처리된 시군구 개수: {}", siCount, sigunguCodeCount, e);
        }
        log.info("-----시군구 코드 동기화 완료 ------");
    }

    @Override
    public void syncSidoCodes() {
        // 구현 필요
    }

}
