package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FuelType {

    @RdfPredicate(value = "fuelTypeId", label = "Fuel Type Identifier", comment = "Identifier of the fuel type")
    @Id
    private String id;

    @RdfPredicate(value = "fuelTypeName", label = "Fuel Type Name", comment = "Free-text fuel type, e.g., Petrol, Diesel, CNG, Electric, Hybrid")
    private String name;
}


