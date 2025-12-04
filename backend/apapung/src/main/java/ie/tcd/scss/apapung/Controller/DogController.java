package ie.tcd.scss.apapung.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ie.tcd.scss.apapung.Service.DogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked") // Suppresses warnings about unchecked type casting
@RestController
@CrossOrigin(origins = "http://localhost:3000") // Allow CORS requests from a specific origin
public class DogController {

    @Autowired
    private DogService dogService; // Injects the DogService dependency

    /**
     * Endpoint to fetch detailed breed information based on the provided breed name.
     * 
     * @param fullBreed The full breed name in URL, which may contain hyphens.
     * @return ResponseEntity containing breed information or an error message.
     */
    @GetMapping("/breed-info/{fullBreed}")
    public ResponseEntity<Map<String, Object>> getBreedInfo(@PathVariable String fullBreed) {
        try {
            // Return an error response if breed name is invalid
            if ("null".equalsIgnoreCase(fullBreed) || fullBreed == null) {
                return new ResponseEntity<>(Map.of("error", "Breed information not found"), HttpStatus.NOT_FOUND);
            }

            // Replace hyphens with spaces to format breed name properly
            String breedQuery = fullBreed.replace("-", " ");
            Map<String, Object> info = dogService.getBreedInfo(breedQuery);

            // Check if breed info is null or empty and return an appropriate error
            if (info == null || info.isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "Breed information not found"), HttpStatus.NOT_FOUND);
            }

            // Return the breed info as a successful response
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            // Return an internal server error if any unexpected exception occurs
            return new ResponseEntity<>(Map.of("error", "An error occurred while fetching breed information"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to fetch cleaned breed information for a given breed.
     * This endpoint returns only essential details about the breed.
     * 
     * @param fullBreed The full breed name in URL, which may contain hyphens.
     * @return ResponseEntity containing cleaned breed information or an error message.
     */
    @GetMapping("/breed-info/{fullBreed}/clean")
    public ResponseEntity<Map<String, Object>> getCleanBreedInfo(@PathVariable String fullBreed) {
        try {
            // Validate input breed name
            if ("null".equalsIgnoreCase(fullBreed) || fullBreed == null) {
                return new ResponseEntity<>(Map.of("error", "Breed information not found"), HttpStatus.NOT_FOUND);
            }

            // Process breed name for query
            String breedQuery = fullBreed.replace("-", " ");
            Map<String, Object> info = dogService.getBreedInfo(breedQuery);

            // Check if valid breed info is found
            if (info == null || info.isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "Breed information not found"), HttpStatus.NOT_FOUND);
            }

            // Extract and clean specific breed details
            Map<String, Object> result = new HashMap<>();
            result.put("name", info.get("name"));
            result.put("weight", ((Map<String, String>) info.get("weight")).get("metric"));
            result.put("height", ((Map<String, String>) info.get("height")).get("metric"));
            result.put("lifespan", info.get("life_span"));
            result.put("images", info.get("images"));
            result.put("strength", "????"); // Placeholder for masked strength

            return ResponseEntity.ok(result);
        } catch (NullPointerException e) {
            // Handle missing fields gracefully
            return new ResponseEntity<>(Map.of("error", "Incomplete breed data available"),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Handle other unexpected errors
            return new ResponseEntity<>(Map.of("error", "An error occurred while processing breed information"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to fetch cleaned information of a random breed.
     * 
     * @return ResponseEntity containing cleaned random breed information or an error message.
     */
    @GetMapping("breed-info/random/clean")
    public ResponseEntity<Map<String, Object>> getCleanRandomBreedInfo() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Fetch a random breed name
            String breedQuery = dogService.getRandomBreedName();

            // Validate the random breed name
            if (breedQuery == null || breedQuery.trim().isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "No random breed name found"), HttpStatus.NOT_FOUND);
            }

            // Get breed info based on the random breed name
            Map<String, Object> info = dogService.getBreedInfo(breedQuery);

            // Validate the breed information
            if (info == null || info.isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "No breed information available"), HttpStatus.NOT_FOUND);
            }

            // Extract relevant information
            result.put("name", info.getOrDefault("name", "Unknown"));

            Map<String, String> weight = (Map<String, String>) info.get("weight");
            result.put("weight", weight != null ? weight.getOrDefault("metric", "Unknown") : "Unknown");

            Map<String, String> height = (Map<String, String>) info.get("height");
            result.put("height", height != null ? height.getOrDefault("metric", "Unknown") : "Unknown");

            result.put("lifespan", info.getOrDefault("life_span", "Unknown"));
            result.put("images", info.getOrDefault("images", List.of())); // Default to empty list if no images
            result.put("strength", "????"); // Placeholder for masked strength

            return ResponseEntity.ok(result);
        } catch (NullPointerException e) {
            // Handle null data gracefully
            return new ResponseEntity<>(Map.of("error", "Unexpected null data encountered"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Handle unexpected errors
            return new ResponseEntity<>(Map.of("error", "An error occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to fetch price information for a specific breed.
     * 
     * @param breed The breed name for which price is requested.
     * @return ResponseEntity containing the breed price or an error message.
     */
    @GetMapping("/price/{breed}")
    public ResponseEntity<Map<String, Object>> getBreedPrice(@PathVariable String breed) {
        try {
            // Fetch breed price using the service
            double price = dogService.getDogPrice(breed);

            // Return an error response if price is not available
            if (price <= 0) {
                return new ResponseEntity<>(Map.of("error", "Price information not available"), HttpStatus.NOT_FOUND);
            }

            // Return breed price in the response
            return ResponseEntity.ok(Map.of("breed", breed, "price", price));
        } catch (Exception e) {
            // Handle unexpected errors while fetching price
            return new ResponseEntity<>(Map.of("error", "An error occurred while fetching price information"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
