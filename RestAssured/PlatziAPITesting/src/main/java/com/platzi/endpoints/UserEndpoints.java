package com.platzi.endpoints;

import com.platzi.config.ConfigManager;
import com.platzi.config.EndpointConfig;
import com.platzi.pojo.request.UserRequest;
import com.platzi.pojo.response.UserResponse;
import io.restassured.response.Response;
import java.util.List;
import static io.restassured.RestAssured.*;

public class UserEndpoints {

    // ============ GET Operations ============

    /**
     * Get all users
     * @return Response object
     */
    public static Response getAllUsers() {
        return given()
                .contentType("application/json")
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.USERS)
                .then()
                .extract().response();
    }

    /**
     * Get all users as List
     * @return List of UserResponse
     */
    public static List<UserResponse> getAllUsersAsList() {
        return getAllUsers()
                .jsonPath()
                .getList("", UserResponse.class);
    }

    /**
     * Get user by ID
     * @param userId User ID
     * @return Response object
     */
    public static Response getUserById(int userId) {
        return given()
                .contentType("application/json")
                .pathParam("id", userId)
                .when()
                .get(ConfigManager.getBaseUrl() + EndpointConfig.USER_BY_ID)
                .then()
                .extract().response();
    }

    /**
     * Get user by ID - returns typed response
     * @param userId User ID
     * @return UserResponse object
     */
    public static UserResponse getUserByIdAsObject(int userId) {
        return getUserById(userId).as(UserResponse.class);
    }

    // ============ POST Operations ============

    /**
     * Create new user
     * @param userRequest User data
     * @return Response object
     */
    public static Response createUser(UserRequest userRequest) {
        return given()
                .contentType("application/json")
                .body(userRequest)
                .when()
                .post(ConfigManager.getBaseUrl() + EndpointConfig.USERS)
                .then()
                .extract().response();
    }

    /**
     * Create user - returns typed response
     * @param userRequest User data
     * @return UserResponse object
     */
    public static UserResponse createUserAsObject(UserRequest userRequest) {
        return createUser(userRequest).as(UserResponse.class);
    }

    /**
     * Create user with default data
     * @return Response object
     */
    public static Response createDefaultUser() {
        UserRequest request = UserRequest.defaultUser().build();
        return createUser(request);
    }


    // ============ PUT Operations ============

    /**
     * Update user
     * @param userId User ID
     * @param userRequest Updated user data
     * @return Response object
     */
    public static Response updateUser(int userId, UserRequest userRequest) {
        return given()
                .contentType("application/json")
                .pathParam("id", userId)
                .body(userRequest)
                .when()
                .put(ConfigManager.getBaseUrl() + EndpointConfig.USER_BY_ID)
                .then()
                .extract().response();
    }

    /**
     * Update user - returns typed response
     * @param userId User ID
     * @param userRequest Updated data
     * @return UserResponse object
     */
    public static UserResponse updateUserAsObject(int userId, UserRequest userRequest) {
        return updateUser(userId, userRequest).as(UserResponse.class);
    }

    // ============ DELETE Operations ============

    /**
     * Delete user
     * @param userId User ID
     * @return Response object
     */
    public static Response deleteUser(int userId) {
        return given()
                .contentType("application/json")
                .pathParam("id", userId)
                .when()
                .delete(ConfigManager.getBaseUrl() + EndpointConfig.USER_BY_ID)
                .then()
                .extract().response();
    }

    /**
     * Delete user and confirm
     * @param userId User ID
     * @return true if deleted
     */
    public static boolean deleteUserAndConfirm(int userId) {
        Response response = deleteUser(userId);
        return response.getStatusCode() == 200 && response.as(Boolean.class);
    }
}