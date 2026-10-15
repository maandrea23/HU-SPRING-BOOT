package com.example.productoapi.mapper;

import com.example.productoapi.dto.VenueCreateDTO;
import com.example.productoapi.dto.VenueResponseDTO;
import com.example.productoapi.model.Venue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VenueMapper {
    @Mapping(target = "id", ignore = true) Venue toEntity(VenueCreateDTO source);
    VenueResponseDTO toResponse(Venue venue);
}
