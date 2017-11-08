package aaa.helpers.team.cio;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.helpers.QAHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.*;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class QA_CreatePolicyE2E extends AutoSSBaseTest
{
    // Must create a helper class object. Cannot be static as it uses non-static methods and objects.
    QAHelper _myHelper = new QAHelper();

    @Parameters({"state"})
    @BeforeClass
    public void testSetup(@Optional("") String state)
    {
        // Launching App
        mainApp().open();
    }

    @Parameters({"state"})
    @Test(groups= {Groups.SMOKE})
    @TestInfo(component= ComponentConstant.Sales.AUTO_SS)
    public void createPolicyAsPrecondition(@Optional("") String state)
    {
        log.info("Beginning createPolicyAsPrecondition()...");
        log.info("Beginning gathering test data...");
        // Create and Define Test Data To Use in Run
        TestData _myTD = getTestSpecificTD("TestData2");

        // Create Customer with helper class obj.
        log.info("Beginning Customer Creation...");
        createCustomerIndividual(_myTD);

        //Create Policy
        log.info("Beginning policy creation...");
        policy.initiate();
        policy.getDefaultView().fillUpTo(_myTD, DocumentsAndBindTab.class, true);

        // Navigates us back to the Driver Tab.
        //NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

    }

    @Parameters({"state"})
    @AfterClass
    public void testClose(@Optional("") String state)
    {
        //_myHelper.closeApp();
    }
}
