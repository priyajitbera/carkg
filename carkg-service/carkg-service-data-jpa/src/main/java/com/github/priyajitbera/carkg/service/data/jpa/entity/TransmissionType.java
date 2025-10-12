package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.serializer.TransmissionTypeSemanticSerializer;
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

@JsonSerialize(using = TransmissionTypeSemanticSerializer.class)
@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TransmissionType {

    @RdfPredicate(value = "transmissionTypeId", label = "Transmission Type Identifier", comment = "Identifier of the transmission type")
    @Id
    private String id;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "transmissionTypeName", label = "Transmission Type Name", comment = "Free-text transmission type, e.g., MT, AMT, CVT, DCT, iMT, AT")
    private String name;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_transmission_type_car"))
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
        TransmissionType that = (TransmissionType) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


