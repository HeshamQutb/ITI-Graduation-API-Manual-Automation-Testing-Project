package com.platzi.tests.products;

import com.platzi.endpoints.ProductEndpoints;
import com.platzi.pojo.request.ProductRequest;
import com.platzi.pojo.response.ProductResponse;
import com.platzi.tests.base.BaseTest;
import com.platzi.utils.LoggerUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

@Epic("Platzi API Testing")
@Feature("Product Management")
public class ProductCRUDTests extends BaseTest {

    private static Integer categoryId;
    private static Integer createdProductId;
    private static String createdProductTitle;

    @Test(priority = 1, description = "TC_PRD_001: Get all products")
    @Story("Get All Products")
    @Severity(SeverityLevel.NORMAL)
    @Description("Retrieve all products and validate response array and status code.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_001")
    public void testGetAllProducts() {
        logStep("Sending GET request to /products");
        Response response = ProductEndpoints.getAllProducts();

        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response and verifying it is an array");
        List<ProductResponse> products = response.jsonPath().getList("", ProductResponse.class);
        Assert.assertNotNull(products, "Products list should not be null");
        Assert.assertFalse(products.isEmpty(), "Products list should not be empty");

        Allure.parameter("Products Count", products.size());
        LoggerUtil.pass("Get all products successful. Total: " + products.size());
    }

    @Test(priority = 2, description = "TC_PRD_002: Verify product structure")
    @Story("Verify Product Structure")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the first product contains all required fields including category info.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_002")
    public void testProductStructure() {
        logStep("Getting products list");
        List<ProductResponse> products = ProductEndpoints.getAllProductsAsList();

        logStep("Verifying first product has all required fields");
        ProductResponse product = products.get(0);
        categoryId = product.getCategory().getId();

        Allure.parameter("Category ID", categoryId);

        Assert.assertNotNull(product.getId(), "Product ID should not be null");
        Assert.assertNotNull(product.getTitle(), "Product title should not be null");
        Assert.assertNotNull(product.getPrice(), "Product price should not be null");
        Assert.assertNotNull(product.getDescription(), "Product description should not be null");
        Assert.assertNotNull(product.getImages(), "Product images should not be null");
        Assert.assertNotNull(product.getCategory(), "Product category should not be null");

        LoggerUtil.pass("Product structure is valid");
    }

    @Test(priority = 3, description = "TC_PRD_003: Verify all product IDs are positive")
    @Story("Validate Product IDs")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that all product IDs in the system are positive numbers.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_003")
    public void testAllProductIdsArePositive() {
        logStep("Getting products list");
        List<ProductResponse> products = ProductEndpoints.getAllProductsAsList();

        logStep("Verifying all IDs are positive numbers");
        for (ProductResponse product : products) {
            Assert.assertTrue(product.getId() > 0,
                    "Product ID should be positive, got: " + product.getId());
        }

        LoggerUtil.pass("All product IDs are positive");
    }

    @Test(priority = 4, description = "TC_PRD_004: Verify all prices are non-negative")
    @Story("Validate Product Prices")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that all products have prices greater than or equal to zero.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_004")
    public void testAllPricesAreNonNegative() {
        logStep("Getting products list");
        List<ProductResponse> products = ProductEndpoints.getAllProductsAsList();

        logStep("Verifying all prices are non-negative");
        for (ProductResponse product : products) {
            Assert.assertTrue(product.getPrice() >= 0,
                    "Product price should be non-negative, got: " + product.getPrice());
        }

        LoggerUtil.pass("All product prices are non-negative");
    }

    @Test(priority = 5, description = "TC_PRD_005: Create product with valid data")
    @Story("Create Product")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new product and verify returned ID, title, and price match request.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_005")
    public void testCreateProduct() {
        logStep("Generating random product data");
        ProductRequest productRequest = ProductRequest.builder()
                .title("Test Product - " + System.currentTimeMillis())
                .price(299)
                .description("This is a test product created via Rest Assured automation")
                .categoryId(categoryId)
                .images(List.of("https://placeimg.com/640/480/any"))
                .build();

        Allure.addAttachment("Request Body", productRequest.toString());
        Allure.parameter("Category ID", categoryId);

        logStep("Sending POST request to /products");
        Response response = ProductEndpoints.createProduct(productRequest);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 201");
        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201");

        logStep("Parsing response to ProductResponse");
        ProductResponse createdProduct = response.as(ProductResponse.class);

        logStep("Verifying product ID is generated");
        Assert.assertNotNull(createdProduct.getId(), "Product ID should not be null");
        Assert.assertTrue(createdProduct.getId() > 0, "Product ID should be positive");

        logStep("Verifying title matches request");
        Assert.assertEquals(createdProduct.getTitle(), productRequest.getTitle(),
                "Product title should match request");

        logStep("Verifying price matches request");
        Assert.assertEquals(createdProduct.getPrice(), productRequest.getPrice(),
                "Product price should match request");

        createdProductId = createdProduct.getId();
        createdProductTitle = createdProduct.getTitle();

        LoggerUtil.pass("Product created successfully. ID: " + createdProductId);
    }

