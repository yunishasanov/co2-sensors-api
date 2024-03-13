package com.company.co2sensorsapi.repository;

import com.company.co2sensorsapi.entity.Sensor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

  Sensor findBySensorUuid(UUID uuid);


}
