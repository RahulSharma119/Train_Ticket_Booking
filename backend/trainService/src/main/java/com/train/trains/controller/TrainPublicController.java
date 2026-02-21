package com.train.trains.controller;

import com.train.trains.dto.*;
import com.train.trains.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
@RequiredArgsConstructor
public class TrainPublicController {

    private final TrainService trainService;

    @GetMapping("/stations")
    public ResponseEntity<Page<StationResponse>> getAllStations(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(trainService.getAllStations(page, size));
    }

    @GetMapping
    public ResponseEntity<Page<TrainResponse>> getAllTrains(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(trainService.getAllTrains(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainResponse> getTrainById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(trainService.getTrainById(id));
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<TrainStationResponse>> getTrainSchedule(@PathVariable("id") Long id) {
        return ResponseEntity.ok(trainService.getTrainSchedule(id));
    }
}
