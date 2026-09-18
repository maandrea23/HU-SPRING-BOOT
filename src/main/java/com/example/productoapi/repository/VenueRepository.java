package com.example.productoapi.repository;

import com.example.productoapi.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Page<Venue> findByNombreContaining(String nombre, Pageable pageable);
}
