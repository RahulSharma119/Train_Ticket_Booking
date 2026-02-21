package com.train.trains.dto;

import lombok.Data;

@Data
public class StationRequest {
    private String name;
    private Integer numberOfPlatforms;
}
