package automation.utils;

import automation.Session;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Instant;

public class Wait
{
    static public void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (Throwable e) { /* Ignore */ }
    }

    static public void waitWhilePageIsLoading() {
        long timeout = Instant.now().getEpochSecond() + 30;
        boolean condition = false;
        while (!condition && timeout >= Instant.now().getEpochSecond()) {
            condition = (Boolean) ((JavascriptExecutor) Session.get().webdriver())
                    .executeScript("return document.readyState=='complete'");
        }
        if (!condition)
            throw new RuntimeException("Page loading takes a lot of time");
    }
}
