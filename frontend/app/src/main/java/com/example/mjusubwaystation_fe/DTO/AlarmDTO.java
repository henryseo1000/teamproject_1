package com.example.mjusubwaystation_fe.DTO;

import com.google.gson.annotations.SerializedName;

public class AlarmDTO {
    @SerializedName("optimizedRoute")
    RouteDTO optimizedRoute;

    @SerializedName("alarmsetting")
    boolean alarmsetting;
}
