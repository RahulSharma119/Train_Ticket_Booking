package com.train.payment.dto;

import com.train.payment.enums.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequest {
    private Long bookingId;
    private Double amount;
    private PaymentMethod paymentMethod;
}
