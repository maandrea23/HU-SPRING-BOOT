package com.example.productoapi.controller;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.model.Event;
import com.example.productoapi.model.Venue;
import com.example.productoapi.model.Category;
import com.example.productoapi.service.CategoryService;
import com.example.productoapi.service.EventService;
import com.example.productoapi.service.VenueService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final EventService eventService;
    private final VenueService venueService;
    private final CategoryService categoryService;

    public AdminController(EventService eventService, VenueService venueService, CategoryService categoryService) {
        this.eventService = eventService;
        this.venueService = venueService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String panel(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) String ciudad,
                        @RequestParam(required = false) String categoria,
                        @RequestParam(required = false) Integer capacidadMinima,
                        @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(
                                iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate desde,
                        @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(
                                iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate hasta,
                        Model model) {
        var resultados = eventService.buscarResumen(ciudad, categoria, capacidadMinima, desde, hasta,
                PageRequest.of(Math.max(page, 0), 10));
        model.addAttribute("eventos", resultados.getContent());
        model.addAttribute("haySiguiente", resultados.hasNext());
        model.addAttribute("hayAnterior", page > 0);
        model.addAttribute("paginaActual", Math.max(page, 0));
        model.addAttribute("ciudad", ciudad);
        model.addAttribute("categoria", categoria);
        model.addAttribute("capacidadMinima", capacidadMinima);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("categorias", categoryService.listar());
        model.addAttribute("lugares", venueService.listar(null, org.springframework.data.domain.Pageable.unpaged()).getContent());
        return "admin/panel";
    }

    @GetMapping("/events/nuevo")
    public String nuevoEvento(Model model) {
        model.addAttribute("event", new Event());
        cargarOpcionesFormulario(model);
        return "admin/event-form";
    }

    @PostMapping("/events")
    public String registrarEvento(@ModelAttribute("event") Event event,
                                  @RequestParam Long venueId,
                                  @RequestParam(required = false) List<Long> categoryIds,
                                  RedirectAttributes redirectAttributes) {
        try {
            event.setVenue(new Venue());
            event.getVenue().setId(venueId);
            event.setCategorias(new LinkedHashSet<>());
            if (categoryIds != null) {
                categoryIds.forEach(id -> event.getCategorias().add(new Category(id, null, null)));
            }
            eventService.registrar(event);
            redirectAttributes.addFlashAttribute("mensajeExito", "Evento registrado correctamente.");
            return "redirect:/admin";
        } catch (DatosInvalidosException exception) {
            redirectAttributes.addFlashAttribute("mensajeError", exception.getMessage());
            return "redirect:/admin/events/nuevo";
        }
    }

    @GetMapping("/venues/nuevo")
    public String nuevoLugar(Model model) {
        model.addAttribute("venue", new Venue());
        return "admin/venue-form";
    }

    @PostMapping("/venues")
    public String registrarLugar(@ModelAttribute("venue") Venue venue, RedirectAttributes redirectAttributes) {
        try {
            venueService.registrar(venue);
            redirectAttributes.addFlashAttribute("mensajeExito", "Lugar registrado correctamente.");
            return "redirect:/admin";
        } catch (DatosInvalidosException exception) {
            redirectAttributes.addFlashAttribute("mensajeError", exception.getMessage());
            return "redirect:/admin/venues/nuevo";
        }
    }

    private void cargarOpcionesFormulario(Model model) {
        model.addAttribute("lugares", venueService.listar(null, org.springframework.data.domain.Pageable.unpaged()).getContent());
        model.addAttribute("categorias", categoryService.listar());
    }
}
