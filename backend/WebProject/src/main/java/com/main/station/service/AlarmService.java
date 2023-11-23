package com.main.station.service;

import com.main.station.dto.AlarmDTO;
import com.main.station.dto.OptimizedRoute;
import com.main.station.dto.StationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    public AlarmDTO getAlarmTime(OptimizedRoute optimizedRoute) {
        AlarmDTO alarmDTO = new AlarmDTO();

        //전체 경로에 대한 시간
        List<String> timeList = optimizedRoute.getShortestTime();
        List<String> alarmTimeList;
        List<List<String>> alarmTotalList1 = new ArrayList<>();
        List<List<String>> alarmTotalList2 = new ArrayList<>();
        System.out.println("전체 시간 리스트 = " + timeList);
        int index=0;
        for(String time:timeList) {
            if (index%2 == 0) {
                alarmTimeList = settingAlarmTime(time);
                alarmTotalList1.add(alarmTimeList);
                alarmDTO.setBoardingTimeList(alarmTotalList1);
            } else {
                alarmTimeList = settingAlarmTime(time);
                alarmTotalList2.add(alarmTimeList);
                alarmDTO.setGettingOffTimeList(alarmTotalList2);
            }
            index++;
        }
        return alarmDTO;
    }

    public List<String> settingAlarmTime(String item){
        List<String> alarmTimeList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time;
        time  = LocalTime.parse(item, formatter);
        for(int i=4;i>=0;i--){
            LocalTime setTime = time.minusMinutes(i);
            String alarmTime = setTime.format(formatter);
            if (alarmTimeList.contains(alarmTime)){
                continue;
            } else {
                alarmTimeList.add(alarmTime);
            }
        }
        return alarmTimeList;
    }
}
