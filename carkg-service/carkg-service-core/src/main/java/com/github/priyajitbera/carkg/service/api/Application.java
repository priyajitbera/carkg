package com.github.priyajitbera.carkg.service.api;

import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.CarView;
import com.github.priyajitbera.carkg.service.data.rdf.OntologySchemaExtractor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.github.priyajitbera.carkg.service"})
@EnableJpaRepositories(basePackages = {"com.github.priyajitbera.carkg.service.data.jpa.repository"})
@EntityScan(basePackages = {"com.github.priyajitbera.carkg.service.data.jpa.entity"})
public class Application implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    OntologySchemaExtractor extractor = new OntologySchemaExtractor();
    extractor.extract(
        "https://github.com/priyajitbera/carkg/ontology/",
        "https://github.com/priyajitbera/carkg/data/",
        "carkg-service-core/src/main/resources/ontology",
        Car.class,
        CarView.class);
  }
}
