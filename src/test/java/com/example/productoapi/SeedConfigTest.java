package com.example.productoapi;

import com.example.productoapi.service.EventService;
import com.example.productoapi.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:seed-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "eventify.seed.enabled=true"
})
class SeedConfigTest {
    @Autowired
    private EventService events;
    @Autowired
    private VenueService venues;

    @Test
    void cargaLosDatosInicialesUnaSolaVez() {
        assertEquals(1, events.listar(null, Pageable.unpaged()).getTotalElements());
        assertEquals(1, venues.listar(null, Pageable.unpaged()).getTotalElements());
    }
}
