package com.company.co2sensorsapi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaxAvgCo2Projection {
  private Integer maxCo2;
  private Double avgCo2;

}
