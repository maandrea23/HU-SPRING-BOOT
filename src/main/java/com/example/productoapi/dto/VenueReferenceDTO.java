package com.example.productoapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/** Compatibilidad temporal para clientes previos; la API documenta venueId como contrato actual. */
@Schema(hidden = true)
public record VenueReferenceDTO(@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) Long id) { }
