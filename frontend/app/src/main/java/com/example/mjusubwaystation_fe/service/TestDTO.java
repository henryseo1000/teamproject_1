package com.example.mjusubwaystation_fe.service;

import com.google.gson.annotations.SerializedName;

public class TestDTO {
    @SerializedName("userId")
    private int userId;

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("completed")
    private boolean completed;

    @Override
    public String toString(){
        return "TestDTO{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed + '\'' +
                '}';
    }
}
