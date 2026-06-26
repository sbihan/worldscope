package com.worldscope.worldscope.service;

import com.worldscope.worldscope.model.Country;
import com.worldscope.worldscope.model.CountryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    void initialiserService() {
        ReflectionTestUtils.setField(countryService, "apiUrl", "https://api.restcountries.com/countries/v5");
        ReflectionTestUtils.setField(countryService, "apiKey", "cle-de-test");
    }

    // -------------------------------------------------------------------------
    // getAllCountries()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getAllCountries : retourne la liste des pays quand l'API répond correctement")
    void devraitRetournerTousLesPays() {
        Country france = unPays("France");
        CountryResponse apiResponse = uneReponse(List.of(france));
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        List<Country> resultat = countryService.getAllCountries();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getCommonName()).isEqualTo("France");
    }

    @Test
    @DisplayName("getAllCountries : retourne une liste vide quand l'API ne renvoie aucun pays")
    void devraitRetournerListeVidePourGetAll() {
        CountryResponse apiResponse = uneReponse(List.of());
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        List<Country> resultat = countryService.getAllCountries();

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("getAllCountries : propage l'exception quand l'API est inaccessible")
    void devraitPropogerExceptionPourGetAll() {
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenThrow(new RestClientException("Serveur inaccessible"));

        assertThatThrownBy(() -> countryService.getAllCountries())
                .isInstanceOf(RestClientException.class)
                .hasMessage("Serveur inaccessible");
    }

    // -------------------------------------------------------------------------
    // searchCountries()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("searchCountries : retourne les pays correspondant au terme de recherche")
    void devraitRetournerPaysCorrespondants() {
        Country allemagne = unPays("Allemagne");
        CountryResponse apiResponse = uneReponse(List.of(allemagne));
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        List<Country> resultat = countryService.searchCountries("Allem");

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getCommonName()).isEqualTo("Allemagne");
    }

    @Test
    @DisplayName("searchCountries : retourne une liste vide quand aucun pays ne correspond")
    void devraitRetournerListeVideSiAucunResultat() {
        CountryResponse apiResponse = uneReponse(List.of());
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        List<Country> resultat = countryService.searchCountries("xyzabc");

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("searchCountries : propage l'exception quand l'API échoue")
    void devraitPropogerExceptionPourSearch() {
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenThrow(new RestClientException("Timeout"));

        assertThatThrownBy(() -> countryService.searchCountries("France"))
                .isInstanceOf(RestClientException.class)
                .hasMessage("Timeout");
    }

    // -------------------------------------------------------------------------
    // getCountryByName()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getCountryByName : retourne le pays quand l'API trouve une correspondance")
    void devraitRetournerLePaysParSonNom() {
        Country espagne = unPays("Espagne");
        CountryResponse apiResponse = uneReponse(List.of(espagne));
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        Country resultat = countryService.getCountryByName("Espagne");

        assertThat(resultat).isNotNull();
        assertThat(resultat.getCommonName()).isEqualTo("Espagne");
    }

    @Test
    @DisplayName("getCountryByName : retourne null quand aucun pays ne correspond au nom")
    void devraitRetournerNullSiPaysIntrouvable() {
        CountryResponse apiResponse = uneReponse(List.of());
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        Country resultat = countryService.getCountryByName("PaysInexistant");

        assertThat(resultat).isNull();
    }

    @Test
    @DisplayName("getCountryByName : propage l'exception quand l'API échoue")
    void devraitPropogerExceptionPourGetByName() {
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenThrow(new RestClientException("Erreur réseau"));

        assertThatThrownBy(() -> countryService.getCountryByName("France"))
                .isInstanceOf(RestClientException.class)
                .hasMessage("Erreur réseau");
    }

    // -------------------------------------------------------------------------
    // Cas limites sur extractCountries() via les méthodes publiques
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getAllCountries : retourne une liste vide si le body de la réponse est null")
    void devraitRetournerListeVideSiBodyNull() {
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        List<Country> resultat = countryService.getAllCountries();

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("getAllCountries : retourne une liste vide si le champ data de la réponse est null")
    void devraitRetournerListeVideSiDataNull() {
        CountryResponse responseAvecDataNull = new CountryResponse();
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(responseAvecDataNull, HttpStatus.OK));

        List<Country> resultat = countryService.getAllCountries();

        assertThat(resultat).isEmpty();
    }

    // -------------------------------------------------------------------------
    // getCountries()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getCountries : sans critères retourne tous les pays via getAllCountries")
    void devraitRetournerTousLesPaysSansCriteres() {
        CountryResponse apiResponse = uneReponse(List.of(unPays("France"), unPays("Japon")));
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        List<Country> resultat = countryService.getCountries(null, null);

        assertThat(resultat).hasSize(2);
    }

    @Test
    @DisplayName("getCountries : avec search seulement délègue la recherche à l'API")
    void devraitRechercherParTermeSeul() {
        CountryResponse apiResponse = uneReponse(List.of(unPays("France")));
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        List<Country> resultat = countryService.getCountries("France", null);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getCommonName()).isEqualTo("France");
    }

    @Test
    @DisplayName("getCountries : avec region seulement filtre les pays par région")
    void devraitFiltrerParRegionSeule() {
        Country france = unPaysAvecRegion("France", "Europe");
        Country japon = unPaysAvecRegion("Japon", "Asia");
        CountryResponse apiResponse = uneReponse(List.of(france, japon));
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        List<Country> resultat = countryService.getCountries(null, "Europe");

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getCommonName()).isEqualTo("France");
    }

    @Test
    @DisplayName("getCountries : avec search et region filtre les résultats de recherche par région")
    void devraitFiltrerParSearchEtRegion() {
        Country france = unPaysAvecRegion("France", "Europe");
        Country guadeloupe = unPaysAvecRegion("Guadeloupe", "Americas");
        CountryResponse apiResponse = uneReponse(List.of(france, guadeloupe));
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        List<Country> resultat = countryService.getCountries("fr", "Europe");

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getCommonName()).isEqualTo("France");
    }

    @Test
    @DisplayName("getCountries : retourne une liste vide si aucun pays ne correspond à la région")
    void devraitRetournerListeVideSiAucunPaysCorrespondALaRegion() {
        CountryResponse apiResponse = uneReponse(List.of(unPaysAvecRegion("Japon", "Asia")));
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(), eq(CountryResponse.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        List<Country> resultat = countryService.getCountries(null, "Europe");

        assertThat(resultat).isEmpty();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Country unPays(String nom) {
        Country pays = new Country();
        pays.setNames(java.util.Map.of("common", nom));
        return pays;
    }

    private Country unPaysAvecRegion(String nom, String region) {
        Country pays = unPays(nom);
        pays.setRegion(region);
        return pays;
    }

    private CountryResponse uneReponse(List<Country> pays) {
        CountryResponse.DataWrapper wrapper = new CountryResponse.DataWrapper();
        wrapper.setObjects(pays);
        CountryResponse response = new CountryResponse();
        response.setData(wrapper);
        return response;
    }
}
