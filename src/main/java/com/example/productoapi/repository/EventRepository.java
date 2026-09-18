package com.example.productoapi.repository;

import com.example.productoapi.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByNombreContaining(String nombre, Pageable pageable);
}
