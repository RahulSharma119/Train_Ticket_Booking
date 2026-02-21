package com.train.booking.controller;

import com.train.booking.dto.BookingRequest;
import com.train.booking.dto.BookingResponse;
import com.train.booking.dto.PaymentDto;
import com.train.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/pnr/{pnr}")
    public ResponseEntity<BookingResponse> getBookingByPnr(@PathVariable String pnr) {
        return ResponseEntity.ok(bookingService.getBookingByPnr(pnr));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    // Webhook from PaymentService
    @PutMapping("/payment-status")
    public ResponseEntity<Void> updatePaymentStatusUnified(@RequestBody PaymentDto paymentDto) {
        bookingService.updatePaymentStatus(paymentDto.getBookingId(), paymentDto.getPaymentStatus());
        return ResponseEntity.ok().build();
    }
}
