package kr.spring.api.service;

import kr.spring.api.mapper.CommonApiMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class CommonDataSyncSupportService {

    public <T> Map<String,Object> processDataListToDB(CommonApiMapper mapper, List<T> list) {

        AtomicInteger insertCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        Map<String, Object> result = null;

        for(T dto : list) {

            //insert 수행중 실패한다면 update 수행
           Map<String, Object> process =  insertOrUpdate(mapper, dto);
           process.forEach((k, v) -> {
               switch (k){
                   case "insertCount":
                       insertCount.addAndGet((int)v);
                       break;
                   case "updateCount":
                       updateCount.addAndGet((int)v);
                       break;
                   case "failedCount":
                       failedCount.addAndGet((int)v);
                       break;
               }
           });

        }

        return Map.of("insertCount", insertCount.get(),
                "updateCount" , updateCount.get(),
                "failedCount" , failedCount.get(),
                "totalCount" , insertCount.get() + failedCount.get() + updateCount.get());

    }

    public <T> Map<String, Object> insertOrUpdate(CommonApiMapper mapper, T dto) {

        try {
            mapper.insert(dto);
            return Map.of("insertCount" , 1);
            } catch (Exception e) {
            log.trace("insert failed", e);
        }
        try{
            mapper.update(dto);
            return Map.of("updateCount" , 1);
        } catch (Exception e) {
            log.debug("insert and update failed", e);
            return Map.of("failedCount" , 1);
        }
    }

}