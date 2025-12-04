package ie.tcd.scss.apapung.Controller;

import ie.tcd.scss.apapung.Service.ComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController // Marks this class as a Spring REST controller
@CrossOrigin(origins = "http://localhost:3000") // Allows cross-origin requests from localhost:3000
public class ComparisonController {

    @Autowired
    private ComparisonService comparisonService; // Injects the ComparisonService dependency

    /**
     * Endpoint to compare a Pokémon with a specific dog breed.
     * 
     * @param pokemonName Name of the Pokémon.
     * @param dogName Name of the dog breed.
     * @return A map containing the comparison details or an error message.
     */
    @GetMapping("/compare/{pokemonName}/{dogName}")
    public Map<String, Object> comparePokemonAndDogs(
            @PathVariable String pokemonName,
            @PathVariable String dogName) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate input parameters
            if (pokemonName == null || pokemonName.isEmpty() || dogName == null || dogName.isEmpty()) {
                response.put("error", "Pokemon name or dog breed cannot be null or empty.");
                return response;
            }

            // Call service to get comparison data
            Map<String, Object> result = comparisonService.comparePokemonAndDogs(pokemonName, dogName);

            // Validate the result map from the service
            if (result == null || !result.containsKey("dogsNeeded") || 
                !result.containsKey("dogStrength") || 
                !result.containsKey("pokemonBaseStatTotal")) {
                response.put("error", "Invalid response from service. Please try again.");
                return response;
            }

            // Extract relevant data from the result map
            int dogsNeeded = (int) result.get("dogsNeeded");
            double dogStrength = (double) result.get("dogStrength");
            int pokemonBaseStatTotal = (int) result.get("pokemonBaseStatTotal");

            // Populate the response map with comparison details
            response.put("pokemon", pokemonName);
            response.put("dogBreed", dogName);
            response.put("dogsNeeded", dogsNeeded);
            response.put("dogStrength", dogStrength);
            response.put("pokemonBaseStatTotal", pokemonBaseStatTotal);
        } catch (NullPointerException e) {
            // Handle null pointer exceptions gracefully
            response.put("error", "Null pointer exception occurred: " + e.getMessage());
        } catch (ClassCastException e) {
            // Handle data type mismatch errors
            response.put("error", "Data type mismatch: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected errors
            response.put("error", "Error occurred during comparison: " + e.getMessage());
        }
        return response;
    }

    /**
     * Endpoint to perform a random comparison between a Pokémon and a dog breed.
     * 
     * @return A map containing the random comparison details or an error message.
     */
    @GetMapping("/compare/random")
    public Map<String, Object> randomComparison() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Call service to get random comparison data
            Map<String, Object> result = comparisonService.compareRandomPokemonAndDogs();

            // Validate the result map from the service
            if (result == null || result.isEmpty()) {
                response.put("error", "No data returned from the service. Please try again.");
                return response;
            }

            // Extract and validate specific details from the result map
            String pokemonName = (String) result.getOrDefault("pokemon", "Unknown");
            String dogName = (String) result.getOrDefault("dogBreed", "Unknown");
            Integer dogsNeeded = (Integer) result.getOrDefault("dogsNeeded", -1);
            Double dogStrength = (Double) result.getOrDefault("dogStrength", -1.0);
            Integer pokemonBaseStatTotal = (Integer) result.getOrDefault("pokemonBaseStatTotal", -1);

            if (dogsNeeded == -1 || dogStrength == -1.0 || pokemonBaseStatTotal == -1) {
                response.put("error", "Incomplete data from the service. Please try again.");
                return response;
            }

            // Populate the response map with random comparison details
            response.put("pokemon", pokemonName);
            response.put("dogBreed", dogName);
            response.put("dogsNeeded", dogsNeeded);
            response.put("dogStrength", dogStrength);
            response.put("pokemonBaseStatTotal", pokemonBaseStatTotal);
        } catch (ClassCastException e) {
            // Handle data type mismatch errors
            response.put("error", "Data type mismatch: " + e.getMessage());
        } catch (NullPointerException e) {
            // Handle unexpected null data errors
            response.put("error", "Unexpected null data encountered: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected errors
            response.put("error", "Error occurred during random comparison: " + e.getMessage());
        }
        return response;
    }

    /**
     * Endpoint to retrieve the type advantage map.
     * 
     * @return A map containing type advantage multipliers.
     */
    @GetMapping("/type-advantage")
    public Map<String, Double> getTypeAdvantageMap() {
        // Expose the typeAdvantageMap from the service layer to the frontend
        return comparisonService.getTypeAdvantageMap();
    }
}
