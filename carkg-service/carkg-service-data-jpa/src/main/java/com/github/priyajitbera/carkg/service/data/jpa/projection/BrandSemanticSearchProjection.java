package com.github.priyajitbera.carkg.service.data.jpa.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class BrandSemanticSearchProjection {
  private String id;
  private String name;
  private String countryOfOrigin;
  private Double score;
}
