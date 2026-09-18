package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.model.Venue;
import com.example.productoapi.repository.VenueRepository;
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
class VenueServiceTest {
    @Mock
    private VenueRepository repository;
    private VenueService service;

    @BeforeEach
    void preparar() {
        service = new VenueService(repository);
    }

    @Test
    void registraDatosValidos() {
        Venue entrada = valido();
        Venue guardado = valido();
        guardado.setId(1L);
        when(repository.guardar(entrada)).thenReturn(guardado);

        Venue resultado = service.registrar(entrada);

        assertEquals(1L, resultado.getId());
        assertEquals(entrada.getNombre(), resultado.getNombre());
        verify(repository).guardar(entrada);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void rechazaNombreInvalido(String nombre) {
        Venue entrada = valido();
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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rechazaDireccionInvalida(String direccion) {
        Venue venue = valido();
        venue.setDireccion(direccion);
        assertThrows(DatosInvalidosException.class, () -> service.registrar(venue));
        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void rechazaCapacidadInvalida(Integer capacidad) {
        Venue venue = valido();
        venue.setCapacidad(capacidad);
        assertThrows(DatosInvalidosException.class, () -> service.registrar(venue));
        verifyNoInteractions(repository);
    }

    private Venue valido() {
        return new Venue(null, "Auditorio", "Calle 10", 100);
    }
}
