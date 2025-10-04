package com.github.priyajitbera.carkg.service.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantCreate {
    private String name;
    private java.util.List<String> transmissionTypeIds;
    private java.util.List<String> engineIds;
    private java.util.List<String> colorOptionIds;
}


