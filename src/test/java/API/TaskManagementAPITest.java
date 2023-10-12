package API;

import automation.app.api.KanboardAPI;
import automation.base.BaseAPITest;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Test(priority = 1)
public class TaskManagementAPITest extends BaseAPITest
{
    @Test(groups = "createTask")
    public void createTask() {
        //Setup
        String projectName = "Project_" + RandomStringUtils.random(3, false, true);
        Integer ownerID = 1;
        Response createProjectResult = KanboardAPI.createProject(projectName, requestID, ownerID, true);
        Integer projectID = createProjectResult.then().extract().body().path("result");
        logger.info("Random actual Project name and ID: " + projectName + " - " + projectID);

        //Test
        String title = "Test_task_" + RandomStringUtils.random(3, false, true);
        Response createTaskResult = KanboardAPI.createTask(title, projectID, ownerID, requestID,
                true, false);
        createTaskResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.not(Matchers.equalTo("false")));

        //Check if the new task is in the all tasks list
        List<HashMap> allTasks = KanboardAPI.getAllTasks(projectID, 1, requestID);
        List<String> allTasksTitles = new ArrayList<>();
        for (HashMap entry : allTasks) {
            allTasksTitles.add(entry.get("title").toString());
        }
        logger.debug("allTasksTitles: " + allTasksTitles);

        Assert.assertListContainsObject(allTasksTitles, title, "Title: " + title);

        //Teardown
        KanboardAPI.removeProject(projectID, requestID, true);
    }

    @Test(groups = "createTask", priority = 1)
    public void createTaskForInvalidProject() {
        String title = "Test_task";
        Integer ownerID = 1;
        Integer projectID = 27;

        Response createTaskResult = KanboardAPI.createTask(title, projectID, ownerID, requestID,
                true, true);
        createTaskResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(false));
    }

    @Test(groups = "createTask", priority = 2)
    public void createTaskWithNoAuth() {
        //Setup
        String projectName = "Project_" + RandomStringUtils.random(3, false, true);
        Integer ownerID = 1;
        Response createProjectResult = KanboardAPI.createProject(projectName, requestID, ownerID, true);
        Integer projectID = createProjectResult.then().extract().body().path("result");
        logger.info("Random actual Project name and ID: " + projectName + " - " + projectID);

        //Test
        String title = "some_task";
        Response result = KanboardAPI.createTask(title, projectID, ownerID, requestID, false, false);
        result
            .then()
                .statusCode(401)
                .body("error.message", Matchers.equalTo("Unauthorized"));

        //Teardown
        KanboardAPI.removeProject(projectID, requestID, true);
    }

    @Test(groups = "removeTask", priority = 3)
    public void removeTask() {
        //Setup
        String projectName = "Project_" + RandomStringUtils.random(3, false, true);
        Integer ownerID = 1;
        Response createProjectResult = KanboardAPI.createProject(projectName, requestID, ownerID, true);
        Integer projectID = createProjectResult.then().extract().body().path("result");
        logger.info("Random actual Project name and ID: " + projectName + " - " + projectID);

        String title = "Test_task_" + RandomStringUtils.random(3, false, true);
        Response createTaskResult = KanboardAPI.createTask(title, projectID, ownerID, requestID,
                true, false);
        Integer taskID = createTaskResult.then().extract().body().path("result");

        //Test
        Response removeTaskResult = KanboardAPI.removeTask(requestID, taskID, true);
        removeTaskResult
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(requestID))
                .body("result", Matchers.equalTo(true));

        //Teardown
        KanboardAPI.removeProject(projectID, requestID, true);
    }

    @Test(groups = "removeTask", priority = 4)
    public void removeTaskWithNoAuth() {
        //Setup
        String projectName = "Project_" + RandomStringUtils.random(3, false, true);
        Integer ownerID = 1;
        Response createProjectResult = KanboardAPI.createProject(projectName, requestID, ownerID, true);
        Integer projectID = createProjectResult.then().extract().body().path("result");
        logger.info("Random actual Project name and ID: " + projectName + " - " + projectID);

        String title = "Test_task_" + RandomStringUtils.random(3, false, true);
        Response createTaskResult = KanboardAPI.createTask(title, projectID, ownerID, requestID,
                true, false);
        Integer taskID = createTaskResult.then().extract().body().path("result");

        //Test
        Response removeTaskResult = KanboardAPI.removeTask(requestID, taskID, false);
        removeTaskResult
            .then()
                .statusCode(401)
                .body("error.message", Matchers.equalTo("Unauthorized"));

        //Teardown
        KanboardAPI.removeProject(projectID, requestID, true);
    }

    @Test(groups = "removeTask", priority = 5)
    public void removeNonexistentTask() {
        //Setup
        String projectName = "Project_" + RandomStringUtils.random(3, false, true);
        Integer ownerID = 1;

        Integer projectID = KanboardAPI.createProject(projectName, requestID, ownerID, true)
                .then().extract().body().path("result");
        logger.info("Random actual Project name and ID: " + projectName + " - " + projectID);

        //Test
        Integer nonexistentTaskID = 10000;
        Response removeTaskResult = KanboardAPI.removeTask(requestID, nonexistentTaskID, true);
        removeTaskResult
            .then()
                .statusCode(403)
                .body("error.message", Matchers.equalTo("Forbidden"));

        //Teardown
        KanboardAPI.removeProject(projectID, requestID, true);

    }


}
