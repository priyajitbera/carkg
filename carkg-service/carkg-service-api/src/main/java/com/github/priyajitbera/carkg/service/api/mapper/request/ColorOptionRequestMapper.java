package com.github.priyajitbera.carkg.service.api.mapper.request;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.mapper.request.context.CarRequestMappingContext;
import com.github.priyajitbera.carkg.service.api.model.request.ColorOptionCreate;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.entity.ColorOption;
import org.apache.commons.lang3.function.TriConsumer;
import org.mapstruct.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

@Mapper(config = CommonMapperConfig.class)
public abstract class ColorOptionRequestMapper {

    @Mapping(target = "car", source = ".", qualifiedBy = MapCar.class)
    public abstract void map(@MappingTarget ColorOption colorOption, ColorOptionCreate src, @Context CarRequestMappingContext context);

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapCar {
    }

    @MapCar
    protected Car mapCar(ColorOptionCreate src, @Context CarRequestMappingContext context) {
        return context.car();
    }

    @AfterMapping
    protected void afterMapping(@MappingTarget ColorOption target) {
        target.deriveAndSetId();
    }

    public static GenericListItemMapper<ColorOptionCreate, ColorOption, CarRequestMappingContext> genericListItemMapper(
            ColorOptionRequestMapper mapper
    ) {
        GenericListItemMapper<ColorOptionCreate,
                ColorOption, CarRequestMappingContext> listItemMapper = new GenericListItemMapper<>() {
            @Override
            TriConsumer<ColorOption, ColorOptionCreate, CarRequestMappingContext> getMapper() {
                return mapper::map;
            }

            @Override
            boolean match(ColorOption target, ColorOptionCreate src) {
                return Objects.equals(target.getId(), (new IdGen()).generate(target.getCar().deriveId(), src.getName()));
            }

            @Override
            ColorOption newTargetInstance() {
                return new ColorOption();
            }
        };
        return listItemMapper;
    }
}




