package com.example.productoapi.dto;

import java.time.LocalDate;

public record EventSummaryDTO(Long id, String nombre, LocalDate fecha, String nombreLugar, String ciudad) {
}
