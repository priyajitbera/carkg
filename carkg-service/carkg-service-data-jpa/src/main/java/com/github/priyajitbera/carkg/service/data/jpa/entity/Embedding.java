package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.github.priyajitbera.carkg.service.data.jpa.converter.FloatArrayToJson;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Embedding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Convert(converter = FloatArrayToJson.class)
    @Column(columnDefinition = "text", length = 16383)
    private float[] vector;

    private LocalDateTime embeddingRefreshTillUtc;
}
