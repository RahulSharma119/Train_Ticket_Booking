package com.train.booking.dto;

import lombok.Data;

@Data
public class PassengerDto {
    private String name;
    private Integer age;
    private String gender;
    private String berthPreference;
}
