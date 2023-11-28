package com.example.mjusubwaystation_fe.service;

import retrofit2.Call;
import retrofit2.http.GET;

import retrofit2.http.Query;

import com.example.mjusubwaystation_fe.DTO.AlarmDTO;
import com.example.mjusubwaystation_fe.service.RetrofitInterface;
import com.example.mjusubwaystation_fe.DTO.RouteDTO;
import com.example.mjusubwaystation_fe.DTO.StationDTO;

import com.example.mjusubwaystation_fe.DTO.RouteDTO;
import com.example.mjusubwaystation_fe.DTO.StationDTO;
import com.example.mjusubwaystation_fe.DTO.StationTimeDTO;

import java.util.List;

public interface RetrofitInterface {

    // @GET( EndPoint- 자원 위치(URI) )
    @GET("/station/StationInfo")
    Call<StationDTO> getStationInfo(@Query("station") int station);

    @GET("/station/search") // 여기에는 실제 서버의 엔드포인트 경로를 넣어야 합니다.
    Call<RouteDTO> getPathData(@Query("start") int start, @Query("end") int end,
                                @Query("type") String type, @Query("time") String time);

    @GET("/station/select") // 여기에는 실제 서버의 엔드포인트 경로를 넣어야 합니다.
    Call<AlarmDTO> getAlarmData(@Query("optimizedRoute")RouteDTO optimizedRoute,
                                @Query("alarmsetting")boolean alarmsetting);

    @GET("/station/StationTimeInfo") // 여기에는 실제 서버의 엔드포인트 경로를 넣어야 합니다.
    Call<List<StationTimeDTO>> selectStationTime(@Query("station") int station);
}