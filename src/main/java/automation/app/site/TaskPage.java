package automation.app.site;

import automation.base.BaseSelenidePage;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

public class TaskPage extends BaseSelenidePage
{
    final public SelenideElement taskSummarySection = Selenide.$x("//*[@id='task-summary']");
    final public SelenideElement taskComment = Selenide.$x("//*[@class='comment ']//*[@class='markdown']");


    @Override
    protected SelenideElement readyElement() {
        return taskSummarySection;
    }

    public String getComment() {
        return taskComment.text();
    }
}
