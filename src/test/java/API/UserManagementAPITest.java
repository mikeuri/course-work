package API;

import automation.app.api.KanboardAPI;
import automation.base.BaseAPITest;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

@Test(priority = 2)
public class UserManagementAPITest extends BaseAPITest
{
    @Test(groups = "createUser", priority = 0)
    public void createValidUser() {
        String username = RandomStringUtils.random(10, true, true);
        String password = "123456";

        logger.info("Random username to create: " + username);

        Response creationResult = KanboardAPI.createUser(username, password, requestID, true);
        creationResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.not(Matchers.equalTo("false")))
                .extract().body().path("result");

        //Check if the new user is in the all users list
        List<HashMap> allUsers = KanboardAPI.getAllUsers();
        List<String> allUsernames = new ArrayList<>();

        for (HashMap entry : allUsers) {
            allUsernames.add(entry.get("username").toString());
        }
        Assert.assertListContainsObject(allUsernames, username, "Username: " + username);

        //Teardown
        Integer userID = creationResult.then().extract().body().path("result");
        Response removingUserResult = KanboardAPI.removeUser(userID, requestID, true);
        removingUserResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(true));
    }

    @Test(groups = "createUser", priority = 1)
    public void createUserWithInvalidParams() {
        String username = "biloute1063";

        Response result = KanboardAPI.createUserWithNoPassword(username, requestID);
        result
            .then()
                .statusCode(200)
                .body("error.message", Matchers.equalTo("Invalid params"))
                .body("error.data", Matchers.equalTo("Wrong number of arguments"));
    }

    @Test(groups = "createUser", priority = 2)
    public void createUserWithNoAuth() {
        String username = RandomStringUtils.random(10, true, true);
        String password = "123456";

        logger.info("Random username to check with no auth: " + username);

        Response result = KanboardAPI.createUser(username, password, requestID, false);
        result
            .then()
                .statusCode(401)
                .body("error.message", Matchers.equalTo("Unauthorized"));
    }

    @Test(groups = "createUser", priority = 3)
    public void createExistingUser() { //Action is not allowed
        Random rand = new Random();

        List<?> allUsers = KanboardAPI.getAllUsers();
        LinkedHashMap<?,?> randomUser = (LinkedHashMap<?, ?>) allUsers.get(rand.nextInt(allUsers.size()));
        String randomActualUsername = (String) randomUser.get("username");

        logger.info("Random actual username to recreate: " + randomActualUsername);

        String password = "$%*6761274";
        Response result = KanboardAPI.createUser(randomActualUsername, password, requestID, true);
        result
            .then()
                .statusCode(200)
                .body("result", Matchers.equalTo(false));
    }

    @Test(groups = "removeUser", priority = 4)
    public void removeValidUser(){
        String username = RandomStringUtils.random(10, true, true);
        Response creationResult = KanboardAPI.createUser(username, "password", requestID, true);
        Integer userID = creationResult.then().extract().body().path("result");

        Response result = KanboardAPI.removeUser(userID, requestID, true);
        result
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(true));

        Response removingTheSameUserResult = KanboardAPI.removeUser(userID, requestID, true);
        removingTheSameUserResult
                .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(false));
    }

    @Test(groups = "removeUser", priority = 5)
    public void removeValidUserWithNoAuth(){
        String username = RandomStringUtils.random(10, true, true);
        Response creationResult = KanboardAPI.createUser(username, "password", requestID, true);
        Integer userID = creationResult.then().extract().body().path("result");

        Response result = KanboardAPI.removeUser(userID, requestID, false);
        result
            .then()
                .statusCode(401)
                .body("error.message", Matchers.equalTo("Unauthorized"));

        //Tear down
        Response removingTheSameUserResult = KanboardAPI.removeUser(userID, requestID, true);
        removingTheSameUserResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(true));
    }
}

//TODO: Add groups for tests
//TODO: Allure reporting
//TODO: Selenide
//TODO: (optional) Cucumber
//TODO: UI тести повинні запускатися для Chrome headless browser, Chrome browser, Firefox
//TODO: Remove commented code
//TODO: Clean up properties and Config file
//TODO: Jenkins
//TODO: Remove TO_DOs