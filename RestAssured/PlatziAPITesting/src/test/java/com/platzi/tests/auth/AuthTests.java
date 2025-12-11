package com.platzi.tests.auth;

import com.platzi.config.ConfigManager;
import com.platzi.endpoints.AuthEndpoints;
import com.platzi.pojo.request.LoginRequest;
import com.platzi.pojo.response.AuthResponse;
import com.platzi.pojo.response.UserResponse;
import com.platzi.tests.base.BaseTest;
import com.platzi.utils.LoggerUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;

@Epic("Platzi API Testing")
@Feature("Auth Management")
public class AuthTests extends BaseTest {

    // POST LOGIN TESTS

    @Test(priority = 1, description = "TC_AUTH_001: Login with valid credentials")
    @Story("User Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that user can login with valid email and password credentials and receive access and refresh tokens")
    @Owner("Hesham Qutb")
    @Issue("AUTH-001")
    @TmsLink("TC_AUTH_001")
    public void testLoginWithValidCredentials() {
        logStep("Creating login request with valid credentials");
        LoginRequest loginRequest = LoginRequest.builder()
                .email(ConfigManager.getTestEmail())
                .password(ConfigManager.getTestPassword())
                .build();

        Allure.addAttachment("Login Request", "application/json",
                String.format("{\"email\":\"%s\",\"password\":\"***\"}", loginRequest.getEmail()));

        logStep("Sending POST request to /auth/login");
        Response response = AuthEndpoints.login(loginRequest);

        Allure.addAttachment("Login Response", "application/json", response.getBody().asString());
        Allure.parameter("Status Code", response.getStatusCode());
        Allure.parameter("Response Time", response.getTime() + " ms");

        logStep("Verifying response status code is 201");
        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201");

        logStep("Parsing response to AuthResponse");
        AuthResponse authResponse = response.as(AuthResponse.class);

        logStep("Verifying access_token is present");
        Assert.assertNotNull(authResponse.getAccessToken(), "Access token should not be null");
        Assert.assertFalse(authResponse.getAccessToken().isEmpty(), "Access token should not be empty");

        logStep("Verifying refresh_token is present");
        Assert.assertNotNull(authResponse.getRefreshToken(), "Refresh token should not be null");
        Assert.assertFalse(authResponse.getRefreshToken().isEmpty(), "Refresh token should not be empty");

        // Save tokens for other tests
        accessToken = authResponse.getAccessToken();
        refreshToken = authResponse.getRefreshToken();

        LoggerUtil.pass("Login successful with valid credentials");
    }

    @Test(priority = 2, description = "TC_AUTH_002: Verify access_token is JWT format")
    @Story("Token Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that access token follows JWT format with 3 parts separated by dots")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_002")
    public void testAccessTokenIsJWTFormat() {
        logStep("Getting access token");
        String token = getAccessToken();

        Allure.parameter("Token Length", token.length());
        Allure.addAttachment("Access Token (Preview)", token.substring(0, 50) + "...");

        logStep("Verifying token has JWT format (3 parts separated by dots)");
        String[] tokenParts = token.split("\\.");
        Assert.assertEquals(tokenParts.length, 3, "JWT should have 3 parts");

        logStep("Verifying each part is not empty");
        for (String part : tokenParts) {
            Assert.assertFalse(part.isEmpty(), "JWT parts should not be empty");
        }

        LoggerUtil.pass("Access token is valid JWT format");
    }

    @Test(priority = 3, description = "TC_AUTH_003: Verify tokens are different")
    @Story("Token Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that access token and refresh token are different from each other")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_003")
    public void testTokensAreDifferent() {
        logStep("Getting both tokens");
        String access = getAccessToken();
        String refresh = getRefreshToken();

        logStep("Verifying access_token and refresh_token are different");
        Assert.assertNotEquals(access, refresh, "Access and refresh tokens should be different");

        LoggerUtil.pass("Tokens are different");
    }

