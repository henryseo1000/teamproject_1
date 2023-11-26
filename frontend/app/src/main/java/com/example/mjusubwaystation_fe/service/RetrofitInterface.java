package com.example.mjusubwaystation_fe.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import com.example.mjusubwaystation_fe.service.RouteDTO;
import com.example.mjusubwaystation_fe.service.StationDTO;

public interface RetrofitInterface {

    // @GET( EndPoint- 자원 위치(URI) )
    @GET("/station/StationInfo")
    Call<StationDTO> getStationInfo(@Query("station") int station);

    @GET("station/search") // 여기에는 실제 서버의 엔드포인트 경로를 넣어야 합니다.
    Call<RouteDTO> getRouteData(@Query("start") int start, @Query("end") int end,
                                                                        @Query("type") String type, @Query("time") String time);

//    @POST("station/search") // 여기에는 실제 서버의 엔드포인트 경로를 넣어야 합니다.
//    Call<RouteDTO> getPathData(@Query("start") int start, @Query("end") int end,
//                               @Query("search_option") String search_option, @Query("start_time") String start_time);
}