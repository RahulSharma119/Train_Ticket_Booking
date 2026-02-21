package com.train.trains.dto;

import lombok.Data;

@Data
public class StationResponse {
    private Long stationId;
    private String name;
    private Integer numberOfPlatforms;
}
