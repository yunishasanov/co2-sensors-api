package com.company.co2sensorsapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.company.co2sensorsapi.common.SensorStatus;
import com.company.co2sensorsapi.dto.SensorMeasurementRequest;
import com.company.co2sensorsapi.entity.Sensor;
import com.company.co2sensorsapi.repository.MeasurementRepository;
import com.company.co2sensorsapi.repository.SensorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SensorControllerIntegrationTest {

  private final UUID sensorUuid = UUID.fromString("882a8619-72dd-459a-9831-4636d96b3c0c");
  private final LocalDateTime createdAt = LocalDateTime.now();
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private SensorRepository sensorRepository;

  @Autowired
  private MeasurementRepository measurementRepository;

  @BeforeEach
  void setup() {
    Sensor sensor = Sensor.builder().sensorUuid(sensorUuid)
        .sensorStatus(SensorStatus.OK).createdAt(createdAt).build();
    sensorRepository.save(sensor);
  }

  @AfterEach
  void cleanup() {
    sensorRepository.deleteAll();
    measurementRepository.deleteAll();
  }

  @Test
  public void testGetSensorStatus() throws Exception {
    mockMvc.perform(get("/api/v1/sensors/{uuid}", sensorUuid.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").exists())
        .andExpect(jsonPath("$.status").value(SensorStatus.OK.name()));
  }

  @Test
  public void testSetMeasurements() throws Exception {
    SensorMeasurementRequest request = new SensorMeasurementRequest();
    request.setCo2(2000);
    request.setTime(OffsetDateTime.now());

    mockMvc.perform(post("/api/v1/sensors/{uuid}/measurements", sensorUuid.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/sensors/{uuid}", sensorUuid.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").exists())
        .andExpect(jsonPath("$.status").value(SensorStatus.WARN.name()));
  }

  @Test
  public void testGetSensorMetrics() throws Exception {

    mockMvc.perform(post("/api/v1/sensors/{uuid}/measurements", sensorUuid.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new SensorMeasurementRequest(1000, OffsetDateTime.now()))))
        .andExpect(status().isNoContent());

    mockMvc.perform(post("/api/v1/sensors/{uuid}/measurements", sensorUuid.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new SensorMeasurementRequest(2000, OffsetDateTime.now()))))
        .andExpect(status().isNoContent());

    mockMvc.perform(post("/api/v1/sensors/{uuid}/measurements", sensorUuid.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new SensorMeasurementRequest(3000, OffsetDateTime.now()))))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/sensors/{uuid}/metrics", sensorUuid.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.avgLast30Days").exists())
        .andExpect(jsonPath("$.avgLast30Days").value(2000))
        .andExpect(jsonPath("$.maxLast30Days").exists())
        .andExpect(jsonPath("$.maxLast30Days").value(3000));
  }

  @Test
  public void testGetSensorMetricsNoMetricsFound() throws Exception {

    mockMvc.perform(get("/api/v1/sensors/{uuid}/metrics", sensorUuid.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").exists())
        .andExpect(jsonPath("$.code").value(404))
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.message").value(
            "No records found for uuid: 882a8619-72dd-459a-9831-4636d96b3c0c"));
  }

  @Test
  public void testGetSensorStatusInvalidUuid() throws Exception {
    String invalidUuid = "invalid-uuid";

    mockMvc.perform(get("/api/v1/sensors/{uuid}", invalidUuid))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").exists())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.message").value("Invalid UUID format for parameter sensor uuid."));
  }
}
