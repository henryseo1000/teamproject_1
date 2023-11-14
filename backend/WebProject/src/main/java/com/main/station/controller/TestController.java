package com.main.station.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "test", description = "test API")
@RestController
@RequiredArgsConstructor
public class TestController {
//    @PostMapping(value = "/test")
//    public ResponseEntity<String> test() {
//        return ResponseEntity.ok("test");
//    }



    @Operation(summary = "get test", description = "test 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "실패", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @GetMapping("/v1/test")
    public String test() {
        return "test";
    }
}
