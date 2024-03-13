# CO2 Sensor Service

## Overview

This Spring Boot application, developed using Java 17, H2 database, and Lombok, is designed to receive measurements from sensors. The service includes alerting functionality based on CO2 levels and provides various metrics for each sensor.

## Alerting

1. Initial sensor status is set to OK.
2. If the CO2 level equals or exceeds 2000 ppm, the sensor status is set to WARN.
3. If the service receives 3 or more consecutive measurements higher than 2000, the sensor status is set to ALERT.
4. When the sensor is in the ALERT state, it remains there until it receives 3 consecutive measurements lower than 2000, then it moves back to OK.

## Metrics

The service provides the following metrics about each sensor:

1. Average CO2 level for the last 30 days.
2. Maximum CO2 level in the last 30 days.

## Implementation Details

- **Spring Boot Version:** 3.x
- **Java Version:** 17
- **Database:** H2
- **Project Structure:** Standard Spring Boot structure with Lombok for concise code.

## Getting Started

1. Clone the repository.
2. Configure the H2 database settings in `application.properties`.
3. Build the project using your preferred build tool.
4. Run the application.

## Swagger

You can hit below link to get swagger doc.

- **http://localhost:8080/swagger-ui/index.html** 

## Usage

1. Sensors can send measurements to the service at a rate of 1 per minute.
2. Monitor sensor status through the provided metrics.

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests. :)


