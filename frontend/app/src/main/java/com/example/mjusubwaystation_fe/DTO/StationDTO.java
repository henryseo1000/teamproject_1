package com.example.mjusubwaystation_fe.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class StationDTO {
    @SerializedName("nowStation")
    private int nowStation;
    @SerializedName("lineList")
    private List<Integer> lineList;
    @SerializedName("surroundStationList")
    private List<List<Integer>> surroundStationList;

    public List<List<Integer>> getSurroundStationList() {
        return surroundStationList;
    }

    public List<Integer> getLineList() {
        return lineList;
    }

    public int getNowStation() {
        return nowStation;
    }
}
