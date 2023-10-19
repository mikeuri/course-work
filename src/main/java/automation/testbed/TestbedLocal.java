package automation.testbed;

import automation.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestbedLocal extends BaseTestbed
{
    @Override
    public WebDriver createDriver() {
        WebDriver driver;
        if ("chrome".equalsIgnoreCase(Config.WEB_BROWSER.value)) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(this.getCommonChromeOptions());
        } else if ("firefox".equalsIgnoreCase(Config.WEB_BROWSER.value)) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver(this.getCommonFirefoxOptions());
        } else {
            throw new RuntimeException("Unsupported browser: " + Config.WEB_BROWSER.value);
        }
        return driver;
    }
}
