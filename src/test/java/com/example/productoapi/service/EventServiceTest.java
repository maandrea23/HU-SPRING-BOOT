package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.model.Event;
import com.example.productoapi.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Prueba unitaria: no usa @SpringBootTest ni levanta el servidor.
@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository repository;
    private EventService service;

    @BeforeEach
    void preparar() {
        service = new EventService(repository);
    }

    @Test
    void registraDatosValidos() {
        Event entrada = valido();
        Event guardado = valido();
        guardado.setId(1L);
        when(repository.guardar(entrada)).thenReturn(guardado);

        Event resultado = service.registrar(entrada);

        assertEquals(1L, resultado.getId());
        assertEquals(entrada.getNombre(), resultado.getNombre());
        verify(repository).guardar(entrada);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void rechazaNombreInvalido(String nombre) {
        Event entrada = valido();
        entrada.setNombre(nombre);

        assertThrows(DatosInvalidosException.class, () -> service.registrar(entrada));
        verifyNoInteractions(repository);
    }

    @Test
    void rechazaObjetoNulo() {
        assertThrows(DatosInvalidosException.class, () -> service.registrar(null));
        verifyNoInteractions(repository);
    }

    @Test
    void devuelveListaVacia() {
        when(repository.listar()).thenReturn(List.of());
        assertTrue(service.listar().isEmpty());
        verify(repository).listar();
    }

    @Test
    void devuelveCatalogo() {
        when(repository.listar()).thenReturn(List.of(valido()));
        assertEquals(List.of(valido()), service.listar());
        verify(repository).listar();
    }

    @Test
    void rechazaFechaAusente() {
        Event event = valido();
        event.setFecha(null);
        assertThrows(DatosInvalidosException.class, () -> service.registrar(event));
        verifyNoInteractions(repository);
    }

    private Event valido() {
        return new Event(null, "Taller Java", LocalDate.of(2026, 10, 15), "Primer taller");
    }
}
