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

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Variant {

    @RdfPredicate(value = "variantId", label = "Variant Identifier", comment = "Identifier of the car variant")
    @Id
    private String id;

    @RdfPredicate(value = "variantName", label = "Variant Name", comment = "Name of the variant, e.g., LX, Sport, Top Model")
    private String name;

    @RdfPredicate(value = "hasEngines", label = "Engines", comment = "Engines associated with this variant")
    @ManyToMany
    @JoinTable(
            name = "variant_engines",
            joinColumns = @JoinColumn(name = "variant_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "engine_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    )
    private List<Engine> engines;

    @RdfPredicate(value = "hasTransmissionTypes", label = "Transmission Types", comment = "Transmission types associated with this variant")
    @ManyToMany
    @JoinTable(
            name = "variant_transmission_types",
            joinColumns = @JoinColumn(name = "variant_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "transmission_type_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    )
    private List<TransmissionType> transmissionTypes;

    // Fuel type now comes via Engine.fuelType

    @JsonIgnore
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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
        Variant that = (Variant) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


