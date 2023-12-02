package com.main.station.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDTO {
    private ArrayList<String> alarmTimeList;
    private ArrayList<String> titleList;
    private ArrayList<String> contentList;

    public static AlarmDTO setResult(ArrayList<String> alarmTime, ArrayList<String> title, ArrayList<String> content){
        AlarmDTO alarmDTO = new AlarmDTO();
        alarmDTO.setAlarmTimeList(alarmTime);
        alarmDTO.setTitleList(title);
        alarmDTO.setContentList(content);
        return alarmDTO;
    }

}
