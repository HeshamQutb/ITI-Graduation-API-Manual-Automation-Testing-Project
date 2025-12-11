package com.platzi.tests.products;

import com.platzi.endpoints.ProductEndpoints;
import com.platzi.pojo.request.ProductRequest;
import com.platzi.pojo.response.ErrorResponse;
import com.platzi.tests.base.BaseTest;
import com.platzi.utils.LoggerUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Platzi API Testing")
@Feature("Product Negative Scenarios")
public class ProductNegativeTests extends BaseTest {

    int invalidId = 999999;

    @Test(priority = 1, description = "TC_PRD_NEG_001: Get product with invalid ID")
    @Story("Get Product by Invalid ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that fetching a product with an invalid ID returns a 400 status code.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_NEG_001")
    public void testGetProductWithInvalidId() {
        Allure.parameter("Invalid ID", invalidId);
        logStep("Sending GET request with invalid ID: " + invalidId);
        Response response = ProductEndpoints.getProductById(invalidId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");

        LoggerUtil.pass("Invalid ID correctly returns 400");
    }

    @Test(priority = 2, description = "TC_PRD_NEG_002: Get product with negative ID")
    @Story("Get Product by Negative ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that fetching a product with a negative ID returns 400 or 404.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_NEG_002")
    public void testGetProductWithNegativeId() {
        int negativeId = -1;
        Allure.parameter("Negative ID", negativeId);

        logStep("Sending GET request with negative ID: " + negativeId);
        Response response = ProductEndpoints.getProductById(negativeId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 400 or 404");
        Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 404,
                "Status code should be 400 or 404");

        LoggerUtil.pass("Negative ID correctly handled");
    }

    @Test(priority = 3, description = "TC_PRD_NEG_003: Create product with missing required fields")
    @Story("Create Product with Missing Fields")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a product without required fields returns an error.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_NEG_003")
    public void testCreateProductWithMissingFields() {
        logStep("Creating product request with only title");
        ProductRequest invalidProduct = ProductRequest.builder()
                .title("Incomplete Product")
                .build();

        logStep("Sending POST request");
        Response response = ProductEndpoints.createProduct(invalidProduct);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 500");
        Assert.assertEquals(response.getStatusCode(), 500, "Status code should be 500");

        logStep("Verifying error message is present");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        Assert.assertNotNull(errorResponse.getMessage(), "Error message should not be null");

        LoggerUtil.pass("Missing fields correctly returns 500");
    }

    @Test(priority = 4, description = "TC_PRD_NEG_004: Update non-existent product")
    @Story("Update Non-Existent Product")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that updating a non-existent product returns 400.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_NEG_004")
    public void testUpdateNonExistentProduct() {

        Allure.parameter("Invalid Product ID", invalidId);

        logStep("Creating update request for non-existent product");
        ProductRequest updateRequest = ProductRequest.builder()
                .title("Updated Title")
                .price(100)
                .build();

        logStep("Sending PUT request to /products/" + invalidId);
        Response response = ProductEndpoints.updateProduct(invalidId, updateRequest);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");

        LoggerUtil.pass("Update non-existent product correctly returns 400");
    }

    @Test(priority = 5, description = "TC_PRD_NEG_005: Delete non-existent product")
    @Story("Delete Non-Existent Product")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that deleting a non-existent product returns 400.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_NEG_005")
    public void testDeleteNonExistentProduct() {
        Allure.parameter("Invalid Product ID", invalidId);

        logStep("Sending DELETE request to /products/" + invalidId);
        Response response = ProductEndpoints.deleteProduct(invalidId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");

        LoggerUtil.pass("Delete non-existent product correctly returns 400");
    }

    @Test(priority = 6, description = "TC_PRD_NEG_006: Create product with invalid category")
    @Story("Create Product with Invalid Category")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a product with a non-existent category handles error properly.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_NEG_006")
    public void testCreateProductWithInvalidCategory() {
        logStep("Creating product with non-existent category");
        Allure.parameter("Invalid Category ID", 999999);

        ProductRequest invalidProduct = ProductRequest.builder()
                .title("Test Product")
                .price(100)
                .description("Test description")
                .categoryId(999999)
                .images(java.util.List.of("https://placeimg.com/640/480/any"))
                .build();

        logStep("Sending POST request");
        Response response = ProductEndpoints.createProduct(invalidProduct);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response handles invalid category");
        Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 404
                        || response.getStatusCode() == 201,
                "Status code should indicate error or success with default category");

        if (response.getStatusCode() == 201) {
            int productId = response.jsonPath().getInt("id");
            ProductEndpoints.deleteProduct(productId);
        }

        LoggerUtil.pass("Invalid category handled");
    }
}
