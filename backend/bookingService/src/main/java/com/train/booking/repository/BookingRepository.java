package com.train.booking.repository;

import com.train.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    Optional<Booking> findByPnr(String pnr);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) FROM Booking b JOIN b.passengers p WHERE b.trainId = :trainId AND b.travelDate = :travelDate AND b.status IN :statuses")
    Integer countBookedSeats(@org.springframework.data.repository.query.Param("trainId") Long trainId,
            @org.springframework.data.repository.query.Param("travelDate") java.time.LocalDate travelDate,
            @org.springframework.data.repository.query.Param("statuses") List<com.train.booking.enums.BookingStatus> statuses);
}
