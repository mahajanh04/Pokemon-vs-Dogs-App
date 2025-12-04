package ie.tcd.scss.apapung.Service;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class to interact with the Amazon API.
 * Provides methods for fetching product details and best-selling products.
 */
@SuppressWarnings("unchecked")
@Service
public class AmazonService {

    @Autowired
    private RestTemplate restTemplate; // Injects RestTemplate for making HTTP requests.

    private final String apiKey; // API key for authenticating requests.

    private static final Logger logger = LoggerFactory.getLogger(ComparisonService.class); // Logger for error handling.

    /**
     * Constructor to load API key from the .env file.
     */
    public AmazonService() {
        Dotenv dotenv = Dotenv.load(); // Load .env file for environment variables.
        this.apiKey = dotenv.get("SULU_TOKEN"); // Retrieve the API key for Amazon API.
    }

    /**
     * Fetches product details based on the provided ASIN.
     * 
     * @param asin Amazon Standard Identification Number.
     * @return A map containing product details including title, price, URL, and image.
     */
    public Map<String, Object> getProductDetails(String asin) {
        String url = "https://real-time-amazon-data.p.sulu.sh/v2/product-offers?country=gb&asin=" + asin;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey); // Set the Authorization header with API key.

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Validate the response and extract product data.
        if (response.getBody() != null) {
            Map<String, Object> productData = (Map<String, Object>) response.getBody().get("data");

            if (productData != null) {
                String productTitle = (String) productData.get("product_title");
                String productPrice = (String) productData.get("product_price");
                String productLink = (String) productData.get("product_url");
                String productImage = (String) productData.get("product_photo");

                // Truncate title if it exceeds 50 characters.
                String shortenedTitle = productTitle.length() > 50 ? productTitle.substring(0, 50) + "..." : productTitle;

                // Convert price from GBP to EUR.
                double priceInGbp = Double.parseDouble(productPrice.replace("£", "").trim());
                double priceInEur = priceInGbp * 1.2;

                return Map.of(
                        "product_title", shortenedTitle,
                        "product_price_gbp", "£" + String.format("%.2f", priceInGbp),
                        "product_price_eur", "€" + String.format("%.2f", priceInEur),
                        "product_link", productLink,
                        "product_image", productImage);
            } else {
                throw new RuntimeException("No product data found for ASIN: " + asin);
            }
        } else {
            throw new RuntimeException("Unable to fetch product data for ASIN: " + asin);
        }
    }

    /**
     * Fetches the best-selling products in a given category.
     * 
     * @param category The category of products.
     * @param dogPrice The price of a dog product for comparison.
     * @return A list of filtered best-selling products with price details.
     */
    public List<Map<String, Object>> getBestSellingProducts(String category, double dogPrice) {
        String url = String.format("https://real-time-amazon-data.p.sulu.sh/v2/best-sellers?country=gb&category=%s&page=1", category);
    
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey); // Set authorization headers.
    
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Extract best-sellers data from the response.
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            List<Map<String, Object>> bestSellers = (List<Map<String, Object>>) data.get("best_sellers");
    
            if (bestSellers == null || bestSellers.isEmpty()) {
                throw new RuntimeException("No best-selling products found.");
            }

            // Convert dog price to EUR and calculate the acceptable price range.
            double dogPriceEur = dogPrice * 1.2;
            double minPrice = dogPriceEur * 0.8; // Minimum acceptable price.
            double maxPrice = dogPriceEur * 1.2; // Maximum acceptable price.

            // Filter products within the price range and map required details.
            List<Map<String, Object>> filteredProducts = bestSellers.stream()
                    .filter(product -> {
                        String priceStr = (String) product.get("product_price");
                        double productPriceGbp = parsePrice(priceStr);
                        double productPriceEur = productPriceGbp * 1.2;
                        return productPriceEur >= minPrice && productPriceEur <= maxPrice;
                    })
                    .map(product -> {
                        String priceStr = (String) product.get("product_price");
                        double productPriceGbp = parsePrice(priceStr);
                        double productPriceEur = productPriceGbp * 1.2;

                        return Map.of(
                                "product_title", truncateTitle((String) product.get("product_title")),
                                "product_price_eur", "€" + String.format("%.2f", productPriceEur),
                                "product_photo", product.get("product_photo"),
                                "product_url", product.get("product_url"),
                                "quantity_can_buy", 1
                        );
                    })
                    .limit(10) // Limit to top 10 products.
                    .collect(Collectors.toList());

            // Fill remaining spots with out-of-range products if needed.
            if (filteredProducts.size() < 10) {
                List<Map<String, Object>> outOfRangeProducts = bestSellers.stream()
                        .filter(product -> {
                            String priceStr = (String) product.get("product_price");
                            double productPriceGbp = parsePrice(priceStr);
                            double productPriceEur = productPriceGbp * 1.2;
                            return productPriceEur < minPrice || productPriceEur > maxPrice;
                        })
                        .map(product -> {
                            String priceStr = (String) product.get("product_price");
                            double productPriceGbp = parsePrice(priceStr);
                            double productPriceEur = productPriceGbp * 1.2;

                            int quantity = (int) (dogPrice / productPriceGbp);

                            return Map.of(
                                    "product_title", truncateTitle((String) product.get("product_title")),
                                    "product_price_eur", "€" + String.format("%.2f", productPriceEur),
                                    "product_photo", product.get("product_photo"),
                                    "product_url", product.get("product_url"),
                                    "quantity_can_buy", quantity
                            );
                        })
                        .collect(Collectors.toList());

                Collections.shuffle(outOfRangeProducts);
                int remainingSpots = 10 - filteredProducts.size();
                filteredProducts.addAll(outOfRangeProducts.subList(0, Math.min(remainingSpots, outOfRangeProducts.size())));
            }

            return filteredProducts;

        } catch (Exception e) {
            logger.error("Failed to fetch best-selling products for category: " + category, e);
            throw new RuntimeException("Error fetching best-selling products.", e);
        }
    }

    /**
     * Helper method to parse a price string and return its numeric value.
     * 
     * @param priceStr Price string in the format "£12.99".
     * @return Parsed numeric value of the price.
     */
    private double parsePrice(String priceStr) {
        try {
            if (priceStr == null || priceStr.trim().isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(priceStr.replace("£", "").trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price format: " + priceStr);
            return 0.0;
        }
    }

    /**
     * Helper method to truncate a title to a maximum of 50 characters.
     * 
     * @param title The original product title.
     * @return Truncated title with "..." appended if it exceeds 50 characters.
     */
    private String truncateTitle(String title) {
        return title.length() > 50 ? title.substring(0, 50) + "..." : title;
    }
}
