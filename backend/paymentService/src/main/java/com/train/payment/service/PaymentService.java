package com.train.payment.service;

import com.train.payment.dto.PaymentRequest;
import com.train.payment.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    List<PaymentResponse> getPaymentsForBooking(Long bookingId);

    void handleStripeWebhook(String payload, String sigHeader);
}
