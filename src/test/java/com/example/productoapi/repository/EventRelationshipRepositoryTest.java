package com.example.productoapi.repository;

import com.example.productoapi.model.Category;
import com.example.productoapi.model.Event;
import com.example.productoapi.model.Venue;
import com.example.productoapi.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class EventRelationshipRepositoryTest {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void filtraDeFormaParcialIgnorandoMayusculasYOcultaElBorradoLogico() {
        Venue venue = venueRepository.save(new Venue(null, "Arena Rock", "Calle 1", 900, "Bogotá"));
        Category category = categoryRepository.save(new Category(null, "Conciertos ROCK", "Música"));
        Event event = new Event(null, "Concierto de ROCK", LocalDate.of(2026, 12, 1), "Prueba");
        event.setVenue(venue);
        event.setCategorias(new LinkedHashSet<>(java.util.List.of(category)));
        event = eventRepository.saveAndFlush(event);

        var resultados = eventRepository.buscarResumen("bog", "rock", 500, null, null, PageRequest.of(0, 5));

        assertEquals(1, resultados.getNumberOfElements());
        assertEquals("Concierto de ROCK", resultados.getContent().getFirst().nombre());

        new EventService(eventRepository, venueRepository, categoryRepository).eliminar(event.getId());
        entityManager.flush();
        entityManager.clear();

        assertTrue(eventRepository.findById(event.getId()).isEmpty());
    }
}
