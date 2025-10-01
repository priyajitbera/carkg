package com.github.priyajitbera.carkg.service.api.model.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandModel extends EmbeddableModel {
    private String id;
    private String name;
    private String countryOfOrigin;
}
