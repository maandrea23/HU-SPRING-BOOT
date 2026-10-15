package com.example.productoapi.mapper;

import com.example.productoapi.dto.EventCreateDTO;
import com.example.productoapi.dto.EventResponseDTO;
import com.example.productoapi.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "categorias", ignore = true)
    Event toEntity(EventCreateDTO source);

    @Mapping(target = "venueName", source = "venue.nombre")
    @Mapping(target = "categoryNames", expression = "java(event.getCategorias().stream().map(com.example.productoapi.model.Category::getNombre).collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new)))")
    EventResponseDTO toResponse(Event event);
}
