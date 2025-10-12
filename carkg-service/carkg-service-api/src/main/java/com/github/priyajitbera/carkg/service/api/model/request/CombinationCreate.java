package com.github.priyajitbera.carkg.service.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CombinationCreate {
    private String variant;
    private String transmissionType;
    private String engineOption;
    private String colorOption;
}


