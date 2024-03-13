package com.company.co2sensorsapi.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorMeasurementRequest {

  private int co2;
  private OffsetDateTime time;

}
