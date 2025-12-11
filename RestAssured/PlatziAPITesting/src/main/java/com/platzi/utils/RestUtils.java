package com.platzi.utils;

import com.platzi.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestUtils {

    private static RequestSpecification requestSpec;

    /**
     * Initialize REST Assured configuration
     */
    public static void initRestAssured() {
        RestAssured.baseURI = ConfigManager.getBaseUrl();

        // Request Specification only (no response validation)
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();

        RestAssured.requestSpecification = requestSpec;
    }
    /**
     * Reset REST Assured configuration
     */
    public static void resetRestAssured() {
        RestAssured.reset();
        requestSpec = null;
    }
}
