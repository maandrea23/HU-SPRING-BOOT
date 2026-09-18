package com.example.productoapi;

import com.example.productoapi.service.EventService;
import com.example.productoapi.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "eventify.seed.enabled=true")
class SeedConfigTest {
    private final EventService events;
    private final VenueService venues;

    @Autowired
    SeedConfigTest(EventService events, VenueService venues) {
        this.events = events;
        this.venues = venues;
    }

    @Test
    void cargaDatosAlArrancar() {
        assertEquals(1, events.listar().size());
        assertEquals(1, venues.listar().size());
        assertNotNull(events.listar().get(0).getId());
        assertNotNull(venues.listar().get(0).getId());
    }
}