    @Test(priority = 6, description = "TC_PRD_006: Verify created product has category")
    @Story("Verify Product Category")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that created product has a valid category attached.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_006")
    public void testCreatedProductHasCategory() {
        logStep("Creating product with category");
        ProductRequest productRequest = ProductRequest.defaultProduct().build();

        ProductResponse product = ProductEndpoints.createProductAsObject(productRequest);
        Allure.addAttachment("Created Product Response", product.toString());

        logStep("Verifying category is present");
        Assert.assertNotNull(product.getCategory(), "Category should not be null");
        Assert.assertNotNull(product.getCategory().getId(), "Category ID should not be null");
        Assert.assertNotNull(product.getCategory().getName(), "Category name should not be null");

        // Cleanup
        ProductEndpoints.deleteProduct(product.getId());

        LoggerUtil.pass("Created product has valid category");
    }

    @Test(priority = 7, description = "TC_PRD_007: Get product by valid ID",
            dependsOnMethods = "testCreateProduct")
    @Story("Get Product by ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve product by ID and verify all returned fields match expected values.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_007")
    public void testGetProductById() {
        logStep("Sending GET request to /products/" + createdProductId);
        Response response = ProductEndpoints.getProductById(createdProductId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response to ProductResponse");
        ProductResponse product = response.as(ProductResponse.class);

        logStep("Verifying product ID matches");
        Assert.assertEquals(product.getId(), createdProductId, "Product ID should match");

        logStep("Verifying product title matches");
        Assert.assertEquals(product.getTitle(), createdProductTitle, "Product title should match");

        LoggerUtil.pass("Get product by ID successful");
    }

    @Test(priority = 8, description = "TC_PRD_008: Verify response is single object not array",
            dependsOnMethods = "testCreateProduct")
    @Story("Verify Single Object Response")
    @Severity(SeverityLevel.NORMAL)
    @Description("Ensure GET by ID returns single object instead of an array.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_008")
    public void testGetProductByIdReturnsSingleObject() {
        logStep("Getting product by ID");
        Response response = ProductEndpoints.getProductById(createdProductId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response is object (has id field directly)");
        Integer id = response.jsonPath().getInt("id");
        Assert.assertNotNull(id, "Response should be single object with id field");

        LoggerUtil.pass("Response is single object");
    }

    @Test(priority = 9, description = "TC_PRD_009: Update product with valid data",
            dependsOnMethods = "testCreateProduct")
    @Story("Update Product")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update an existing product and verify returned data.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_009")
    public void testUpdateProduct() {
        logStep("Generating update data");
        ProductRequest updateRequest = ProductRequest.builder()
                .title("Updated Test Product - " + System.currentTimeMillis())
                .price(399)
                .description("This product has been updated via Rest Assured automation")
                .categoryId(categoryId)
                .images(List.of("https://placeimg.com/640/480/any"))
                .build();

        Allure.addAttachment("Update Request", updateRequest.toString());

        logStep("Sending PUT request to /products/" + createdProductId);
        Response response = ProductEndpoints.updateProduct(createdProductId, updateRequest);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response to ProductResponse");
        ProductResponse updatedProduct = response.as(ProductResponse.class);

        logStep("Verifying ID remains unchanged");
        Assert.assertEquals(updatedProduct.getId(), createdProductId, "Product ID should not change");

        logStep("Verifying title is updated");
        Assert.assertEquals(updatedProduct.getTitle(), updateRequest.getTitle(),
                "Product title should be updated");

        logStep("Verifying price is updated");
        Assert.assertEquals(updatedProduct.getPrice(), updateRequest.getPrice(),
                "Product price should be updated");

        createdProductTitle = updatedProduct.getTitle();

        LoggerUtil.pass("Product updated successfully");
    }

    @Test(priority = 10, description = "TC_PRD_010: Verify update persisted",
            dependsOnMethods = "testUpdateProduct")
    @Story("Verify Update Persistence")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that product updates are persisted when retrieved again.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_010")
    public void testUpdatePersisted() {
        logStep("Getting product after update");
        ProductResponse product = ProductEndpoints.getProductByIdAsObject(createdProductId);

        logStep("Verifying updated title is persisted");
        Assert.assertEquals(product.getTitle(), createdProductTitle,
                "Updated title should be persisted");

        logStep("Verifying updated price is persisted");
        Assert.assertEquals(product.getPrice(), Integer.valueOf(399),
                "Updated price should be persisted");

        LoggerUtil.pass("Update persisted successfully");
    }

    @Test(priority = 11, description = "TC_PRD_011: Delete product",
            dependsOnMethods = "testUpdatePersisted")
    @Story("Delete Product")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Delete a product and verify deletion response is correct.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_011")
    public void testDeleteProduct() {
        logStep("Sending DELETE request to /products/" + createdProductId);
        Response response = ProductEndpoints.deleteProduct(createdProductId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Verifying response contains 'true'");
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("true"), "Response should contain 'true'");

        LoggerUtil.pass("Product deleted successfully");
    }

    @Test(priority = 12, description = "TC_PRD_012: Verify product removed after delete",
            dependsOnMethods = "testDeleteProduct")
    @Story("Verify Deletion")
    @Severity(SeverityLevel.NORMAL)
    @Description("Ensure deleted product cannot be retrieved and returns expected error code.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_012")
    public void testProductRemovedAfterDelete() {
        logStep("Getting product after deletion");
        Response response = ProductEndpoints.getProductById(createdProductId);

        logStep("Verifying response status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");

        LoggerUtil.pass("Deleted product correctly returns 400");
    }
}
