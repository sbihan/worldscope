package com.worldscope.worldscope.controller;

import com.worldscope.worldscope.model.Country;
import com.worldscope.worldscope.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) String search) {
        try {
            List<Country> countries;
            if (search != null && !search.isBlank()) {
                countries = countryService.searchCountries(search);
            } else {
                countries = countryService.getAllCountries();
            }
            log.info("Nombre de pays récupérés : {}", countries.size());
            model.addAttribute("countries", countries);
            model.addAttribute("search", search);
        } catch (Exception e) {
            log.error("Erreur lors de l'appel API : {}", e.getMessage());
            model.addAttribute("countries", java.util.Collections.emptyList());
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }

    @GetMapping("/country/{name}")
    public String detail(@PathVariable String name, Model model) {
        try {
            Country country = countryService.getCountryByName(name);
            model.addAttribute("country", country);
        } catch (Exception e) {
            log.error("Erreur : {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "detail";
    }
}