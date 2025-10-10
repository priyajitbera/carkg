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
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ColorOption {

    @RdfPredicate(value = "colorOptionId", label = "Color Option Identifier", comment = "Identifier of the color option")
    @Id
    private String id;

    @RdfPredicate(value = "colorName", label = "Color Name", comment = "Human-readable color name, e.g., Midnight Black, Pearl White")
    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_color_option_car"))
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
        ColorOption that = (ColorOption) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


