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
        return "RouteDTO{" +
                "start=" + start +
                ", end=" + end +
                ", result='" + result + '\''  +
                '}';
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getResult() {
        return result;
    }

    public LinkedList<Integer> getShortestPath() {
        return shortestPath;
    }

    public List<String> getShortestTime() {
        return shortestTime;
    }
}
