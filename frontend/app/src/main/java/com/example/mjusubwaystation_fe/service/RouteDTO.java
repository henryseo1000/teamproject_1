package com.example.mjusubwaystation_fe.service;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class RouteDTO {

    @SerializedName("start")
    private int start;
    @SerializedName("end")
    private int end;
    @SerializedName("time")
    private int time;
    @SerializedName("distance")
    private int distance;
    @SerializedName("shortestPath")
    private LinkedList<Integer> shortestPath;
    @SerializedName("shortestTime")
    private List<String> shortestTime;
    @SerializedName("expense")
    private int expense;
    @SerializedName("transferCount")
    private int transferCount;

    @Override
    public String toString(){
        return "RouteDTO{" +
                "start=" + start +
                ", end=" + end +
                ", result='" + time + '\''  +
                '}';
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getTime() {
        return time;
    }

    public LinkedList<Integer> getShortestPath() {
        return shortestPath;
    }

    public List<String> getShortestTime() {
        return shortestTime;
    }

    public int getTotalPrice(){
        return expense;
    }

    public int getDistance() {
        return distance;
    }

    public int getTransferCount() {
        return transferCount;
    }
}
