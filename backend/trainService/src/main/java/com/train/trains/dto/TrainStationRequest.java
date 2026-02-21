package com.train.trains.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class TrainStationRequest {
    private Long stationId;
    private Long trainId;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
}
