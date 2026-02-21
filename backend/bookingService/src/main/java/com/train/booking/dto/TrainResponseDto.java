package com.train.booking.dto;

import lombok.Data;

@Data
public class TrainResponseDto {
    private Long trainId;
    private String name;
    private Integer numberOfSeats;
}
