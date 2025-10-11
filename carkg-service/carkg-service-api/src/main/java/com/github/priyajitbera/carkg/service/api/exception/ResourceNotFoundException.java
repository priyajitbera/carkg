package com.github.priyajitbera.carkg.service.api.exception;

import com.github.priyajitbera.carkg.service.data.jpa.entity.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

public class ResourceNotFoundException extends ResponseStatusException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public ResourceNotFoundException(String resourceName, String id) {
        super(HttpStatus.NOT_FOUND, String.format("%s not found with id: %s", resourceName, id));
    }

    public static Supplier<ResourceNotFoundException> carById(String id) {
        return () -> new ResourceNotFoundException(Car.class.getSimpleName(), id);
    }

    public static Supplier<ResourceNotFoundException> brandById(String id) {
        return () -> new ResourceNotFoundException(Brand.class.getSimpleName(), id);
    }

    public static Supplier<ResourceNotFoundException> variantByNameCarId(String name, String carId) {
        return () -> new ResourceNotFoundException(String.format("%s: %s not found for car with: %s", Variant.class.getSimpleName(), name, carId));
    }

    public static Supplier<ResourceNotFoundException> engineByNameCarId(String name, String carId) {
        return () -> new ResourceNotFoundException(String.format("%s: %s not found for car with: %s", Engine.class.getSimpleName(), name, carId));
    }

    public static Supplier<ResourceNotFoundException> transmissionTypeByNameCarId(String name, String carId) {
        return () -> new ResourceNotFoundException(String.format("%s: %s not found for car with: %s", TransmissionType.class.getSimpleName(), name, carId));
    }

    public static Supplier<ResourceNotFoundException> colorOptionByNameCarId(String name, String carId) {
        return () -> new ResourceNotFoundException(String.format("%s: %s not found for car with: %s", ColorOption.class.getSimpleName(), name, carId));
    }

//    public static Supplier<ResourceNotFoundException> transmissionTypeByNameCarBrand(String name, String car, String brand) {
//        return () -> new ResourceNotFoundException(String.format("%s: %s not found for car: %s of brand: %s", Engine.class.getSimpleName(), name, car, brand));
//    }
}
