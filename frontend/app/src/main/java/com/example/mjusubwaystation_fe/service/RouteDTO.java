package com.example.mjusubwaystation_fe.service;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class RouteDTO {

    @SerializedName("start")
    private int start;
    @SerializedName("end")
    private int end;
    @SerializedName("result")
    private int result;
    @SerializedName("shortestPath")
    private LinkedList<Integer> shortestPath;
    @SerializedName("shortestTime")
    private List<String> shortestTime;



    @Override
    public String toString(){
        return "TestDTO{" +
                "start=" + start +
                ", end=" + end +
                ", result='" + result + '\''  +
                '}';
    }
}
