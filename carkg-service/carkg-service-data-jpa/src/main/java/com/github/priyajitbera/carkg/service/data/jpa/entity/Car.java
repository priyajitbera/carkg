package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;
import com.github.priyajitbera.carkg.service.data.rdf.interfaces.Identifiable;
import jakarta.persistence.*;
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
public class Car implements Identifiable {

    @RdfPredicate(value = "carId", label = "Car Identifier", comment = "Identifier of the Car")
    @Id
    private String id;

    @RdfPredicate(value = "carName", label = "Car Name", comment = """
            A "Car Name" refers to either the specific model of a vehicle, such as a Toyota Corolla or Ford F-Series, or the make or brand of the car, like Toyota or Ford. To find the name of a specific car, you typically look for the model name, often found on the back of the vehicle.""")
    private String name;

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
}
