package automation.base;

import automation.Config;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class BaseTestNG
{
    final protected Logger logger = LogManager.getLogger(this.getClass());

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method, Object[] testArgs) {
        /*logger.debug("---------------------------------------------------------------------------");
        logger.debug("-- Run test: " + method.getAnnotation(Test.class).testName());
        logger.debug("---------------------------------------------------------------------------");
        int i = 1;
        for (Object obj : testArgs) {
            logger.debug("Argument " + i ++ + ": " + obj);
        }*/
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuiteTestNG() {
        String envFilePath = Config.ALLURE_RESULTS.value + "/environment.properties";
        try {
            Properties props = new Properties();
            FileOutputStream fos = new FileOutputStream(envFilePath);
            for (Config.Param param : Config.list()) {
                props.put(param.name, param.value);
            }
            // props.putAll(System.getenv());
            props.store(fos, "Config properties");
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write resource properties file " + envFilePath);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(Method method) {
        /*logger.debug("---------------------------------------------------------------------------");
        logger.debug("-- End test: " + method.getAnnotation(Test.class).testName());
        logger.debug("---------------------------------------------------------------------------");*/
        this.attachResourceFile("env/" + Config.getEnvironmentName() + ".properties");
    }

    @Attachment("Resource file: {resourceFilePath}")
    private byte[] attachResourceFile(String resourceFilePath) {
        try {
            URI    uri      = ClassLoader.getSystemResource(resourceFilePath).toURI();
            Path   path     = Paths.get(uri);
            String content  = Files.readString(path);
            //System.out.println(content);
            return content.getBytes();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
