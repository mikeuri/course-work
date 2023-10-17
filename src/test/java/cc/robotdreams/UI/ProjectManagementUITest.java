package cc.robotdreams.UI;

import automation.app.api.KanboardAPI;
import automation.app.site.DashboardPage;
import automation.app.site.LoginPage;
import automation.app.site.ProjectConfigurationPage;
import automation.base.BaseGUITest;
import automation.utils.Wait;
import com.codeborne.selenide.WebDriverRunner;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProjectManagementUITest extends BaseGUITest
{
    @Test(groups = {"createProjectWithUI", "smoke"})
    public void createValidProject() {
        LoginPage loginPage = new LoginPage();

        DashboardPage dashboardPage = loginPage.login(username, password);
        Wait.sleep(500);
        dashboardPage.confirm();

        String testPrjName = "test_project_102938!@#$%&^%";
        ProjectConfigurationPage prjConfigPage = dashboardPage.createNewProject(testPrjName);
        Wait.sleep(500);
        prjConfigPage.confirm();

        Assert.assertEquals(prjConfigPage.projectTitle.text(), testPrjName);

        //Teardown
        String[] urlParts = WebDriverRunner.getWebDriver().getCurrentUrl().split("/");
        Integer projectID = Integer.parseInt(urlParts[urlParts.length-1]);

        logger.debug("Project ID: " + projectID);

        KanboardAPI.removeProject(projectID, 1, true);
        //Wait.sleep(2000);
    }

    @Test(groups = {"createProjectWithUI"})
    public void emptyProjectName() {
        LoginPage loginPage = new LoginPage();

        DashboardPage dashboardPage = loginPage.login(username, password);
        Wait.sleep(500);
        dashboardPage.confirm();

        dashboardPage.newProjectBtn.click();
        dashboardPage.newProjectSaveBtn.click();

        Assert.assertTrue(dashboardPage.projectNameErrorMsg.exists());
        Assert.assertEquals(dashboardPage.projectNameErrorMsg.text(), "The project name is required");
        //Wait.sleep(2000);
    }
}
