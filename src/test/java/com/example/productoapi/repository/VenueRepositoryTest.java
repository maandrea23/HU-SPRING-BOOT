package com.example.productoapi.repository;

import com.example.productoapi.model.Venue;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class VenueRepositoryTest {
    @Test
    void comienzaVacio() {
        assertTrue(new VenueRepository().listar().isEmpty());
    }

    @Test
    void generaIdsYConservaLosRegistros() {
        VenueRepository repository = new VenueRepository();
        Venue entrada = new Venue(null, "Auditorio", "Calle 10", 100);
        entrada.setId(99L);
        Venue primero = repository.guardar(entrada);
        Venue segundo = repository.guardar(entrada);

        assertEquals(1L, primero.getId());
        assertEquals(2L, segundo.getId());
        assertEquals(2, repository.listar().size());
        assertEquals(primero, repository.listar().get(0));
    }

    @Test
    void protegeLosDatosDeCambiosExternos() {
        VenueRepository repository = new VenueRepository();
        Venue entrada = new Venue(null, "Auditorio", "Calle 10", 100);
        String nombreOriginal = entrada.getNombre();
        Venue guardado = repository.guardar(entrada);
        entrada.setNombre("Cambio externo");
        guardado.setNombre("Otro cambio");
        repository.listar().get(0).setNombre("Cambio desde listado");
        repository.listar().clear();
        assertEquals(nombreOriginal, repository.listar().get(0).getNombre());
    }
}
