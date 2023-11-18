package com.example.mjusubwaystation_fe.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {
    String URL = "http://10.0.2.2:8000/";//서버 API
    Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build();
    RetrofitInterface server = retrofit.create(RetrofitInterface.class);

}
