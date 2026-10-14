package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.exception.RecursoNoEncontradoException;
import com.example.productoapi.model.Venue;
import com.example.productoapi.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void rechazaCapacidadNoPositiva() {
        Venue venue = new Venue(null, "Auditorio", "Calle 10", 0, "Bogotá");
        assertThrows(DatosInvalidosException.class, () -> service.registrar(venue));
        verifyNoInteractions(repository);
    }

    @Test
    void actualizaLugarExistente() {
        Venue existente = new Venue(1L, "Auditorio", "Calle 10", 100, "Bogotá");
        Venue cambios = new Venue(null, "Auditorio nuevo", "Calle 20", 150, "Medellín");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Venue resultado = service.actualizar(1L, cambios);

        assertEquals("Auditorio nuevo", resultado.getNombre());
        assertEquals(150, resultado.getCapacidad());
    }

    @Test
    void informaCuandoNoExisteElLugar() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(999L));
        verify(repository, never()).delete(any());
    }
}
