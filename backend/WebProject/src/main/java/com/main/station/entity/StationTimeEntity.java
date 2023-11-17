package com.main.station.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name="station_time_table")
public class StationTimeEntity {

    @Id
    private int time_id;

    @Column
    private int station_id;
    @Column
    private String direction;
    @Column
    private int next_line;
    @Column
    private String start_time;

}
