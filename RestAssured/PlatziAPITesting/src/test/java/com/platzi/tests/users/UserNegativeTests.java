package com.platzi.tests.users;

import com.platzi.endpoints.UserEndpoints;
import com.platzi.pojo.request.UserRequest;
import com.platzi.tests.base.BaseTest;
import com.platzi.utils.LoggerUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Platzi API Testing")
@Feature("User Negative Scenarios")
public class UserNegativeTests extends BaseTest {

    @Test(priority = 1, description = "TC_USR_NEG_001: Create user with invalid email format")
    @Story("Create User with Invalid Email")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a user with an invalid email format returns a 400 status code.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_NEG_001")
    public void testCreateUserWithInvalidEmail() {
        String invalidEmail = "invalid-email-format";
        Allure.parameter("Invalid Email", invalidEmail);

        logStep("Creating user with invalid email format");
        UserRequest userRequest = UserRequest.builder()
                .name("Test User")
                .email(invalidEmail)
                .password("testpass123")
                .build();

        logStep("Sending POST request");
        Response response = UserEndpoints.createUser(userRequest);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");

        LoggerUtil.pass("Invalid email format correctly returns 400");
    }

    @Test(priority = 2, description = "TC_USR_NEG_002: Get user with invalid ID")
    @Story("Get User with Invalid ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that fetching a user with an invalid ID returns a 400 status code.")
    @Owner("Hesham Qutb")
    @TmsLink("TC_USR_NEG_002")
    public void testGetUserWithInvalidId() {
        int invalidId = 999999;
        Allure.parameter("Invalid User ID", invalidId);

        logStep("Sending GET request with invalid ID: " + invalidId);
        Response response = UserEndpoints.getUserById(invalidId);
        Allure.addAttachment("Response Body", response.getBody().asString());

        logStep("Verifying response status code is 400");
        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");

        LoggerUtil.pass("Invalid ID correctly returns 400");
    }
}
