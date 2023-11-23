package com.main.station.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDTO {
    private boolean alarmSetting;
    private List<List<String>> boardingTimeList;
    private List<List<String>> gettingOffTimeList;

}
