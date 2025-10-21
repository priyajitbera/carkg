package com.github.priyajitbera.carkg.service.api.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorModel {

    private Integer code;
    private String message;
}
