package com.example.productoapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venues")
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Generado por el servidor")
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 200)
    private String direccion;
    @Column(nullable = false)
    private Integer capacidad;
}