    @Test(priority = 4, description = "TC_AUTH_004: Verify response time is acceptable")
    @Story("User Login")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that login API response time is within acceptable limits (< 2000ms)")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_004")
    public void testLoginResponseTime() {
        logStep("Creating login request");
        LoginRequest loginRequest = LoginRequest.builder()
                .email(ConfigManager.getTestEmail())
                .password(ConfigManager.getTestPassword())
                .build();

        logStep("Sending request and measuring response time");
        Response response = AuthEndpoints.login(loginRequest);
        long responseTime = response.getTime();

        Allure.parameter("Response Time", responseTime + " ms");

        logStep("Verifying response time is less than 2000ms");
        Assert.assertTrue(responseTime < 2000,
                "Response time should be less than 2000ms, actual: " + responseTime + "ms");

        LoggerUtil.pass("Response time is acceptable: " + responseTime + "ms");
    }

    @Test(priority = 5, description = "TC_AUTH_005: Login with invalid email")
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that login fails with appropriate error when using invalid email address")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_005")
    public void testLoginWithInvalidEmail() {
        logStep("Creating login request with invalid email");
        LoginRequest loginRequest = LoginRequest.builder()
                .email("invalid@email.com")
                .password(ConfigManager.getTestPassword())
                .build();

        Allure.addAttachment("Invalid Login Request", "application/json",
                String.format("{\"email\":\"%s\",\"password\":\"***\"}", loginRequest.getEmail()));

        logStep("Sending POST request to /auth/login");
        Response response = AuthEndpoints.login(loginRequest);

        Allure.addAttachment("Error Response", "application/json", response.getBody().asString());
        Allure.parameter("Status Code", response.getStatusCode());

        logStep("Verifying response status code is 401");
        Assert.assertEquals(response.getStatusCode(), 401, "Status code should be 401");

        LoggerUtil.pass("Invalid email correctly returns 401");
    }

    @Test(priority = 6, description = "TC_AUTH_006: Login with invalid password")
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that login fails with appropriate error when using invalid password")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_006")
    public void testLoginWithInvalidPassword() {
        logStep("Creating login request with invalid password");
        LoginRequest loginRequest = LoginRequest.builder()
                .email(ConfigManager.getTestEmail())
                .password("wrongpassword")
                .build();

        logStep("Sending POST request to /auth/login");
        Response response = AuthEndpoints.login(loginRequest);

        Allure.parameter("Status Code", response.getStatusCode());

        logStep("Verifying response status code is 401");
        Assert.assertEquals(response.getStatusCode(), 401, "Status code should be 401");

        LoggerUtil.pass("Invalid password correctly returns 401");
    }

    @Test(priority = 7, description = "TC_AUTH_007: Login with missing email")
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that login fails when email field is missing from request")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_007")
    public void testLoginWithMissingEmail() {
        logStep("Creating login request without email");
        LoginRequest loginRequest = LoginRequest.builder()
                .password(ConfigManager.getTestPassword())
                .build();

        logStep("Sending POST request to /auth/login");
        Response response = AuthEndpoints.login(loginRequest);

        logStep("Verifying response status code is 400 or 401");
        Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 401,
                "Status code should be 400 or 401");

        LoggerUtil.pass("Missing email correctly handled");
    }

    @Test(priority = 8, description = "TC_AUTH_008: Login with empty body")
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that login fails when request body is empty")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_008")
    public void testLoginWithEmptyBody() {
        logStep("Creating empty login request");
        LoginRequest loginRequest = new LoginRequest();

        logStep("Sending POST request to /auth/login");
        Response response = AuthEndpoints.login(loginRequest);

        logStep("Verifying response status code is 400 or 401");
        Assert.assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 401,
                "Status code should be 400 or 401");

        LoggerUtil.pass("Empty body correctly handled");
    }

    // GET PROFILE TESTS

    @Test(priority = 9, description = "TC_AUTH_009: Get profile with valid token")
    @Story("User Profile")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that authenticated user can retrieve their profile information")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_009")
    public void testGetProfileWithValidToken() {
        logStep("Getting access token");
        String token = getAccessToken();

        logStep("Sending GET request to /auth/profile");
        Response response = AuthEndpoints.getProfile(token);

        Allure.addAttachment("Profile Response", "application/json", response.getBody().asString());
        Allure.parameter("Status Code", response.getStatusCode());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response to UserResponse");
        UserResponse userResponse = response.as(UserResponse.class);

        logStep("Verifying required fields are present");
        Assert.assertNotNull(userResponse.getId(), "ID should not be null");
        Assert.assertNotNull(userResponse.getEmail(), "Email should not be null");
        Assert.assertNotNull(userResponse.getName(), "Name should not be null");
        Assert.assertNotNull(userResponse.getRole(), "Role should not be null");

        Allure.parameter("User ID", userResponse.getId());
        Allure.parameter("User Email", userResponse.getEmail());
        Allure.parameter("User Role", userResponse.getRole());

        LoggerUtil.pass("Profile retrieved successfully");
    }

    @Test(priority = 10, description = "TC_AUTH_010: Verify profile email matches login email")
    @Story("User Profile")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that email in profile matches the email used for login")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_010")
    public void testProfileEmailMatchesLoginEmail() {
        logStep("Getting access token");
        String token = getAccessToken();

        logStep("Getting user profile");
        UserResponse userResponse = AuthEndpoints.getProfileAsUser(token);

        logStep("Verifying email matches test email");
        Assert.assertEquals(userResponse.getEmail(), ConfigManager.getTestEmail(),
                "Profile email should match login email");

        LoggerUtil.pass("Profile email matches login email");
    }

    @Test(priority = 11, description = "TC_AUTH_011: Verify profile role is valid")
    @Story("User Profile")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that user role is either 'customer' or 'admin'")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_011")
    public void testProfileRoleIsValid() {
        logStep("Getting user profile");
        UserResponse userResponse = AuthEndpoints.getProfileAsUser(getAccessToken());

        logStep("Verifying role is 'customer' or 'admin'");
        String role = userResponse.getRole();
        Assert.assertTrue(role.equals("customer") || role.equals("admin"),
                "Role should be 'customer' or 'admin', actual: " + role);

        Allure.parameter("User Role", role);

        LoggerUtil.pass("Profile role is valid: " + role);
    }

    @Test(priority = 12, description = "TC_AUTH_012: Get profile without token")
    @Story("User Profile")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that profile endpoint returns 401 when no authentication token is provided")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_012")
    public void testGetProfileWithoutToken() {
        logStep("Sending GET request to /auth/profile without token");
        Response response = AuthEndpoints.getProfileWithoutAuth();

        logStep("Verifying response status code is 401");
        Assert.assertEquals(response.getStatusCode(), 401, "Status code should be 401");

        LoggerUtil.pass("Unauthorized access correctly returns 401");
    }

    @Test(priority = 13, description = "TC_AUTH_013: Get profile with invalid token")
    @Story("User Profile")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that profile endpoint returns 401 when invalid authentication token is provided")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_013")
    public void testGetProfileWithInvalidToken() {
        logStep("Sending GET request with invalid token");
        Response response = AuthEndpoints.getProfile("invalid_token_12345");

        Allure.parameter("Status Code", response.getStatusCode());

        logStep("Verifying response status code is 401");
        Assert.assertEquals(response.getStatusCode(), 401, "Status code should be 401");

        LoggerUtil.pass("Invalid token correctly returns 401");
    }

    // REFRESH TOKEN TESTS

    @Test(priority = 14, description = "TC_AUTH_014: Refresh token with valid refresh token")
    @Story("Token Refresh")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that user can refresh access token using valid refresh token")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_014")
    public void testRefreshTokenWithValidToken() {
        logStep("Getting refresh token");
        String refresh = getRefreshToken();

        logStep("Sending POST request to /auth/refresh-token");
        Response response = AuthEndpoints.refreshToken(refresh);

        logStep("Verifying response status code is 201");
        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201");

        Allure.addAttachment("Refresh Token Response", "application/json", response.getBody().asString());
        Allure.parameter("Status Code", response.getStatusCode());

        logStep("Parsing response to AuthResponse");
        AuthResponse authResponse = response.as(AuthResponse.class);

        logStep("Verifying new tokens are returned");
        Assert.assertNotNull(authResponse.getAccessToken(), "New access token should not be null");
        Assert.assertNotNull(authResponse.getRefreshToken(), "New refresh token should not be null");

        LoggerUtil.pass("Token refresh successful");
    }

    @Test(priority = 15, description = "TC_AUTH_015: Refresh token with invalid token")
    @Story("Token Refresh")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that token refresh fails with invalid refresh token")
    @Owner("Hesham Qutb")
    @TmsLink("TC_AUTH_015")
    public void testRefreshTokenWithInvalidToken() {
        logStep("Sending refresh request with invalid token");
        Response response = AuthEndpoints.refreshToken("invalid_refresh_token");

        Allure.parameter("Status Code", response.getStatusCode());

        logStep("Verifying response status code is 401");
        Assert.assertEquals(response.getStatusCode(), 401, "Status code should be 401");

        LoggerUtil.pass("Invalid refresh token correctly returns 401");
    }
}