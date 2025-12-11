package com.platzi.endpoints;

import com.platzi.config.ConfigManager;
import com.platzi.config.EndpointConfig;
import com.platzi.pojo.request.CategoryRequest;
import com.platzi.pojo.response.CategoryResponse;
import com.platzi.pojo.response.ProductResponse;
import io.restassured.response.Response;
import java.util.List;
import static io.restassured.RestAssured.*;

public class CategoryEndpoints {

    // ============ GET Operations ============

    /**
     * Get all categories
     * @return Response object
     */
    public static Response getAllCategories() {
        return given()
                .contentType("application/json")
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.CATEGORIES)
                .then()
                .extract().response();
    }

    /**
     * Get all categories as List
     * @return List of CategoryResponse
     */
    public static List<CategoryResponse> getAllCategoriesAsList() {
        return getAllCategories()
                .jsonPath()
                .getList("", CategoryResponse.class);
    }

    /**
     * Get category by ID
     * @param categoryId Category ID
     * @return Response object
     */
    public static Response getCategoryById(int categoryId) {
        return given()
                .contentType("application/json")
                .pathParam("id", categoryId)
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.CATEGORY_BY_ID)
                .then()
                .extract().response();
    }

    /**
     * Get products by category ID
     * @param categoryId Category ID
     * @return Response object
     */
    public static Response getProductsByCategory(int categoryId) {
        return given()
                .contentType("application/json")
                .pathParam("id", categoryId)
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.CATEGORY_PRODUCTS)
                .then()
                .extract().response();
    }

    /**
     * Get products by category as List
     * @param categoryId Category ID
     * @return List of ProductResponse
     */
    public static List<ProductResponse> getProductsByCategoryAsList(int categoryId) {
        return getProductsByCategory(categoryId)
                .jsonPath()
                .getList("", ProductResponse.class);
    }

    // ============ POST Operations ============

    /**
     * Create new category
     * @param categoryRequest Category data
     * @return Response object
     */
    public static Response createCategory(CategoryRequest categoryRequest) {
        return given()
                .contentType("application/json")
                .body(categoryRequest)
                .when()
                .post(ConfigManager.getBaseUrl() + EndpointConfig.CATEGORIES)
                .then()
                .extract().response();
    }

    // ============ PUT Operations ============

    /**
     * Update category
     * @param categoryId Category ID
     * @param categoryRequest Updated category data
     * @return Response object
     */
    public static Response updateCategory(int categoryId, CategoryRequest categoryRequest) {
        return given()
                .contentType("application/json")
                .pathParam("id", categoryId)
                .body(categoryRequest)
                .when()
                .put(ConfigManager.getBaseUrl() + EndpointConfig.CATEGORY_BY_ID)
                .then()
                .extract().response();
    }

    // ============ DELETE Operations ============

    /**
     * Delete category
     * @param categoryId Category ID
     * @return Response object
     */
    public static Response deleteCategory(int categoryId) {
        return given()
                .contentType("application/json")
                .pathParam("id", categoryId)
                .when()
                .delete(ConfigManager.getBaseUrl() + EndpointConfig.CATEGORY_BY_ID)
                .then()
                .extract().response();
    }
}