package com.example.productoapi.repository;

import com.example.productoapi.model.Venue;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VenueRepository {
    private final Map<Long, Venue> datos = new LinkedHashMap<>();
    private long siguienteId = 1;

    // synchronized evita conflictos si llegan dos peticiones al mismo tiempo.
    public synchronized Venue guardar(Venue venue) {
        Venue guardado = new Venue(siguienteId++, venue.getNombre(), venue.getDireccion(), venue.getCapacidad());
        datos.put(guardado.getId(), guardado);
        return copiar(guardado);
    }

    public synchronized List<Venue> listar() {
        List<Venue> resultado = new ArrayList<>();
        for (Venue venue : datos.values()) {
            resultado.add(copiar(venue));
        }
        return resultado;
    }

    // Las copias impiden modificar la coleccion desde fuera del repositorio.
    private Venue copiar(Venue venue) {
        return new Venue(venue.getId(), venue.getNombre(), venue.getDireccion(), venue.getCapacidad());
    }
}
