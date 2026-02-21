package com.train.trains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.train.trains.model.TrainStation;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainStationRepository extends JpaRepository<TrainStation, Long> {
    List<TrainStation> findByTrainId(Long trainId);

    List<TrainStation> findByStationId(Long stationId);

    Optional<TrainStation> findByTrainIdAndStationId(Long trainId, Long stationId);
}
