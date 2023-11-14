package com.main.station.controller;

import com.main.station.dto.StationDTO;
import com.main.station.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/station")
public class StationController {

    private final RouteService routeService;

    @PostMapping("/search")
    @Operation(summary = "search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    public ResponseEntity<StationDTO> search(@RequestParam("start") int start, @RequestParam("end") int end) throws IOException, IOException {
        StationDTO stationDTO = routeService.search(start,end);
        return new ResponseEntity<>(stationDTO, HttpStatus.OK);
    }





//    @PostMapping("/search")
//    public ResponseBody search(@RequestParam("start") int start, @RequestParam("end") int end, Model model) throws IOException {
//        StationDTO stationDTO = routeService.search(start,end);
//        model.addAttribute("resultRoute", stationDTO);
//        return "route";
//    }
}
