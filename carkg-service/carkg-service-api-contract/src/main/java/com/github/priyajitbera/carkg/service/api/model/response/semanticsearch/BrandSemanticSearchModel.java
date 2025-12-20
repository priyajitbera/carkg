package com.github.priyajitbera.carkg.service.api.model.response.semanticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandSemanticSearchModel {
  private String id;
  private String name;
  private String countryOfOrigin;
  private Double score;
}
