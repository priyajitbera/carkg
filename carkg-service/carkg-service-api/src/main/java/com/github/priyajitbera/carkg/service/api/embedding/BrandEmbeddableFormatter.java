package com.github.priyajitbera.carkg.service.api.embedding;

import com.github.priyajitbera.carkg.service.data.jpa.entity.Brand;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.BrandView;
import org.springframework.stereotype.Component;

@Component
public class BrandEmbeddableFormatter extends DefaultEmbeddableFormatter<Brand> {

  @Override
  public Class<?> getView() {
    return BrandView.class;
  }
}
