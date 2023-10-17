package cc.robotdreams.API;

import automation.app.api.KanboardAPI;
import automation.base.BaseAPITest;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

@Test(priority = 0)
public class ProjectManagementAPITest extends BaseAPITest {
    @Test(groups = {"createProject", "smoke"}, priority = 0)
    public void createProject() {
        //Setup
        String projectName = "Project_" + RandomStringUtils.random(3, false, true);
        Integer ownerID = 1;

        logger.info("Random project name to create: " + projectName);

        //Test
        Response createProjectResult = KanboardAPI.createProject(projectName, requestID, ownerID, true);
        createProjectResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.not(Matchers.equalTo("false")))
                .extract().body().path("result");

        //Check if the new project is in the all projects list
        List<HashMap> allProjects = KanboardAPI.getAllProjects();
        List<String> allProjectNames = new ArrayList<>();

        for (HashMap entry : allProjects) {
            allProjectNames.add(entry.get("name").toString());
        }
        Assert.assertListContainsObject(allProjectNames, projectName, "project name: "
                + projectName);

        //Teardown
        Integer projectID = createProjectResult.then().extract().body().path("result");
        Response removeProjectResult = KanboardAPI.removeProject(projectID, requestID, true);
        removeProjectResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(true));
    }

    @Test(groups = {"createProject"}, priority = 1)
    public void createProjectWithInvalidParams() {
        Response result = KanboardAPI.createProjectWithWrongRequestBody(requestID);
        result
            .then()
                .statusCode(200)
                .body("error.message", Matchers.equalTo("Invalid params"))
                .body("error.data", Matchers.equalTo("Wrong number of arguments"));
    }

    @Test(groups = {"createProject"}, priority = 2)
    public void createProjectWithNoAuth() {
        String projectName = "Project_" + RandomStringUtils.random(3, false, true);
        Integer ownerID = 1;

        logger.info("Project name to check with no auth: " + projectName);

        Response result = KanboardAPI.createProject(projectName, requestID, ownerID, false);
        result
            .then()
                .statusCode(401)
                .body("error.message", Matchers.equalTo("Unauthorized"));
    }

    @Test(groups = "createProject", priority = 3)
    public void createExistingProject() { //Allowed action
        //Setup
        Random rand = new Random();
        List<?> allProjects = KanboardAPI.getAllProjects();
        LinkedHashMap<?,?> randomProject = (LinkedHashMap<?, ?>) allProjects.get(rand.nextInt(allProjects.size()));
        String randomActualProjectName = (String) randomProject.get("name");

        logger.info("Random actual Project name to recreate: " + randomActualProjectName);

        //Test
        Integer ownerID = 1;
        Response creationResult = KanboardAPI.createProject(randomActualProjectName, requestID, ownerID, true);
        creationResult
            .then()
                .statusCode(200)
                .body("result", Matchers.not(Matchers.equalTo("false")));

        //Teardown
        Integer projectID = creationResult.then().extract().body().path("result");
        Response removingUserResult = KanboardAPI.removeProject(projectID, requestID, true);
        removingUserResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(true));
    }

    @Test(groups = "removeProject", priority = 4)
    public void removeValidProject() {
        //Setup
        String projectName = "Project_" + RandomStringUtils.random(3, false, true);
        Integer ownerID = 1;
        Response creationResult = KanboardAPI.createProject(projectName, requestID, ownerID, true);
        Integer projectID = creationResult.then().extract().body().path("result");

        logger.info("Random actual Project name to remove: " + projectName);

        //Test
        Response removingUserResult = KanboardAPI.removeProject(projectID, requestID, true);
        removingUserResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(true));
    }

    @Test(groups = "removeProject", priority = 4)
    public void removeValidProjectWithNoAuth() {
        //Setup
        String projectName = "Project_" + RandomStringUtils.random(3, false, true);
        Integer ownerID = 1;
        Response creationResult = KanboardAPI.createProject(projectName, requestID, ownerID, true);
        Integer projectID = creationResult.then().extract().body().path("result");

        logger.info("Random actual Project name to remove with No Auth: " + projectName);

        //Test
        Response removingUserResult = KanboardAPI.removeProject(projectID, requestID, false);
        removingUserResult
            .then()
                .statusCode(401)
                .body("error.message", Matchers.equalTo("Unauthorized"));

        //Teardown
        Response removingTheSameUserResult = KanboardAPI.removeProject(projectID, requestID, true);
        removingTheSameUserResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(true));
    }
}
