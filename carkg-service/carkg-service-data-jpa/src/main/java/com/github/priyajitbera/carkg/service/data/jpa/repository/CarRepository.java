package com.github.priyajitbera.carkg.service.data.jpa.repository;


import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.projection.CarSemanticSearchProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, String> {

    @Query(value = """
            SELECT c.id, c.name, b.name brand_name, b.country_of_origin brand_country_of_origin,
            cosine_similarity(e.vector, :queryEmbedding) AS score
            FROM car c JOIN brand b ON c.brand_id = b.id
            JOIN embedding e on c.embedding_id = e.id
            ORDER BY score DESC""", nativeQuery = true)
    List<CarSemanticSearchProjection> cosineSimilarity(@Param("queryEmbedding") String queryEmbedding);

    Optional<Car> findByBrandIdAndName(String brandId, String name);
}
