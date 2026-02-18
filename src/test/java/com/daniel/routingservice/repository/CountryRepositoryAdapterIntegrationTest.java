package com.daniel.routingservice.repository;

import com.daniel.routingservice.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class CountryRepositoryAdapterIntegrationTest extends IntegrationTest {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    void getBordersByCountry_shouldReturnImmutableMap_whenAccessed() {

        var bordersByCountry = countryRepository.getBordersByCountry();

        assertThat(bordersByCountry).isUnmodifiable();
    }

    @Test
    void initialize_shouldLoadCountriesFromFile_whenApplicationStarts() {

        var bordersByCountry = countryRepository.getBordersByCountry();

        assertThat(bordersByCountry)
            .isNotEmpty()
            .hasSizeGreaterThan(5);
    }

    @Test
    void getBordersByCountry_shouldContainCzechRepublicWithBorders_whenDataLoaded() {

        var cca3 = "CZE";

        var bordersByCountry = countryRepository.getBordersByCountry();

        assertThat(bordersByCountry).containsKey(cca3);
        assertThat(bordersByCountry.get(cca3))
            .contains("AUT", "DEU", "POL", "SVK")
            .hasSize(4);
    }

    @Test
    void getBordersByCountry_shouldContainIcelandWithNoBorders_whenDataLoaded() {

        var cca3 = "ISL";

        var bordersByCountry = countryRepository.getBordersByCountry();

        assertThat(bordersByCountry).containsKey(cca3);
        assertThat(bordersByCountry.get(cca3)).isEmpty();
    }

    @Test
    void findByCca3_shouldReturnCountry_whenCountryExists() {

        var cca3 = "CZE";

        var country = countryRepository.findByCca3(cca3);

        assertThat(country).isPresent();
        assertThat(country.get().cca3()).isEqualTo(cca3);
        assertThat(country.get().borders()).contains("AUT", "DEU", "POL", "SVK");
    }

    @Test
    void findByCca3_shouldReturnEmpty_whenCountryDoesNotExist() {

        var cca3 = "XXX";

        var country = countryRepository.findByCca3(cca3);

        assertThat(country).isEmpty();
    }

    @Test
    void findByCca3_shouldReturnIceland_whenIcelandExists() {

        var cca3 = "ISL";

        var country = countryRepository.findByCca3(cca3);

        assertThat(country).isPresent();
        assertThat(country.get().cca3()).isEqualTo(cca3);
        assertThat(country.get().borders()).isEmpty();
    }
}