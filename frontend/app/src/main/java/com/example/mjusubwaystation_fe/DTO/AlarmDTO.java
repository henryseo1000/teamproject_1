package com.example.mjusubwaystation_fe.DTO;

import com.google.gson.annotations.SerializedName;
import java.util.LinkedList;
import java.util.List;

public class AlarmDTO {


    @SerializedName("alarmSetting")
    private boolean alarmSetting;
    @SerializedName("boardingTimeList")
    private List<String> boardingTimeList;
    @SerializedName("transferTimeList")
    private List<List<String>> transferTimeList;

    public boolean isAlarmSetting() {
        return alarmSetting;
    }

    public void setAlarmSetting(boolean alarmSetting) {
        this.alarmSetting = alarmSetting;
    }

    public List<String> getBoardingTimeList() {
        return boardingTimeList;
    }

    public void setBoardingTimeList(List<String> boardingTimeList) {
        this.boardingTimeList = boardingTimeList;
    }

    public List<List<String>> getTransferTimeList() {
        return transferTimeList;
    }

    public void setTransferTimeList(List<List<String>> transferTimeList) {
        this.transferTimeList = transferTimeList;
    }
}
