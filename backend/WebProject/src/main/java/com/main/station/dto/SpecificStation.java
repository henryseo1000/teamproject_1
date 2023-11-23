package com.main.station.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SpecificStation {
    private List<List<Integer>> surroundStationList;

}
