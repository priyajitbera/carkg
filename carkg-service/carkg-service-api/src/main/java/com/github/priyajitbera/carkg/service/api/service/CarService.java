package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.client.JenaFusekiClient;
import com.github.priyajitbera.carkg.service.api.embedding.CarEmbeddableFormatter;
import com.github.priyajitbera.carkg.service.api.embedding.EmbeddingOperations;
import com.github.priyajitbera.carkg.service.api.exception.ResourceNotFoundException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
public class CarService implements
        CreateReadOps<Car, String, CarCreate, CarModel>,
        SemanticOps<CarEmbeddingRequest, CarEmbeddingModel, CarSemanticSearchModel> {

    private final CarRequestMapper carRequestMapper;
    private final CarResponseMapper carResponseMapper;
    private final EmbeddingService embeddingService;
    private final CarEmbeddableFormatter carEmbeddableFormatter;
    private final CarRepository carRepository;
    private final BrandRepository brandRepository;
    private final EntityToRDF entityToRDF;
    private final JenaFusekiClient jenaFusekiClient;
    private final RdfMaterializer rdfMaterializer;

    public CarService(
            CarRequestMapper carRequestMapper,
            CarResponseMapper carResponseMapper,
            EmbeddingService embeddingService,
            CarEmbeddableFormatter carEmbeddableFormatter, CarRepository carRepository,
            BrandRepository brandRepository,

            EntityToRDF entityToRDF,
            JenaFusekiClient jenaFusekiClient,
            RdfMaterializer rdfMaterializer
    ) {
        this.carRequestMapper = carRequestMapper;
        this.carResponseMapper = carResponseMapper;
        this.embeddingService = embeddingService;
        this.carEmbeddableFormatter = carEmbeddableFormatter;
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
        carRepository.saveAndFlush(carEntity);

        return carResponseMapper.map(carEntity);
    }

    public List<CarSemanticSearchModel> semanticSearch(String query) {
        float[] embedding = embeddingService.embed(query);
        final String ser = new FloatArrayToJson().convertToDatabaseColumn(embedding);
        return carRepository.cosineSimilarity(ser)
                .stream().map(carResponseMapper::map).toList();
    }

    @Transactional
    public List<CarModel> saveBatch(List<CarCreate> creates) {
        return creates.stream().map(this::save).toList();
    }

    public CarModel findById(String id) {
        Car car = carRepository.findById(id)
                .orElseThrow(ResourceNotFoundException.carById(id));
        return carResponseMapper.map(car);
    }

    public CarEmbeddingModel embed(CarEmbeddingRequest embeddingRequest) {
        Car carEntity = carRepository.findById(embeddingRequest.getId())
                .orElseThrow(ResourceNotFoundException.carById(embeddingRequest.getId()));
        if (carEntity.getEmbedding() == null) {
            carEntity.setEmbedding(new com.github.priyajitbera.carkg.service.data.jpa.entity.Embedding());
        }

        if (embeddingRequest.getForce() ||
                (carEntity.getEmbedding().getEmbeddingRefreshTillUtc() == null
                        || carEntity.getEmbedding().getEmbeddingRefreshTillUtc().isBefore(carEntity.getUpdatedAtUtc()))) {

            float[] vector = EmbeddingOperations.generate(
                    carEntity,
                    carEmbeddableFormatter::format,
                    embeddingService::embed
            );
            carEntity.getEmbedding().setVector(vector);
            carEntity.getEmbedding().setEmbeddingRefreshTillUtc(carEntity.getUpdatedAtUtc());
            carRepository.saveAndFlush(carEntity);
        } else {
            log.info("Skipping embedding for Car with id: {} as it is already embedded at: {}",
                    carEntity.getId(), carEntity.getEmbedding().getEmbeddingRefreshTillUtc());
        }

        return carResponseMapper.mapEmbeddingModel(carEntity);
    }
}
