package kr.spring.api.controller;

import com.project.dugaza.api.service.TourSyncService;
import com.project.dugaza.common.ContentTypeid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/admin/tour")
@Slf4j
@RequiredArgsConstructor
public class TourDataSyncController {

    private final TourSyncService tourSyncService;

    @GetMapping("/sync/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> syncAllTourData() {
        log.info("관광 데이터 전체 동기화 요청 받음");
        Map<String, Object> result = tourSyncService.getAllTourData();
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/sync/{contentTypeId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> syncTourDataByType(@PathVariable ContentTypeid contentTypeId) {
        log.info("관광 데이터 동기화 요청 받음: {}", contentTypeId.name());
        Map<String, Object> result = tourSyncService.getTouristData(contentTypeId);
        return ResponseEntity.ok(result);
    }
}
