package com.example.productoapi.controller;

import com.example.productoapi.model.Venue;
import com.example.productoapi.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@Tag(name = "Lugares", description = "Catálogo de lugares en memoria")
public class VenueController {
    private final VenueService service;

    public VenueController(VenueService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Consultar lugares")
    @ApiResponse(responseCode = "200", description = "Listado, posiblemente vacío")
    public ResponseEntity<List<Venue>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    @Operation(summary = "Registrar lugar")
    @ApiResponse(responseCode = "201", description = "Registro creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<Venue> registrar(@RequestBody Venue venue) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(venue));
    }
}
