package com.worldscope.worldscope.service;

import com.worldscope.worldscope.model.Country;
import com.worldscope.worldscope.model.CountryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Collections;
import java.util.List;

/**
 * Service métier chargé des appels à l'API REST Countries externe.
 * <p>
 * Centralise la construction des requêtes HTTP authentifiées (Bearer token)
 * et le déballage de l'enveloppe JSON {@code { data: { objects: [...] } }}
 * vers des objets {@link Country} exploitables par les controllers.
 * </p>
 */
@Service
public class CountryService {

    private final RestTemplate restTemplate;

    @Value("${restcountries.api.url}")
    private String apiUrl;

    @Value("${restcountries.api.key}")
    private String apiKey;

    /**
     * Construit le service en injectant le {@link RestTemplate} partagé.
     *
     * @param restTemplate client HTTP utilisé pour appeler l'API externe
     */
    public CountryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Récupère la liste complète des pays disponibles dans l'API (limite 100).
     *
     * @return liste des pays retournés par l'API ; jamais {@code null} mais peut être vide
     * @throws RestClientException si l'appel HTTP échoue ou si la réponse est inattendue
     */
    public List<Country> getAllCountries() {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("limit", 100)
                .toUriString();
        ResponseEntity<CountryResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, buildRequest(), CountryResponse.class);
        return extractCountries(response);
    }

    /**
     * Recherche des pays dont le nom correspond au terme fourni.
     *
     * @param query terme de recherche transmis tel quel au paramètre {@code q} de l'API
     * @return liste des pays correspondant à la recherche (jusqu'à 100 résultats)
     * @throws RestClientException si l'appel HTTP échoue ou si la réponse est inattendue
     */
    public List<Country> searchCountries(String query) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("q", query)
                .queryParam("limit", 100)
                .toUriString();
        ResponseEntity<CountryResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, buildRequest(), CountryResponse.class);
        return extractCountries(response);
    }

    /**
     * Récupère un pays unique par son nom exact.
     * <p>
     * Interroge l'API avec {@code limit=1} et retourne le premier résultat.
     * Retourne {@code null} si aucun pays ne correspond au nom fourni.
     * </p>
     *
     * @param name nom du pays à rechercher (ex : {@code "France"})
     * @return le pays trouvé, ou {@code null} si la recherche ne donne aucun résultat
     * @throws RestClientException si l'appel HTTP échoue ou si la réponse est inattendue
     */
    public Country getCountryByName(String name) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("q", name)
                .queryParam("limit", 1)
                .toUriString();
        ResponseEntity<CountryResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, buildRequest(), CountryResponse.class);
        List<Country> results = extractCountries(response);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Extrait la liste des pays depuis la réponse API en gérant les cas de body null.
     *
     * @param response réponse HTTP brute de l'API
     * @return liste des pays, ou liste vide si le body ou les données sont absents
     */
    private List<Country> extractCountries(ResponseEntity<CountryResponse> response) {
        if (response.getBody() == null || response.getBody().getData() == null) {
            return Collections.emptyList();
        }
        List<Country> objects = response.getBody().getData().getObjects();
        return objects != null ? objects : Collections.emptyList();
    }

    /**
     * Construit l'entité HTTP avec l'en-tête d'authentification Bearer.
     *
     * @return entité HTTP sans corps, avec le header {@code Authorization} renseigné
     */
    private HttpEntity<Void> buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        return new HttpEntity<>(headers);
    }
}