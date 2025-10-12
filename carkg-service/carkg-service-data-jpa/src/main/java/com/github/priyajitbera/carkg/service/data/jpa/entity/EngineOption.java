package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.serializer.EngineSemanticSerializer;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.BrandView;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.CarView;
import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@JsonSerialize(using = EngineSemanticSerializer.class)
@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EngineOption {

    @RdfPredicate(value = "engineId", label = "Engine Identifier", comment = "Identifier of the engine")
    @Id
    private String id;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "engineName", label = "Engine Name", comment = "Human readable engine name, e.g., K12N, 1.5 TSI")
    private String name;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "engineCapacityCc", label = "Engine Capacity (cc)", comment = "Engine displacement in cubic centimeters")
    private Double capacityCc;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "fuelType", label = "Fuel Type", comment = "Fuel type for this engine, free text via reference entity")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_engine_fuel_type"))
    private FuelType fuelType;

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
        EngineOption that = (EngineOption) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


