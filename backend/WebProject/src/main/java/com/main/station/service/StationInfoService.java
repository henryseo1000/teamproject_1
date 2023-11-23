package com.main.station.service;
import com.main.station.dto.SpecificStation;
import com.main.station.dto.StationDTO;
import com.main.station.entity.StationEntity;
import com.main.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        List<StationDTO> stationDTOList = new ArrayList<>();
        for (StationEntity stationEntity : stationEntityList) {
            stationDTOList.add(StationDTO.toStationDTOLine(stationEntity));
        }

        for(StationDTO stationDTO : stationDTOList){
            item  = new ArrayList<>();
            int comPStart = stationDTO.getStart();
            int comPEnd = stationDTO.getEnd();
            if (comPStart == station){
                item.add(comPEnd);
                item.add(stationDTO.getLine());
                stationInfoList.add(item);
            }
            else if (comPEnd == station) {
                item.add(comPStart);
                item.add(stationDTO.getLine());
                stationInfoList.add(item);
            }
        }
        specificStation.setSurroundStationList(stationInfoList);
        return specificStation;
    }
}
