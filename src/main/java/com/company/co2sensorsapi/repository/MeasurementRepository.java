package com.company.co2sensorsapi.repository;

import com.company.co2sensorsapi.dto.MaxAvgCo2Projection;
import com.company.co2sensorsapi.entity.Measurement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

  @Query("SELECT m FROM Measurement m WHERE m.sensorUuid = :sensorUuid ORDER BY m.createdAt DESC")
  List<Measurement> findAllBySensorUuidOrderByCreatedAtDesc(@Param("sensorUuid") UUID sensorUuid,
      Pageable pageable);

  @Query("SELECT new com.company.co2sensorsapi.dto.MaxAvgCo2Projection(MAX(m.co2), AVG(m.co2)) FROM Measurement m WHERE m.sensorUuid = :sensorUuid AND m.createdAt >= :startDate")
  MaxAvgCo2Projection findMaxAvgCo2BySensorUuidAndCreatedAtAfter(@Param("sensorUuid") UUID sensorUuid, @Param("startDate") LocalDateTime startDate);


}
