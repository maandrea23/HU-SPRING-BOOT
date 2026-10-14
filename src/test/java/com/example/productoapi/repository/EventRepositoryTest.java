package com.example.productoapi.repository;

import com.example.productoapi.model.Event;
import com.example.productoapi.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class EventRepositoryTest {
    @Autowired
    private EventRepository repository;
    @Autowired
    private VenueRepository venueRepository;

    @Test
    void guardaYBuscaPorParteDelNombre() {
        Venue venue = venueRepository.save(new Venue(null, "Auditorio", "Calle 10", 200, "Bogotá"));
        repository.save(evento("Taller Java", LocalDate.of(2026, 10, 15), venue));
        repository.save(evento("Concierto", LocalDate.of(2026, 10, 20), venue));

        var pagina = repository.findByNombreContaining("Taller", PageRequest.of(0, 5));

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Taller Java", pagina.getContent().get(0).getNombre());
    }

    @Test
    void paginaLosRegistros() {
        Venue venue = venueRepository.save(new Venue(null, "Auditorio", "Calle 10", 200, "Bogotá"));
        for (int i = 1; i <= 6; i++) {
            repository.save(evento("Evento " + i, LocalDate.of(2026, 10, i), venue));
        }

        var pagina = repository.findAll(PageRequest.of(0, 5));

        assertEquals(5, pagina.getNumberOfElements());
        assertEquals(6, pagina.getTotalElements());
        assertEquals(2, pagina.getTotalPages());
        assertTrue(pagina.hasNext());
    }

    private Event evento(String nombre, LocalDate fecha, Venue venue) {
        Event event = new Event(null, nombre, fecha, "");
        event.setVenue(venue);
        return event;
    }
}
