package com.main.station.repository;

import com.main.station.entity.StationTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationTimeRepository extends JpaRepository<StationTimeEntity, Integer> {
}
