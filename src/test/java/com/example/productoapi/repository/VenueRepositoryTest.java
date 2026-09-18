package com.example.productoapi.repository;

import com.example.productoapi.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class VenueRepositoryTest {
    @Autowired
    private VenueRepository repository;

    @Test
    void guardaYBuscaPorParteDelNombre() {
        repository.save(new Venue(null, "Auditorio Central", "Calle 10", 200));
        repository.save(new Venue(null, "Teatro", "Carrera 7", 500));

        var pagina = repository.findByNombreContaining("Auditorio", PageRequest.of(0, 5));

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Auditorio Central", pagina.getContent().get(0).getNombre());
    }
}
