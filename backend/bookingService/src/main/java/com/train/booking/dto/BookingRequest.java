package com.train.booking.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingRequest {
    private Long userId;
    private Long trainId;
    private Long routeId;
    private LocalDate travelDate;
    private String paymentMethod;
    private List<PassengerDto> passengers;
}
