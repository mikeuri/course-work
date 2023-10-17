package automation.testbed;

import automation.Config;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class TestbedGrid extends BaseTestbed
{
    @Override
    public WebDriver createDriver() {
        String gridHost;
        String gridPort;
        DesiredCapabilities caps = new DesiredCapabilities();
        if ("chrome".equalsIgnoreCase(Config.WEB_BROWSER.value)) {
            gridHost = Config.SELENIUM_GRID_CHROME_HOST.value;
            gridPort = Config.SELENIUM_GRID_CHROME_PORT.value;
            caps.setCapability(ChromeOptions.CAPABILITY, this.getCommonChromeOptions());
            caps.setCapability(CapabilityType.BROWSER_NAME, Browser.CHROME.browserName());
            caps.setCapability(CapabilityType.TIMEOUTS, 30);
            caps.setCapability("se:vncEnabled", true);
            caps.setCapability(CapabilityType.BROWSER_VERSION, "117.0");
            caps.setCapability(CapabilityType.PLATFORM_NAME, Platform.LINUX);
            /*
            {"browserName":"chrome","browserVersion":"117.0","platformName":"linux","se:noVncPort":7900,"se:vncEnabled":true}

             */
        } else if ("firefox".equalsIgnoreCase(Config.WEB_BROWSER.value)) {
            gridHost = Config.SELENIUM_GRID_FIREFOX_HOST.value;
            gridPort = Config.SELENIUM_GRID_FIREFOX_PORT.value;
            caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, this.getCommonFirefoxOptions());
            caps.setCapability(CapabilityType.BROWSER_NAME, Browser.FIREFOX.browserName());
        } else
            throw new RuntimeException("Unsupported browser: " + Config.WEB_BROWSER.value);

        String gridUrl = "http://" + gridHost + ":" + gridPort + "/wd/hub";
        System.out.println("gridUrl: " + gridUrl);
        try {
            URL url = new URL(gridUrl);
            return new RemoteWebDriver(url, caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL is not valid: " + gridUrl, e);
        }
    }
}
