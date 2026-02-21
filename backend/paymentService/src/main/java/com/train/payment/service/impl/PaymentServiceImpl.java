package com.train.payment.service.impl;

import com.train.payment.dto.PaymentRequest;
import com.train.payment.dto.PaymentResponse;
import com.train.payment.enums.PaymentStatus;
import com.train.payment.enums.RefundStatus;
import com.train.payment.model.Payment;
import com.train.payment.repository.PaymentRepository;
import com.train.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.param.PaymentIntentCreateParams;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${payment.actual.enabled:false}")
    private boolean isActualPaymentEnabled;

    @Value("${payment.stripe.api-key:}")
    private String stripeApiKey;

    @Value("${payment.stripe.webhook-secret:}")
    private String stripeWebhookSecret;

    @Value("${payment.currency:usd}")
    private String currency;

    @Value("${payment.notification.urls:}")
    private List<String> notificationUrls;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        String transactionId = null;

        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(currency);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setRefundStatus(RefundStatus.NONE);

        if (isActualPaymentEnabled) {
            // Actual Payment using Stripe
            Stripe.apiKey = stripeApiKey;
            try {
                // Stripe expects the amount in cents
                long amountInCents = (long) (request.getAmount() * 100);

                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency(currency)
                        .setDescription("Train Ticket Booking ID: " + request.getBookingId())
                        .build();

                PaymentIntent intent = PaymentIntent.create(params);

                // Assuming successful creation means success for this backend mock
                // (In a full Stripe flow, you confirm on the frontend first)
                transactionId = intent.getId();
            } catch (Exception e) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setFailureMessage(e.getMessage());
                System.err.println("Stripe payment failed: " + e.getMessage());
            }
        } else {
            // Mock payment processing logic -> 90% success rate
            transactionId = UUID.randomUUID().toString();
            boolean isSuccess = Math.random() > 0.1;
            payment.setPaymentStatus(isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        }

        if (transactionId != null) {
            payment.setTransactionId(transactionId);
        }

        Payment savedPayment = paymentRepository.save(payment);

        // Notify Booking Service if transaction is finalized
        if (savedPayment.getPaymentStatus() == PaymentStatus.SUCCESS
                || savedPayment.getPaymentStatus() == PaymentStatus.FAILED) {
            notifyService(savedPayment);
        }

        return mapToResponse(savedPayment);
    }

    @Override
    public void handleStripeWebhook(String payload, String sigHeader) {
        try {
            Event event = com.stripe.net.Webhook.constructEvent(
                    payload, sigHeader, stripeWebhookSecret);

            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

            if ("payment_intent.succeeded".equals(event.getType())) {
                if (stripeObject instanceof PaymentIntent) {
                    PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                    String transactionId = paymentIntent.getId();

                    paymentRepository.findByTransactionId(transactionId).ifPresent(payment -> {
                        payment.setPaymentStatus(PaymentStatus.SUCCESS);
                        Payment savedPayment = paymentRepository.save(payment);
                        notifyService(savedPayment);
                    });
                }
            } else if ("payment_intent.payment_failed".equals(event.getType())) {
                if (stripeObject instanceof PaymentIntent) {
                    PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                    String transactionId = paymentIntent.getId();
                    String failureMsg = "Payment failed";
                    if (paymentIntent.getLastPaymentError() != null) {
                        failureMsg = paymentIntent.getLastPaymentError().getMessage();
                    }
                    final String finalFailureMsg = failureMsg;

                    paymentRepository.findByTransactionId(transactionId).ifPresent(payment -> {
                        payment.setPaymentStatus(PaymentStatus.FAILED);
                        payment.setFailureMessage(finalFailureMsg);
                        Payment savedPayment = paymentRepository.save(payment);
                        notifyService(savedPayment);
                    });
                }
            }
        } catch (com.stripe.exception.SignatureVerificationException e) {
            System.err.println("Invalid signature: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Webhook error: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsForBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setBookingId(payment.getBookingId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setTransactionDate(payment.getTransactionDate());
        response.setTransactionId(payment.getTransactionId());
        response.setCurrency(payment.getCurrency());
        response.setFailureMessage(payment.getFailureMessage());
        response.setRefundStatus(payment.getRefundStatus());
        return response;
    }

    private void notifyService(Payment payment) {
        if (notificationUrls == null || notificationUrls.isEmpty()) {
            return;
        }

        for (String url : notificationUrls) {
            try {
                webClientBuilder.build().put()
                        .uri(url)
                        .bodyValue(payment)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .subscribe(
                                success -> System.out.println(
                                        "Service notified successfully at: " + url),
                                error -> System.err
                                        .println("Failed to notify service at: " + url + " - " + error.getMessage()));
            } catch (Exception e) {
                System.err.println("Error triggering service update for " + url + ": " + e.getMessage());
            }
        }
    }
}
