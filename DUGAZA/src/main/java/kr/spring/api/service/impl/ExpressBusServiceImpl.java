package kr.spring.api.service.impl;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.client.ExpressBusApiClient;
import kr.spring.api.dto.ExpressBusCityApiDto;
import kr.spring.api.dto.ExpressBusTerminalApiDto;
import kr.spring.api.mapper.ExpressBusCityApiMapper;
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
    private final ExpressBusTerminalApiMapper expressBusTerminalApiDtomapper;

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
        return Map.of("insert", insertCount, "updateC", updateCount, "failed", failedCount, "total", total);
    }

    @Override
    @LogExecutionTime(category = "Express Bus Terminal Sync")
    public Map<String, Object> syncTerminalData() {
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        List<ExpressBusTerminalApiDto> terminalList = expressBusApiClient.getTerminalData();

        terminalList.forEach(dto -> {
            try{
                expressBusTerminalApiDtomapper.insertTerminal(dto);
                insertCount.incrementAndGet();
                return;
            }catch (Exception e){
                log.debug("INSERT 실패 UPDATE 시도, terminalId : {}", dto.getTerminalId());
            }
            try{
                expressBusTerminalApiDtomapper.updateTerminal(dto);
                updateCount.incrementAndGet();
            } catch (Exception e) {
                log.error("INSERT UPDATE 모두 실패, cityCode : {}", dto.getTerminalId());
                failedCount.incrementAndGet();
            }
        });
        int total = insertCount.get() + updateCount.get() + failedCount.get();
        log.debug("터미널 동기화 성공 총 {}개 지역", total);
        return Map.of("insert", insertCount, "updateC", updateCount, "failed", failedCount, "total", total);

    }
}
