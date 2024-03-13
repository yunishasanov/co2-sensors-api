package com.company.co2sensorsapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

class SensorServiceTest {

  @Mock
  private ApplicationConfig applicationConfig;

  @Mock
  private SensorRepository sensorRepository;

  @Mock
  private MeasurementRepository measurementRepository;

  @Spy
  @InjectMocks
  private SensorService sensorService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getSensorStatus_ExistingSensor_ReturnsStatusResponse() {
    UUID uuid = UUID.randomUUID();
    Sensor sensor = new Sensor();
    sensor.setSensorStatus(SensorStatus.OK);
    when(sensorRepository.findBySensorUuid(uuid)).thenReturn(sensor);

    SensorStatusResponse response = sensorService.getSensorStatus(uuid);

    assertEquals(SensorStatus.OK.name(), response.getStatus());
  }

  @Test
  void getSensorStatus_NonExistingSensor_ThrowsNotFoundException() {
    UUID uuid = UUID.fromString("3c7c0616-e359-474e-a71b-821072fff3ac");
    when(sensorRepository.findBySensorUuid(uuid)).thenReturn(null);
    Assertions.assertThatExceptionOfType(NotFoundException.class)
        .isThrownBy(() -> sensorService.getSensorStatus(uuid))
        .withMessage("Resurce not found with uuid: 3c7c0616-e359-474e-a71b-821072fff3ac");
  }

  @Test
  void getSensorMetrics_ExistingMeasurements_ReturnsMetricsResponse() {
    UUID sensorUuid = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    MaxAvgCo2Projection maxAvgCo2 = new MaxAvgCo2Projection(3000, 2000.0);

    when(applicationConfig.getAvgAndMaxInDays()).thenReturn(30);
    when(measurementRepository.findMaxAvgCo2BySensorUuidAndCreatedAtAfter(
        any(), any())).thenReturn(maxAvgCo2);

    SensorMetricsResponse response = sensorService.getSensorMetrics(sensorUuid);

    assertEquals(3000, response.getMaxLast30Days());
    assertEquals(2000, response.getAvgLast30Days());
  }

  @Test
  void getSensorMetrics_NoMeasurements_ThrowsNotFoundException() {
    UUID sensorUuid = UUID.fromString("3c7c0616-e359-474e-a71b-821072fff3ac");

    when(applicationConfig.getAvgAndMaxInDays()).thenReturn(30);
    when(measurementRepository.findMaxAvgCo2BySensorUuidAndCreatedAtAfter(any(), any()))
        .thenReturn(new MaxAvgCo2Projection());
    Assertions.assertThatExceptionOfType(NotFoundException.class)
        .isThrownBy(() -> sensorService.getSensorMetrics(sensorUuid))
        .withMessage("No records found for uuid: 3c7c0616-e359-474e-a71b-821072fff3ac");
  }


  @Test
  void addMeasurement_ExistingSensor_SuccessfullyAdded() {
    UUID sensorUuid = UUID.fromString("d5499212-647f-477a-b888-575ccbed23d8");
    Sensor existingSensor = new Sensor();
    existingSensor.setSensorUuid(sensorUuid);
    existingSensor.setSensorStatus(SensorStatus.OK);

    SensorMeasurementRequest request = new SensorMeasurementRequest();
    request.setCo2(1000);
    request.setTime(OffsetDateTime.now());

    when(sensorRepository.findBySensorUuid(sensorUuid)).thenReturn(existingSensor);
    when(applicationConfig.getTopLimit()).thenReturn(3);
    when(applicationConfig.getCo2Limit()).thenReturn(2000);

    sensorService.addMeasurement(sensorUuid, request);

    verify(sensorRepository, times(0)).save(any());
    verify(measurementRepository, times(1)).save(any(Measurement.class));
  }

  @Test
  void addMeasurement_NewSensor_SuccessfullyAdded() {
    UUID sensorUuid = UUID.fromString("d5499212-647f-477a-b888-575ccbed23d8");
    Sensor newSensor = new Sensor();
    newSensor.setSensorUuid(sensorUuid);
    newSensor.setSensorStatus(SensorStatus.OK);

    SensorMeasurementRequest request = new SensorMeasurementRequest();
    request.setCo2(1000);
    request.setTime(OffsetDateTime.now());

    when(sensorRepository.findBySensorUuid(sensorUuid)).thenReturn(null);
    when(applicationConfig.getTopLimit()).thenReturn(3);
    when(applicationConfig.getCo2Limit()).thenReturn(2000);
    when(sensorRepository.save(any(Sensor.class))).thenReturn(newSensor);

    sensorService.addMeasurement(sensorUuid, request);

    verify(sensorRepository, times(1)).save(any(Sensor.class));
    verify(measurementRepository, times(1)).save(any(Measurement.class));
  }

}
