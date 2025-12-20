package com.github.priyajitbera.carkg.service.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.BrandView;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.CarView;
import com.github.priyajitbera.carkg.service.data.rdf.annotation.RdfPredicate;
import com.github.priyajitbera.carkg.service.data.rdf.interfaces.Identifiable;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
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
public class Brand implements Identifiable, CommonEntity<String, LocalDateTime> {

  @JsonView(BrandView.class)
  @RdfPredicate(value = "brandId", label = "Brand Identifier", comment = "Identifier of the brand")
  @Id
  private String id;

  @JsonIgnore @CreationTimestamp private LocalDateTime createdAtUtc;

  @JsonIgnore @UpdateTimestamp private LocalDateTime updatedAtUtc;

  @JsonView({CarView.class, BrandView.class})
  @RdfPredicate(
      value = "brandName",
      label = "Brand Name",
      comment =
          """
            A "Brand Name" of a car brand is the specific name a company uses to market and sell its cars, such as Maruti Suzuki, Hyundai, Tata Motors, Toyota, Honda, Ford, or Volkswagen. These names identify the manufacturer and often have origins in the founders' names (like Toyota) or historical figures (like Cadillac).""")
  private String name;

  @RdfPredicate(
      value = "brandCountryOfOrigin",
      label = "Brand Country of Origin",
      comment =
          "A car brand's country of origin is the country where the brand was founded and historically developed. For example, Toyota is a Japanese brand, Volkswagen is German, and Ford is American, according to various sources on car brands and manufacturers. While manufacturing may occur in other countries, the \"country of origin\" refers to the brand's original national roots.")
  private String countryOfOrigin;

  @JsonView(BrandView.class)
  @RdfPredicate(
      value = "cars",
      label = "Offered Cars",
      comment =
          """
            List of the cars offered by this brand""")
  @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
  private List<Car> cars;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "fk_brand_embedding"))
  private Embedding embedding;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "fk_brand_semantic_object"))
  private SemanticObject semanticObject;

  public String deriveId() {
    assert name != null;
    return name.replace(" ", "_").toLowerCase();
  }

  public void deriveAndSetId() {
    this.id = deriveId();
  }
}
