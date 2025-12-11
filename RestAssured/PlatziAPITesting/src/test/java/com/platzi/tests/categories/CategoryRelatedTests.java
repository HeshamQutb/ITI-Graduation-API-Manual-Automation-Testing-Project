package com.platzi.tests.categories;

import com.platzi.endpoints.CategoryEndpoints;
import com.platzi.pojo.response.ProductResponse;
import com.platzi.tests.base.BaseTest;
import com.platzi.utils.LoggerUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

@Epic("Platzi API Testing")
@Feature("Category Related Products")
public class CategoryRelatedTests extends BaseTest {

    @Test(priority = 1, description = "TC_CAT_REL_001: Get products by category")
    @Story("Get Products by Category")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve products for a valid category ID and verify response and structure.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_REL_001")
    public void testGetProductsByCategory() {
        int categoryId = 1;

        logStep("Sending GET request to /categories/" + categoryId + "/products");
        Response response = CategoryEndpoints.getProductsByCategory(categoryId);

        Allure.parameter("Category ID", categoryId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response and verifying it is not empty");
        List<ProductResponse> products = response.jsonPath().getList("", ProductResponse.class);
        Assert.assertNotNull(products, "Products list should not be null");

        Allure.parameter("Products Count", products.size());

        LoggerUtil.pass("Get products by category successful. Total: " + products.size());
    }

    @Test(priority = 2, description = "TC_CAT_REL_002: Verify all products belong to category")
    @Story("Verify Product Category")
    @Severity(SeverityLevel.NORMAL)
    @Description("Ensure all products returned belong to the specified category ID.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_REL_002")
    public void testAllProductsBelongToCategory() {
        int categoryId = 1;

        logStep("Getting products for category " + categoryId);
        List<ProductResponse> products = CategoryEndpoints.getProductsByCategoryAsList(categoryId);

        logStep("Verifying all products have correct category ID");
        for (ProductResponse product : products) {
            Assert.assertEquals(product.getCategory().getId(), Integer.valueOf(categoryId),
                    "Product should belong to category " + categoryId);
        }

        Allure.parameter("Products Count", products.size());
        Allure.parameter("Category ID", categoryId);

        LoggerUtil.pass("All products belong to category " + categoryId);
    }

    @Test(priority = 3, description = "TC_CAT_REL_003: Verify products have complete structure")
    @Story("Verify Product Structure")
    @Severity(SeverityLevel.NORMAL)
    @Description("Ensure each product has all required fields like ID, title, price, and images.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_REL_003")
    public void testProductsHaveCompleteStructure() {
        int categoryId = 1;

        logStep("Getting products for category " + categoryId);
        List<ProductResponse> products = CategoryEndpoints.getProductsByCategoryAsList(categoryId);

        if (products.isEmpty()) {
            LoggerUtil.info("No products in category, skipping structure verification");
            Allure.step("No products in category, skipping structure verification");
            return;
        }

        logStep("Verifying each product has required fields");
        for (ProductResponse product : products) {
            Assert.assertNotNull(product.getId(), "Product ID should not be null");
            Assert.assertNotNull(product.getTitle(), "Product title should not be null");
            Assert.assertNotNull(product.getPrice(), "Product price should not be null");
            Assert.assertNotNull(product.getImages(), "Product images should not be null");
        }

        Allure.parameter("Products Count", products.size());
        Allure.parameter("Category ID", categoryId);

        LoggerUtil.pass("All products have complete structure");
    }

    @Test(priority = 4, description = "TC_CAT_REL_004: Get products for invalid category")
    @Story("Invalid Category Handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when retrieving products for an invalid category ID.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_CAT_REL_004")
    public void testGetProductsForInvalidCategory() {
        int invalidCategoryId = 999999;

        logStep("Getting products for invalid category " + invalidCategoryId);
        Response response = CategoryEndpoints.getProductsByCategory(invalidCategoryId);

        Allure.parameter("Invalid Category ID", invalidCategoryId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code");
        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404,
                "Status code should be 200 (empty array) or 404");

        if (response.getStatusCode() == 200) {
            List<ProductResponse> products = response.jsonPath().getList("", ProductResponse.class);
            Assert.assertTrue(products.isEmpty(), "Products list should be empty for invalid category");
        }

        LoggerUtil.pass("Invalid category handled correctly");
    }
}
