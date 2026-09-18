package com.example.productoapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Generado por el servidor")
    private Long id;
    private String nombre;
    private String direccion;
    private Integer capacidad;
}
