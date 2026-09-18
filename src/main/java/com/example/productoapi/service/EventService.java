package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.exception.RecursoNoEncontradoException;
import com.example.productoapi.model.Event;
import com.example.productoapi.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository repository;

    // Spring entrega el repositorio mediante este constructor.
    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Event registrar(Event event) {
        validar(event);
        event.setId(null);
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
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el evento con id " + id));
    }

    public Event actualizar(Long id, Event eventActualizado) {
        validar(eventActualizado);
        Event existente = buscarPorId(id);
        existente.setNombre(eventActualizado.getNombre());
        existente.setFecha(eventActualizado.getFecha());
        existente.setDescripcion(eventActualizado.getDescripcion());
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        Event existente = buscarPorId(id);
        repository.delete(existente);
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
