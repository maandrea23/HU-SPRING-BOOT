package com.example.productoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Complementa las pruebas unitarias verificando todas las capas juntas.
@SpringBootTest(properties = "eventify.seed.enabled=false")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CatalogoHttpTest {
    private final MockMvc mvc;

    @Autowired
    CatalogoHttpTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void catalogosVaciosResponden200() throws Exception {
        mvc.perform(get("/api/events")).andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get("/api/venues")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void registraYConsultaEvento() throws Exception {
        mvc.perform(post("/api/events").contentType("application/json").content("""
                {"nombre":"Taller Java","fecha":"2026-10-15","descripcion":"Aprender Spring"}
                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fecha").value("2026-10-15"));
        mvc.perform(get("/api/events")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Taller Java"));
    }

    @Test
    void eventoInvalidoNoSeGuarda() throws Exception {
        mvc.perform(post("/api/events").contentType("application/json").content("""
                {"nombre":"   ","fecha":"2026-10-15"}
                """))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensaje").value("El nombre es obligatorio"));
        mvc.perform(get("/api/events")).andExpect(content().json("[]"));
    }

    @Test
    void registraYConsultaLugar() throws Exception {
        mvc.perform(post("/api/venues").contentType("application/json").content("""
                {"nombre":"Auditorio","direccion":"Calle 10","capacidad":100}
                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(get("/api/venues")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].capacidad").value(100));
    }

    @Test
    void lugarInvalidoNoSeGuarda() throws Exception {
        mvc.perform(post("/api/venues").contentType("application/json").content("""
                {"nombre":"Auditorio","direccion":"Calle 10","capacidad":0}
                """))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensaje").exists());
        mvc.perform(get("/api/venues")).andExpect(content().json("[]"));
    }

    @Test
    void fechaMalFormadaDevuelve400() throws Exception {
        mvc.perform(post("/api/events").contentType("application/json").content("""
                {"nombre":"Taller","fecha":"15/10/2026"}
                """))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensaje").exists());
        mvc.perform(get("/api/events")).andExpect(content().json("[]"));
    }

    @Test
    void swaggerDocumentaLosCuatroMetodos() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/events'].get.summary").value("Consultar eventos"))
                .andExpect(jsonPath("$.paths['/api/events'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/api/venues'].get.summary").value("Consultar lugares"))
                .andExpect(jsonPath("$.paths['/api/venues'].post.responses['201']").exists());
        mvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
