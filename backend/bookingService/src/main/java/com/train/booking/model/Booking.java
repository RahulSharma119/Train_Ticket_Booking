package com.train.booking.model;

import com.train.booking.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long trainId;

    @Column(nullable = false)
    private Long routeId;

    @Column(nullable = false)
    private LocalDate travelDate;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(unique = true)
    private String pnr; // Populated only after successful payment

    @Column(nullable = false, updatable = false)
    private LocalDateTime bookingDate;

    // One-to-many relationship with Passenger
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passenger> passengers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        bookingDate = LocalDateTime.now();
    }

    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
        passenger.setBooking(this);
    }

    public void removePassenger(Passenger passenger) {
        passengers.remove(passenger);
        passenger.setBooking(null);
    }
}
