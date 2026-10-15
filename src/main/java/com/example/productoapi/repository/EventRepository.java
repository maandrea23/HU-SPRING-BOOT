package com.example.productoapi.repository;

import com.example.productoapi.model.Event;
import com.example.productoapi.dto.EventSummaryDTO;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Override
    @EntityGraph(attributePaths = {"venue", "categorias"})
    Optional<Event> findById(Long id);
    Page<Event> findByNombreContaining(String nombre, Pageable pageable);

    @EntityGraph(attributePaths = {"venue", "categorias"})
    Slice<Event> findByActiveTrueOrderByFechaDesc(Pageable pageable);

    @Query("""
            select distinct new com.example.productoapi.dto.EventSummaryDTO(e.id, e.nombre, e.fecha, v.nombre, v.ciudad)
            from Event e join e.venue v left join e.categorias c
            where e.active = true
              and (:ciudad is null or lower(v.ciudad) like lower(concat('%', :ciudad, '%')))
              and (:categoria is null or lower(c.nombre) like lower(concat('%', :categoria, '%')))
              and (:capacidadMinima is null or v.capacidad >= :capacidadMinima)
              and (:desde is null or e.fecha >= :desde)
              and (:hasta is null or e.fecha <= :hasta)
            order by e.fecha desc
            """)
    Slice<EventSummaryDTO> buscarResumen(@Param("ciudad") String ciudad,
                                         @Param("categoria") String categoria,
                                         @Param("capacidadMinima") Integer capacidadMinima,
                                         @Param("desde") java.time.LocalDate desde,
                                         @Param("hasta") java.time.LocalDate hasta,
                                         Pageable pageable);

    Slice<Event> findByVenueCiudadContainingIgnoreCaseOrderByFechaDesc(String ciudad, Pageable pageable);

    Slice<Event> findByFechaBetweenOrderByFechaDesc(java.time.LocalDate desde, java.time.LocalDate hasta,
                                                     Pageable pageable);
}
