package automation.app.site;

import automation.Config;
import automation.base.BaseSelenidePage;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

public class DashboardPage  extends BaseSelenidePage
{
    final public SelenideElement tabOverview = Selenide.$x("//*[@id='dashboard']//a[@href='/dashboard/1']");
    final public SelenideElement newProjectBtn = Selenide.$x("//*[@id='main']//a[@href='/project/create']");
    final public SelenideElement newProjectInputName = Selenide.$x("//*[@id='form-name']");
    final public SelenideElement newProjectSaveBtn = Selenide.$x("//*[@class='btn btn-blue']");
    final public SelenideElement projectNameErrorMsg = Selenide.$x("//*[@class='form-errors']");

    @Override
    protected SelenideElement readyElement() {
        return tabOverview;
    }

    public ProjectConfigurationPage createNewProject(String projectName) {
        newProjectBtn.click();
        this.newProjectInputName.val(projectName);
        newProjectSaveBtn.click();

        return new ProjectConfigurationPage();
    }

    public ProjectBoard goToProjectBoard(Integer projectID) {
        wd().get(String.format("%s://%s/board/%s",
                Config.HTTP_BASE_PROTO.value,
                Config.HTTP_BASE_HOST.value,
                projectID));

        return new ProjectBoard();
    }
}
