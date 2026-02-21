package com.train.booking.service.impl;

import com.train.booking.dto.BookingRequest;
import com.train.booking.dto.BookingResponse;
import com.train.booking.dto.PaymentDto;
import com.train.booking.dto.PaymentRequestDto;
import com.train.booking.dto.PassengerDto;
import com.train.booking.dto.TrainResponseDto;
import com.train.booking.enums.BookingStatus;
import com.train.booking.model.Booking;
import com.train.booking.model.Passenger;
import com.train.booking.repository.BookingRepository;
import com.train.booking.service.BookingService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${train.service.url}")
    private String trainServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        // 1. Verify train exists and check basic capacity via TrainService
        try {
            TrainResponseDto train = webClientBuilder.build().get()
                    .uri(trainServiceUrl + "/api/trains/" + request.getTrainId())
                    .retrieve()
                    .bodyToMono(TrainResponseDto.class)
                    .block(); // Blocking for simplicity, standard approach in simple microservices

            if (train == null) {
                throw new RuntimeException("Train not found");
            }

            // Check availability
            List<BookingStatus> activeStatuses = List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING);
            Integer bookedSeats = bookingRepository.countBookedSeats(request.getTrainId(), request.getTravelDate(),
                    activeStatuses);
            int availableSeats = train.getNumberOfSeats() - (bookedSeats != null ? bookedSeats : 0);

            if (request.getPassengers().size() > availableSeats) {
                throw new RuntimeException("Not enough seats available. Requested: " + request.getPassengers().size()
                        + ", Available: " + availableSeats);
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify train availability: " + e.getMessage());
        }

        // 2. Create Booking
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setTrainId(request.getTrainId());
        booking.setRouteId(request.getRouteId());
        booking.setTravelDate(request.getTravelDate());

        // Mock pricing logic: e.g. $50 per passenger
        double pricePerPassenger = 50.0;
        booking.setTotalAmount(request.getPassengers().size() * pricePerPassenger);

        booking.setStatus(BookingStatus.PENDING);

        // 3. Add Passengers
        for (PassengerDto pDto : request.getPassengers()) {
            Passenger passenger = new Passenger();
            passenger.setName(pDto.getName());
            passenger.setAge(pDto.getAge());
            passenger.setGender(pDto.getGender());
            passenger.setBerthPreference(pDto.getBerthPreference());
            booking.addPassenger(passenger);
        }

        // 4. Save to DB
        Booking savedBooking = bookingRepository.save(booking);

        // 5. Initiate Payment
        PaymentDto paymentResponse = null;
        try {
            PaymentRequestDto paymentRequest = new PaymentRequestDto();
            paymentRequest.setBookingId(savedBooking.getBookingId());
            paymentRequest.setAmount(savedBooking.getTotalAmount());
            // Default to CREDIT_CARD if not provided
            paymentRequest
                    .setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CREDIT_CARD");

            paymentResponse = webClientBuilder.build().post()
                    .uri(paymentServiceUrl + "/api/payments")
                    .bodyValue(paymentRequest)
                    .retrieve()
                    .bodyToMono(PaymentDto.class)
                    .block();
        } catch (Exception e) {
            System.err.println(
                    "Failed to initiate payment for booking " + savedBooking.getBookingId() + ": " + e.getMessage());
        }

        BookingResponse response = mapToResponse(savedBooking);
        response.setPaymentDetails(paymentResponse);
        return response;
    }

    @Override
    public BookingResponse getBookingByPnr(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new RuntimeException("Booking not found with PNR: " + pnr));
        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updatePaymentStatus(Long bookingId, String paymentStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
            booking.setStatus(BookingStatus.CONFIRMED);

            if (booking.getPnr() == null) {
                // Generate a random unique PNR
                booking.setPnr("PNR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

                // Assign a mock seat number for each passenger
                int seatCounter = 1;
                for (Passenger passenger : booking.getPassengers()) {
                    passenger.setSeatNumber("B1-" + seatCounter++);
                }
            }
        } else if ("FAILED".equalsIgnoreCase(paymentStatus)) {
            booking.setStatus(BookingStatus.CANCELLED);
        }

        bookingRepository.save(booking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setUserId(booking.getUserId());
        response.setTrainId(booking.getTrainId());
        response.setRouteId(booking.getRouteId());
        response.setTravelDate(booking.getTravelDate());
        response.setTotalAmount(booking.getTotalAmount());
        response.setStatus(booking.getStatus());
        response.setPnr(booking.getPnr());
        response.setBookingDate(booking.getBookingDate());

        List<PassengerDto> passengerDtos = booking.getPassengers().stream().map(p -> {
            PassengerDto dto = new PassengerDto();
            dto.setName(p.getName());
            dto.setAge(p.getAge());
            dto.setGender(p.getGender());
            dto.setBerthPreference(p.getBerthPreference());
            return dto;
        }).collect(Collectors.toList());

        response.setPassengers(passengerDtos);

        return response;
    }
}
