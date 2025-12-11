package com.platzi.tests.users;

import com.platzi.endpoints.UserEndpoints;
import com.platzi.pojo.request.UserRequest;
import com.platzi.pojo.response.UserResponse;
import com.platzi.tests.base.BaseTest;
import com.platzi.utils.LoggerUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

@Epic("Platzi API Testing")
@Feature("Users Management")
public class UserCRUDTests extends BaseTest {

    private static Integer createdUserId;
    private static String createdUserEmail;
    private static String createdUserName;

    @Test(priority = 1, description = "TC_USR_001: Get all users")
    @Story("Get All Users")
    @Severity(SeverityLevel.NORMAL)
    @Description("Retrieve all users and validate array structure and status code.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_001")
    public void testGetAllUsers() {
        logStep("Sending GET request to /users");
        Response response = UserEndpoints.getAllUsers();
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Verifying response is an array");
        List<UserResponse> users = response.jsonPath().getList("", UserResponse.class);
        Assert.assertNotNull(users, "Users list should not be null");
        Assert.assertFalse(users.isEmpty(), "Users list should not be empty");

        LoggerUtil.pass("Get all users successful. Total: " + users.size());
    }

    @Test(priority = 2, description = "TC_USR_002: Verify user structure")
    @Story("Verify User Structure")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that each user has all required fields.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_002")
    public void testUserStructure() {
        logStep("Getting users list");
        List<UserResponse> users = UserEndpoints.getAllUsersAsList();

        logStep("Verifying first user has all required fields");
        UserResponse user = users.get(0);

        Assert.assertNotNull(user.getId(), "User ID should not be null");
        Assert.assertNotNull(user.getEmail(), "User email should not be null");
        Assert.assertNotNull(user.getName(), "User name should not be null");
        Assert.assertNotNull(user.getRole(), "User role should not be null");
        Assert.assertNotNull(user.getAvatar(), "User avatar should not be null");

        LoggerUtil.pass("User structure is valid");
    }

    @Test(priority = 3, description = "TC_USR_003: Verify email format is valid")
    @Story("Validate Email Format")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that all user emails are in valid format.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_003")
    public void testEmailFormatIsValid() {
        logStep("Getting users list");
        List<UserResponse> users = UserEndpoints.getAllUsersAsList();

        logStep("Verifying all emails have valid format");
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        for (UserResponse user : users) {
            Assert.assertTrue(user.getEmail().matches(emailRegex),
                    "Email should be valid format: " + user.getEmail());
        }

        LoggerUtil.pass("All user emails have valid format");
    }

    @Test(priority = 4, description = "TC_USR_004: Create user with valid data")
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a user with valid payload and verify ID and fields.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_004")
    public void testCreateUser() {
        logStep("Generating random user data");
        UserRequest userRequest = UserRequest.builder()
                .name("Test User" + System.currentTimeMillis())
                .email("testuser" + System.currentTimeMillis()+ "@gmail.com")
                .password("tespass123")
                .avatar("https://api.lorem.space/image/face?w=640&h=480")
                .build();
        Allure.addAttachment("Request Body", userRequest.toString());

        logStep("Sending POST request to /users");
        Response response = UserEndpoints.createUser(userRequest);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 201");
        Assert.assertEquals(response.getStatusCode(), 201, "Status code should be 201");

        logStep("Parsing response to UserResponse");
        UserResponse createdUser = response.as(UserResponse.class);

        logStep("Verifying user ID is generated");
        Assert.assertNotNull(createdUser.getId(), "User ID should not be null");
        Assert.assertTrue(createdUser.getId() > 0, "User ID should be positive");

        logStep("Verifying name matches request");
        Assert.assertEquals(createdUser.getName(), userRequest.getName(),
                "User name should match request");

        logStep("Verifying email matches request");
        Assert.assertEquals(createdUser.getEmail(), userRequest.getEmail(),
                "User email should match request");

        createdUserId = createdUser.getId();
        createdUserEmail = createdUser.getEmail();
        createdUserName = createdUser.getName();

        LoggerUtil.pass("User created successfully. ID: " + createdUserId);
    }

    @Test(priority = 5, description = "TC_USR_005: Get user by valid ID", dependsOnMethods = "testCreateUser")
    @Story("Get User By ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve user by ID and validate returned user data.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_005")
    public void testGetUserById() {
        logStep("Sending GET request to /users/" + createdUserId);
        Allure.parameter("User ID", createdUserId);
        Response response = UserEndpoints.getUserById(createdUserId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response to UserResponse");
        UserResponse user = response.as(UserResponse.class);

        logStep("Verifying user ID matches");
        Assert.assertEquals(user.getId(), createdUserId, "User ID should match");

        logStep("Verifying user email matches");
        Assert.assertEquals(user.getEmail(), createdUserEmail, "User email should match");

        LoggerUtil.pass("Get user by ID successful");
    }

    @Test(priority = 6, description = "TC_USR_006: Update user with valid data", dependsOnMethods = "testCreateUser")
    @Story("Update User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update an existing user and verify updated fields.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_006")
    public void testUpdateUser() {
        logStep("Generating update data");
        UserRequest updateRequest = UserRequest.builder()
                .name("Updated User Name " + System.currentTimeMillis())
                .email("updated" + System.currentTimeMillis() + "@mail.com")
                .build();
        Allure.addAttachment("Update Request", updateRequest.toString());

        logStep("Sending PUT request to /users/" + createdUserId);
        Response response = UserEndpoints.updateUser(createdUserId, updateRequest);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Parsing response to UserResponse");
        UserResponse updatedUser = response.as(UserResponse.class);

        logStep("Verifying ID remains unchanged");
        Assert.assertEquals(updatedUser.getId(), createdUserId, "User ID should not change");

        logStep("Verifying name is updated");
        Assert.assertEquals(updatedUser.getName(), updateRequest.getName(),
                "User name should be updated");

        createdUserName = updatedUser.getName();
        createdUserEmail = updatedUser.getEmail();

        LoggerUtil.pass("User updated successfully");
    }

    @Test(priority = 7, description = "TC_USR_007: Verify update persisted", dependsOnMethods = "testUpdateUser")
    @Story("Verify User Update Persistence")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the updates on the user are persisted.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_007")
    public void testUpdatePersisted() {
        logStep("Getting user after update");
        UserResponse user = UserEndpoints.getUserByIdAsObject(createdUserId);

        logStep("Verifying updated name is persisted");
        Assert.assertEquals(user.getName(), createdUserName,
                "Updated name should be persisted");

        LoggerUtil.pass("Update persisted successfully");
    }

    @Test(priority = 8, description = "TC_USR_008: Delete user", dependsOnMethods = "testUpdatePersisted")
    @Story("Delete User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Delete an existing user by ID and validate deletion response.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_008")
    public void testDeleteUser() {
        logStep("Sending DELETE request to /users/" + createdUserId);
        Response response = UserEndpoints.deleteUser(createdUserId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 200");
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        logStep("Verifying response contains 'true'");
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("true"), "Response should contain 'true'");

        LoggerUtil.pass("User deleted successfully");
    }

    @Test(priority = 9, description = "TC_USR_009: Verify user removed after delete", dependsOnMethods = "testDeleteUser")
    @Story("Verify User Deletion")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify deleted user cannot be retrieved and returns expected error code.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_009")
    public void testUserRemovedAfterDelete() {
        logStep("Getting user after deletion");
        Response response = UserEndpoints.getUserById(createdUserId);

        logStep("Verifying response status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");

        LoggerUtil.pass("Deleted user correctly returns 400");
    }
}
