package com.train.trains.controller;

import com.train.trains.dto.*;
import com.train.trains.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/trains")
@RequiredArgsConstructor
public class TrainAdminController {

    private final TrainService trainService;

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> addStation(@RequestBody StationRequest request) {
        return new ResponseEntity<>(trainService.addStation(request), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<TrainResponse> addTrain(@RequestBody TrainRequest request) {
        return new ResponseEntity<>(trainService.addTrain(request), HttpStatus.CREATED);
    }

    @PostMapping("/schedule")
    public ResponseEntity<TrainStationResponse> addTrainStation(@RequestBody TrainStationRequest request) {
        return new ResponseEntity<>(trainService.addTrainStation(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/schedule/{id}")
    public ResponseEntity<Void> removeTrainStation(@PathVariable("id") Long id) {
        trainService.removeTrainStation(id);
        return ResponseEntity.noContent().build();
    }
}
