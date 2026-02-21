package com.train.trains.service;

import com.train.trains.dto.*;
import org.springframework.data.domain.Page;
import java.util.List;

public interface TrainService {
    StationResponse addStation(StationRequest request);

    Page<StationResponse> getAllStations(int page, int size);

    TrainResponse addTrain(TrainRequest request);

    TrainResponse getTrainById(Long id);

    Page<TrainResponse> getAllTrains(int page, int size);

    TrainStationResponse addTrainStation(TrainStationRequest request);

    List<TrainStationResponse> getTrainSchedule(Long trainId);

    void removeTrainStation(Long trainStationId);
}
