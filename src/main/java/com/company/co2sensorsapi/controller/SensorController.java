package com.company.co2sensorsapi.controller;

import com.company.co2sensorsapi.dto.SensorMeasurementRequest;
import com.company.co2sensorsapi.dto.SensorMetricsResponse;
import com.company.co2sensorsapi.dto.SensorStatusResponse;
import com.company.co2sensorsapi.exception.InvalidUuidException;
import com.company.co2sensorsapi.service.SensorService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SensorController {

  private final SensorService sensorService;

  @GetMapping("/sensors/{uuid}")
  public ResponseEntity<SensorStatusResponse> getSensorStatus(@PathVariable("uuid") String uuid) {

    return new ResponseEntity<>(sensorService.getSensorStatus(checkUuid(uuid)),
        HttpStatus.OK);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping("/sensors/{uuid}/measurements")
  public void setMeasurements(@PathVariable("uuid") String uuid, @RequestBody
  SensorMeasurementRequest request) {
    sensorService.addMeasurement(checkUuid(uuid), request);

    System.out.println(request);
  }


  @GetMapping("/sensors/{uuid}/metrics")
  public ResponseEntity<SensorMetricsResponse> getSensorMetrics(@PathVariable("uuid") String uuid) {

    return new ResponseEntity<>(sensorService.getSensorMetrics(checkUuid(uuid)),
        HttpStatus.OK);
  }

  private UUID checkUuid(String StrUuid) {
    try {
      return UUID.fromString(StrUuid);
    } catch (IllegalArgumentException exc) {
      throw new InvalidUuidException("Invalid UUID format for parameter sensor uuid.");
    }
  }

}
