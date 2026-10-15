package com.example.productoapi.controller;

import com.example.productoapi.dto.EventCreateDTO;
import com.example.productoapi.dto.VenueCreateDTO;
import com.example.productoapi.validation.CreateValidation;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Set;

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
        model.addAttribute("event", new EventCreateDTO(null, null, null, null, Set.of(), null));
        cargarOpcionesFormulario(model);
        return "admin/event-form";
    }

    @PostMapping("/events")
    public String registrarEvento(@Validated(CreateValidation.class) @ModelAttribute("event") EventCreateDTO event,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarOpcionesFormulario(model);
            return "admin/event-form";
        }
        try {
            eventService.registrar(event);
            redirectAttributes.addFlashAttribute("mensajeExito", "Evento registrado correctamente.");
            return "redirect:/admin";
        } catch (RuntimeException exception) {
            bindingResult.reject("event", exception.getMessage());
            cargarOpcionesFormulario(model);
            return "admin/event-form";
        }
    }

    @GetMapping("/venues/nuevo")
    public String nuevoLugar(Model model) {
        model.addAttribute("venue", new VenueCreateDTO(null, null, null, null));
        return "admin/venue-form";
    }

    @PostMapping("/venues")
    public String registrarLugar(@Validated(CreateValidation.class) @ModelAttribute("venue") VenueCreateDTO venue,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "admin/venue-form";
        try {
            venueService.registrar(venue);
            redirectAttributes.addFlashAttribute("mensajeExito", "Lugar registrado correctamente.");
            return "redirect:/admin";
        } catch (RuntimeException exception) {
            bindingResult.reject("venue", exception.getMessage());
            return "admin/venue-form";
        }
    }

    private void cargarOpcionesFormulario(Model model) {
        model.addAttribute("lugares", venueService.listar(null, org.springframework.data.domain.Pageable.unpaged()).getContent());
        model.addAttribute("categorias", categoryService.listar());
    }
}
