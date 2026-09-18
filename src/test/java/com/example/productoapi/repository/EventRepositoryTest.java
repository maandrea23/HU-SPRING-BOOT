package com.example.productoapi.repository;

import com.example.productoapi.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private EventRepository repository;

    @Test
    void guardaYBuscaPorParteDelNombre() {
        repository.save(new Event(null, "Taller Java", LocalDate.of(2026, 10, 15), "A"));
        repository.save(new Event(null, "Concierto", LocalDate.of(2026, 10, 20), "B"));

        var pagina = repository.findByNombreContaining("Taller", PageRequest.of(0, 5));

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Taller Java", pagina.getContent().get(0).getNombre());
    }

    @Test
    void paginaLosRegistros() {
        for (int i = 1; i <= 6; i++) {
            repository.save(new Event(null, "Evento " + i, LocalDate.of(2026, 10, i), ""));
        }

        var pagina = repository.findAll(PageRequest.of(0, 5));

        assertEquals(5, pagina.getNumberOfElements());
        assertEquals(6, pagina.getTotalElements());
        assertEquals(2, pagina.getTotalPages());
        assertTrue(pagina.hasNext());
    }
}
