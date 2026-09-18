package com.example.productoapi.controller;

import com.example.productoapi.model.Event;
import com.example.productoapi.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventos", description = "Catálogo persistente de eventos")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Consultar eventos paginados")
    @ApiResponse(responseCode = "200", description = "Página de eventos y metadatos")
    public ResponseEntity<Page<Event>> listar(
            @RequestParam(required = false) String nombre,
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nombre, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar evento")
    @ApiResponse(responseCode = "201", description = "Registro creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<Event> registrar(@RequestBody Event event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(event));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un evento por id")
    @ApiResponse(responseCode = "200", description = "Evento encontrado")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    public ResponseEntity<Event> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un evento")
    @ApiResponse(responseCode = "200", description = "Evento actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    public ResponseEntity<Event> actualizar(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(service.actualizar(id, event));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento")
    @ApiResponse(responseCode = "204", description = "Evento eliminado")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
