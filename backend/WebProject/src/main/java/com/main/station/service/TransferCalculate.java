package com.main.station.service;

import com.main.station.dto.StationDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class TransferCalculate {
    
    ///현재 각 역마다 호선을 배열에 넣어서 넘김
    private List<Integer> totalLineList;
    private List<Integer> transferStation;
    public List<Integer> getTransfer(LinkedList<Integer> shortestPath, List<StationDTO> stationDTOList) {
        totalLineList = new ArrayList<>();
        //노선 리스트로 변환
        List<Integer> list = new LinkedList<>(shortestPath);
        int index;
        for(index=0;index<list.size()-1;index++) {
            int start = list.get(index); int end = list.get(index + 1);
            for (StationDTO stationDTO : stationDTOList) {
                int compareA = stationDTO.getStart(); int compareB = stationDTO.getEnd();
                if ( ( compareA== start && compareB== end) || (compareA== end && compareB== start) ) {
                    totalLineList.add(start);
                    totalLineList.add(stationDTO.getLine());
                }
            }
        }
        totalLineList.add(list.get(index));
        totalLineList.add(0);
        return totalLineList;
    }

    //환승 횟수 세서 넘김
    public int getTransferCount(List<Integer> totalLineList) {
        int count=0;
        //01234567
        int comp = totalLineList.get(1);
        for (int i=1;i<totalLineList.size();i=i+2){
            int getLine = totalLineList.get(i);
            if ( getLine ==0 ){
                break;
            }
            if (comp != getLine){
                count++;
            }
            comp = getLine;
        }
        return count;
    }


}
