package com.train.trains.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainStationId;

    private Long stationId;

    private Long trainId;

    private LocalTime arrivalTime;

    private LocalTime departureTime;
}
