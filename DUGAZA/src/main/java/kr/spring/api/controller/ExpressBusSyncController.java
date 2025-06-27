package kr.spring.api.controller;

import kr.spring.api.service.ExpressBusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
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


}
