package com.example.productoapi.controller;

import com.example.productoapi.model.Event;
import com.example.productoapi.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventos", description = "Catálogo de eventos en memoria")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Consultar eventos")
    @ApiResponse(responseCode = "200", description = "Listado, posiblemente vacío")
    public ResponseEntity<List<Event>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    @Operation(summary = "Registrar evento")
    @ApiResponse(responseCode = "201", description = "Registro creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<Event> registrar(@RequestBody Event event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(event));
    }
}
