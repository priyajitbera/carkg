package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.BrandView;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.CarView;
import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

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

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "fuelTypeName", label = "Fuel Type Name", comment = "Free-text fuel type, e.g., Petrol, Diesel, CNG, Electric, Hybrid")
    private String name;

    public String deriveId() {
        assert name != null;
        return (new IdGen()).generate(name);
    }

    public void deriveAndSetId() {
        this.id = deriveId();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FuelType that = (FuelType) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


