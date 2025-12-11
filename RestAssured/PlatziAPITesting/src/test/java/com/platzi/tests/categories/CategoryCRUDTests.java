package com.platzi.tests.categories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platzi.endpoints.CategoryEndpoints;
import com.platzi.pojo.request.CategoryRequest;
import com.platzi.pojo.response.CategoryResponse;
import com.platzi.tests.base.BaseTest;
import com.platzi.utils.LoggerUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Epic("Platzi API Testing")
@Feature("Categories Management")
public class CategoryCRUDTests extends BaseTest {

    private static Integer createdCategoryId;
    private static String createdCategoryName;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test(priority = 1, description = "TC_CAT_001: Get all categories")
    @Story("Get Categories")
    @Severity(SeverityLevel.NORMAL)
    @Description("Retrieve all categories and validate array structure and status code.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_001")
    public void testGetAllCategories() {
        logStep("Sending GET request to /categories");
        Response response = CategoryEndpoints.getAllCategories();

        Allure.parameter("Endpoint", "/categories");
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response and verifying list is not empty");
        List<CategoryResponse> categories = response.jsonPath().getList("", CategoryResponse.class);
        Assert.assertNotNull(categories, "Categories list should not be null");
        Assert.assertFalse(categories.isEmpty(), "Categories list should not be empty");

        LoggerUtil.pass("Get all categories successful. Total: " + categories.size());
    }

    @Test(priority = 2, description = "TC_CAT_002: Verify category structure")
    @Story("Get Categories")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the category object contains all required fields")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_002")
    public void testCategoryStructure() {
        logStep("Getting categories list");
        List<CategoryResponse> categories = CategoryEndpoints.getAllCategoriesAsList();

        logStep("Verifying first category has all required fields");
        CategoryResponse category = categories.get(0);

        Assert.assertNotNull(category.getId(), "Category ID should not be null");
        Assert.assertNotNull(category.getName(), "Category name should not be null");
        Assert.assertNotNull(category.getImage(), "Category image should not be null");

        Allure.parameter("Category ID", category.getId());
        Allure.parameter("Category Name", category.getName());

        LoggerUtil.pass("Category structure is valid");
    }

    @Test(priority = 3, description = "TC_CAT_003: Verify category image is URL format")
    @Story("Get Categories")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that category images are in URL format")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_003")
    public void testCategoryImageIsUrl() {
        logStep("Getting categories list");
        List<CategoryResponse> categories = CategoryEndpoints.getAllCategoriesAsList();

        logStep("Verifying all images are URLs");
        String urlRegex = "^https?://.*$";

        for (CategoryResponse category : categories) {
            Assert.assertTrue(category.getImage().matches(urlRegex),
                    "Image should be URL format: " + category.getImage());
            Allure.parameter("Category Image URL", category.getImage());
        }

        LoggerUtil.pass("All category images are valid URLs");
    }

    @Test(priority = 4, description = "TC_CAT_004: Create category with valid data")
    @Story("Category Creation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a category with valid payload and verify ID and fields.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_002")
    public void testCreateCategory() throws JsonProcessingException {
        logStep("Generating category request body");
        CategoryRequest categoryRequest = CategoryRequest.defaultCategory().build();

        String requestJson = mapper.writeValueAsString(categoryRequest);
        Allure.addAttachment("Request JSON", requestJson);
        Allure.parameter("Category Name", categoryRequest.getName());

        logStep("Sending POST request to /categories");
        Response response = CategoryEndpoints.createCategory(categoryRequest);

        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 201");
        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201");

        logStep("Parsing response to CategoryResponse");
        CategoryResponse createdCategory = response.as(CategoryResponse.class);

        logStep("Verifying category ID is generated");
        Assert.assertNotNull(createdCategory.getId(), "Category ID should not be null");
        Assert.assertTrue(createdCategory.getId() > 0, "Category ID should be positive");

        logStep("Verifying name matches request");
        Assert.assertEquals(createdCategory.getName(), categoryRequest.getName(),
                "Category name should match request");

        // Save for later tests
        createdCategoryId = createdCategory.getId();
        createdCategoryName = createdCategory.getName();

        LoggerUtil.pass("Category created successfully. ID: " + createdCategoryId);
    }

    @Test(priority = 5, description = "TC_CAT_005: Get category by ID", dependsOnMethods = "testCreateCategory")
    @Story("Get Category")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve category by ID and validate returned category data.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_003")
    public void testGetCategoryById() {
        logStep("Sending GET request to /categories/" + createdCategoryId);
        Response response = CategoryEndpoints.getCategoryById(createdCategoryId);

        Allure.parameter("Category ID", createdCategoryId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response to CategoryResponse");
        CategoryResponse category = response.as(CategoryResponse.class);

        logStep("Verifying category ID matches");
        Assert.assertEquals(category.getId(), createdCategoryId, "Category ID should match");

        logStep("Verifying category name matches");
        Assert.assertEquals(category.getName(), createdCategoryName, "Category name should match");

        LoggerUtil.pass("Get category by ID successful");
    }

    @Test(priority = 6, description = "TC_CAT_006: Update category with valid data", dependsOnMethods = "testCreateCategory")
    @Story("Category Update")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update an existing category and verify updated fields.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_004")
    public void testUpdateCategory() throws JsonProcessingException {
        logStep("Generating update data");
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("Updated Category " + System.currentTimeMillis())
                .image("https://api.lorem.space/image?w=800&h=600")
                .build();

        String requestJson = mapper.writeValueAsString(updateRequest);
        Allure.addAttachment("Request JSON", requestJson);
        Allure.parameter("Category ID", createdCategoryId);

        logStep("Sending PUT request to /categories/" + createdCategoryId);
        Response response = CategoryEndpoints.updateCategory(createdCategoryId, updateRequest);

        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response to CategoryResponse");
        CategoryResponse updatedCategory = response.as(CategoryResponse.class);

        logStep("Verifying ID remains unchanged");
        Assert.assertEquals(updatedCategory.getId(), createdCategoryId, "Category ID should not change");

        logStep("Verifying name is updated");
        Assert.assertEquals(updatedCategory.getName(), updateRequest.getName(),
                "Category name should be updated");

        // Update saved name
        createdCategoryName = updatedCategory.getName();

        LoggerUtil.pass("Category updated successfully");
    }

    @Test(priority = 7, description = "TC_CAT_007: Delete category", dependsOnMethods = "testUpdateCategory")
    @Story("Category Deletion")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Delete an existing category by ID and validate deletion response.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_005")
    public void testDeleteCategory() {
        logStep("Sending DELETE request to /categories/" + createdCategoryId);
        Response response = CategoryEndpoints.deleteCategory(createdCategoryId);

        Allure.parameter("Category ID", createdCategoryId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Verifying response contains 'true'");
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("true"), "Response should contain 'true'");

        LoggerUtil.pass("Category deleted successfully");
    }

    @Test(priority = 8, description = "TC_CAT_008: Verify category removed after delete", dependsOnMethods = "testDeleteCategory")
    @Story("Category Deletion")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify deleted category cannot be retrieved and returns expected error code.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_006")
    public void testCategoryRemovedAfterDelete() {
        logStep("Getting category after deletion");
        Response response = CategoryEndpoints.getCategoryById(createdCategoryId);

        Allure.parameter("Category ID", createdCategoryId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");

        LoggerUtil.pass("Deleted category correctly returns 400");
    }
}
