package base.modules.platform;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.base.app.LoginPage;

import aaa.modules.BaseTest;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.utils.TestInfo;
import toolkit.webdriver.BrowserController;

/**
 * @author Rokas Lazdauskas
 * @name Test for login into application
 * @scenario
 * 1. Open Browser
 * 2. Fill username and password
 * 3. Click login
 * 4. Check if user succesfully loged in
 * @details
 */
public class LoginTest extends BaseTest {

   
    @Test(groups = {"7.2_All_UC_LoggingIntoTheEISSuiteApplication"})
    @TestInfo(component = "Platform")
    public void testBrandCreation() {
        BrowserController.initBrowser();
        BrowserController.get().open("http://" + PropertyProvider.getProperty(TestProperties.APP_HOST) + PropertyProvider.getProperty(TestProperties.EU_URL_TEMPLATE));
        BrowserController.get().maximize();
        mainApp().getLogin().login();
        LoginPage.linkLogout.verify.present();
    }
}
