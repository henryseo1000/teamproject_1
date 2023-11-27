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

        //알림을 울릴 시간의 인덱스 리스트
        List<String> pointTimeList = getPointTimeList(optimizedRoute.getShortestTime(),optimizedRoute.getTotalLineList());
        System.out.println("pointTimeList = " + pointTimeList);
        
        //AlarmDTO에 넣을 컬렉션
        List<String> boardingList = new ArrayList<>();
        List<List<String>> transferList = new ArrayList<>();

        int index=0;
        for(String time:pointTimeList) {
            if (index==0) {
                System.out.println("boarding에 넣을 시간 = " + time);
                boardingList = getAlarmTime(time);
            } else {
                System.out.println("transfer에 넣을 시간 = " + time);
                transferList.add(getAlarmTime(time));
            }
            index++;
        }
        alarmDTO.setBoardingTimeList(boardingList);
        alarmDTO.setTransferTimeList(transferList);
        return alarmDTO;
    }
    
    // 실제 알람 울릴 시간을 컬렉션으로 반환해줌
    public List<String> getAlarmTime(String time){

        List<String> alarmTimeList;
        //시간 리스트를 저장해 반환할 컬렉션
        alarmTimeList = setTimeList(time);
        return alarmTimeList;
    }
    
    //알람 울릴 시간을 계산하는 메서드
    public List<String> setTimeList(String item){

        //문자열->시간 변환
        List<String> alarmTimeList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time;
        time  = LocalTime.parse(item, formatter);
        
        for(int i=4;i>=0;i--){
            LocalTime setTime = time.minusMinutes(i);
            //시간->문자열 변환
            String alarmTime = setTime.format(formatter);
            alarmTimeList.add(alarmTime);
        }
        return alarmTimeList;
    }
    
    // 첫 역 출발시간, 환승역의 출발 시간에 대한 리스트 (알람 필터 전)
    public List<String> getPointTimeList(List<String> totalTimeList, List<Integer> totalLineList) {
        List<String> pointTimeList = new ArrayList<>();
        int comp = totalLineList.get(1);
        pointTimeList.add(totalTimeList.get(0));
        for (int i=1;i<totalLineList.size();i=i+2){
            int getLine = totalLineList.get(i);
            if ( getLine ==0 ){
                break;
            }
            if (comp != getLine){
                pointTimeList.add(totalTimeList.get(i-1));

            }
            comp = getLine;
        }
        return pointTimeList;
    }

}
