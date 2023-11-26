package com.main.station.repository;

import com.main.station.entity.StationEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StationRepository extends JpaRepository<StationEntity, Long> {

}
