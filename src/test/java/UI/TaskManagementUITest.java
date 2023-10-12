package UI;

import automation.app.api.KanboardAPI;
import automation.app.site.DashboardPage;
import automation.app.site.LoginPage;
import automation.app.site.ProjectBoard;
import automation.app.site.TaskPage;
import automation.base.BaseGUITest;
import automation.utils.Wait;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManagementUITest extends BaseGUITest
{
    //-------------------------------------
    String usernameToCreate = "testuser" + RandomStringUtils.random(4, true, true);
    String passwordToCreate = "qwerty" + RandomStringUtils.random(4, true, true);
    String testPrjName = "test_project" + RandomStringUtils.random(4, true, true);

    //------------------------------------

    private HashMap<String,Integer> testSetup(String username, String password, String testPrjName){
        Integer userID;
        Response createUserResult = KanboardAPI.createUser(username, password, 1, true);
        try {
            userID = createUserResult.then().extract().body().path("result");
        } catch (java.lang.ClassCastException e) {
            userID = null;
            logger.error("Cannot create user. UserID is not Integer");
        }
        logger.debug("UserID: " + userID);

        Response createProjectResult = KanboardAPI.createProject(testPrjName, 1, userID, true);
        Integer projectID = createProjectResult.then().extract().body().path("result");
        logger.debug("ProjectID: " + projectID);

        HashMap<String,Integer> result = new HashMap<>();
        result.put("user_id", userID);
        result.put("project_id", projectID);

        return result;
    }

    private void testTearDown(Integer projectID, Integer userID){
        KanboardAPI.removeProject(projectID, 1,true);
        KanboardAPI.removeUser(userID, 1, true);
    }

    @Test(groups = "createTaskWithUI")
    public void createValidTask() {
        //-------------------------------------
        String taskTitleToAdd = "test_task" + RandomStringUtils.random(3, true, true);
        //------------------------------------

        //Setup
        HashMap<String,Integer> setupResult = testSetup(usernameToCreate, passwordToCreate, testPrjName);
        Integer userID = setupResult.get("user_id");
        Integer projectID = setupResult.get("project_id");

        //Test
        LoginPage loginPage = new LoginPage();

        DashboardPage dashboardPage = loginPage.login(usernameToCreate, passwordToCreate);
        Wait.sleep(500);
        //dashboardPage.confirm(); //TODO:check why confirmation doesn't work

        ProjectBoard projectBoard = dashboardPage.goToProjectBoard(projectID);
        //Wait.sleep(500);
        projectBoard.confirm();

        projectBoard.addTaskToBacklog(taskTitleToAdd);
        //Wait.sleep(500);
        Assert.assertTrue(projectBoard.taskDraggableItem.exists());
        Assert.assertEquals(projectBoard.taskDraggableItemTitle.text(), taskTitleToAdd);

        testTearDown(projectID, userID);
    }

    @Test(groups = "createTaskWithUI", priority = 1)
    public void createInvalidTask() {
        //-------------------------------------
        String taskTitleToAdd = "";
        //------------------------------------

        //Setup
        HashMap<String,Integer> setupResult = testSetup(usernameToCreate, passwordToCreate, testPrjName);
        Integer userID = setupResult.get("user_id");
        Integer projectID = setupResult.get("project_id");

        //Test
        LoginPage loginPage = new LoginPage();

        DashboardPage dashboardPage = loginPage.login(usernameToCreate, passwordToCreate);
        Wait.sleep(500);
        //dashboardPage.confirm(); //TODO:check why confirmation doesn't work
        ProjectBoard projectBoard = dashboardPage.goToProjectBoard(projectID);
        projectBoard.confirm();
        projectBoard.addTaskToBacklog(taskTitleToAdd);

        Assert.assertEquals(projectBoard.errorMsg.text(), "The title is required");

        //Teardown
        testTearDown(projectID, userID);
    }

    @Test(groups = "deleteTaskWithUI", priority = 2)
    public void deleteTask() {
        //-------------------------------------
        String taskTitleToAdd = "test_task" + RandomStringUtils.random(3, true, true);
        //------------------------------------

        //Setup
        HashMap<String,Integer> setupResult = testSetup(usernameToCreate, passwordToCreate, testPrjName);
        Integer userID = setupResult.get("user_id");
        Integer projectID = setupResult.get("project_id");

        //Test
        LoginPage loginPage = new LoginPage();

        DashboardPage dashboardPage = loginPage.login(usernameToCreate, passwordToCreate);
        //dashboardPage.confirm(); //TODO:check why confirmation doesn't work

        ProjectBoard projectBoard = dashboardPage.goToProjectBoard(projectID);
        projectBoard.confirm();
        projectBoard.addTaskToBacklog(taskTitleToAdd);
        projectBoard.openContextMenu();
        projectBoard.closeTask();
        projectBoard.confirmDeletion();

        //Check if the deleted task is available in the list of deactivated tasks list
        List<HashMap> allTasks = KanboardAPI.getAllTasks(projectID, 0, 123);
        List<String> allTasksTitles = new ArrayList<>();
        for (HashMap entry : allTasks) {
            allTasksTitles.add(entry.get("title").toString());
        }
        logger.debug("allTasksTitles: " + allTasksTitles);
        Assert.assertListContainsObject(allTasksTitles, taskTitleToAdd, "Title: " + taskTitleToAdd);

        //Teardown
        testTearDown(projectID, userID);
    }

    @Test(groups = "commentTaskWithUI", priority = 3)
    public void addComment() {
        //-------------------------------------
        String taskTitleToAdd = "test_task" + RandomStringUtils.random(3, true, true);
        String comment = "Some text to comment";
        //------------------------------------

        //Setup
        HashMap<String,Integer> setupResult = testSetup(usernameToCreate, passwordToCreate, testPrjName);
        Integer userID = setupResult.get("user_id");
        Integer projectID = setupResult.get("project_id");

        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login(usernameToCreate, passwordToCreate);
        ProjectBoard projectBoard = dashboardPage.goToProjectBoard(projectID);
        projectBoard.confirm();
        projectBoard.addTaskToBacklog(taskTitleToAdd);
        Integer taskID = KanboardAPI.getTaskIDByName(taskTitleToAdd, projectID);

        //Test
        projectBoard.openContextMenu();
        projectBoard.addComment(comment);
        TaskPage taskPage = projectBoard.goToTaskPage(taskID);
        Wait.sleep(500);
        taskPage.confirm();

        Assert.assertEquals(comment, taskPage.getComment());

        //Teardown
        testTearDown(projectID, userID);
    }

    @Test(groups = "commentTaskWithUI", priority = 4)
    public void addEmptyComment() {
        //-------------------------------------
        String taskTitleToAdd = "test_task" + RandomStringUtils.random(3, true, true);
        String comment = "";
        //------------------------------------

        //Setup
        HashMap<String,Integer> setupResult = testSetup(usernameToCreate, passwordToCreate, testPrjName);
        Integer userID = setupResult.get("user_id");
        Integer projectID = setupResult.get("project_id");

        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login(usernameToCreate, passwordToCreate);
        ProjectBoard projectBoard = dashboardPage.goToProjectBoard(projectID);
        projectBoard.confirm();
        projectBoard.addTaskToBacklog(taskTitleToAdd);

        //Test
        projectBoard.openContextMenu();
        projectBoard.addCommentItem.click();
        projectBoard.modalSaveBtn.click();

        Assert.assertEquals(projectBoard.errorMsg.text(), "Comment is required");

        //Teardown
        testTearDown(projectID, userID);
    }
}

