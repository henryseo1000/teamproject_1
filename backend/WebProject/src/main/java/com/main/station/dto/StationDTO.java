package com.main.station.dto;

import com.main.station.entity.StationEntity;
import lombok.*;

import java.util.LinkedList;

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

    private int result;
    private LinkedList<Integer> shortestPath;

    public static StationDTO toStationDTOTime(StationEntity stationEntity) {
        StationDTO stationDTO = new StationDTO();
        stationDTO.setId(stationEntity.getId());
        stationDTO.setEnd(stationEntity.getEnd());
        stationDTO.setStart(stationEntity.getStart());
        stationDTO.setTime(stationEntity.getTime());
        return stationDTO;
    }

    public static StationDTO setResult(int start, int end, int result, LinkedList<Integer> shortestPath){
        StationDTO stationDTO = new StationDTO();
        stationDTO.setStart(start);
        stationDTO.setEnd(end);
        stationDTO.setResult(result);
        stationDTO.setShortestPath(shortestPath);
        return stationDTO;
    }
}
