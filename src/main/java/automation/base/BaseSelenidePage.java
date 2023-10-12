package automation.base;

import automation.utils.Wait;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.testng.Assert;


import java.time.Instant;

abstract public class BaseSelenidePage extends BasePage
{

    public boolean isPageLoaded() {
        return this.isPageLoaded(5);
    }

    public boolean isPageLoaded(int timeoutSec) {
        Boolean customConfirm = this.customConfirm();
        if (customConfirm != null)
            return customConfirm;

        boolean result = false;

        long timeout = Instant.now().getEpochSecond() + timeoutSec;
        while (timeout > Instant.now().getEpochSecond()) {
            result = this.readyElement().exists();
            if (result)
                break;
            Wait.sleep(1000);
        }

        return result;
    }

    public void confirm() {
        // Confirm that page is loaded
        if (!this.isPageLoaded(10))
            throw new RuntimeException("Could not confirm that page is loaded: " + this.getClass().getSimpleName());
    }

    public void confirmByUrl(String urlToCheck) {

        String currentUrl = WebDriverRunner.getWebDriver().getCurrentUrl();
        if (!currentUrl.contains(urlToCheck)) {
            throw new RuntimeException("Could not confirm that page is loaded: " + this.getClass().getSimpleName());
        }
    }

    protected Boolean customConfirm() {
        return null;
    }

    abstract protected SelenideElement readyElement();


}
