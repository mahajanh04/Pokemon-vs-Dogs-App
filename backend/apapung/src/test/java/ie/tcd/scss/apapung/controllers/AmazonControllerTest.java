package ie.tcd.scss.apapung.controllers;

import ie.tcd.scss.apapung.Controller.AmazonController;
import ie.tcd.scss.apapung.Service.AmazonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AmazonControllerTest {

    @Mock
    private AmazonService amazonService;

    @InjectMocks
    private AmazonController amazonController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductDetails() {

        String testAsin = "B00TEST123";
        Map<String, Object> mockProductDetails = new HashMap<>();
        mockProductDetails.put("product_title", "Test Product");
        mockProductDetails.put("product_price_gbp", "£19.99");
        mockProductDetails.put("product_price_eur", "€23.99");

        when(amazonService.getProductDetails(testAsin)).thenReturn(mockProductDetails);


        Map<String, Object> result = amazonController.getProductDetails(testAsin);


        assertEquals(mockProductDetails, result);
        verify(amazonService, times(1)).getProductDetails(testAsin);
    }

    @Test
    void testGetBestSellingProducts() {

        String testCategory = "dog-accessories";
        double testDogPrice = 50.0;

        List<Map<String, Object>> mockBestSellingProducts = Arrays.asList(
                createMockProduct("Dog Toy 1", "€15.99"),
                createMockProduct("Dog Collar 2", "€22.50")
        );

        when(amazonService.getBestSellingProducts(testCategory, testDogPrice))
                .thenReturn(mockBestSellingProducts);


        List<Map<String, Object>> result = amazonController.getBestSellingProducts(testCategory, testDogPrice);


        assertEquals(mockBestSellingProducts, result);
        verify(amazonService, times(1)).getBestSellingProducts(testCategory, testDogPrice);
    }

    // Helper method to create mock product map
    private Map<String, Object> createMockProduct(String title, String price) {
        Map<String, Object> product = new HashMap<>();
        product.put("product_title", title);
        product.put("product_price_eur", price);
        product.put("product_photo", "http://example.com/product-image.jpg");
        product.put("product_url", "http://example.com/product");
        product.put("quantity_can_buy", 1);
        return product;
    }
}