package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.exception.RecursoNoEncontradoException;
import com.example.productoapi.exception.ResourceNotFoundException;
import com.example.productoapi.exception.DuplicateResourceException;
import com.example.productoapi.dto.VenueCreateDTO;
import com.example.productoapi.dto.VenueResponseDTO;
import com.example.productoapi.mapper.VenueMapper;
import com.example.productoapi.model.Venue;
import com.example.productoapi.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class VenueService {
    private final VenueRepository repository;
    private final VenueMapper mapper;

    // Spring entrega el repositorio mediante este constructor.
    @Autowired
    public VenueService(VenueRepository repository, VenueMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /** Conserva compatibilidad con pruebas unitarias aisladas de semanas anteriores. */
    public VenueService(VenueRepository repository) {
        this(repository, org.mapstruct.factory.Mappers.getMapper(VenueMapper.class));
    }

    public VenueResponseDTO registrar(VenueCreateDTO dto) {
        if (repository.existsByNombreIgnoreCase(dto.nombre())) {
            throw new DuplicateResourceException("Ya existe un lugar con el nombre " + dto.nombre());
        }
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
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

    public Page<VenueResponseDTO> listarRespuestas(String nombre, Pageable pageable) {
        return listar(nombre, pageable).map(mapper::toResponse);
    }

    public Venue buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El lugar con ID " + id + " no existe"));
    }

    public VenueResponseDTO buscarRespuestaPorId(Long id) { return mapper.toResponse(buscarPorId(id)); }

    public VenueResponseDTO actualizar(Long id, VenueCreateDTO dto) {
        Venue existente = buscarPorId(id);
        Venue datos = mapper.toEntity(dto);
        existente.setNombre(datos.getNombre()); existente.setDireccion(datos.getDireccion());
        existente.setCapacidad(datos.getCapacidad()); existente.setCiudad(datos.getCiudad());
        return mapper.toResponse(repository.save(existente));
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
