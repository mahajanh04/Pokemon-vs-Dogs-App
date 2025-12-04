package ie.tcd.scss.apapung.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComparisonControllerTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;


    @Test
    public void comparePokemonAndDogs_shouldReturnExpectedData() {
        String pokemonName = "pikachu";
        String dogName = "labrador";

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/compare/{pokemonName}/{dogName}",
                Map.class,
                pokemonName,
                dogName
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).containsKeys("pokemon", "dogBreed", "dogsNeeded", "dogStrength", "pokemonBaseStatTotal");
        assertThat((String) body.get("pokemon")).isEqualTo(pokemonName);
        assertThat((String) body.get("dogBreed")).isEqualTo(dogName);
        assertThat((Integer) body.get("dogsNeeded")).isPositive();
        assertThat((Double) body.get("dogStrength")).isPositive();
        assertThat((Integer) body.get("pokemonBaseStatTotal")).isPositive();
    }

    @Test
    public void compareRandomPokemonAndDogs_shouldReturnExpectedData() {

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/compare/random",
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).containsKeys("pokemon", "dogBreed", "dogsNeeded", "dogStrength", "pokemonBaseStatTotal");
        assertThat((String) body.get("pokemon")).isNotEmpty();
        assertThat((String) body.get("dogBreed")).isNotEmpty();
        assertThat((Integer) body.get("dogsNeeded")).isPositive();
        assertThat((Double) body.get("dogStrength")).isPositive();
        assertThat((Integer) body.get("pokemonBaseStatTotal")).isPositive();
    }


    @Test
    public void comparePokemonAndDogs_shouldHandleException() {
        String pokemonName = "invalidPokemon";
        String dogName = "invalidDog";

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/compare/{pokemonName}/{dogName}",
                Map.class,
                pokemonName,
                dogName
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).containsKey("error");
        assertThat((String) body.get("error")).startsWith("Error occurred during comparison:");
    }
}
