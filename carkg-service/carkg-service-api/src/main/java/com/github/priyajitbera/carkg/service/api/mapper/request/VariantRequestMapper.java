package com.github.priyajitbera.carkg.service.api.mapper.request;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.mapper.request.context.CarRequestMappingContext;
import com.github.priyajitbera.carkg.service.api.model.request.VariantCreate;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Variant;
import org.apache.commons.lang3.function.TriConsumer;
import org.mapstruct.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

@Mapper(config = CommonMapperConfig.class)
public abstract class VariantRequestMapper {

    @Mapping(target = "car", source = ".", qualifiedBy = MapCar.class)
    public abstract void map(@MappingTarget Variant variant, VariantCreate src, @Context CarRequestMappingContext context);

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapCar {
    }

    @MapCar
    protected Car mapCar(VariantCreate src, @Context CarRequestMappingContext context) {
        return context.car();
    }

    @AfterMapping
    protected void afterMapping(@MappingTarget Variant variant) {
        variant.deriveAndSetId();
    }

    public static GenericListItemMapper<VariantCreate, Variant, CarRequestMappingContext> genericListItemMapper(
            VariantRequestMapper mapper
    ) {
        GenericListItemMapper<VariantCreate,
                Variant, CarRequestMappingContext> listItemMapper = new GenericListItemMapper<>() {
            @Override
            TriConsumer<Variant, VariantCreate, CarRequestMappingContext> getMapper() {
                return mapper::map;
            }

            @Override
            boolean match(Variant target, VariantCreate src) {
                return Objects.equals(target.getId(), (new IdGen()).generate(target.getCar().deriveId(), src.getName()));
            }

            @Override
            Variant newTargetInstance() {
                return new Variant();
            }
        };
        return listItemMapper;
    }
}




