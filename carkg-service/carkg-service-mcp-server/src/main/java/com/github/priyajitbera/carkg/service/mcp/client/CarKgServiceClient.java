package com.github.priyajitbera.carkg.service.mcp.client;

import com.github.priyajitbera.carkg.service.api.model.request.BrandCreate;
import com.github.priyajitbera.carkg.service.api.model.request.CarCreate;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class CarKgServiceClient {

    private final WebClient webClient;

    public CarKgServiceClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }


    public CarModel saveCar(CarCreate carCreate) {
        return webClient.post().uri("/car")
                .bodyValue(carCreate)
                .retrieve()
                .bodyToMono(CarModel.class)
                .block();
    }

    public CarModel findCarById(String id) {
        return webClient.get().uri("/car/" + id)
                .retrieve()
                .bodyToMono(CarModel.class)
                .block();
    }

    public List<CarSemanticSearchModel> semanticSearchCar(@RequestParam("query") String query) {
        return webClient.get().uri(uriBuilder ->
                        uriBuilder.path("/car/semantic-search")
                                .queryParam("query", query)
                                .build())
                .retrieve()
                .toEntityList(CarSemanticSearchModel.class)
                .block()
                .getBody();
    }

    public BrandModel saveBrand(BrandCreate create) {
        return webClient.post().uri("/brand")
                .bodyValue(create)
                .retrieve()
                .bodyToMono(BrandModel.class)
                .block();
    }

    public BrandModel findBrandById(String id) {
        return webClient.get().uri("/brand/" + id)
                .retrieve()
                .bodyToMono(BrandModel.class)
                .block();
    }

    public List<BrandSemanticSearchModel> semanticSearchBrand(@RequestParam("query") String query) {
        return webClient.get().uri(uriBuilder ->
                        uriBuilder.path("/brand/semantic-search")
                                .queryParam("query", query)
                                .build())
                .retrieve()
                .toEntityList(BrandSemanticSearchModel.class)
                .block()
                .getBody();
    }
}
