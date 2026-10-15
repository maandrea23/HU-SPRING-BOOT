package com.example.productoapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "Vista pública de un evento; no expone entidades ni identificadores relacionales")
public record EventResponseDTO(Long id, String nombre, LocalDate fecha, String descripcion,
                               String venueName, Set<String> categoryNames) { }
