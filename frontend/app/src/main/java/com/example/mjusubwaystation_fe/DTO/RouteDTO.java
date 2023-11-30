package com.example.mjusubwaystation_fe.DTO;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class RouteDTO implements Serializable {

    @SerializedName("start")
    private int start;
    @SerializedName("end")
    private int end;
    @SerializedName("time")
    private int time;
    @SerializedName("distance")
    private int distance;
    @SerializedName("expense")
    private int expense;
    @SerializedName("transferCount")
    private int transferCount;
    @SerializedName("shortestPath")
    private LinkedList<Integer> shortestPath;
    @SerializedName("shortestTime")
    private List<String> shortestTime;
    @SerializedName("totalLineList")
    private List<Integer> totalLineList;

    @Override
    public String toString(){
        return "RouteDTO{" +
                "start=" + start +
                ", end=" + end +
                ", time='" + time + '\''  +
                ", shortestTime='" + shortestTime + '\''  +
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

    public List<Integer> getTotalLineList() {return totalLineList;}

    public int getTotalPrice(){
        return expense;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }

    public void setTransferCount(int transferCount) {
        this.transferCount = transferCount;
    }

    public void setShortestPath(LinkedList<Integer> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public void setShortestTime(List<String> shortestTime) {
        this.shortestTime = shortestTime;
    }

    public void setTotalLineList(List<Integer> totalLineList) {
        this.totalLineList = totalLineList;
    }

    public int getDistance() {
        return distance;
    }

    public int getTransferCount() {
        return transferCount;
    }

    public int getExpense() {
        return expense;
    }
}
