package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.github.priyajitbera.carkg.service.data.jpa.converter.FloatArrayToJson;
import com.github.priyajitbera.carkg.service.data.rdf.interfaces.Identifiable;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Embedding implements Identifiable, CommonEntity<String, LocalDateTime> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @CreationTimestamp private LocalDateTime createdAtUtc;

  @UpdateTimestamp private LocalDateTime updatedAtUtc;

  @Convert(converter = FloatArrayToJson.class)
  @Column(columnDefinition = "text", length = 16383)
  private float[] vector;

  private LocalDateTime embeddingRefreshTillUtc;
}
