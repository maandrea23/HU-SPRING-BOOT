package com.example.productoapi.dto;

import com.example.productoapi.validation.CreateValidation;
import com.example.productoapi.validation.UpdateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

@Schema(description = "Datos permitidos para crear o actualizar un lugar")
public record VenueCreateDTO(
        @NotBlank(message = "no debe estar vacío", groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Size(min = 3, max = 100, groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Schema(example = "Teatro Central", minLength = 3, maxLength = 100) String nombre,
        @NotBlank(message = "no debe estar vacía", groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Size(max = 200, groups = {Default.class, CreateValidation.class, UpdateValidation.class}) String direccion,
        @Min(value = 1, message = "debe ser mayor o igual a 1", groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Schema(example = "500", minimum = "1") Integer capacidad,
        @NotBlank(message = "no debe estar vacía", groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Size(max = 100, groups = {Default.class, CreateValidation.class, UpdateValidation.class}) String ciudad) { }
