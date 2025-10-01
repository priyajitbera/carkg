package com.github.priyajitbera.carkg.service.api.projection.sparql;

import com.github.priyajitbera.carkg.service.jena.annoations.SparqlProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SparqlProjection
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CarBasicV1 {
    private String carName;
    private String brandName;
}
