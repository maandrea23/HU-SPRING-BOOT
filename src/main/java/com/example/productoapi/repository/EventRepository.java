package com.example.productoapi.repository;

import com.example.productoapi.model.Event;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EventRepository {
    private final Map<Long, Event> datos = new LinkedHashMap<>();
    private long siguienteId = 1;

    // synchronized evita conflictos si llegan dos peticiones al mismo tiempo.
    public synchronized Event guardar(Event event) {
        Event guardado = new Event(siguienteId++, event.getNombre(), event.getFecha(), event.getDescripcion());
        datos.put(guardado.getId(), guardado);
        return copiar(guardado);
    }

    public synchronized List<Event> listar() {
        List<Event> resultado = new ArrayList<>();
        for (Event event : datos.values()) {
            resultado.add(copiar(event));
        }
        return resultado;
    }

    // Las copias impiden modificar la coleccion desde fuera del repositorio.
    private Event copiar(Event event) {
        return new Event(event.getId(), event.getNombre(), event.getFecha(), event.getDescripcion());
    }
}
