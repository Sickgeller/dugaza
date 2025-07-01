package kr.spring.api.controller;

import kr.spring.api.service.HouseDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/house")
public class HouseDataSyncController {

    private final HouseDataSyncService houseDataSyncService;

    @RequestMapping("/sync")
    public ResponseEntity<Map<String,Object>> syncHouseData(){
        log.debug("house data sync");
        Map<String, Object> result = houseDataSyncService.syncHouseData();
        return ResponseEntity.ok(result);
    }
}
