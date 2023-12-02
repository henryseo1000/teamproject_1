package com.example.mjusubwaystation_fe.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AlarmDTO {


    @SerializedName("alarmTimeList")
    private ArrayList<String> alarmTimeList;
    @SerializedName("titleList")
    private ArrayList<String> titleList;
    @SerializedName("contentList")
    private ArrayList<String> contentList;


    public ArrayList<String> getAlarmTimeList() {return alarmTimeList;}
    public ArrayList<String> getTitleList() {return titleList;}
    public ArrayList<String> getContentList() {return contentList;}

    @Override
    public String toString(){
        return "AlarmDTO{" +
                "alarmTimeList=" + alarmTimeList +
                ", titleList=" + titleList +
                ", contentList='" + contentList + '\''  +
                '}';
    }

    public void setAlarmTimeList(ArrayList<String> alarmTimeList) {
        this.alarmTimeList = alarmTimeList;
    }

    public void setTitleList(ArrayList<String> titleList) {
        this.titleList = titleList;
    }

    public void setContentList(ArrayList<String> contentList) {
        this.contentList = contentList;
    }
}
