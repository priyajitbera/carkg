package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.serializer.CombinationSemanticSerializer;
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

@JsonSerialize(using = CombinationSemanticSerializer.class)
@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Combination {

    @RdfPredicate(value = "combinationId", label = "Combination Identifier", comment = "Identifier of the car variant")
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Car car;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "hasVariant", label = "Variant", comment = "Variant associated with this combination")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_combination_variant"))
    private Variant variant;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "hasEngine", label = "Engine", comment = "Engines associated with this combination")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_combination_engine"))
    private Engine engine;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "hasTransmissionType", label = "Transmission Type", comment = "Transmission Type associated with this combination")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_combination_transmission_type"))
    private TransmissionType transmissionType;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "hasTransmissionType", label = "Color Option", comment = "Transmission Type associated with this combination")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_combination_color_option"))
    private ColorOption colorOption;


    public String deriveId() {
        assert car != null;
        assert variant != null;
        assert engine != null;
        assert transmissionType != null;
        assert colorOption != null;
        return (new IdGen()).generate(car.deriveId(), variant.getName(), engine.getName(), transmissionType.getName(), colorOption.getName());
    }

    public void deriveAndSetId() {
        this.id = deriveId();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Combination that = (Combination) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


