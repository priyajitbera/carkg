package com.github.priyajitbera.carkg.service.api.mapper.request;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.mapper.request.context.CarRequestMappingContext;
import com.github.priyajitbera.carkg.service.api.model.request.EngineCreate;
import com.github.priyajitbera.carkg.service.api.model.request.FuelTypeCreate;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Engine;
import com.github.priyajitbera.carkg.service.data.jpa.entity.FuelType;
import com.github.priyajitbera.carkg.service.data.jpa.repository.FuelTypeRepository;
import org.apache.commons.lang3.function.TriConsumer;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.Optional;

@Mapper(config = CommonMapperConfig.class)
public abstract class EngineRequestMapper {

    @Autowired
    private FuelTypeRepository fuelTypeRepository;

    @Mapping(target = "car", source = ".", qualifiedBy = MapCar.class)
    @Mapping(target = "fuelType", qualifiedBy = MapFuelType.class)
    public abstract void map(@MappingTarget Engine target, EngineCreate src, @Context CarRequestMappingContext context);

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapCar {
    }

    @MapCar
    protected Car mapCar(EngineCreate src, @Context CarRequestMappingContext context) {
        return context.car();
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapFuelType {
    }

    @MapFuelType
    protected FuelType mapFuelType(FuelTypeCreate src, @Context CarRequestMappingContext context) {
        Optional<FuelType> newlyCreatedOpt = context.createdFuelTypes().stream().filter(createdFuelType -> Objects.equals(createdFuelType.getName(), src.getName()))
                .findFirst();
        return newlyCreatedOpt.orElseGet(() -> {
            Optional<FuelType> previouslyCreated = fuelTypeRepository.findByName(src.getName());
            return previouslyCreated.orElseGet(() -> {
                FuelType newFuelType = new FuelType() {{
                    setId(new IdGen().generate(src.getName()));
                    setName(src.getName());
                }};
                context.createdFuelTypes().add(newFuelType);
                return newFuelType;
            });
        });
    }

    @AfterMapping
    protected void afterMapping(@MappingTarget Engine target) {
        target.deriveAndSetId();
    }

    public static GenericListItemMapper<EngineCreate, Engine, CarRequestMappingContext> genericListItemMapper(
            EngineRequestMapper mapper
    ) {
        GenericListItemMapper<EngineCreate,
                Engine, CarRequestMappingContext> listItemMapper = new GenericListItemMapper<>() {
            @Override
            TriConsumer<Engine, EngineCreate, CarRequestMappingContext> getMapper() {
                return mapper::map;
            }

            @Override
            boolean match(Engine target, EngineCreate src) {
                return Objects.equals(target.getId(), (new IdGen()).generate(target.getCar().deriveId(), src.getName()));
            }

            @Override
            Engine newTargetInstance() {
                return new Engine();
            }
        };
        return listItemMapper;
    }
}



