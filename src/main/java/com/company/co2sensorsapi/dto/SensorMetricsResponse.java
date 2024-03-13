package com.company.co2sensorsapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorMetricsResponse {

  private int maxLast30Days;
  private int avgLast30Days;
}
