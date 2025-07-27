package kr.spring.trainsportation.controller;

import kr.spring.api.client.ExpressBusApiClient;
import kr.spring.api.dto.*;
import kr.spring.api.mapper.ExpressBusTerminalApiMapper;
import kr.spring.api.mapper.TrainCityApiMapper;
import kr.spring.trainsportation.service.TrainService;
import kr.spring.trainsportation.vo.TrainCityVO;
import kr.spring.trainsportation.vo.TrainRouteVO;
import kr.spring.trainsportation.vo.TrainStationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
	private final TrainService trainService;

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
	public ResponseEntity<List<ExpressBusTerminalApiDto>> getTerminalsByCity(@PathVariable(name = "cityCode") Long cityCode) {
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
			@RequestParam(name = "depTerminalId") String depTerminalId,
			@RequestParam(name = "arrTerminalId") String arrTerminalId,
			@RequestParam(name = "depPlandTime") String depPlandTime) {
		try {
			List<ExpressBusRouteApiDto> routes = expressBusApiClient.searchRoutes(
				depTerminalId, arrTerminalId, depPlandTime);
			return ResponseEntity.ok(routes);
		} catch (Exception e) {
			log.error("고속버스 경로 검색 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 기차 도시 목록 조회
	 */
	@GetMapping("/train/cities")
	@ResponseBody
	public ResponseEntity<List<TrainCityVO>> getTrainCities() {
		try {
			List<TrainCityVO> cities = trainService.getAllCities();
			return ResponseEntity.ok(cities);
		} catch (Exception e) {
			log.error("기차 도시 목록 조회 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 도시별 기차역 목록 조회
	 */
	@GetMapping("/train/stations/{cityCode}")
	@ResponseBody
	public ResponseEntity<List<TrainStationVO>> getTrainStationsByCity(@PathVariable Integer cityCode) {
		try {
			List<TrainStationVO> stations = trainService.getStationsByCity(cityCode);
			return ResponseEntity.ok(stations);
		} catch (Exception e) {
			log.error("도시별 기차역 목록 조회 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 기차 노선 검색
	 */
	@PostMapping("/train/search")
	@ResponseBody
	public ResponseEntity<List<TrainRouteApiDto>> searchTrainRoutes(
			@RequestParam(name = "depPlaceId") String depPlaceId,
			@RequestParam(name = "arrPlaceId") String arrPlaceId,
			@RequestParam(name = "depPlandTime") String depPlandTime) {
		try {
			List<TrainRouteApiDto> routes = trainService.searchRoutes(depPlaceId, arrPlaceId, depPlandTime);
			return ResponseEntity.ok(routes);
		} catch (Exception e) {
			log.error("기차 노선 검색 중 오류 발생", e);
			return ResponseEntity.internalServerError().build();
		}
	}
}
