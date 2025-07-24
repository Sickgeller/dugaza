package kr.spring.api.controller;

import kr.spring.api.service.ExpressBusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/express-bus")
@Slf4j
@RequiredArgsConstructor
public class ExpressBusSyncController {

    private final ExpressBusService expressBusService;

    @GetMapping("/sync/city")
    public ResponseEntity<Map<String,Object>> syncCityData() {
        log.debug("sync express bus city data");
        return ResponseEntity.ok(expressBusService.syncCityData());
    }

    @GetMapping("/sync/terminal")
    public ResponseEntity<Map<String,Object>> syncTerminalData() {
        log.debug("sync express bus terminal data");
        return ResponseEntity.ok(expressBusService.syncTerminalData());
    }

    @GetMapping("/sync/bus-grade")
    public ResponseEntity<Map<String,Object>> syncBusGradeData(){
        log.debug("sync express bus grade data");
        return ResponseEntity.ok(expressBusService.syncGradeData());
    }

}
