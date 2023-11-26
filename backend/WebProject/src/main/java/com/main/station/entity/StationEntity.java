package com.main.station.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name="station_table")
@ToString
public class StationEntity {


    @Id
    private Long id;

    @Column
    private int start;

    @Column
    private int end;

    @Column
    private int time;

    @Column
    private int distance;

    @Column
    private int expense;

    @Column
    private int line;

}
