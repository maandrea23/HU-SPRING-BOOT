package com.example.productoapi.controller;

import com.example.productoapi.exception.DatosInvalidosException;
import com.example.productoapi.model.Event;
import com.example.productoapi.model.Venue;
import com.example.productoapi.service.EventService;
import com.example.productoapi.service.VenueService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final EventService eventService;
    private final VenueService venueService;

    public AdminController(EventService eventService, VenueService venueService) {
        this.eventService = eventService;
        this.venueService = venueService;
    }

    @GetMapping
    public String panel(Model model) {
        model.addAttribute("eventos", eventService.listar(null, Pageable.unpaged()).getContent());
        model.addAttribute("lugares", venueService.listar(null, Pageable.unpaged()).getContent());
        return "admin/panel";
    }

    @GetMapping("/events/nuevo")
    public String nuevoEvento(Model model) {
        model.addAttribute("event", new Event());
        return "admin/event-form";
    }

    @PostMapping("/events")
    public String registrarEvento(@ModelAttribute("event") Event event, RedirectAttributes redirectAttributes) {
        try {
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
}
