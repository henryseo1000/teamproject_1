package com.main.station.service;
import com.main.station.dto.SpecificStation;
import com.main.station.dto.StationDTO;
import com.main.station.entity.StationEntity;
import com.main.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StationInfoService {

    private final StationRepository stationRepository;

    public SpecificStation getInfo(int station) {
        SpecificStation specificStation = new SpecificStation();
        List<StationEntity> stationEntityList = stationRepository.findAll();
        //겉 리스트
        List<List<Integer>> stationInfoList = new ArrayList<>();

        //속 리스트
        List<Integer> item;

        //임시로 라인을 저장한 set
        Set<Integer> tmpLineList = new HashSet<>();

        //DB에서 호선정보 끌고옴
        List<StationDTO> stationDTOList = new ArrayList<>();
        for (StationEntity stationEntity : stationEntityList) {
            stationDTOList.add(StationDTO.toStationDTOLine(stationEntity));
        }

        //DB에서 끌고온 DTOLIST하나씩 필터링
        for(StationDTO stationDTO : stationDTOList){
            item  = new ArrayList<>();
            int comPStart = stationDTO.getStart();
            int comPEnd = stationDTO.getEnd();
            int line = stationDTO.getLine();
            //다음역을 저장한다.
            if (comPStart == station){
                item.add(comPEnd);
                item.add(line);
                stationInfoList.add(item);
                tmpLineList.add(line);
            }
            //이전역을 저장한다.
            else if (comPEnd == station) {
                item.add(comPStart);
                item.add(stationDTO.getLine());
                stationInfoList.add(item);
                tmpLineList.add(line);
            }
        }
        List<Integer> lineList = new ArrayList<>(tmpLineList);
        stationInfoList = updateSurroundStation(stationInfoList, station);
        specificStation.setSurroundStationList(stationInfoList);
        specificStation.setLineList(lineList);
        specificStation.setNowStation(station);
        return specificStation;
    }

    public List<List<Integer>> updateSurroundStation(List<List<Integer>> stationInfoList, int selectStation){

        //이전역/다음역이 1개인 경우 0을 추가하는 컬렉션
        List<Integer> addList = new ArrayList<>();

        int line1 = stationInfoList.get(0).get(1); int line2 = 0;
        int count1=1, count2=0;
        //이전역/다음역의 수가 1인 역을 필터링함
        for(List<Integer> infoList : stationInfoList){
            int compLine = infoList.get(1);
            if (line1 != compLine ){
                line2 = compLine;
                count2++;
            } else {
                count1++;
            }
        }
        if (count1==1){
            addList.add(0);
            addList.add(line1);
            stationInfoList.add(2,addList);
        } else if( count2 == 1){
            addList.add(0);
            addList.add(line2);
            stationInfoList.add(2,addList);
        }

        return stationInfoList;
    }
}
