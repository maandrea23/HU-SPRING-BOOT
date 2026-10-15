package com.example.productoapi.controller;

import com.example.productoapi.dto.VenueCreateDTO;
import com.example.productoapi.dto.VenueResponseDTO;
import com.example.productoapi.validation.CreateValidation;
import com.example.productoapi.validation.UpdateValidation;
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
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

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
    public ResponseEntity<Page<VenueResponseDTO>> listar(
            @RequestParam(required = false) String nombre,
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(service.listarRespuestas(nombre, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar lugar")
    @ApiResponse(responseCode = "201", description = "Registro creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Lugar duplicado o regla de negocio incumplida", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Error inesperado")
    public ResponseEntity<VenueResponseDTO> registrar(@Valid @Validated(CreateValidation.class) @RequestBody VenueCreateDTO venue) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(venue));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un lugar por id")
    @ApiResponse(responseCode = "200", description = "Lugar encontrado")
    @ApiResponse(responseCode = "404", description = "Lugar no encontrado")
    @ApiResponse(responseCode = "500", description = "Error inesperado")
    public ResponseEntity<VenueResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarRespuestaPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un lugar")
    @ApiResponse(responseCode = "200", description = "Lugar actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Lugar no encontrado")
    @ApiResponse(responseCode = "409", description = "Regla de negocio incumplida")
    @ApiResponse(responseCode = "500", description = "Error inesperado")
    public ResponseEntity<VenueResponseDTO> actualizar(@PathVariable Long id,
            @Valid @Validated(UpdateValidation.class) @RequestBody VenueCreateDTO venue) {
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
