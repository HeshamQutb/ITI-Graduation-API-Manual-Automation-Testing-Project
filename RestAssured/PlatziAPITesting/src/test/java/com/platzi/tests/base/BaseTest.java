package com.platzi.tests.base;

import com.platzi.config.ConfigManager;
import com.platzi.endpoints.AuthEndpoints;
import com.platzi.pojo.request.LoginRequest;
import com.platzi.pojo.response.AuthResponse;
import com.platzi.utils.LoggerUtil;
import com.platzi.utils.RestUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.testng.annotations.*;

public class BaseTest {

    protected static Logger logger;
    protected static String accessToken;
    protected static String refreshToken;

    @BeforeSuite
    @Step("Setup Test Suite")
    public void setupSuite() {
        logger = LoggerUtil.getLogger(BaseTest.class);
        LoggerUtil.info("=== Test Suite Setup Started ===");
        LoggerUtil.info("Base URL: " + ConfigManager.getBaseUrl());

        // Add environment info to Allure
        Allure.addAttachment("Base URL", "text/plain", ConfigManager.getBaseUrl(), "txt");
        Allure.addAttachment("Tester", "text/plain", "Hesham Qutb", "txt");
    }

    @BeforeClass
    @Step("Setup Test Class: {this.class.simpleName}")
    public void setupClass() {
        RestUtils.initRestAssured();
        LoggerUtil.info("REST Assured initialized for: " + this.getClass().getSimpleName());
    }

    @BeforeMethod
    public void setupMethod() {
        // Can be overridden in child classes
    }

    @AfterMethod
    public void teardownMethod() {
        // Can be overridden in child classes
    }

    @AfterClass
    @Step("Teardown Test Class")
    public void teardownClass() {
        LoggerUtil.info("Test class completed: " + this.getClass().getSimpleName());
    }

    @AfterSuite
    @Step("Teardown Test Suite")
    public void teardownSuite() {
        RestUtils.resetRestAssured();
        LoggerUtil.info("=== Test Suite Completed ===");
    }

    @Step("Authenticate and get access token")
    protected void authenticate() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email(ConfigManager.getTestEmail())
                .password(ConfigManager.getTestPassword())
                .build();

        AuthResponse response = AuthEndpoints.login(loginRequest).as(AuthResponse.class);
        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();

        LoggerUtil.info("Authentication successful. Token obtained.");
        Allure.addAttachment("Access Token (Preview)", "text/plain",
                accessToken.substring(0, Math.min(30, accessToken.length())) + "...", "txt");
    }

    protected String getAccessToken() {
        if (accessToken == null || accessToken.isEmpty()) {
            authenticate();
        }
        return accessToken;
    }

    protected String getRefreshToken() {
        if (refreshToken == null || refreshToken.isEmpty()) {
            authenticate();
        }
        return refreshToken;
    }

    protected void clearAuthentication() {
        accessToken = null;
        refreshToken = null;
        LoggerUtil.info("Authentication cleared.");
    }

    @Step("{step}")
    protected void logStep(String step) {
        LoggerUtil.step(step);
    }

    protected void assertStatusCode(int actual, int expected) {
        if (actual == expected) {
            LoggerUtil.pass("Status code is " + expected);
        } else {
            LoggerUtil.fail("Expected status " + expected + " but got " + actual);
        }
    }
}