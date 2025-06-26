package kr.spring.api.service.impl;

import com.project.dugaza.api.client.TrainApiClient;
import com.project.dugaza.api.dto.TrainKindApiDto;
import com.project.dugaza.api.mapper.TrainKindApiMapper;
import com.project.dugaza.api.service.TrainSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainSyncServiceImpl implements TrainSyncService {

    private final TrainApiClient trainApiClient;
    private final TrainKindApiMapper trainKindApiMapper;

    @Override
    public Map<String, Object> syncTrainKindData() {
        log.info("-----> Train Kind Sync Service Start");
        List<TrainKindApiDto> trainKindList = trainApiClient.getTrainKindData();
        int total = trainKindList.size();
        log.debug("----------> Train Kind Insert Or Update Service Start, Train Kind Data Count: {}", total);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        AtomicInteger updateCount = new AtomicInteger(0);
        for (TrainKindApiDto trainApiDto : trainKindList) {
            log.info("----------> Train Kind Mapping Start total : {}", total);
            if (trainKindApiMapper.insert(trainApiDto) != 1) {
                log.debug("----------> existing Train Kind try to update vehicle Id : {} ", trainApiDto.getVehicleKindId());
                if (trainKindApiMapper.update(trainApiDto) != 1) {
                    log.warn("---------->failed to mapping Train Kind, vehicle Id : {}", trainApiDto.getVehicleKindId());
                    failedCount.incrementAndGet();
                    continue;
                }
                updateCount.incrementAndGet();
            }
            successCount.incrementAndGet();
        }
        log.debug("-----> total : {} , success : {} , failed : {} , updated : {}", total, successCount.get(), failedCount.get(), updateCount.get());
        log.info("-----> Train Kind Sync Service End ");
        return Map.of("totalCount", total,
                "successCount", successCount.get(),
                "failedCount", failedCount.get(),
                "updateCount", updateCount.get());

    }
}