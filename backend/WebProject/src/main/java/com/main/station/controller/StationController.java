package com.main.station.controller;

import com.main.station.dto.*;
import com.main.station.service.AlarmService;
import com.main.station.service.RouteService;
import com.main.station.service.StationInfoService;
import com.main.station.service.StationTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/station")
public class StationController {

    private final StationInfoService stationInfoService;
    private final RouteService routeService;
    private final StationTimeService stationTimeService;
    private final AlarmService alarmService;
    private OptimizedRoute optimizedRoute_;

    @Operation(summary = "역 정보 조회", description = "역 정보를 조회 한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/search")
    public ResponseEntity<OptimizedRoute> search(@RequestParam("start") int start,
                                             @RequestParam("end") int end,
                                             @RequestParam("search_option") String type,
                                             @RequestParam("start_time") String time
                                             ) throws IOException, IOException {
        optimizedRoute_ = routeService.search(start,end,type,time);
        return new ResponseEntity<>(optimizedRoute_, HttpStatus.OK);
    }


    @Operation(summary = "최적 경로 가져오기", description = "최적의 경로를 보여준다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/select")
    public String select(@ModelAttribute OptimizedRoute optimizedRoute) throws IOException {
        System.out.println("optimizedRoute = " + optimizedRoute);
        optimizedRoute = optimizedRoute_;
        return "select";
    }


///////////////////////////// 모바일 통신 메서드 //////////////////////////////////////////////////

    @Operation(summary = "최적 경로 찾기", description = "최적의 경로를 찾아 보여준다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/search")
    public ResponseEntity<OptimizedRoute> getRouteData(@RequestParam int start,
                                                       @RequestParam int end,
                                                       @RequestParam String type,
                                                       @RequestParam String time) throws IOException {
        optimizedRoute_ = routeService.search(start,end,type,time);
        System.out.println("optimizedRoute_ = " + optimizedRoute_.getTotalLineList());
        return new ResponseEntity<>(optimizedRoute_, HttpStatus.OK);
    }

    @Operation(summary = "알람데이터 가져오기", description = "알람 시간데이터를 가져온다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })

    @GetMapping("/select")
    public ResponseEntity<AlarmDTO> selectOptimizedRoute(@ModelAttribute OptimizedRoute optimizedRoute, @RequestParam boolean alarmsetting) throws IOException {
        AlarmDTO alarmDTO = null;
        if (alarmsetting) {
            alarmDTO = alarmService.getAlarmTime(optimizedRoute);
            System.out.println("alarmDTO = " + alarmDTO.getBoardingTimeList());
            System.out.println("alarmDTO = " + alarmDTO.getGettingOffTimeList());
        }
        return new ResponseEntity<>(alarmDTO, HttpStatus.OK);
    }

    @Operation(summary = "특정 역 정보 가져오기", description = "특정 역의 정보를 가져온다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/StationInfo")
    public ResponseEntity<SpecificStation> selectStation(@RequestParam int station) throws IOException {
        SpecificStation specificStation;
        specificStation = stationInfoService.getInfo(station);
        System.out.println("specificStation = " + specificStation);
        return new ResponseEntity<>(specificStation, HttpStatus.OK);
    }

    
    
    @Operation(summary = "특정 역 시간표 가져오기", description = "특정 역의 시간표를 가져온다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @GetMapping("/StationTimeInfo")
    public ResponseEntity<List<StationTimeDTO>> selectStationTime(@RequestParam int station) throws IOException {
        List<StationTimeDTO> stationTimeDTOList;
        stationTimeDTOList = stationTimeService.getTimeInfo(station);
        System.out.println("stationTimeDTOList = " + stationTimeDTOList);
        return new ResponseEntity<>(stationTimeDTOList, HttpStatus.OK);
    }

}
