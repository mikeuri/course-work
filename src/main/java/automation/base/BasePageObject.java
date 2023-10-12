package automation.base;

import org.openqa.selenium.support.PageFactory;

public class BasePageObject extends BasePage
{
    public BasePageObject() {
        PageFactory.initElements(this.wd(), this);
    }

}
