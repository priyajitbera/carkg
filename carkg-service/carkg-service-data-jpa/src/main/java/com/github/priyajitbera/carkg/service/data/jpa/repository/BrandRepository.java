package com.github.priyajitbera.carkg.service.data.jpa.repository;

import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.projection.BrandSemanticSearchProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {

    @Query(value = """
            SELECT b.id, b.name, b.country_of_origin,
            cosine_similarity(embedding, :queryEmbedding) AS score
            FROM brand b join embedding e on b.embedding_id = e.id
            ORDER BY score DESC""", nativeQuery = true)
    List<BrandSemanticSearchProjection> cosineSimilarity(@Param("queryEmbedding") String queryEmbedding);

    Optional<Brand> findByName(String name);
}
