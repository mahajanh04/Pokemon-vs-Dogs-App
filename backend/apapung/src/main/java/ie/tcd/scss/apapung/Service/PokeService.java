package ie.tcd.scss.apapung.Service;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ie.tcd.scss.apapung.Repository.TypesRepository;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Service
public class PokeService {
    private final String pokeAPIToken;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PokeService.class);

    @Autowired
    public PokeService(RestTemplateBuilder restTemplateBuilder) {
        Dotenv dotenv = Dotenv.configure().load();
        this.pokeAPIToken = dotenv.get("SULU_TOKEN");
        if (this.pokeAPIToken == null) {
            throw new IllegalStateException("SULU_TOKEN is missing from the .env file.");
        }

        // debug prints
        logger.debug("Loaded PokeAPI Token: {}", pokeAPIToken);

        // Configure RestTemplate with timeouts
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    // checks if the parameter is a valid pokemon name.
    public boolean isValidName(String name) {
        String url = "https://pokeapi.p.sulu.sh/api/v2/pokemon/" + name + "/";

        // Set up access headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.pokeAPIToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    public Map<String, Object> getPokeApiStats(String pokemon) {
        // set up api url
        String url = "https://pokeapi.p.sulu.sh/api/v2/pokemon/" + pokemon + "/";

        // Set up api access headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.pokeAPIToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, Object> resultMap = new HashMap<>();

        try {
            // Get response and parse
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();

            // Use TypeReference to avoid unchecked conversion warning
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(),
                    new TypeReference<Map<String, Object>>() {
                    });

            // Calculate the total base stats
            int totalBaseStat = 0;
            List<Map<String, Object>> stats = (List<Map<String, Object>>) responseMap.get("stats");
            for (Map<String, Object> stat : stats) {
                totalBaseStat += (int) stat.get("base_stat");
            }
            // String typesString = "";
            List<Map<String, Object>> types = (List<Map<String, Object>>) responseMap.get("types");
            List<String> typesList = types.stream()
                    .map(typeEntry -> (Map<String, Object>) typeEntry.get("type"))
                    .map(typeMap -> (String) typeMap.get("name"))
                    .collect(Collectors.toList());

            // convert types list to list of urls
            TypesRepository typeRepo = new TypesRepository();
            List<String> typesURLList = typeRepo.getTypeUrls(typesList);

            Map<String, Object> sprites = (Map<String, Object>) responseMap.get("sprites");
            String spriteUrl = (String) sprites.get("front_default");

            String name = (String) responseMap.get("name");
            int pokedexNumber = (int) responseMap.get("id");

            resultMap.put("Name", name);
            resultMap.put("Dex number", pokedexNumber);
            resultMap.put("Types", typesURLList);
            resultMap.put("Total base stats", totalBaseStat);
            resultMap.put("Sprite url", spriteUrl);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public String getRandomPokemonName() {
        // Generate random Pokemon ID between 1 and 1025
        Random random = new Random();
        int randomPokemonId = random.nextInt(1025) + 1;

        // Construct the URL for getting the specific Pokemon
        String url = "https://pokeapi.p.sulu.sh/api/v2/pokemon/" + randomPokemonId + "/";

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.pokeAPIToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Make the GET request
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> pokemonInfo = response.getBody();

            if (pokemonInfo != null) {
                // Return just the Pokemon name
                return (String) pokemonInfo.get("name");
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No data found for random Pokemon ID: " + randomPokemonId
                );
            }
        } catch (HttpClientErrorException.NotFound e) {
            // If the random ID doesn't exist, try again with a different random number
            return getRandomPokemonName();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching random Pokemon name: " + e.getMessage()
            );
        }
    }

    public List<String> getPokemonTypes(String pokemonName) {
        // Construct the API URL for the Pokémon
        String url = "https://pokeapi.p.sulu.sh/api/v2/pokemon/" + pokemonName.toLowerCase() + "/";

        // Set up the API access headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.pokeAPIToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Send the GET request to the API
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Extract the list of types from the response body
            List<Map<String, Object>> types = (List<Map<String, Object>>) response.getBody().get("types");

            // Extract and return the names of the types
            return types.stream()
                    .map(typeEntry -> (Map<String, Object>) typeEntry.get("type"))
                    .map(typeMap -> (String) typeMap.get("name"))
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error fetching Pokémon types for: " + pokemonName, e);
        }
    }
}