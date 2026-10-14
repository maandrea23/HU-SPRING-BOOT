package com.example.productoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import com.example.productoapi.model.Venue;
import com.example.productoapi.repository.VenueRepository;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:admin-view-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "eventify.seed.enabled=false"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminViewTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private VenueRepository venueRepository;

    @Test
    void panelDevuelveVistaYListasDelModelo() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/panel"))
                .andExpect(model().attributeExists("eventos", "lugares"))
                .andExpect(content().string(containsString("Actualmente no hay eventos programados")));
    }

    @Test
    void formulariosDevuelvenSusObjetosDeModelo() throws Exception {
        mvc.perform(get("/admin/events/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/event-form"))
                .andExpect(model().attributeExists("event"));
        mvc.perform(get("/admin/venues/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/venue-form"))
                .andExpect(model().attributeExists("venue"));
    }

    @Test
    void registrarEventoRedirigeAlPanelYMuestraElRegistro() throws Exception {
        Long venueId = venueRepository.save(new Venue(null, "Auditorio", "Calle 1", 100, "Bogotá")).getId();
        mvc.perform(post("/admin/events")
                        .param("nombre", "Conferencia Eventify")
                        .param("fecha", "2026-11-10")
                        .param("descripcion", "Panel web")
                        .param("venueId", venueId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attribute("mensajeExito", "Evento registrado correctamente."));

        mvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Conferencia Eventify")));
    }

    @Test
    void swaggerNoIncluyeRutasAdministrativas() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("/admin"))));
    }
}
