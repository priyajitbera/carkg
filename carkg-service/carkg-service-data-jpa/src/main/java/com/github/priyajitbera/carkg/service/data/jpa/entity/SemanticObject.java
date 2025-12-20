package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.github.priyajitbera.carkg.service.data.rdf.interfaces.Identifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SemanticObject implements Identifiable, CommonEntity<String, LocalDateTime> {

  @Id private String id;

  @CreationTimestamp private LocalDateTime createdAtUtc;

  @UpdateTimestamp private LocalDateTime updatedAtUtc;

  @Column(columnDefinition = "text", length = 16383)
  private String jsonView;
}
