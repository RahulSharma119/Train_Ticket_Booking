package com.train.booking.service;

import com.train.booking.dto.BookingRequest;
import com.train.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);

    BookingResponse getBookingByPnr(String pnr);

    List<BookingResponse> getBookingsByUserId(Long userId);

    void updatePaymentStatus(Long bookingId, String paymentStatus);
}
