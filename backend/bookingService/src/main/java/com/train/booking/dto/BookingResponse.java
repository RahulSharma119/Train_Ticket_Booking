package com.train.booking.dto;

import com.train.booking.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponse {
    private Long bookingId;
    private Long userId;
    private Long trainId;
    private Long routeId;
    private LocalDate travelDate;
    private Double totalAmount;
    private BookingStatus status;
    private String pnr;
    private LocalDateTime bookingDate;
    private PaymentDto paymentDetails;
    private List<PassengerDto> passengers;
}
