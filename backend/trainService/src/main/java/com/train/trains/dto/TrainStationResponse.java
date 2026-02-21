package com.train.trains.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class TrainStationResponse {
    private Long trainStationId;
    private Long stationId;
    private String stationName;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
}
