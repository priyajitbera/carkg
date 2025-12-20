package com.github.priyajitbera.carkg.service.api.mapper.request;

import com.github.priyajitbera.carkg.service.api.mapper.CommonMapperConfig;
import com.github.priyajitbera.carkg.service.api.mapper.request.context.CarRequestMappingContext;
import com.github.priyajitbera.carkg.service.api.model.request.TransmissionTypeCreate;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.entity.TransmissionType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import org.apache.commons.lang3.function.TriConsumer;
import org.mapstruct.*;

@Mapper(config = CommonMapperConfig.class)
public abstract class TransmissiongTypeRequestMapper {

  @Mapping(target = "car", source = ".", qualifiedBy = MapCar.class)
  public abstract void map(
      @MappingTarget TransmissionType target,
      TransmissionTypeCreate src,
      @Context CarRequestMappingContext context);

  @Qualifier
  @Retention(RetentionPolicy.CLASS)
  @interface MapCar {}

  @MapCar
  protected Car mapCar(TransmissionTypeCreate src, @Context CarRequestMappingContext context) {
    return context.car();
  }

  @AfterMapping
  protected void afterMapping(@MappingTarget TransmissionType target) {
    target.deriveAndSetId();
  }

  public static GenericListItemMapper<
          TransmissionTypeCreate, TransmissionType, CarRequestMappingContext>
      genericListItemMapper(TransmissiongTypeRequestMapper mapper) {
    GenericListItemMapper<TransmissionTypeCreate, TransmissionType, CarRequestMappingContext>
        listItemMapper =
            new GenericListItemMapper<>() {
              @Override
              TriConsumer<TransmissionType, TransmissionTypeCreate, CarRequestMappingContext>
                  getMapper() {
                return mapper::map;
              }

              @Override
              boolean match(TransmissionType target, TransmissionTypeCreate src) {
                return Objects.equals(
                    target.getId(),
                    (new IdGen()).generate(target.getCar().deriveId(), src.getName()));
              }

              @Override
              TransmissionType newTargetInstance() {
                return new TransmissionType();
              }
            };
    return listItemMapper;
  }
}
