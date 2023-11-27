package com.main.station.service;

import com.main.station.dto.StationTimeDTO;
import com.main.station.entity.StationEntity;
import com.main.station.entity.StationTimeEntity;
import com.main.station.repository.StationTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StationTimeService {

    private final StationTimeRepository stationTimeRepository;

    public List<StationTimeDTO> getTimeInfo(int station) {
        //반환할 컬렉션
        List<StationTimeDTO> resultTimeDTOList = new ArrayList<>();;

        List<StationTimeEntity> stationTimeEntityList = stationTimeRepository.findAll();
        List<StationTimeDTO> stationTimeList = findTime(stationTimeEntityList);
        for(StationTimeDTO stationTimeDTO : stationTimeList){
            if (stationTimeDTO.getStation_id() == station) {
                resultTimeDTOList.add(stationTimeDTO);
            }
        }
        return resultTimeDTOList;
    }




    public List<StationTimeDTO> findTime(List<StationTimeEntity> stationTimeEntityList) {
        List<StationTimeDTO> stationTimeDTOList = new ArrayList<>();
        for(StationTimeEntity stationTimeEntity : stationTimeEntityList) {
            stationTimeDTOList.add(StationTimeDTO.toStationDTO(stationTimeEntity));
        }
        return stationTimeDTOList;
    }
}
