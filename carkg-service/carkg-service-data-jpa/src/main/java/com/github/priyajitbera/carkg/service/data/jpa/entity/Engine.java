package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_engine_name_capacity_fuel", columnNames = {"name", "capacityCc", "fuelType_id"})
})
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Engine {

    @RdfPredicate(value = "engineId", label = "Engine Identifier", comment = "Identifier of the engine")
    @Id
    private String id;

    @RdfPredicate(value = "engineName", label = "Engine Name", comment = "Human readable engine name, e.g., K12N, 1.5 TSI")
    private String name;

    @RdfPredicate(value = "engineCapacityCc", label = "Engine Capacity (cc)", comment = "Engine displacement in cubic centimeters")
    private Double capacityCc;

    @RdfPredicate(value = "fuelType", label = "Fuel Type", comment = "Fuel type for this engine, free text via reference entity")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_engine_fuel_type"))
    private FuelType fuelType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_engine_car"))
    private Car car;

    public String deriveId() {
        assert car != null;
        assert name != null;
        return (new IdGen()).generate(car.deriveId(), name);
    }

    public void deriveAndSetId() {
        this.id = deriveId();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Engine that = (Engine) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


