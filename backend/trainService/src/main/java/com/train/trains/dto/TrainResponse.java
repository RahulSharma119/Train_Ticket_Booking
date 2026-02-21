package com.train.trains.dto;

import lombok.Data;
import java.util.List;

@Data
public class TrainResponse {
    private Long trainId;
    private String name;
    private Long startStationId;
    private String startStationName;
    private Long endStationId;
    private String endStationName;
    private Integer numberOfSeats;
    private List<TrainStationResponse> schedule;
}
