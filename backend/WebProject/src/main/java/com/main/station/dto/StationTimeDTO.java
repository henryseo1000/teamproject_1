package com.main.station.dto;
import com.main.station.entity.StationTimeEntity;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StationTimeDTO {
    private int time_id;
    private int station_id;
    private int direction;
    private String start_time;

    public static StationTimeDTO toStationDTO(StationTimeEntity stationTimeEntity) {
        StationTimeDTO stationTimeDTO = new StationTimeDTO();
        stationTimeDTO.setStation_id(stationTimeEntity.getStation_id());
        stationTimeDTO.setTime_id(stationTimeEntity.getTime_id());
        stationTimeDTO.setDirection(stationTimeEntity.getDirection());
        stationTimeDTO.setStart_time(stationTimeEntity.getStart_time());

        return stationTimeDTO;
    }
}
