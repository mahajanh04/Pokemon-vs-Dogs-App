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
public class PokeControllerTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Test
    public void getPokeStats_shouldReturnValidPokemonStats() {
        String pokemon = "pikachu";
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/pokemon/" + pokemon + "/stats",
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();


        assertThat(body.get("Dex number")).isEqualTo(25);
        assertThat(body.get("Name")).isEqualTo("pikachu");
        assertThat(body.get("Sprite url"))
                .isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png");
        assertThat(body.get("Total base stats")).isEqualTo(320);


        assertThat(body).hasSize(5);
    }

    @Test
    public void getPokeStats_shouldReturnNotFoundForInvalidPokemon() {
        String invalidPokemon = "foobar";
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/pokemon/" + invalidPokemon + "/stats",
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("Error", "Pokemon name invalid");
    }
}