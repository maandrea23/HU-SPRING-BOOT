package com.example.productoapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Vista pública de un lugar")
public record VenueResponseDTO(Long id, String nombre, String direccion, Integer capacidad, String ciudad) { }
