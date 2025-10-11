package com.github.priyajitbera.carkg.service.api.embedding;

import com.github.priyajitbera.carkg.service.data.jpa.entity.Car;
import com.github.priyajitbera.carkg.service.data.jpa.view.serialization.CarView;
import org.springframework.stereotype.Component;

@Component
public class CarEmbeddableFormatter extends DefaultEmbeddableFormatter<Car> {

    @Override
    public Class<?> getView() {
        return CarView.class;
    }
}
