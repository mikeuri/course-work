package automation.base;

import automation.Config;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeMethod;

public class BaseAPITest extends BaseTestNG
{
    public Integer requestID = 1;
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        RestAssured.baseURI = String.format("%s://%s",
                Config.HTTP_BASE_PROTO.value,
                Config.HTTP_BASE_HOST.value
        );
        RestAssured.port = Integer.parseInt(Config.HTTP_BASE_PORT.value);
    }
}
