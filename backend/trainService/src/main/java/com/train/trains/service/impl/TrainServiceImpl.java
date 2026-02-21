package com.train.trains.service.impl;

import com.train.trains.dto.*;
import com.train.trains.model.Station;
import com.train.trains.model.Train;
import com.train.trains.model.TrainStation;
import com.train.trains.repository.StationRepository;
import com.train.trains.repository.TrainRepository;
import com.train.trains.repository.TrainStationRepository;
import com.train.trains.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final TrainStationRepository trainStationRepository;

    @Override
    public StationResponse addStation(StationRequest request) {
        Station station = new Station();
        station.setName(request.getName());
        station.setNumberOfPlatforms(request.getNumberOfPlatforms());
        Station saved = stationRepository.save(station);
        return mapToStationResponse(saved);
    }

    @Override
    public Page<StationResponse> getAllStations(int page, int size) {
        return stationRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToStationResponse);
    }

    @Override
    public TrainResponse addTrain(TrainRequest request) {
        Train train = new Train();
        train.setName(request.getName());
        train.setStartStationId(request.getStartStationId());
        train.setEndStationId(request.getEndStationId());
        train.setNumberOfSeats(request.getNumberOfSeats());
        Train saved = trainRepository.save(train);
        return getTrainById(saved.getTrainId());
    }

    @Override
    public TrainResponse getTrainById(Long id) {
        Train train = trainRepository.findById(id).orElseThrow(() -> new RuntimeException("Train not found"));
        return mapToTrainResponse(train);
    }

    @Override
    public Page<TrainResponse> getAllTrains(int page, int size) {
        return trainRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToTrainResponse);
    }

    @Override
    public TrainStationResponse addTrainStation(TrainStationRequest request) {
        Optional<TrainStation> existingOpt = trainStationRepository.findByTrainIdAndStationId(request.getTrainId(),
                request.getStationId());

        TrainStation ts;
        if (existingOpt.isPresent()) {
            ts = existingOpt.get();
        } else {
            ts = new TrainStation();
            ts.setStationId(request.getStationId());
            ts.setTrainId(request.getTrainId());
        }

        ts.setArrivalTime(request.getArrivalTime());
        ts.setDepartureTime(request.getDepartureTime());

        TrainStation saved = trainStationRepository.save(ts);
        return mapToTrainStationResponse(saved);
    }

    @Override
    public List<TrainStationResponse> getTrainSchedule(Long trainId) {
        return trainStationRepository.findByTrainId(trainId).stream()
                .map(this::mapToTrainStationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeTrainStation(Long trainStationId) {
        trainStationRepository.deleteById(trainStationId);
    }

    private StationResponse mapToStationResponse(Station station) {
        StationResponse response = new StationResponse();
        response.setStationId(station.getStationId());
        response.setName(station.getName());
        response.setNumberOfPlatforms(station.getNumberOfPlatforms());
        return response;
    }

    private TrainResponse mapToTrainResponse(Train train) {
        TrainResponse response = new TrainResponse();
        response.setTrainId(train.getTrainId());
        response.setName(train.getName());
        response.setStartStationId(train.getStartStationId());
        response.setEndStationId(train.getEndStationId());
        response.setNumberOfSeats(train.getNumberOfSeats());

        // Get Station Names
        Optional<Station> startOpt = stationRepository.findById(train.getStartStationId());
        startOpt.ifPresent(station -> response.setStartStationName(station.getName()));

        Optional<Station> endOpt = stationRepository.findById(train.getEndStationId());
        endOpt.ifPresent(station -> response.setEndStationName(station.getName()));

        // Get Schedule
        response.setSchedule(getTrainSchedule(train.getTrainId()));
        return response;
    }

    private TrainStationResponse mapToTrainStationResponse(TrainStation ts) {
        TrainStationResponse response = new TrainStationResponse();
        response.setTrainStationId(ts.getTrainStationId());
        response.setStationId(ts.getStationId());
        response.setArrivalTime(ts.getArrivalTime());
        response.setDepartureTime(ts.getDepartureTime());

        Optional<Station> stationOpt = stationRepository.findById(ts.getStationId());
        stationOpt.ifPresent(station -> response.setStationName(station.getName()));

        return response;
    }
}
