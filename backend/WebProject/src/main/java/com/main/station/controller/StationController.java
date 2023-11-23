package com.main.station.controller;

import com.main.station.dto.OptimizedRoute;
import com.main.station.dto.StationDTO;
import com.main.station.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/station")
public class StationController {

    private final RouteService routeService;
    private OptimizedRoute optimizedRoute_;

    @Operation(summary = "최단 경로 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
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


    @Operation(summary = "최단 경로 선택")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PostMapping("/select")
    public String select(@ModelAttribute OptimizedRoute optimizedRoute) throws IOException {
        System.out.println("optimizedRoute = " + optimizedRoute);
        optimizedRoute = optimizedRoute_;
        return "select";
    }

    @Operation(summary = "최단 경로 조회 결과")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping("/search")
    public ResponseEntity<OptimizedRoute> getRouteData(@RequestParam int start, @RequestParam int end,  @RequestParam String type,  @RequestParam String time) throws IOException {
        optimizedRoute_ = routeService.search(start,end,type,time);
        return new ResponseEntity<>(optimizedRoute_, HttpStatus.OK);
    }



}
