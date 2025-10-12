package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.BrandView;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.CarView;
import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;
import com.github.priyajitbera.carkg.service.data.rdf.interfaces.Identifiable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Car implements Identifiable {

    @JsonView(CarView.class)
    @RdfPredicate(value = "carId", label = "Car Identifier", comment = "Identifier of the Car")
    @Id
    private String id;

    @CreationTimestamp
    private LocalDateTime createdAtUtc;

    @UpdateTimestamp
    private LocalDateTime updatedAtUtc;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "carName", label = "Car Name", comment = """
            A "Car Name" refers to either the specific model of a vehicle, such as a Toyota Corolla or Ford F-Series, or the make or brand of the car, like Toyota or Ford. To find the name of a specific car, you typically look for the model name, often found on the back of the vehicle.""")
    private String name;

    @JsonView(CarView.class)
    @RdfPredicate(value = "hasBrand", label = "Car Brand", comment = """
            A Car Brand, also known as the make, is the manufacturer or company that designs and builds vehicles, such as Toyota, Ford, or Mercedes-Benz. It identifies the company, while the car model (like the Ford Mustang or Toyota Corolla) is the specific type of vehicle a brand offers.""")
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_car_brand"))
    private Brand brand;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_car_embedding"))
    private Embedding embedding;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_car_semantic_object"))
    private SemanticObject semanticObject;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "hasVariants", label = "Car Variants", comment = "List of variants available for this car model")
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variant> variants;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "hasEngines", label = "Available Engines", comment = "Engines associated with this car model")
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EngineOption> engineOptions;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "hasColorOptions", label = "Available Color Options", comment = "Colors in which this car is offered")
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<ColorOption> colorOptions;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "hasTransmissionTypes", label = "Available Transmission Types", comment = "Transmission types associated with this car model")
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransmissionType> transmissionTypes;

    @JsonView({CarView.class, BrandView.class})
    @RdfPredicate(value = "hasCombination", label = "Available Combination of Variant, Engine, TransmissionType and ColorOption", comment = "Available Combination of Variant, Engine, TransmissionType and ColorOption")
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Combination> combinations;

    public String deriveId() {
        assert brand != null;
        assert name != null;
        return (new IdGen()).generate(brand.deriveId(), name);
    }

    public void deriveAndSetId() {
        this.id = deriveId();
    }
}
