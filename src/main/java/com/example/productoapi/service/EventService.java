package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.exception.RecursoNoEncontradoException;
import com.example.productoapi.exception.ResourceNotFoundException;
import com.example.productoapi.exception.BusinessRuleViolationException;
import com.example.productoapi.dto.EventCreateDTO;
import com.example.productoapi.dto.EventResponseDTO;
import com.example.productoapi.mapper.EventMapper;
import com.example.productoapi.model.Event;
import com.example.productoapi.model.Category;
import com.example.productoapi.model.Venue;
import com.example.productoapi.dto.EventSummaryDTO;
import com.example.productoapi.repository.CategoryRepository;
import com.example.productoapi.repository.EventRepository;
import com.example.productoapi.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;


@Service
public class EventService {
    private final EventRepository repository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper mapper;

    // Spring entrega el repositorio mediante este constructor.
    @Autowired
    public EventService(EventRepository repository, VenueRepository venueRepository,
                        CategoryRepository categoryRepository, EventMapper mapper) {
        this.repository = repository;
        this.venueRepository = venueRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    /** Conserva compatibilidad con pruebas unitarias aisladas de semanas anteriores. */
    public EventService(EventRepository repository, VenueRepository venueRepository, CategoryRepository categoryRepository) {
        this(repository, venueRepository, categoryRepository, org.mapstruct.factory.Mappers.getMapper(EventMapper.class));
    }

    public EventResponseDTO registrar(EventCreateDTO dto) {
        Event event = mapper.toEntity(dto);
        event.setActive(true);
        prepararRelaciones(event, venueId(dto), dto.categoryIds());
        return mapper.toResponse(repository.save(event));
    }

    public Event registrar(Event event) {
        validar(event);
        event.setId(null);
        prepararRelaciones(event);
        return repository.save(event);
    }

    public Page<Event> listar(String nombre, Pageable pageable) {
        if (nombre == null || nombre.isBlank()) {
            return repository.findAll(pageable);
        }
        return repository.findByNombreContaining(nombre, pageable);
    }

    public Event buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El evento con ID " + id + " no existe"));
    }

    public EventResponseDTO buscarRespuestaPorId(Long id) { return mapper.toResponse(buscarPorId(id)); }

    public EventResponseDTO actualizar(Long id, EventCreateDTO dto) {
        Event existente = buscarPorId(id);
        Event datos = mapper.toEntity(dto);
        existente.setNombre(datos.getNombre());
        existente.setFecha(datos.getFecha());
        existente.setDescripcion(datos.getDescripcion());
        prepararRelaciones(existente, venueId(dto), dto.categoryIds());
        return mapper.toResponse(repository.save(existente));
    }

    public Event actualizar(Long id, Event eventActualizado) {
        validar(eventActualizado);
        Event existente = buscarPorId(id);
        existente.setNombre(eventActualizado.getNombre());
        existente.setFecha(eventActualizado.getFecha());
        existente.setDescripcion(eventActualizado.getDescripcion());
        existente.setVenue(eventActualizado.getVenue());
        existente.setCategorias(eventActualizado.getCategorias());
        prepararRelaciones(existente);
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        Event existente = buscarPorId(id);
        existente.setActive(false);
        repository.save(existente);
    }

    public Slice<EventSummaryDTO> buscarResumen(String ciudad, String categoria, Integer capacidadMinima,
                                                  LocalDate desde, LocalDate hasta, Pageable pageable) {
        return repository.buscarResumen(normalizar(ciudad), normalizar(categoria), capacidadMinima, desde, hasta,
                pageable);
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private void prepararRelaciones(Event event) {
        if (event.getVenue() == null || event.getVenue().getId() == null) {
            throw new DatosInvalidosException("Debe seleccionar un lugar para el evento");
        }
        Venue venue = venueRepository.findById(event.getVenue().getId())
                .orElseThrow(() -> new DatosInvalidosException("El lugar seleccionado no existe"));
        event.setVenue(venue);
        if (event.getCategorias() == null || event.getCategorias().isEmpty()) {
            event.setCategorias(new LinkedHashSet<>());
            return;
        }
        List<Long> ids = event.getCategorias().stream().map(Category::getId).filter(java.util.Objects::nonNull).toList();
        var categorias = categoryRepository.findAllById(ids);
        if (categorias.size() != ids.size()) {
            throw new DatosInvalidosException("Una categoría seleccionada no existe");
        }
        event.setCategorias(new LinkedHashSet<>(categorias));
    }

    private void prepararRelaciones(Event event, Long venueId, java.util.Set<Long> categoryIds) {
        if (venueId == null) throw new BusinessRuleViolationException("Debe seleccionar un lugar para el evento");
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("El lugar con ID " + venueId + " no existe"));
        event.setVenue(venue);
        if (categoryIds == null || categoryIds.isEmpty()) { event.setCategorias(new LinkedHashSet<>()); return; }
        List<Category> categorias = categoryRepository.findAllById(categoryIds);
        if (categorias.size() != categoryIds.size()) {
            throw new BusinessRuleViolationException("Una categoría seleccionada no existe");
        }
        event.setCategorias(new LinkedHashSet<>(categorias));
    }

    private Long venueId(EventCreateDTO dto) {
        return dto.venueId() != null ? dto.venueId() : dto.venue() == null ? null : dto.venue().id();
    }

    private void validar(Event event) {
        if (event == null || event.getNombre() == null || event.getNombre().isBlank()) {
            throw new DatosInvalidosException("El nombre es obligatorio");
        }
        if (event.getFecha() == null) {
            throw new DatosInvalidosException("La fecha del evento es obligatoria");
        }
    }
}
