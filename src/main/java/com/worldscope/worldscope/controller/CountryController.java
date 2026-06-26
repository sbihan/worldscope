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
import org.springframework.web.client.RestClientException;
import java.util.Collections;
import java.util.List;

/**
 * Contrôleur MVC principal de l'application WorldScope.
 * <p>
 * Gère les requêtes HTTP entrantes, délègue le traitement à {@link CountryService}
 * et alimente le modèle Thymeleaf pour le rendu des vues. Ne contient aucune logique métier.
 * </p>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    /**
     * Affiche la page d'accueil avec la liste des pays.
     * <p>
     * Applique optionnellement un filtre texte et/ou un filtre par région.
     * En cas d'erreur d'appel à l'API, affiche une liste vide et un message d'erreur.
     * </p>
     *
     * @param model  le modèle Thymeleaf alimenté avec {@code countries}, {@code search}, {@code region} et éventuellement {@code error}
     * @param search terme de recherche saisi par l'utilisateur (optionnel)
     * @param region région à filtrer parmi Africa, Americas, Asia, Europe, Oceania (optionnel)
     * @return le nom de la vue Thymeleaf {@code index}
     */
    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) String region) {
        try {
            List<Country> countries = countryService.getCountries(search, region);
            log.info("Nombre de pays récupérés : {}", countries.size());
            model.addAttribute("countries", countries);
            model.addAttribute("search", search);
            model.addAttribute("region", region);
        } catch (RestClientException e) {
            log.error("Erreur lors de l'appel API : {}", e.getMessage());
            model.addAttribute("countries", Collections.emptyList());
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }

    /**
     * Affiche la page de détail d'un pays identifié par son nom commun.
     * <p>
     * En cas d'erreur d'appel à l'API, affiche un message d'erreur sans données de pays.
     * </p>
     *
     * @param name  nom commun du pays tel qu'il apparaît dans l'API (ex : {@code "France"})
     * @param model le modèle Thymeleaf alimenté avec l'attribut {@code country} et éventuellement {@code error}
     * @return le nom de la vue Thymeleaf {@code detail}
     */
    @GetMapping("/country/{name}")
    public String detail(@PathVariable String name, Model model) {
        try {
            Country country = countryService.getCountryByName(name);
            model.addAttribute("country", country);
        } catch (RestClientException e) {
            log.error("Erreur : {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "detail";
    }
}