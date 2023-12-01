package com.main.station.service;

import com.main.station.dto.AlarmDTO;
import com.main.station.dto.OptimizedRoute;
import com.main.station.dto.StationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AlarmService {


    public AlarmDTO getAlarmTime(ArrayList<String> pointTimeList){
        AlarmDTO alarmDTO;
        ArrayList<String> alarmTime = new ArrayList<>();
        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> content = new ArrayList<>();


        int gap = 3;
        int size = pointTimeList.size();

//      포인트 타임이 2개 이상일 때

        for (int i = gap; i >= 1; i--) {
            alarmTime.add(minusMinutes(pointTimeList.get(0), i));
            title.add("[출발역 열차 알림]");
            content.add("출발역 열차 도착 "+i+"분 전입니다!");
        }

        if (size > 1) {
            for (int i = 1; i < size; i++) {
                //간격에 따라 알람 시간 저장함
                for (int j = gap; j >= 1; j--) {
                    //실제 알람이 울릴 시간
                    String setTime = minusMinutes(pointTimeList.get(i), j);
                    //만약 알람시간이 이미 포함돼 있으면,
                    if (alarmTime.contains(setTime)) {
                        continue;
                    } else {
                        alarmTime.add(setTime);
                        title.add("[환승역 열차 알림]");
                        content.add("환승역 열차 도착 "+j+"분 전입니다!");
                    }

                }
            }
        }

        alarmDTO = AlarmDTO.setResult(alarmTime,title,content);

        return alarmDTO;
    }


    public String minusMinutes(String time, int gap) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, -gap);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            // 예외 처리: 올바르지 않은 형식의 문자열이 들어온 경우
            return null;
        }
    }

}
