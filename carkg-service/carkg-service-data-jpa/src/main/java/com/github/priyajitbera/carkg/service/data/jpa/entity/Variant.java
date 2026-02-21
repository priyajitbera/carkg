package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.priyajitbera.carkg.service.data.jpa.IdGen;
import com.github.priyajitbera.carkg.service.data.jpa.serializer.VariantSemanticSerializer;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.BrandView;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.CarView;
import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;
import com.github.priyajitbera.carkg.service.data.rdf.interfaces.Identifiable;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@JsonSerialize(using = VariantSemanticSerializer.class)
@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Variant implements Identifiable, CommonEntity<String, LocalDateTime> {

  @JsonView(BrandView.class)
  @RdfPredicate(
      value = "variantId",
      label = "Variant Identifier",
      comment = "Identifier of the car variant")
  @Id
  private String id;

  @CreationTimestamp private LocalDateTime createdAtUtc;

  @UpdateTimestamp private LocalDateTime updatedAtUtc;

  @JsonView({CarView.class, BrandView.class})
  @RdfPredicate(
      value = "variantName",
      label = "Variant Name",
      comment = "Name of the variant, e.g., LX, Sport, Top Model")
  private String name;

  @ManyToOne
  @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private Car car;

  public String deriveId() {
    assert car != null;
    assert name != null;
    return (new IdGen()).generate(car.deriveId(), name);
  }

  public void deriveAndSetId() {
    this.id = deriveId();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    Variant that = (Variant) object;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
