package com.example.productoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import com.example.productoapi.model.Venue;
import com.example.productoapi.repository.VenueRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:http-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "eventify.seed.enabled=false"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CatalogoHttpTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private VenueRepository venueRepository;

    @Test
    void listaVaciaIncluyeMetadatosDePaginacion() throws Exception {
        mvc.perform(get("/api/events?page=0&size=5&sort=nombre,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.numberOfElements").value(0))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void creaActualizaConsultaYPuedeEliminarEvento() throws Exception {
        Long venueId = crearVenue();
        String creado = mvc.perform(post("/api/events").contentType("application/json").content("""
                {"nombre":"Taller Java","fecha":"2026-10-15","descripcion":"Primera versión","venue":{"id":%d}}
                """.formatted(venueId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();
        Number id = com.jayway.jsonpath.JsonPath.read(creado, "$.id");

        mvc.perform(put("/api/events/{id}", id.longValue()).contentType("application/json").content("""
                {"nombre":"Taller Spring","fecha":"2026-10-20","descripcion":"Versión final","venue":{"id":%d}}
                """.formatted(venueId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Taller Spring"));
        mvc.perform(get("/api/events/{id}", id.longValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Versión final"));
        mvc.perform(delete("/api/events/{id}", id.longValue())).andExpect(status().isNoContent());
        mvc.perform(get("/api/events/{id}", id.longValue()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el evento con id " + id.longValue()));
    }

    @Test
    void paginaCincuentaEventosConOrdenamiento() throws Exception {
        Long venueId = crearVenue();
        for (int i = 1; i <= 50; i++) {
            mvc.perform(post("/api/events").contentType("application/json").content("""
                    {"nombre":"Curso %d","fecha":"2026-10-15","descripcion":"","venue":{"id":%d}}
                    """.formatted(i, venueId))).andExpect(status().isCreated());
        }
        mvc.perform(get("/api/events?page=0&size=5&sort=nombre,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.numberOfElements").value(5))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void rechazaEventoInvalidoYDevuelve404ParaIdAusente() throws Exception {
        mvc.perform(post("/api/events").contentType("application/json").content("""
                {"nombre":"","fecha":"2026-10-15"}
                """))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/events/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el evento con id 9999"));
        mvc.perform(put("/api/events/9999").contentType("application/json").content("""
                {"nombre":"Evento actualizado","fecha":"2026-10-15"}
                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el evento con id 9999"));
        mvc.perform(delete("/api/events/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el evento con id 9999"));
    }

    @Test
    void creaYEliminaLugar() throws Exception {
        String creado = mvc.perform(post("/api/venues").contentType("application/json").content("""
                {"nombre":"Auditorio","direccion":"Calle 10","capacidad":100,"ciudad":"Bogotá"}
                """))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Number id = com.jayway.jsonpath.JsonPath.read(creado, "$.id");
        mvc.perform(delete("/api/venues/{id}", id.longValue())).andExpect(status().isNoContent());
        mvc.perform(get("/api/venues/{id}", id.longValue())).andExpect(status().isNotFound());
    }

    @Test
    void swaggerIncluyeCrudYPaginacion() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/events'].get.parameters").isArray())
                .andExpect(jsonPath("$.paths['/api/events'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/events'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/api/events/{id}'].delete.responses['204']").exists())
                .andExpect(jsonPath("$.paths['/api/events/{id}'].get.responses['404']").exists());
    }

    private Long crearVenue() {
        return venueRepository.save(new Venue(null, "Auditorio Test", "Calle 1", 100, "Bogotá")).getId();
    }
}
