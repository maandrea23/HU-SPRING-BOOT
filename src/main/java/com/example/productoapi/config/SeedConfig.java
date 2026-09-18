package com.example.productoapi.config;

import com.example.productoapi.model.Event;
import com.example.productoapi.model.Venue;
import com.example.productoapi.service.EventService;
import com.example.productoapi.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;

@Configuration
public class SeedConfig {
    private final EventService eventService;
    private final VenueService venueService;

    public SeedConfig(EventService eventService, VenueService venueService) {
        this.eventService = eventService;
        this.venueService = venueService;
    }

    @Bean
    @ConditionalOnProperty(name = "eventify.seed.enabled", havingValue = "true")
    public CommandLineRunner cargarDatosIniciales() {
        // Spring ejecuta este Bean una vez al iniciar la aplicación.
        return args -> {
            venueService.registrar(new Venue(null, "Auditorio Central", "Calle 10 # 20-30", 200));
            eventService.registrar(new Event(null, "Introducción a Spring Boot",
                    LocalDate.of(2026, 10, 15), "Encuentro para aprender a crear una API"));
        };
    }
}
