package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.client.JenaFusekiClient;
import com.github.priyajitbera.carkg.service.api.mapper.CarMapper;
import com.github.priyajitbera.carkg.service.api.model.request.CarCreate;
import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.entity.SemanticObject;
import com.github.priyajitbera.carkg.service.data.jpa.repository.BrandRepository;
import com.github.priyajitbera.carkg.service.data.jpa.repository.CarRepository;
import com.github.priyajitbera.carkg.service.data.rdf.RdfMaterializer;
import com.github.priyajitbera.carkg.service.data.rdf.jpa.support.EntityToRDF;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.ai.embedding.Embedding;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CarService {

    private final EmbeddingService embeddingService;
    private final CarRepository carRepository;
    private final BrandRepository brandRepository;
    private final CarMapper carMapper;
    private final EntityToRDF entityToRDF;
    private final JenaFusekiClient jenaFusekiClient;
    private final RdfMaterializer rdfMaterializer;

    public CarService(
            EmbeddingService embeddingService,
            CarRepository carRepository,
            BrandRepository brandRepository,
            CarMapper carMapper,
            EntityToRDF entityToRDF,
            JenaFusekiClient jenaFusekiClient,
            RdfMaterializer rdfMaterializer
    ) {
        this.embeddingService = embeddingService;
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;
        this.carMapper = carMapper;
        this.entityToRDF = entityToRDF;
        this.jenaFusekiClient = jenaFusekiClient;
        this.rdfMaterializer = rdfMaterializer;
    }

    @Transactional
    public CarModel save(CarCreate carCreate) {

        Brand brand = brandRepository.findById(carCreate.getBrandId()).orElseThrow();
        String id = (brand.getName() + "-" + carCreate.getName()).replace(' ', '_').toLowerCase();

        Car carEntity = Car.builder()
                .id(id)
                .name(carCreate.getName())
                .brand(brand)
                .build();

        final String carEntitySer = embeddingService.serializeEmbeddable(carEntity);
        Embedding embedding = embeddingService.embedding(carEntitySer);
        final String embeddingJson = embeddingService.serializeEmbedding(embedding);

        carEntity.setSemanticObject(SemanticObject.builder().id(Car.class.getName() + "-" + id).jsonView(carEntitySer).build());
        carEntity.setEmbedding(com.github.priyajitbera.carkg.service.data.jpa.entity.Embedding.builder().embedding(embeddingJson).build());

        carRepository.save(carEntity);

        Model ontologyModel = entityToRDF.entityToOntologyModel(carEntity.getClass());
        Model dataModel = entityToRDF.entityToDataModel(carEntity);

        System.out.println("DATA MODEL " + id + ":");
        dataModel.write(System.out, "TURTLE");

        Model materializedGraph = rdfMaterializer.materialize(dataModel, ontologyModel);
        System.out.println("MATERIALIZED " + id + ":");
        materializedGraph.write(System.out, "TURTLE");

        jenaFusekiClient.saveToFuseki(materializedGraph);

        return carMapper.to(carEntity);
    }

    public List<CarSemanticSearchModel> semanticSearch(String query) {
        Embedding embedding = embeddingService.embedding(query);
        final String ser = embeddingService.serializeEmbedding(embedding);
        return carRepository.cosineSimilarity(ser)
                .stream().map(carMapper::to).toList();
    }

    @Transactional
    public List<CarModel> saveBatch(List<CarCreate> creates) {
        return creates.stream().map(this::save).toList();
    }
}
