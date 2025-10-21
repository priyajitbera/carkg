package com.github.priyajitbera.carkg.service.mcp.tool;

import com.github.priyajitbera.carkg.service.api.model.request.BrandCreate;
import com.github.priyajitbera.carkg.service.api.model.request.CarCreate;
import com.github.priyajitbera.carkg.service.api.model.response.BrandModel;
import com.github.priyajitbera.carkg.service.api.model.response.CarModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.BrandSemanticSearchModel;
import com.github.priyajitbera.carkg.service.api.model.response.semanticsearch.CarSemanticSearchModel;
import com.github.priyajitbera.carkg.service.mcp.client.CarKgServiceClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarKgMcpTools {

    private final CarKgServiceClient carKgServiceClient;

    public CarKgMcpTools(CarKgServiceClient carKgServiceClient) {
        this.carKgServiceClient = carKgServiceClient;
    }


    @Tool(name = "createUpdateCar", description = "Create or update a car entity by providing the car details. e.g JSON input { \"name\": \"Amaze\", \"brandId\": \"honda\", \"fuelTypes\": [ \"Petrol\" ], \"colorOptions\": [ { \"name\": \"Lunar Silver Metallic\" }, { \"name\": \"Meteoroid Grey Metallic\" }, { \"name\": \"Golden Brown Metallic\" }, { \"name\": \"Platinum White Pearl\" }, { \"name\": \"Radiant Red Metallic\" }, { \"name\": \"Obsidian Blue Pearl\" }, { \"name\": \"Crystal Black Pearl\" } ], \"engines\": [ { \"name\": \"1.2 L i-VTEC Petrol Engine\", \"capacityCc\": 1199, \"fuelType\": \"Petrol\" } ], \"transmissionTypes\": [ { \"name\": \"5-speed Manual (MT)\" }, { \"name\": \"CVT Automatic\" } ], \"variants\": [ { \"name\": \"V\" }, { \"name\": \"VX\" }, { \"name\": \"ZX\" } ], \"combinations\": [ { \"variant\": \"V\", \"engine\": \"1.2 L i-VTEC Petrol Engine\", \"transmissionType\": \"5-speed Manual (MT)\", \"colorOption\": \"Lunar Silver Metallic\" }, { \"variant\": \"V\", \"engine\": \"1.2 L i-VTEC Petrol Engine\", \"transmissionType\": \"5-speed Manual (MT)\", \"colorOption\": \"Platinum White Pearl\" }, { \"variant\": \"V\", \"engine\": \"1.2 L i-VTEC Petrol Engine\", \"transmissionType\": \"CVT Automatic\", \"colorOption\": \"Obsidian Blue Pearl\" }, { \"variant\": \"VX\", \"engine\": \"1.2 L i-VTEC Petrol Engine\", \"transmissionType\": \"5-speed Manual (MT)\", \"colorOption\": \"Meteoroid Grey Metallic\" }, { \"variant\": \"VX\", \"engine\": \"1.2 L i-VTEC Petrol Engine\", \"transmissionType\": \"CVT Automatic\", \"colorOption\": \"Golden Brown Metallic\" }, { \"variant\": \"ZX\", \"engine\": \"1.2 L i-VTEC Petrol Engine\", \"transmissionType\": \"5-speed Manual (MT)\", \"colorOption\": \"Radiant Red Metallic\" }, { \"variant\": \"ZX\", \"engine\": \"1.2 L i-VTEC Petrol Engine\", \"transmissionType\": \"CVT Automatic\", \"colorOption\": \"Crystal Black Pearl\" } ] }")
    public CarModel createUpdateCar(CarCreate carCreate) {
        return carKgServiceClient.saveCar(carCreate);
    }

    @Tool(name = "findCarById", description = "Find a car by its ID.")
    public CarModel findCarById(@ToolParam(description = "identifier of the car. e.g skoda-kylaq, tata-nexon") String id) {
        return carKgServiceClient.findCarById(id);
    }

    @Tool(name = "semanticSearchCars", description = "Search cars with natural language queries")
    public List<CarSemanticSearchModel> semanticSearchCars(@ToolParam(description = "Query in natural language e.g. Cars from brand Tata") String query) {
        return carKgServiceClient.semanticSearchCar(query);
    }

    @Tool(name = "createUpdateBrand", description = """
            Create or update a brand entity by providing the brand details. e.g JSON input {"name":"Skoda","countryOfOrigin":"Czech Republic"}""")
    public BrandModel createUpdateCar(BrandCreate create) {
        return carKgServiceClient.saveBrand(create);
    }

    @Tool(name = "findBrandById", description = "Find a brand by its ID.")
    public BrandModel findBrandById(@ToolParam(description = "identifier of the car. e.g skoda, tata") String id) {
        return carKgServiceClient.findBrandById(id);
    }

    @Tool(name = "semanticSearchBrands", description = "Search brands with natural language queries")
    public List<BrandSemanticSearchModel> semanticSearchBrands(@ToolParam(description = "Query in natural language e.g. Brands which originated from India") String query) {
        return carKgServiceClient.semanticSearchBrand(query);
    }
}
