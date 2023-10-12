package automation.app.site;

import automation.base.BaseSelenidePage;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

public class LoginPage extends SiteBasePage
{
    final public SelenideElement username   = Selenide.$x("//*[@id='form-username']");
    final public SelenideElement password   = Selenide.$x("//*[@id='form-password']");
    final public SelenideElement signInBtn  = Selenide.$x("//*[@Class='btn btn-blue']");

    @Override
    protected SelenideElement readyElement() {
        return this.signInBtn;
    }

    public DashboardPage login(String username, String password) {
        this.username.val(username);
        this.password.val(password);
        this.signInBtn.click();

        return new DashboardPage();
    }
}
