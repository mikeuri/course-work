package automation.base;

import automation.Config;
import automation.Session;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Attachment;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseGUITest extends BaseTestNG
{
    public String username = Config.API_AUTH_USER_USERNAME.value;
    public String password = Config.API_AUTH_USER_PASSWORD.value;

    @BeforeMethod(alwaysRun = true)
    public void beforeGUIMethod() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false)
                .includeSelenideSteps(true)
        );

        this.wd().get(String.format("%s://%s:%s",
                Config.HTTP_BASE_PROTO.value,
                Config.HTTP_BASE_HOST.value,
                Config.HTTP_BASE_PORT.value
        ));
        WebDriverRunner.setWebDriver(this.wd());

        RestAssured.baseURI = String.format("%s://%s",
                Config.HTTP_BASE_PROTO.value,
                Config.HTTP_BASE_HOST.value
        );
        RestAssured.port = Integer.parseInt(Config.HTTP_BASE_PORT.value);

    }

    @AfterMethod(alwaysRun = true)
    public void afterGUIMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE || result.getStatus() == ITestResult.CREATED) {
            this.takeScreenshot();
        }
        Session.get().close();
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] takeScreenshot(){
        return ((TakesScreenshot) this.wd()).getScreenshotAs(OutputType.BYTES);
    }

    protected WebDriver wd() {
        return Session.get().webdriver();
    }
}
