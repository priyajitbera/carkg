package com.github.priyajitbera.carkg.service.data.jpa.repository;

import com.github.priyajitbera.carkg.service.data.jpa.entity.FuelType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, String> {

  Optional<FuelType> findByName(String name);
}
