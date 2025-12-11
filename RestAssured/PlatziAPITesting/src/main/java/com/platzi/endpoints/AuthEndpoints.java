package com.platzi.endpoints;
import com.platzi.config.ConfigManager;
import com.platzi.config.EndpointConfig;
import com.platzi.pojo.request.LoginRequest;
import com.platzi.pojo.request.RefreshTokenRequest;
import com.platzi.pojo.response.UserResponse;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class AuthEndpoints {

    /**
     * Login with email and password
     * @param loginRequest Login credentials
     * @return Response object
     */
    public static Response login(LoginRequest loginRequest) {
        return given()
                    .contentType("application/json")
                    .body(loginRequest)
                .when()
                    .post(ConfigManager.getBaseUrl() + EndpointConfig.AUTH_LOGIN)
                .then()
                    .extract().response();
    }

    /**
     * Get user profile with access token
     * @param accessToken Bearer token
     * @return Response object
     */
    public static Response getProfile(String accessToken) {
        return given()
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + accessToken)
                .when()
                    .get(ConfigManager.getBaseUrl() + EndpointConfig.AUTH_PROFILE)
                .then()
                    .extract().response();
    }

    /**
     * Get user profile - returns typed response
     * @param accessToken Bearer token
     * @return UserResponse object
     */
    public static UserResponse getProfileAsUser(String accessToken) {
        return getProfile(accessToken).as(UserResponse.class);
    }

    /**
     * Get profile without authorization
     * @return Response object
     */
    public static Response getProfileWithoutAuth() {
        return given()
                .contentType("application/json")
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.AUTH_PROFILE)
                .then()
                .extract().response();
    }

    /**
     * Refresh access token
     * @param refreshToken Current refresh token
     * @return Response object
     */
    public static Response refreshToken(String refreshToken) {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(refreshToken)
                .build();

        return given()
                .contentType("application/json")
                .body(request)
                .when()
                .post(ConfigManager.getBaseUrl() + EndpointConfig.AUTH_REFRESH)
                .then()
                .extract().response();
    }

}