package com.train.booking.dto;

import lombok.Data;

@Data
public class PaymentDto {
    private Long paymentId;
    private Long bookingId;
    private String paymentStatus;
    private String transactionId;
    private String currency;
    private String failureMessage;
    private String refundStatus;
}
