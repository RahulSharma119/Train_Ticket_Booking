package com.train.payment.model;

import com.train.payment.enums.PaymentMethod;
import com.train.payment.enums.PaymentStatus;
import com.train.payment.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    // Optional identifier for simulated gateway response
    private String transactionId;

    @Column(nullable = false)
    private String currency;

    private String failureMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus = RefundStatus.NONE;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }
}
