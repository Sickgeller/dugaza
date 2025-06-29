package kr.spring.api.controller;

import kr.spring.aop.LogExecutionTime;
import kr.spring.api.service.TrainSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping ( "/admin/train")
@Slf4j
@RequiredArgsConstructor
public class TrainSyncController {

    private final TrainSyncService trainSyncService;

    @GetMapping("/sync/kind")
    @LogExecutionTime(category = "TrainSync")
    public ResponseEntity<Map<String, Object>> syncTrainKind() {
        Map<String,Object> result = trainSyncService.syncTrainKindData();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync/area-code")
    @LogExecutionTime(category = "TrainSync")
    public ResponseEntity<Map<String,Object>> syncAreaCode(){
        Map<String,Object> result = trainSyncService.syncTrainAreaCode();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync/station")

    public ResponseEntity<Map<String,Object>> syncStationCode(){
        Map<String,Object> result = trainSyncService.syncStationCode();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync/route")
    @LogExecutionTime(category = "TrainSync")
    public ResponseEntity<Map<String,Object>> syncRouteCode(){
        Map<String,Object> result = trainSyncService.syncTrainRouteData();
        return ResponseEntity.ok(result);
    }
}
