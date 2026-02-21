package com.train.payment.dto;

import com.train.payment.enums.PaymentMethod;
import com.train.payment.enums.PaymentStatus;
import com.train.payment.enums.RefundStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long paymentId;
    private Long bookingId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime transactionDate;
    private String transactionId;
    private String currency;
    private String failureMessage;
    private RefundStatus refundStatus;
}
