package com.platzi.endpoints;

import com.platzi.config.ConfigManager;
import com.platzi.config.EndpointConfig;
import com.platzi.pojo.request.ProductRequest;
import com.platzi.pojo.response.ProductResponse;
import io.restassured.response.Response;
import java.util.List;
import static io.restassured.RestAssured.*;

public class ProductEndpoints {

    // ============ GET Operations ============

    /**
     * Get all products
     * @return Response object
     */
    public static Response getAllProducts() {
        return given()
                .contentType("application/json")
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.PRODUCTS)
                .then()
                .extract().response();
    }

    /**
     * Get all products as List
     * @return List of ProductResponse
     */
    public static List<ProductResponse> getAllProductsAsList() {
        return getAllProducts()
                .jsonPath()
                .getList("", ProductResponse.class);
    }

    /**
     * Get products with pagination
     * @param offset Starting index
     * @param limit Number of products
     * @return Response object
     */
    public static Response getProductsWithPagination(int offset, int limit) {
        return given()
                .contentType("application/json")
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.PRODUCTS)
                .then()
                .extract().response();
    }

    /**
     * Get products by price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return Response object
     */
    public static Response getProductsByPriceRange(int minPrice, int maxPrice) {
        return given()
                .contentType("application/json")
                .queryParam("price_min", minPrice)
                .queryParam("price_max", maxPrice)
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.PRODUCTS)
                .then()
                .extract().response();
    }

    /**
     * Get products by category
     * @param categoryId Category ID
     * @return Response object
     */
    public static Response getProductsByCategory(int categoryId) {
        return given()
                .contentType("application/json")
                .queryParam("categoryId", categoryId)
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.PRODUCTS)
                .then()
                .extract().response();
    }

    /**
     * Get product by ID
     * @param productId Product ID
     * @return Response object
     */
    public static Response getProductById(int productId) {
        return given()
                .contentType("application/json")
                .pathParam("id", productId)
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.PRODUCT_BY_ID)
                .then()
                .extract().response();
    }

    /**
     * Get product by ID - returns typed response
     * @param productId Product ID
     * @return ProductResponse object
     */
    public static ProductResponse getProductByIdAsObject(int productId) {
        return getProductById(productId).as(ProductResponse.class);
    }

    // ============ POST Operations ============

    /**
     * Create new product
     * @param productRequest Product data
     * @return Response object
     */
    public static Response createProduct(ProductRequest productRequest) {
        return given()
                .contentType("application/json")
                .body(productRequest)
                .when()
                .post(ConfigManager.getBaseUrl() + EndpointConfig.PRODUCTS)
                .then().log().all()
                .extract().response();
    }

    /**
     * Create product - returns typed response
     * @param productRequest Product data
     * @return ProductResponse object
     */
    public static ProductResponse createProductAsObject(ProductRequest productRequest) {
        return createProduct(productRequest).as(ProductResponse.class);
    }

    // ============ PUT Operations ============

    /**
     * Update product
     * @param productId Product ID
     * @param productRequest Updated product data
     * @return Response object
     */
    public static Response updateProduct(int productId, ProductRequest productRequest) {
        return given()
                .contentType("application/json")
                .pathParam("id", productId)
                .body(productRequest)
                .when()
                .put(ConfigManager.getBaseUrl() + EndpointConfig.PRODUCT_BY_ID)
                .then()
                .extract().response();
    }

    // ============ DELETE Operations ============

    /**
     * Delete product
     * @param productId Product ID
     * @return Response object
     */
    public static Response deleteProduct(int productId) {
        return given()
                .contentType("application/json")
                .pathParam("id", productId)
                .when()
                .delete(ConfigManager.getBaseUrl() + EndpointConfig.PRODUCT_BY_ID)
                .then()
                .extract().response();
    }
}