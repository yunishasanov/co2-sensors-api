package com.company.co2sensorsapi.entity;

import com.company.co2sensorsapi.common.SensorStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sensors")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "sensor_uuid", nullable = false, unique = true)
  private UUID sensorUuid;

  @Column(name = "sensor_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private SensorStatus sensorStatus;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

}
