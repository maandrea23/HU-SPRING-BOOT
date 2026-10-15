package com.example.productoapi.controller;

import com.example.productoapi.dto.EventCreateDTO;
import com.example.productoapi.dto.EventResponseDTO;
import com.example.productoapi.validation.CreateValidation;
import com.example.productoapi.validation.UpdateValidation;
import com.example.productoapi.dto.EventSummaryDTO;
import com.example.productoapi.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventos", description = "Catálogo persistente de eventos")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Consultar catálogo optimizado de eventos",
            description = "Devuelve un Slice de resúmenes ordenados por fecha descendente. Los eventos desactivados " +
                    "por borrado lógico no aparecen en ningún resultado.")
    @ApiResponse(responseCode = "200", description = "Fragmento de EventSummaryDTO y estado de navegación")
    public ResponseEntity<Slice<EventSummaryDTO>> listar(
            @io.swagger.v3.oas.annotations.Parameter(description = "Ciudad parcial, sin distinguir mayúsculas")
            @RequestParam(required = false) String ciudad,
            @io.swagger.v3.oas.annotations.Parameter(description = "Categoría parcial, sin distinguir mayúsculas")
            @RequestParam(required = false) String categoria,
            @io.swagger.v3.oas.annotations.Parameter(description = "Capacidad mínima del lugar")
            @RequestParam(required = false) Integer capacidadMinima,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @ParameterObject @PageableDefault(size = 10, sort = "fecha", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(service.buscarResumen(ciudad, categoria, capacidadMinima, desde, hasta, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar evento")
    @ApiResponse(responseCode = "201", description = "Registro creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Regla de negocio incumplida", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Error inesperado", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<EventResponseDTO> registrar(@Valid @Validated(CreateValidation.class) @RequestBody EventCreateDTO event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(event));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un evento por id")
    @ApiResponse(responseCode = "200", description = "Evento encontrado")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<EventResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarRespuestaPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un evento")
    @ApiResponse(responseCode = "200", description = "Evento actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    @ApiResponse(responseCode = "409", description = "Regla de negocio incumplida")
    @ApiResponse(responseCode = "500", description = "Error inesperado")
    public ResponseEntity<EventResponseDTO> actualizar(@PathVariable Long id,
            @Valid @Validated(UpdateValidation.class) @RequestBody EventCreateDTO event) {
        return ResponseEntity.ok(service.actualizar(id, event));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un evento", description = "Aplica borrado lógico: el evento se conserva para auditoría " +
            "pero queda excluido automáticamente de consultas posteriores.")
    @ApiResponse(responseCode = "204", description = "Evento desactivado")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    @ApiResponse(responseCode = "500", description = "Error inesperado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
