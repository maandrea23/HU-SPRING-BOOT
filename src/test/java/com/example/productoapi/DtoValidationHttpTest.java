package com.example.productoapi;

import com.example.productoapi.controller.EventController;
import com.example.productoapi.exception.ResourceNotFoundException;
import com.example.productoapi.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class DtoValidationHttpTest {
    @Autowired MockMvc mvc;
    @MockBean EventService eventService;

    @Test
    void eventoInvalidoDevuelveProblemDetailConErroresPorCampo() throws Exception {
        String body = "{\"nombre\":\"\",\"fecha\":\"" + LocalDate.now().minusDays(1) + "\",\"venueId\":1}";
        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.fecha").exists());
    }

    @Test
    void eventoAusenteDevuelveProblemDetailRfc7807() throws Exception {
        when(eventService.buscarRespuestaPorId(9999L))
                .thenThrow(new ResourceNotFoundException("El evento con ID 9999 no existe"));
        mvc.perform(get("/api/events/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.detail").value("El evento con ID 9999 no existe"))
                .andExpect(jsonPath("$.instance").value("/api/events/9999"));
    }
}
