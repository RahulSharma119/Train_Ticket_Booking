package com.train.trains.dto;

import lombok.Data;

@Data
public class TrainRequest {
    private String name;
    private Long startStationId;
    private Long endStationId;
    private Integer numberOfSeats;
}
