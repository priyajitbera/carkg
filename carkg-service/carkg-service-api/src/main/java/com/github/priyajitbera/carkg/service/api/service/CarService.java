package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.client.JenaFusekiClient;
import com.github.priyajitbera.carkg.service.api.embedding.EmbeddableFormatterCar;
import com.github.priyajitbera.carkg.service.api.embedding.EmbeddingOperations;
import com.github.priyajitbera.carkg.service.api.mapper.request.CarRequestMapper;
import com.github.priyajitbera.carkg.service.api.mapper.request.context.CarRequestMappingContext;
import com.github.priyajitbera.carkg.service.api.mapper.response.CarResponseMapper;
import com.github.priyajitbera.carkg.service.api.model.request.CarCreate;
import com.github.priyajitbera.carkg.service.api.model.request.CarEmbeddingRequest;
import com.github.priyajitbera.carkg.service.api.model.response.CarEmbeddingModel;
import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.converter.FloatArrayToJson;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.repository.BrandRepository;
import com.github.priyajitbera.carkg.service.data.jpa.repository.CarRepository;
import com.github.priyajitbera.carkg.service.data.rdf.RdfMaterializer;
import com.github.priyajitbera.carkg.service.data.rdf.jpa.support.EntityToRDF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.Embedding;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
public class CarService {

    private final CarRequestMapper carRequestMapper;
    private final CarResponseMapper carResponseMapper;
    private final EmbeddingService embeddingService;
    private final EmbeddableFormatterCar embeddableFormatterCar;
    private final CarRepository carRepository;
    private final BrandRepository brandRepository;
    private final EntityToRDF entityToRDF;
    private final JenaFusekiClient jenaFusekiClient;
    private final RdfMaterializer rdfMaterializer;

    public CarService(
            CarRequestMapper carRequestMapper,
            CarResponseMapper carResponseMapper,
            EmbeddingService embeddingService,
            EmbeddableFormatterCar embeddableFormatterCar, CarRepository carRepository,
            BrandRepository brandRepository,

            EntityToRDF entityToRDF,
            JenaFusekiClient jenaFusekiClient,
            RdfMaterializer rdfMaterializer
    ) {
        this.carRequestMapper = carRequestMapper;
        this.carResponseMapper = carResponseMapper;
        this.embeddingService = embeddingService;
        this.embeddableFormatterCar = embeddableFormatterCar;
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;

        this.entityToRDF = entityToRDF;
        this.jenaFusekiClient = jenaFusekiClient;
        this.rdfMaterializer = rdfMaterializer;
    }

    @Transactional
    public CarModel save(CarCreate carCreate) {

        Brand brand = brandRepository.findById(carCreate.getBrandId()).orElseThrow();

        Car carEntity = carRepository.findByBrandIdAndName(brand.getId(), carCreate.getName()).orElse(new Car());
        CarRequestMappingContext context = CarRequestMappingContext.builder()
                .brand(brand).car(carEntity)
                .createdFuelTypes(new LinkedHashSet<>())
                .build();
        carRequestMapper.map(carEntity, carCreate, context);
        carEntity.deriveAndSetId();
        carRepository.saveAndFlush(carEntity);

        return carResponseMapper.map(carEntity);
    }

    public List<CarSemanticSearchModel> semanticSearch(String query) {
        Embedding embedding = embeddingService.embedding(query);
        final String ser = new FloatArrayToJson().convertToDatabaseColumn(embedding.getOutput());
        return carRepository.cosineSimilarity(ser)
                .stream().map(carResponseMapper::map).toList();
    }

    @Transactional
    public List<CarModel> saveBatch(List<CarCreate> creates) {
        return creates.stream().map(this::save).toList();
    }

    public CarModel get(String id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("Car not found for id: %s", id)));
        return carResponseMapper.map(car);
    }

    public CarEmbeddingModel embed(CarEmbeddingRequest embeddingRequest) {
        Car carEntity = carRepository.findById(embeddingRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("Car not found for id: %s", embeddingRequest.getId())));
        if (carEntity.getEmbedding() == null) {
            carEntity.setEmbedding(new com.github.priyajitbera.carkg.service.data.jpa.entity.Embedding());
        }

        if (embeddingRequest.getForce() ||
                (carEntity.getEmbedding().getEmbeddingRefreshTillUtc() == null
                        || carEntity.getEmbedding().getEmbeddingRefreshTillUtc().isBefore(carEntity.getUpdatedAtUtc()))) {

            float[] vector = EmbeddingOperations.generate(
                    carEntity,
                    embeddableFormatterCar::format,
                    text -> embeddingService.embedding(text).getOutput()
            );
            carEntity.getEmbedding().setVector(vector);
            carEntity.getEmbedding().setEmbeddingRefreshTillUtc(carEntity.getUpdatedAtUtc());
            carRepository.saveAndFlush(carEntity);
        } else {
            log.info("Skipping embedding for car id: {} as it is already embedded at: {}",
                    carEntity.getId(), carEntity.getEmbedding().getEmbeddingRefreshTillUtc());
        }

        return carResponseMapper.mapEmbeddingModel(carEntity);
    }
}
