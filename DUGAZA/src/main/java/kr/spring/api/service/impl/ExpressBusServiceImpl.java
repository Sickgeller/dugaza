package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.ExpressBusApiClient;
import kr.spring.api.dto.ExpressBusCityApiDto;
import kr.spring.api.dto.ExpressBusGradeApiDto;
import kr.spring.api.dto.ExpressBusTerminalApiDto;
import kr.spring.api.mapper.ExpressBusCityApiMapper;
import kr.spring.api.mapper.ExpressBusGradeApiMapper;
import kr.spring.api.mapper.ExpressBusTerminalApiMapper;
import kr.spring.api.service.ExpressBusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpressBusServiceImpl implements ExpressBusService {

    private final ExpressBusApiClient expressBusApiClient;
    private final ExpressBusCityApiMapper expressBusCityApiMapper;
    private final ExpressBusTerminalApiMapper expressBusTerminalApiMapper;
    private final ExpressBusGradeApiMapper expressBusGradeApiMapper;

    @Override
    @LogExecutionTime(category = "ExpressBusCitySync")
    public Map<String, Object> syncCityData() {
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        List<ExpressBusCityApiDto> areaList = expressBusApiClient.getCityData();
       areaList.forEach(dto -> {
           try{
               expressBusCityApiMapper.insertCity(dto);
               insertCount.incrementAndGet();
               return;
           }catch (Exception e){
               log.debug("INSERT 실패 UPDATE 시도, cityCode : {}", dto.getCityCode());
           }
           try{
               expressBusCityApiMapper.updateCity(dto);
               updateCount.incrementAndGet();
           } catch (Exception e) {
               log.error("INSERT UPDATE 모두 실패, cityCode : {}", dto.getCityCode());
               failedCount.incrementAndGet();
           }
       });
        int total = insertCount.get() + updateCount.get() + failedCount.get();
        log.debug("지역 동기화 성공 총 {}개 지역", total);
        return Map.of("insert", insertCount, "update", updateCount, "failed", failedCount, "total", total);
    }

    @Override
    @LogExecutionTime(category = "ExpressBusTerminalSync")
    public Map<String, Object> syncTerminalData() {
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        List<ExpressBusTerminalApiDto> terminalList = expressBusApiClient.getTerminalData();

        terminalList.forEach(dto -> {
            try{
                expressBusTerminalApiMapper.insert(dto);
                insertCount.incrementAndGet();
                return;
            }catch (Exception e){
                log.debug("INSERT 실패 UPDATE 시도, terminalId : {}", dto.getTerminalId());
            }
            try{
                expressBusTerminalApiMapper.update(dto);
                updateCount.incrementAndGet();
            } catch (Exception e) {
                log.error("INSERT UPDATE 모두 실패, cityCode : {} , {}", dto.getTerminalId(), e.getMessage());
                failedCount.incrementAndGet();
            }
        });
        int total = insertCount.get() + updateCount.get() + failedCount.get();
        log.debug("터미널 동기화 성공 총 {}개 지역", total);
        return Map.of("insert", insertCount, "update", updateCount, "failed", failedCount, "total", total);

    }

    @Override
    @LogExecutionTime(category = "ExpressBusGradeSync")
    public Map<String, Object> syncGradeData() {
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        List<ExpressBusGradeApiDto> terminalList = expressBusApiClient.getGradeData();

        terminalList.forEach(dto -> {
            try{
                expressBusGradeApiMapper.insert(dto);
                insertCount.incrementAndGet();
                return;
            }catch (Exception e){
                log.debug("INSERT 실패 UPDATE 시도, gradeId : {}", dto.getGradeId());
            }
            try{
                expressBusGradeApiMapper.update(dto);
                updateCount.incrementAndGet();
            } catch (Exception e) {
                log.error("INSERT UPDATE 모두 실패, gradeId : {} , gradeNm :  {}", dto.getGradeId(), dto.getGradeNm(), e);
                failedCount.incrementAndGet();
            }
        });
        int total = insertCount.get() + updateCount.get() + failedCount.get();
        log.debug("터미널 동기화 성공 총 {}개 지역", total);
        return Map.of("insert", insertCount, "update", updateCount, "failed", failedCount, "total", total);

    }
}
