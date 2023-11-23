package com.main.station.dto;

import com.main.station.entity.StationEntity;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StationDTO {
    private Long id;
    private int start;
    private int end;
    private int time;
    private int distance;
    private int expense;
    private int line;

    private int result;
    private LinkedList<Integer> shortestPath;
    private List<String> shortestTime;

    public static StationDTO toStationDTOTime(StationEntity stationEntity) {
        StationDTO stationDTO = new StationDTO();
        stationDTO.setId(stationEntity.getId());
        stationDTO.setEnd(stationEntity.getEnd());
        stationDTO.setStart(stationEntity.getStart());
        stationDTO.setTime(stationEntity.getTime());
        stationDTO.setLine(stationEntity.getLine());
        return stationDTO;
    }

    public static StationDTO toStationDTODistance(StationEntity stationEntity) {
        StationDTO stationDTO = new StationDTO();
        stationDTO.setId(stationEntity.getId());
        stationDTO.setEnd(stationEntity.getEnd());
        stationDTO.setStart(stationEntity.getStart());
        stationDTO.setDistance(stationEntity.getDistance());
        stationDTO.setLine(stationEntity.getLine());
        return stationDTO;
    }

    public static StationDTO toStationDTOExpense(StationEntity stationEntity) {
        StationDTO stationDTO = new StationDTO();
        stationDTO.setId(stationEntity.getId());
        stationDTO.setEnd(stationEntity.getEnd());
        stationDTO.setStart(stationEntity.getStart());
        stationDTO.setExpense(stationEntity.getExpense());
        stationDTO.setLine(stationEntity.getLine());
        return stationDTO;
    }

    public static StationDTO toStationDTOLine(StationEntity stationEntity) {
        StationDTO stationDTO = new StationDTO();
        stationDTO.setId(stationEntity.getId());
        stationDTO.setEnd(stationEntity.getEnd());
        stationDTO.setStart(stationEntity.getStart());
        stationDTO.setLine(stationEntity.getLine());
        return stationDTO;
    }

    public static StationDTO setResult(int start, int end, int result, LinkedList<Integer> shortestPath, List<String> shortestTime){
        StationDTO stationDTO = new StationDTO();
        stationDTO.setStart(start);
        stationDTO.setEnd(end);
        stationDTO.setResult(result);
        stationDTO.setShortestPath(shortestPath);
        stationDTO.setShortestTime(shortestTime);
        return stationDTO;
    }


}
