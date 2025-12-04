package ie.tcd.scss.apapung.controllers;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ie.tcd.scss.apapung.Service.DogService;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DogServiceTest {
    
    @Autowired
    private DogService dogService;

    @Autowired
    protected TestRestTemplate restTemplate;

    @LocalServerPort
    protected int port;
    
    @Test
    public void testCalculateStrengthScore() {
        // sample input
        Map<String, Object> breedInfo = Map.of(
            "weight", Map.of("imperial", "25 - 38", "metric", "11 - 17"),
            "height", Map.of("imperial", "10.5 - 12.5", "metric", "27 - 32"),
            "id", 68,
            "name", "Cardigan Welsh Corgi",
            "bred_for", "Cattle droving",
            "breed_group", "Herding",
            "life_span", "12 - 14 years",
            "temperament", "Affectionate, Devoted, Alert, Companionable, Intelligent, Active",
            "reference_image_id", "SyXN-e9NX",
            // "image", Map.of("id", "SyXN-e9NX", "width", 800, "height", 600, "url", "https://cdn2.thedogapi.com/images/SyXN-e9NX.jpg"),
            "images", new String[]{"https://cdn2.thedogapi.com/images/SyXN-e9NX_1280.jpg"}
        );

        // Call the method
        double result = dogService.calculateStrengthScore(breedInfo);

        // Assert the result
        assertThat(result).isEqualTo(0.6181428571428572); 
    }

    @Test
    public void getBreed_shouldReturnFirstResultFound() {
        String breed = "hound";
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/breed-info/" + breed,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getBreed_shouldReturnCorrectAttributes() {
        String breed = "scottish-deerhound";
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/breed-info/" + breed,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String responseBody = response.getBody();

        assertThat(responseBody).contains("\"weight\":{\"imperial\":\"70 - 130\",\"metric\":\"32 - 59\"}");
        assertThat(responseBody).contains("\"height\":{\"imperial\":\"28 - 32\",\"metric\":\"71 - 81\"}");
        assertThat(responseBody).contains("\"life_span\":\"8 - 10 years\"");

    }

    @Test
    public void getBreedInfo_shouldReturnNotFoundForInvalidBreed() {
        String invalidBreed = "foobar";
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/breed-info/" + invalidBreed,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getCleanBreedInfo_shouldReturnExpectedFields() {
        String breed = "poodle";
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/breed-info/" + breed + "/clean",
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).containsKeys("name", "weight", "height", "lifespan", "images", "strength");
        assertThat((String) body.get("weight")).startsWith("7 - 8");
        assertThat((String) body.get("height")).startsWith("28 - 38");
        assertThat((String) body.get("lifespan")).contains("12 – 15 years");
        assertThat((String) body.get("strength")).isEqualTo("????");
    }

    @Test
    public void getBreedPrice_shouldReturnPrice() {
        String breed = "labrador";
        ResponseEntity<Integer> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/price/" + breed,
                Integer.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isPositive();
    }

    @Test
    public void getRandomDog() {
        String breed = dogService.getRandomBreedName();
    }
}
