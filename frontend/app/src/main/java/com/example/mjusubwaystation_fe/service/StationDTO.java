package com.example.mjusubwaystation_fe.service;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class StationDTO {

    @SerializedName("surroundStationList")
    private List<List<Integer>> surroundStationList;

    public List<List<Integer>> getSurroundStationList() {
        return surroundStationList;
    }
}
