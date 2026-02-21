package com.train.trains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.train.trains.model.Train;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
}
