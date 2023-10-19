package automation;

import automation.testbed.BaseTestbed;
import automation.testbed.TestbedGrid;
import automation.testbed.TestbedLocal;
import org.openqa.selenium.WebDriver;

public class Session
{
    static final private ThreadLocal<Session> _instance = new ThreadLocal<>();

    private Session() {
        Runtime.getRuntime().addShutdownHook(new Thread(Session.this::close));
    }

    static public Session get() {
        if (_instance.get() == null)
            _instance.set(new Session());
        return _instance.get();
    }

    private BaseTestbed _testbed;
    private WebDriver _webdriver;

    public WebDriver webdriver() {
        if (this._webdriver == null) {
            if ("local".equalsIgnoreCase(Config.TESTBED.value)) {
                // Create local testbed
                this._testbed = new TestbedLocal();
            } else if ("grid".equalsIgnoreCase(Config.TESTBED.value)) {
                // Create grid testbed
                this._testbed = new TestbedGrid();
            } else
                throw new RuntimeException("Unsupported testbed: " + Config.TESTBED.value);

            this._webdriver = this._testbed.createDriver();
            this._webdriver.manage().window().maximize();
        }

        return this._webdriver;
    }

    public void close() {
        if (this._webdriver != null) {
            this._webdriver.quit();
            this._webdriver = null;
        }
    }
}
