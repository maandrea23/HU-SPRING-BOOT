package com.example.productoapi.dto;

import com.example.productoapi.validation.CreateValidation;
import com.example.productoapi.validation.NoPastEvents;
import com.example.productoapi.validation.UpdateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import jakarta.validation.groups.Default;

@Schema(description = "Datos permitidos para crear o actualizar un evento")
public record EventCreateDTO(
        @NotBlank(message = "no debe estar vacío", groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Size(min = 3, max = 100, message = "debe tener entre 3 y 100 caracteres", groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Schema(description = "Nombre público del evento", example = "Concierto de ROCK", minLength = 3, maxLength = 100) String nombre,
        @NotNull(message = "no debe ser nula", groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Future(message = "debe ser una fecha futura", groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @NoPastEvents(groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Schema(description = "Fecha futura del evento", example = "2027-08-15") LocalDate fecha,
        @Size(max = 500, message = "no puede superar 500 caracteres", groups = {Default.class, CreateValidation.class, UpdateValidation.class})
        @Schema(example = "Presentación principal del festival", maxLength = 500) String descripcion,
        @Schema(example = "1") Long venueId,
        @Schema(example = "[1, 3]") Set<Long> categoryIds,
        @Schema(hidden = true) VenueReferenceDTO venue) { }
