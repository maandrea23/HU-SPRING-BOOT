package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.exception.RecursoNoEncontradoException;
import com.example.productoapi.model.Event;
import com.example.productoapi.model.Venue;
import com.example.productoapi.repository.CategoryRepository;
import com.example.productoapi.repository.EventRepository;
import com.example.productoapi.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository repository;
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private CategoryRepository categoryRepository;
    private EventService service;

    @BeforeEach
    void preparar() {
        service = new EventService(repository, venueRepository, categoryRepository);
    }

    @Test
    void registraEventoValidoSinConservarElIdDelCliente() {
        Event entrada = new Event(99L, "Taller Java", LocalDate.of(2026, 10, 15), "Primer taller");
        entrada.setVenue(new Venue());
        entrada.getVenue().setId(1L);
        when(venueRepository.findById(1L)).thenReturn(Optional.of(new Venue(1L, "Auditorio", "Calle", 100, "Bogotá")));
        when(repository.save(any(Event.class))).thenAnswer(invocation -> {
            Event evento = invocation.getArgument(0);
            assertNull(evento.getId());
            evento.setId(1L);
            return evento;
        });

        Event resultado = service.registrar(entrada);

        assertEquals(1L, resultado.getId());
        verify(repository).save(entrada);
    }

    @Test
    void rechazaNombreVacioAntesDeGuardar() {
        Event evento = new Event(null, " ", LocalDate.now(), "");
        assertThrows(DatosInvalidosException.class, () -> service.registrar(evento));
        verifyNoInteractions(repository);
    }

    @Test
    void actualizaUnEventoExistente() {
        Event existente = new Event(1L, "Nombre anterior", LocalDate.of(2026, 1, 1), "A");
        Event cambios = new Event(null, "Nombre nuevo", LocalDate.of(2026, 2, 2), "B");
        cambios.setVenue(new Venue());
        cambios.getVenue().setId(1L);
        when(venueRepository.findById(1L)).thenReturn(Optional.of(new Venue(1L, "Auditorio", "Calle", 100, "Bogotá")));
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Event resultado = service.actualizar(1L, cambios);

        assertEquals("Nombre nuevo", resultado.getNombre());
        assertEquals(LocalDate.of(2026, 2, 2), resultado.getFecha());
        verify(repository).save(existente);
    }

    @Test
    void informaCuandoNoExisteElEvento() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(999L));
    }

    @Test
    void eliminaSoloSiExiste() {
        Event existente = new Event(3L, "Evento", LocalDate.now(), "");
        when(repository.findById(3L)).thenReturn(Optional.of(existente));

        service.eliminar(3L);

        verify(repository).save(existente);
        assertFalse(existente.getActive());
    }
}
