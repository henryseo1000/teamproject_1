package com.example.mjusubwaystation_fe.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {

    // @GET( EndPoint- 자원 위치(URI) )
    @GET("/todos/{todos}")
    Call<TestDTO> get_data(@Path("todos") String get);
}