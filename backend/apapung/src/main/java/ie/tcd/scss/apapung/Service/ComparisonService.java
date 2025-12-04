package ie.tcd.scss.apapung.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ComparisonService {
    // Constant scaling coefficient for dog strength calculation
    private static final int SCALING_COEFFICIENT = 69;
    
    // Logger instance for logging important information
    private static final Logger logger = LoggerFactory.getLogger(ComparisonService.class);

    // Autowired services for accessing Pokémon and dog data
    @Autowired
    private DogService dogService;

    @Autowired
    private PokeService pokeService;

    // Define a map for type multipliers to apply type advantage in comparisons
    private static final Map<String, Double> typeAdvantageMap = Map.of(
        "dragon", 2.0,     // Dragon type gets a strong multiplier
        "psychic", 1.5,    // Psychic type gets a moderate multiplier
        "flying", 1.4,     // Flying type also has an advantage
        "fire", 1.2,       // Fire type multiplier
        "water", 1.2,      // Water type multiplier
        "grass", 1.2,      // Grass type multiplier
        "electric", 1.2,   // Electric type multiplier
        "fighting", 0.8,   // Fighting type disadvantage
        "bug", 0.5,        // Bug type major disadvantage
        "ice", 0.3         // Ice type severe disadvantage
    );

    // Method to compare a Pokémon and a dog breed
    public Map<String, Object> comparePokemonAndDogs(String pokemonName, String dogName) {
        logger.info("Comparing Pokemon: {} with dog: {}", pokemonName, dogName);

        // Initialize the response map
        Map<String, Object> response = new HashMap<>();

        // Fetch Pokémon stats from the PokeService
        Map<String, Object> pokemonStats = pokeService.getPokeApiStats(pokemonName);

        // Ensure the Pokémon stats contain the total base stats
        if (!pokemonStats.containsKey("Total base stats")) {
            throw new RuntimeException("Total base stats not found for Pokémon: " + pokemonName);
        }

        // Retrieve and log Pokémon's total base stats
        int pokemonBaseStatTotal = (int) pokemonStats.get("Total base stats");
        logger.info("Pokémon Base Stat Total: {}", pokemonBaseStatTotal);

        // Fetch dog breed information from the DogService
        Map<String, Object> dogBreedInfo = dogService.getBreedInfo(dogName);

        // Ensure dog breed information is available
        if (dogBreedInfo == null) {
            throw new RuntimeException("Breed information not found for dog: " + dogName);
        }

        // Calculate dog strength using the scaling coefficient
        double dogStrength = dogService.calculateStrengthScore(dogBreedInfo) * SCALING_COEFFICIENT;
        logger.info("Dog Strength: {}", dogStrength);

        // Validate the calculated dog strength
        if (dogStrength <= 0) {
            throw new RuntimeException("Invalid dog strength score: " + dogStrength);
        }

        // Fetch Pokémon types and calculate type advantage multiplier
        List<String> pokemonTypes = pokeService.getPokemonTypes(pokemonName);
        double typeAdvantageMultiplier = getTypeAdvantageMultiplier(pokemonTypes);

        // Calculate the number of dogs needed based on the Pokémon's stats and dog strength
        int dogsNeeded = (int) (Math.ceil((double) pokemonBaseStatTotal  / (dogStrength)) * typeAdvantageMultiplier);
        logger.info("Number of dogs required (with type advantage): {}", dogsNeeded);

        // Populate the response map with comparison results
        response.put("pokemon", pokemonName);
        response.put("dogBreed", dogName);
        response.put("dogsNeeded", dogsNeeded);
        response.put("dogStrength", dogStrength);
        response.put("pokemonBaseStatTotal", pokemonBaseStatTotal);
        response.put("typeAdvantageMultiplier", typeAdvantageMultiplier);  // Adding multiplier to the response

        return response;
    }

    // Method to compare random Pokémon and dog breeds
    public Map<String, Object> compareRandomPokemonAndDogs(){
        // Fetch random Pokémon and dog breed names
        String pokemonName = pokeService.getRandomPokemonName();
        String breedName = dogService.getRandomBreedName();
        
        // Perform the comparison
        return comparePokemonAndDogs(pokemonName, breedName);
    }

    // Method to retrieve the type advantage map
    public Map<String, Double> getTypeAdvantageMap() {
        return typeAdvantageMap;
    }

    // Helper method to calculate the type advantage multiplier for given Pokémon types
    private double getTypeAdvantageMultiplier(List<String> pokemonTypes) {
        double multiplier = 1.0;

        for (String type : pokemonTypes) {
            // Retrieve the multiplier for each type or default to 1.0
            multiplier *= typeAdvantageMap.getOrDefault(type.toLowerCase(), 1.0);
        }

        return multiplier;
    }
}

