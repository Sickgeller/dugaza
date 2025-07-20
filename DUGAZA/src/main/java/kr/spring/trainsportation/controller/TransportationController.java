package kr.spring.trainsportation.controller;

import kr.spring.api.client.ExpressBusApiClient;
import kr.spring.api.dto.ExpressBusCityApiDto;
import kr.spring.api.dto.ExpressBusRouteApiDto;
import kr.spring.api.dto.ExpressBusTerminalApiDto;
import kr.spring.api.dto.TrainCityApiDto;
import kr.spring.api.mapper.ExpressBusTerminalApiMapper;
import kr.spring.api.mapper.TrainCityApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/transportation")
@RequiredArgsConstructor
public class TransportationController {

	private final ExpressBusApiClient expressBusApiClient;
	private final ExpressBusTerminalApiMapper expressBusTerminalApiMapper;
	private final TrainCityApiMapper trainCityApiMapper;

	@GetMapping("")
	public String transportationMain() {
		return "views/transportation/transportation";
	}
	
	@GetMapping("/train")
	public String trainForm() {
		return "views/transportation/train";
	}
	
	@GetMapping("/bus")
	public String busForm() { 
		return "views/transportation/bus";
	}

	/**
	 * 고속버스 도시 목록 조회
	 */
	@GetMapping("/bus/cities")
	@ResponseBody
	public ResponseEntity<List<TrainCityApiDto>> getCities() {
		try {
			// train_city 테이블에서 도시 목록 조회
			List<TrainCityApiDto> cities = trainCityApiMapper.getAllCityDto();
			return ResponseEntity.ok(cities);
		} catch (Exception e) {
			log.error("고속버스 도시 목록 조회 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 고속버스 터미널 목록 조회
	 */
	@GetMapping("/bus/terminals")
	@ResponseBody
	public ResponseEntity<List<ExpressBusTerminalApiDto>> getTerminals() {
		try {
			List<ExpressBusTerminalApiDto> terminals = expressBusTerminalApiMapper.selectAll();
			return ResponseEntity.ok(terminals);
		} catch (Exception e) {
			log.error("고속버스 터미널 목록 조회 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 도시별 고속버스 터미널 목록 조회
	 */
	@GetMapping("/bus/terminals/{cityCode}")
	@ResponseBody
	public ResponseEntity<List<ExpressBusTerminalApiDto>> getTerminalsByCity(@PathVariable Long cityCode) {
		try {
			List<ExpressBusTerminalApiDto> terminals = expressBusTerminalApiMapper.selectByCityCode(cityCode);
			return ResponseEntity.ok(terminals);
		} catch (Exception e) {
			log.error("도시별 고속버스 터미널 목록 조회 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 고속버스 경로 검색
	 */
	@PostMapping("/bus/search")
	@ResponseBody
	public ResponseEntity<List<ExpressBusRouteApiDto>> searchBusRoutes(
			@RequestParam String depTerminalId,
			@RequestParam String arrTerminalId,
			@RequestParam String depPlandTime) {
		try {
			List<ExpressBusRouteApiDto> routes = expressBusApiClient.searchRoutes(
				depTerminalId, arrTerminalId, depPlandTime);
			return ResponseEntity.ok(routes);
		} catch (Exception e) {
			log.error("고속버스 경로 검색 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}
}
