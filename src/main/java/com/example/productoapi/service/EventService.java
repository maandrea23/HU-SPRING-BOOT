package com.example.productoapi.service;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.model.Event;
import com.example.productoapi.repository.EventRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;

    // Spring entrega el repositorio mediante este constructor.
    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Event registrar(Event event) {
        if (event == null || event.getNombre() == null || event.getNombre().isBlank()) {
            throw new DatosInvalidosException("El nombre es obligatorio");
        }
        if (event.getFecha() == null) {
            throw new DatosInvalidosException("La fecha del evento es obligatoria");
        }
        return repository.guardar(event);
    }

    public List<Event> listar() {
        return repository.listar();
    }
}
