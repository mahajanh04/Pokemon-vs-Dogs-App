package ie.tcd.scss.apapung.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unchecked")
@Service
public class DogService {
    @Autowired
    private RestTemplate restTemplate;

    private String groqApiKey;

    private String dogAPIToken;

    public DogService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // Load .env file and get the OpenAI API key
        Dotenv dotenv = Dotenv.load();
        this.dogAPIToken = dotenv.get("DOG_TOKEN");
        this.groqApiKey = dotenv.get("OPENAI_TOKEN");
    }

    public Map<String, Object> getBreedInfo(String breedQuery) {
        // Construct the URL for searching the breed and sub-breed
        String url = "https://api.thedogapi.com/v1/breeds/search?q=" + breedQuery;

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", dogAPIToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the GET request
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            entity, 
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        List<Map<String, Object>> breeds = response.getBody();

        if (breeds != null && !breeds.isEmpty()) {
            Map<String, Object> breedInfo = breeds.get(0); // Get the first result

            // Fetch images using the breed ID from the response
            int breedId = (int) breedInfo.get("id");
            List<String> images = getBreedImages(breedId);

            // Add images to the breed info
            breedInfo.put("images", images);

            // Calculate and add the strength score to the breed info
            double strengthScore = calculateStrengthScore(breedInfo);
            breedInfo.put("strength", strengthScore);

            double dogPrice = getDogPrice(breedQuery);
            breedInfo.put("average price", dogPrice);

            return breedInfo;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found for breed: " + breedQuery);
        }
    }

    public String getRandomBreedName() {
        // Generate random breed ID between 1 and 264
        Random random = new Random();
        int randomBreedId = random.nextInt(264) + 1;

        // Construct the URL for getting the specific breed
        String url = "https://api.thedogapi.com/v1/breeds/" + randomBreedId;

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", dogAPIToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Make the GET request
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {} // Properly parameterized type
            );

            Map<String, Object> breedInfo = response.getBody();
            if (!nameInData((String) breedInfo.get("name"))) {
                return getRandomBreedName();
            }
            return (String) breedInfo.get("name");
        } catch (HttpClientErrorException.NotFound e) {
            return getRandomBreedName();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching random breed name: " + e.getMessage()
            );
        }
    }

    //helper function for getRandomBreedName
    public boolean nameInData(String breedQuery) {
        String url = "https://api.thedogapi.com/v1/breeds/search?q=" + breedQuery;

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", dogAPIToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Make the GET request
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {} // Properly parameterized type
            );

            List<Map<String, Object>> breeds = response.getBody();
            return breeds != null && !breeds.isEmpty();
        } catch (RestClientException e) {
            return false;
        }
    }



    private List<String> getBreedImages(int breedId) {
        String url = "https://api.thedogapi.com/v1/images/search?breed_id=" + breedId + "&limit=5"; // Limit to 5 images

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", dogAPIToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            entity, 
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        List<Map<String, Object>> imagesData = response.getBody();

        return imagesData.stream()
                .map(imageData -> (String) imageData.get("url"))
                .toList();
    }

    public double calculateStrengthScore(Map<String, Object> breedInfo) {
        int weightScore = 0;
        int heightScore = 0;

        // Weight: get the metric weight and normalize it
        Map<String, String> weightMap = (Map<String, String>) breedInfo.get("weight");
        if (weightMap != null) {
            String metricWeight = weightMap.get("metric");
            int avgWeight = parseRangeToAverage(metricWeight);
            weightScore = Math.min(avgWeight, 20); // Max out weight score at 40
        }

        // Height: get the metric height and normalize it
        Map<String, String> heightMap = (Map<String, String>) breedInfo.get("height");
        if (heightMap != null) {
            String metricHeight = heightMap.get("metric");
            int avgHeight = parseRangeToAverage(metricHeight);
            heightScore = Math.min(avgHeight, 30); // Max out height score at 30
        }

        // Calculate the raw strength score, with a max of 100
        double rawStrengthScore = weightScore + heightScore;

        // Now, scale it down to the range 0.01 to 1
        // Assuming the max possible raw strength score is 70 (40 + 30 from weight and
        // height max)
        double normalizedStrength = Math.min(rawStrengthScore / 70.0, 1.0); // Scale to [0, 1]
        return Math.max(normalizedStrength * 0.99 + 0.01, 0.01); // Scale to [0.01, 1]
    }

    // Helper method to parse a range and get the average
    private int parseRangeToAverage(String range) {
        try {
            String[] parts = range.split("-");
            int low = Integer.parseInt(parts[0].trim());
            int high = Integer.parseInt(parts[1].trim());
            return (low + high) / 2;
        } catch (Exception e) {
            return 0;
        }
    }

    // prompt llama3-8b for the price of a dog breed
    public double getDogPrice(String breed) {
        String gptPrompt = String.format("What is the average price of a %s in Euros." +
                                        "Give me a single price, do not display any text.", breed);
    
        // Create the request payload
        Map<String, Object> payload = Map.of(
                "model", "llama3-8b-8192", // Specify the LLaMA model or other GPT-based model
                "messages", List.of(
                        Map.of("role", "user", "content", gptPrompt)));
    
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + groqApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
    
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://api.groq.com/openai/v1/chat/completions", request, Map.class);

            // Check if the response body contains the expected fields
            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0); // Access the first choice
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
    
                    // Ensure content exists and is valid
                    if (message != null && message.containsKey("content")) {
                        String content = (String) message.get("content");
    
                        // Attempt to parse the content as a price
                        String priceText = content.replaceAll("[^\\d.,]", ""); // Allow digits, commas, and periods
                        priceText = priceText.replaceAll(",", ""); // Remove commas
    
                        // Ensure only one decimal point exists
                        int lastIndex = priceText.lastIndexOf('.');
                        if (lastIndex != -1) {
                            // Remove all but the last decimal point
                            priceText = priceText.substring(0, lastIndex).replace(".", "") 
                                        + priceText.substring(lastIndex);
                        }
    
                        return Double.parseDouble(priceText);
                    }
                }
            }
    
            throw new RuntimeException("No price data returned from OpenAI API for breed: " + breed);
    
        } catch (Exception e) {
            throw new RuntimeException("Failed to estimate dog price: " + e.getMessage(), e);
        }
    }     
}