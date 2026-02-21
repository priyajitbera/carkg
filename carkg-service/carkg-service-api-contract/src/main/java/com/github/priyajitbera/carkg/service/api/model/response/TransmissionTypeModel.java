package com.github.priyajitbera.carkg.service.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransmissionTypeModel {
  private String id;
  private String name;
}
