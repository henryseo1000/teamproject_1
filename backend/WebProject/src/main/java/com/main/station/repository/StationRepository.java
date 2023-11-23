package com.main.station.repository;

import com.main.station.entity.StationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface StationRepository extends JpaRepository<StationEntity, Long> {
}
