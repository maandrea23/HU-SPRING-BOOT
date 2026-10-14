package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.exception.RecursoNoEncontradoException;
import com.example.productoapi.model.Venue;
import com.example.productoapi.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class VenueService {
    private final VenueRepository repository;

    // Spring entrega el repositorio mediante este constructor.
    public VenueService(VenueRepository repository) {
        this.repository = repository;
    }

    public Venue registrar(Venue venue) {
        validar(venue);
        venue.setId(null);
        return repository.save(venue);
    }

    public Page<Venue> listar(String nombre, Pageable pageable) {
        if (nombre == null || nombre.isBlank()) {
            return repository.findAll(pageable);
        }
        return repository.findByNombreContaining(nombre, pageable);
    }

    public Venue buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el lugar con id " + id));
    }

    public Venue actualizar(Long id, Venue venueActualizado) {
        validar(venueActualizado);
        Venue existente = buscarPorId(id);
        existente.setNombre(venueActualizado.getNombre());
        existente.setDireccion(venueActualizado.getDireccion());
        existente.setCapacidad(venueActualizado.getCapacidad());
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        Venue existente = buscarPorId(id);
        repository.delete(existente);
    }

    private void validar(Venue venue) {
        if (venue == null || venue.getNombre() == null || venue.getNombre().isBlank()) {
            throw new DatosInvalidosException("El nombre es obligatorio");
        }
        if (venue.getDireccion() == null || venue.getDireccion().isBlank()) {
            throw new DatosInvalidosException("La dirección del lugar es obligatoria");
        }
        if (venue.getCapacidad() == null || venue.getCapacidad() <= 0) {
            throw new DatosInvalidosException("La capacidad debe ser mayor que cero");
        }
        if (venue.getCiudad() == null || venue.getCiudad().isBlank()) {
            throw new DatosInvalidosException("La ciudad del lugar es obligatoria");
        }
    }
}
