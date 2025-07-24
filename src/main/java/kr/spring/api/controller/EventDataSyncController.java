package kr.spring.api.controller;

import kr.spring.api.service.EventDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/admin/event")
@Slf4j
@RequiredArgsConstructor
public class EventDataSyncController {

    private final EventDataSyncService eventDataSyncService;

    @GetMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncEventCode() {
        // 올해 년도를 기본값으로 사용
        Long currentYear = (long) LocalDate.now().getYear();
        log.info("이벤트 정보 동기화 요청받음 - 기본 시작 연도: {}", currentYear);
        
        Map<String, Object> result = eventDataSyncService.syncEventData(currentYear);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync/{startYear}")
    public ResponseEntity<Map<String, Object>> syncEventCodeWithYear(@PathVariable Long startYear) {
        log.info("이벤트 정보 동기화 요청받음 - 시작 연도: {}", startYear);
        
        Map<String, Object> result = eventDataSyncService.syncEventData(startYear);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync/{startYear}/date")
    public ResponseEntity<Map<String, Object>> syncEventDateWithYear(@PathVariable("startYear") Long startYear){
        log.info("이벤트 시작 끝 정도 동기화 요청 - 시작 연도 : {}", startYear);
        Map<String,Object> result = eventDataSyncService.syncEventDateData(startYear);
        return ResponseEntity.ok(result);
    }
}
