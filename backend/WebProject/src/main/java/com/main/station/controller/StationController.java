package com.main.station.controller;

import com.main.station.dto.OptimizedRoute;
import com.main.station.dto.StationDTO;
import com.main.station.service.RouteService;
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

    @PostMapping("/search")
    public ResponseEntity<OptimizedRoute> search(@RequestParam("start") int start,
                                             @RequestParam("end") int end,
                                             @RequestParam("search_option") String type,
                                             @RequestParam("start_time") String time
                                             ) throws IOException, IOException {
        optimizedRoute_ = routeService.search(start,end,type,time);
        return new ResponseEntity<>(optimizedRoute_, HttpStatus.OK);
    }


    @PostMapping("/select")
    public String select(@ModelAttribute OptimizedRoute optimizedRoute) throws IOException {
        System.out.println("optimizedRoute = " + optimizedRoute);
        optimizedRoute = optimizedRoute_;
        return "select";
    }

    @GetMapping("/search")
    public ResponseEntity<OptimizedRoute> getRouteData(@RequestParam int start, @RequestParam int end,  @RequestParam String type,  @RequestParam String time) throws IOException {
        optimizedRoute_ = routeService.search(start,end,type,time);
        return new ResponseEntity<>(optimizedRoute_, HttpStatus.OK);
    }



}
