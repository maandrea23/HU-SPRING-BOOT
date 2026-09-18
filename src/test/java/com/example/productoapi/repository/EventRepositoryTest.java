package com.example.productoapi.repository;

import com.example.productoapi.model.Event;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class EventRepositoryTest {
    @Test
    void comienzaVacio() {
        assertTrue(new EventRepository().listar().isEmpty());
    }

    @Test
    void generaIdsYConservaLosRegistros() {
        EventRepository repository = new EventRepository();
        Event entrada = new Event(null, "Taller Java", LocalDate.of(2026, 10, 15), "Primer taller");
        entrada.setId(99L);
        Event primero = repository.guardar(entrada);
        Event segundo = repository.guardar(entrada);

        assertEquals(1L, primero.getId());
        assertEquals(2L, segundo.getId());
        assertEquals(2, repository.listar().size());
        assertEquals(primero, repository.listar().get(0));
    }

    @Test
    void protegeLosDatosDeCambiosExternos() {
        EventRepository repository = new EventRepository();
        Event entrada = new Event(null, "Taller Java", LocalDate.of(2026, 10, 15), "Primer taller");
        String nombreOriginal = entrada.getNombre();
        Event guardado = repository.guardar(entrada);
        entrada.setNombre("Cambio externo");
        guardado.setNombre("Otro cambio");
        repository.listar().get(0).setNombre("Cambio desde listado");
        repository.listar().clear();
        assertEquals(nombreOriginal, repository.listar().get(0).getNombre());
    }
}
