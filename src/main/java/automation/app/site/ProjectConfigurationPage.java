package automation.app.site;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

public class ProjectConfigurationPage extends SiteBasePage
{
    final public SelenideElement sideBarSummaryActive =
            Selenide.$x("//*[@class='active']//a[text()='Summary']");
    final public SelenideElement projectTitle = Selenide.$x("//*[@class='title']");

    @Override
    protected SelenideElement readyElement() {
        return sideBarSummaryActive;
    }
}
