package com.company.co2sensorsapi.service;

import com.company.co2sensorsapi.common.SensorStatus;
import com.company.co2sensorsapi.configuration.ApplicationConfig;
import com.company.co2sensorsapi.dto.MaxAvgCo2Projection;
import com.company.co2sensorsapi.dto.SensorMeasurementRequest;
import com.company.co2sensorsapi.dto.SensorMetricsResponse;
import com.company.co2sensorsapi.dto.SensorStatusResponse;
import com.company.co2sensorsapi.entity.Measurement;
import com.company.co2sensorsapi.entity.Sensor;
import com.company.co2sensorsapi.exception.NotFoundException;
import com.company.co2sensorsapi.repository.MeasurementRepository;
import com.company.co2sensorsapi.repository.SensorRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SensorService {

  private final ApplicationConfig applicationConfig;
  private final SensorRepository sensorRepository;
  private final MeasurementRepository measurementRepository;


  public SensorStatusResponse getSensorStatus(UUID uuid) {
    Sensor sensor = sensorRepository.findBySensorUuid(uuid);
    if (Objects.isNull(sensor)) {
      throw new NotFoundException("Resurce not found with uuid: " + uuid);
    }
    return new SensorStatusResponse(sensor.getSensorStatus().name());
  }

  public SensorMetricsResponse getSensorMetrics(UUID sensorUuid) {
    LocalDateTime from = LocalDateTime.now().minusDays(applicationConfig.getAvgAndMaxInDays());
    MaxAvgCo2Projection maxAvgCo2 = measurementRepository
        .findMaxAvgCo2BySensorUuidAndCreatedAtAfter(sensorUuid, from);

    if (Objects.isNull(maxAvgCo2.getMaxCo2())
        || Objects.isNull(maxAvgCo2.getAvgCo2())) {
      throw new NotFoundException("No records found for uuid: " + sensorUuid);
    }

    return new SensorMetricsResponse(maxAvgCo2.getMaxCo2(),
        (int) Math.round(maxAvgCo2.getAvgCo2()));
  }

  @Transactional
  public void addMeasurement(UUID sensorUuid, SensorMeasurementRequest request) {
    Sensor sensor = sensorRepository.findBySensorUuid(sensorUuid);
    if (Objects.isNull(sensor)) {
      sensor = createNewSensor(sensorUuid);
    }
    Measurement measurement = Measurement.builder().sensorUuid(sensor.getSensorUuid())
        .co2(request.getCo2()).createdAt(request.getTime().toLocalDateTime()).build();
    measurementRepository.save(measurement);

    Pageable pageable = PageRequest.of(0, applicationConfig.getTopLimit());
    List<Measurement> measurements = measurementRepository.findAllBySensorUuidOrderByCreatedAtDesc(
        sensorUuid, pageable);
    SensorStatus newStatus = calculateNewStatus(sensor.getSensorStatus(), measurements, request);

    if (!newStatus.equals(sensor.getSensorStatus())) {
      sensor.setSensorStatus(newStatus);
      sensorRepository.save(sensor);
    }

  }

  private SensorStatus calculateNewStatus(SensorStatus currentStatus,
      List<Measurement> measurements,
      SensorMeasurementRequest request) {
    int aboveLimitCounter = 0;
    int belowLimitCounter = 0;
    for (Measurement item : measurements) {
      if (item.getCo2() >= applicationConfig.getCo2Limit()) {
        aboveLimitCounter++;
      } else {
        belowLimitCounter++;
      }
    }

    SensorStatus newStatus =
        (request.getCo2() >= applicationConfig.getCo2Limit()
            && !SensorStatus.ALERT.equals(currentStatus)) ? SensorStatus.WARN : currentStatus;
    if (aboveLimitCounter == applicationConfig.getTopLimit()) {
      newStatus = SensorStatus.ALERT;
    } else if (belowLimitCounter == applicationConfig.getTopLimit()) {
      newStatus = SensorStatus.OK;
    }
    return newStatus;
  }

  private Sensor createNewSensor(UUID sensorUuid) {
    Sensor sensor = Sensor.builder().sensorUuid(sensorUuid).sensorStatus(SensorStatus.OK)
        .createdAt(LocalDateTime.now()).build();
    sensor = sensorRepository.save(sensor);
    return sensor;
  }


}
