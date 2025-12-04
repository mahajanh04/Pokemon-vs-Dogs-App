package ie.tcd.scss.apapung.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ie.tcd.scss.apapung.Service.PokeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController // Marks this class as a Spring REST controller.
@CrossOrigin(origins = "http://localhost:3000") // Allows cross-origin requests from localhost:3000.
@RequestMapping("/pokemon") // Base mapping for all endpoints in this controller.
public class PokeController {

    private final PokeService pokeService;

    @Autowired
    public PokeController(PokeService pokeService) {
        this.pokeService = pokeService; // Dependency injection for the PokeService.
    }

    /**
     * Endpoint to retrieve stats for a specific Pokémon by name.
     * 
     * @param pokemon Name of the Pokémon.
     * @return A ResponseEntity containing the Pokémon's stats or an error message.
     */
    @GetMapping("/{pokemon}/stats")
    public ResponseEntity<Map<String, Object>> getPokeStats(@PathVariable String pokemon) {
        // Validate if the Pokémon name is provided.
        if (pokemon == null || pokemon.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", "Pokemon name cannot be null or empty"));
        }

        // Check for a specific "null" string case for Pokémon name.
        if ("null".equalsIgnoreCase(pokemon) || pokemon == null) {
            return new ResponseEntity<>(Map.of("error", "Pokemon name cannot be null or empty"), HttpStatus.NOT_FOUND);
        }

        // Validate if the Pokémon name is valid.
        if (!pokeService.isValidName(pokemon)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", "Pokemon name invalid"));
        }

        try {
            // Attempt to fetch stats for the given Pokémon name.
            Map<String, Object> stats = pokeService.getPokeApiStats(pokemon);

            // Handle cases where no stats are found.
            if (stats == null || stats.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("Error", "No stats found for the specified pokemon"));
            }

            // Return the Pokémon stats.
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            // Handle unexpected errors.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Endpoint to retrieve stats for a randomly selected Pokémon.
     * 
     * @return A ResponseEntity containing the stats of a random Pokémon or an error message.
     */
    @GetMapping("/random/stats")
    public ResponseEntity<Map<String, Object>> getRandomPokeStats() {
        try {
            // Fetch a random Pokémon name.
            String pokemon = pokeService.getRandomPokemonName();

            // Validate if a valid Pokémon name is generated.
            if (pokemon == null || pokemon.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("Error", "Failed to generate a valid random Pokemon name"));
            }

            // Validate the randomly generated Pokémon name.
            if (!pokeService.isValidName(pokemon)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("Error", "Randomly generated Pokemon name is invalid"));
            }

            // Fetch stats for the random Pokémon.
            Map<String, Object> stats = pokeService.getPokeApiStats(pokemon);

            // Handle cases where no stats are found.
            if (stats == null || stats.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("Error", "No stats found for the randomly selected Pokemon"));
            }

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Handle unexpected errors.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Endpoint to retrieve the types of a specific Pokémon by name.
     * 
     * @param pokemon Name of the Pokémon.
     * @return A ResponseEntity containing a list of Pokémon types or an empty list in case of errors.
     */
    @GetMapping("/{pokemon}/types")
    public ResponseEntity<List<String>> getPokemonTypes(@PathVariable String pokemon) {
        try {
            // Validate if the Pokémon name is provided.
            if (pokemon == null || pokemon.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ArrayList<>()); // Empty list to indicate no valid types.
            }

            // Validate if the Pokémon name is valid.
            if (!pokeService.isValidName(pokemon)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ArrayList<>()); // Empty list for invalid Pokémon.
            }

            // Fetch Pokémon types.
            List<String> types = pokeService.getPokemonTypes(pokemon);

            // Handle cases where no types are found.
            if (types == null || types.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ArrayList<>()); // Empty list for no types found.
            }

            return ResponseEntity.ok(types);
        } catch (Exception e) {
            // Handle unexpected errors.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>()); // Empty list for unexpected errors.
        }
    }

    /**
     * Endpoint to retrieve the types of a randomly selected Pokémon.
     * 
     * @return A ResponseEntity containing a list of types or an empty list in case of errors.
     */
    @GetMapping("/random/types")
    public ResponseEntity<List<String>> getRandomPokemonTypes() {
        try {
            // Fetch a random Pokémon name.
            String pokemon = pokeService.getRandomPokemonName();

            // Validate if a valid Pokémon name is generated.
            if (pokemon == null || pokemon.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ArrayList<>()); // Return empty list when random Pokémon name fails.
            }

            // Validate the randomly generated Pokémon name.
            if (!pokeService.isValidName(pokemon)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ArrayList<>()); // Empty list if the random name is invalid.
            }

            // Fetch Pokémon types for the random Pokémon.
            List<String> types = pokeService.getPokemonTypes(pokemon);

            // Handle cases where no types are found.
            if (types == null || types.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ArrayList<>()); // Empty list for missing types.
            }

            return ResponseEntity.ok(types);
        } catch (Exception e) {
            // Handle unexpected errors.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>()); // Empty list for unexpected errors.
        }
    }
}
