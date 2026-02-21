package com.train.trains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.train.trains.model.Station;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
}
