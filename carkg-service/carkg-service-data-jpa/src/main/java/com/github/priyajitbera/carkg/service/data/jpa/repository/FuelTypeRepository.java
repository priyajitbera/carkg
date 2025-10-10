package com.github.priyajitbera.carkg.service.data.jpa.repository;


import com.github.priyajitbera.carkg.service.data.jpa.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, String> {

    Optional<FuelType> findByName(String name);
}
