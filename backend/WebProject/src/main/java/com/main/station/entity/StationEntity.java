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

}
