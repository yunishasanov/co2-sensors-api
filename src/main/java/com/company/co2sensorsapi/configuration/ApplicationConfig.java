package com.company.co2sensorsapi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  @Value("${application.measurement.top-limit}")
  private int topLimit;
  @Value("${application.measurement.co2-limit}")
  private int co2Limit;

  @Value("${application.metrics.avg-and-max-in-days}")
  private int avgAndMaxInDays;


  public int getTopLimit() {
    return topLimit;
  }

  public int getCo2Limit() {
    return co2Limit;
  }

  public int getAvgAndMaxInDays() {
    return avgAndMaxInDays;
  }

}
