package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.model.Venue;
import com.example.productoapi.repository.VenueRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VenueService {
    private final VenueRepository repository;

    // Spring entrega el repositorio mediante este constructor.
    public VenueService(VenueRepository repository) {
        this.repository = repository;
    }

    public Venue registrar(Venue venue) {
        if (venue == null || venue.getNombre() == null || venue.getNombre().isBlank()) {
            throw new DatosInvalidosException("El nombre es obligatorio");
        }
        if (venue.getDireccion() == null || venue.getDireccion().isBlank()) {
            throw new DatosInvalidosException("La dirección del lugar es obligatoria");
        }
        if (venue.getCapacidad() == null || venue.getCapacidad() <= 0) {
            throw new DatosInvalidosException("La capacidad debe ser mayor que cero");
        }
        return repository.guardar(venue);
    }

    public List<Venue> listar() {
        return repository.listar();
    }
}
