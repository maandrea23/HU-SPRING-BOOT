package com.example.productoapi.controller;

import com.example.productoapi.model.Venue;
import com.example.productoapi.service.VenueService;
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
@RequestMapping("/api/venues")
@Tag(name = "Lugares", description = "Catálogo persistente de lugares")
public class VenueController {
    private final VenueService service;

    public VenueController(VenueService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Consultar lugares paginados")
    @ApiResponse(responseCode = "200", description = "Página de lugares y metadatos")
    public ResponseEntity<Page<Venue>> listar(
            @RequestParam(required = false) String nombre,
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nombre, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar lugar")
    @ApiResponse(responseCode = "201", description = "Registro creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<Venue> registrar(@RequestBody Venue venue) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(venue));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un lugar por id")
    @ApiResponse(responseCode = "200", description = "Lugar encontrado")
    @ApiResponse(responseCode = "404", description = "Lugar no encontrado")
    public ResponseEntity<Venue> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un lugar")
    @ApiResponse(responseCode = "200", description = "Lugar actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Lugar no encontrado")
    public ResponseEntity<Venue> actualizar(@PathVariable Long id, @RequestBody Venue venue) {
        return ResponseEntity.ok(service.actualizar(id, venue));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un lugar")
    @ApiResponse(responseCode = "204", description = "Lugar eliminado")
    @ApiResponse(responseCode = "404", description = "Lugar no encontrado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
