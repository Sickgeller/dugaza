package kr.spring.api.controller;

import com.project.dugaza.api.service.TrainSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping ( "/admin/train")
@Slf4j
@RequiredArgsConstructor
public class TrainSyncController {

    private final TrainSyncService trainSyncService;

    @GetMapping("/sync/kind")
    public ResponseEntity<Map<String, Object>> syncTrainKind() {
        log.info("kind of train sync request");
        Map<String,Object> result = trainSyncService.syncTrainKindData();
        log.info("kind of train sync done");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync/area-code")
    public ResponseEntity<Map<String,Object>> syncAreaCode(){
        log.info("area code of train sync request");
        Map<String,Object> result = trainSyncService.syncTrainAreaCode();
        log.info("area code of train sync end");
    }

}
