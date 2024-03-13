package com.company.co2sensorsapi.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "measurements")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Measurement {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "sensor_uuid", nullable = false)
  private UUID sensorUuid;

  @Column(name = "co2", nullable = false)
  private Integer co2;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

}
