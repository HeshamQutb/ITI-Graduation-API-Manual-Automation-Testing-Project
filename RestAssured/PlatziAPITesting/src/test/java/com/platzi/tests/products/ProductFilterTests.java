package com.platzi.tests.products;

import com.platzi.endpoints.ProductEndpoints;
import com.platzi.pojo.response.ProductResponse;
import com.platzi.tests.base.BaseTest;
import com.platzi.utils.LoggerUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

@Epic("Platzi API Testing")
@Feature("Product Filters")
public class ProductFilterTests extends BaseTest {

    @Test(priority = 1, description = "TC_PRD_FILTER_001: Get products with pagination")
    @Story("Pagination")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify API supports pagination with offset and limit.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_FILTER_001")
    public void testGetProductsWithPagination() {
        int limit = 10;
        int offset = 0;

        Allure.parameter("Offset", offset);
        Allure.parameter("Limit", limit);
        logStep("Sending GET request with pagination: offset=" + offset + ", limit=" + limit);
        Response response = ProductEndpoints.getProductsWithPagination(offset, limit);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Verifying result count does not exceed limit");
        List<ProductResponse> products = response.jsonPath().getList("", ProductResponse.class);
        Assert.assertTrue(products.size() <= limit,
                "Products count should be <= " + limit + ", got: " + products.size());

        LoggerUtil.pass("Pagination working. Products returned: " + products.size());
    }

    @Test(priority = 2, description = "TC_PRD_FILTER_002: Get products by price range")
    @Story("Filter by Price")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify products can be filtered by minimum and maximum price.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_FILTER_002")
    public void testGetProductsByPriceRange() {
        int minPrice = 100;
        int maxPrice = 500;

        Allure.parameter("Min Price", minPrice);
        Allure.parameter("Max Price", maxPrice);
        logStep("Sending GET request with price range: " + minPrice + "-" + maxPrice);
        Response response = ProductEndpoints.getProductsByPriceRange(minPrice, maxPrice);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Verifying all prices are within range");
        List<ProductResponse> products = response.jsonPath().getList("", ProductResponse.class);

        for (ProductResponse product : products) {
            Assert.assertTrue(product.getPrice() >= minPrice && product.getPrice() <= maxPrice,
                    "Price should be within range. Product: " + product.getTitle() +
                            ", Price: " + product.getPrice());
        }

        LoggerUtil.pass("Price filter working. Products in range: " + products.size());
    }

    @Test(priority = 3, description = "TC_PRD_FILTER_003: Get products by category")
    @Story("Filter by Category")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify products returned belong to the specified category ID.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_FILTER_003")
    public void testGetProductsByCategory() {
        int categoryId = 1;

        Allure.parameter("Category ID", categoryId);
        logStep("Sending GET request with categoryId=" + categoryId);
        Response response = ProductEndpoints.getProductsByCategory(categoryId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Verifying all products belong to category " + categoryId);
        List<ProductResponse> products = response.jsonPath().getList("", ProductResponse.class);

        for (ProductResponse product : products) {
            Assert.assertEquals(product.getCategory().getId(), Integer.valueOf(categoryId),
                    "Product should belong to category " + categoryId);
        }

        LoggerUtil.pass("Category filter working. Products in category: " + products.size());
    }

    @Test(priority = 4, description = "TC_PRD_FILTER_004: Get products with offset")
    @Story("Pagination Offset")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that using offset in pagination returns different products for different pages.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_PRD_FILTER_004")
    public void testGetProductsWithOffset() {
        logStep("Getting first page of products");
        List<ProductResponse> firstPage = ProductEndpoints.getProductsWithPagination(0, 5)
                .jsonPath().getList("", ProductResponse.class);

        logStep("Getting second page of products");
        List<ProductResponse> secondPage = ProductEndpoints.getProductsWithPagination(5, 5)
                .jsonPath().getList("", ProductResponse.class);

        logStep("Verifying pages have different products");
        if (!secondPage.isEmpty() && !firstPage.isEmpty()) {
            Assert.assertNotEquals(firstPage.get(0).getId(), secondPage.get(0).getId(),
                    "First product of each page should be different");
        }

        LoggerUtil.pass("Offset pagination working correctly");
    }
}
