package com.github.priyajitbera.carkg.service.api.embedding;

import com.github.priyajitbera.carkg.service.data.jpa.entity.*;
import org.springframework.stereotype.Component;

@Component
public class EmbeddableFormatterCar implements EmbeddableFormatter<Car> {

    @Override
    public String format(Car car) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Car Name: %s\n", car.getName()));
        builder.append(String.format("Brand Name: %s\n", car.getBrand().getName()));
        builder.append(String.format("Variants Name: %s\n", car.getVariants().stream().map(Variant::getName).toList()));
        builder.append(String.format("Engines Name: %s\n", car.getEngines().stream().map(Engine::getName).toList()));
        builder.append(String.format("Color Options: %s\n", car.getColorOptions().stream().map(ColorOption::getName).toList()));
        builder.append(String.format("Transmission Types: %s\n", car.getTransmissionTypes().stream().map(TransmissionType::getName).toList()));
        builder.append(String.format("Combinations: %s\n", car.getCombinations().stream()
                .map(combination -> String.format("[%s %s %s %s]", combination.getVariant().getName(), combination.getEngine().getName(), combination.getTransmissionType().getName(), combination.getColorOption().getName())).toList()));

        return builder.toString();
    }
}
