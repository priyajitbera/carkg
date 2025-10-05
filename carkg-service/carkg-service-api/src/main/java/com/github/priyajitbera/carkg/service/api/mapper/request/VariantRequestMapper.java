package com.github.priyajitbera.carkg.service.api.mapper.request;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.mapper.request.context.CarRequestMappingContext;
import com.github.priyajitbera.carkg.service.api.mapper.request.context.VariantRequestMappingContext;
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

    @Mappings({
            @Mapping(target = "car", source = ".", qualifiedBy = MapCar.class)
    })
    public abstract void map(@MappingTarget Variant variant, VariantCreate src, @Context VariantRequestMappingContext context);

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @interface MapCar {
    }

    @MapCar
    protected Car mapCar(VariantCreate src, @Context VariantRequestMappingContext context) {
        return context.carRequestMappingContext().car();
    }

    @AfterMapping
    protected void afterMapping(@MappingTarget Variant target) {
        target.deriveAndSetId();
    }

    public static GenericListItemMapper<VariantCreate, Variant, VariantRequestMappingContext> genericListItemMapper(
            VariantRequestMapper mapper
    ) {
        GenericListItemMapper<VariantCreate,
                Variant, VariantRequestMappingContext> listItemMapper = new GenericListItemMapper<>() {
            @Override
            TriConsumer<Variant, VariantCreate, VariantRequestMappingContext> getMapper() {
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

    public static GenericListItemMapper2<VariantCreate, Variant, CarRequestMappingContext, VariantRequestMappingContext> genericListItemMapper2(
            VariantRequestMapper mapper
    ) {
        GenericListItemMapper2<VariantCreate,
                Variant, CarRequestMappingContext, VariantRequestMappingContext> listItemMapper = new GenericListItemMapper2<>() {
            @Override
            TriConsumer<Variant, VariantCreate, VariantRequestMappingContext> getMapper() {
                return (target, source, context) -> mapper.map(target, source, context);
            }

            @Override
            VariantRequestMappingContext getContext(CarRequestMappingContext parentContext, Variant target) {
                return new VariantRequestMappingContext(parentContext, target);
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




