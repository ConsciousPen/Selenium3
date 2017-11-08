package aaa.helpers.team.cio;

import aaa.helpers.QAHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.*;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class QA_FillQuoteUntilTab extends AutoSSBaseTest
{// Must create a helper class object. Cannot be static as it uses non-static methods and objects.
    QAHelper _myHelper = new QAHelper();

    @Parameters({"state"})
    @BeforeClass
    public void testSetup(@Optional("") String state)
    {
        // Launching App
        _myHelper.launchApp();
    }

    @Parameters({"state"})
    @Test(groups= {Groups.SMOKE})
    @TestInfo(component= ComponentConstant.Sales.AUTO_CA_SELECT)
    public void createPreconditions(@Optional("") String state)
    {
        // Create and Define Test Data To Use in Run
        TestData _myTD = getTestSpecificTD("TestData");

        // Create Customer with helper class obj.
        createCustomerIndividual(_myTD);

        // Create a quote.
        policy.initiate();
        policy.getDefaultView().fillUpTo(_myTD, PremiumAndCoveragesTab.class, true);
    }

    @Parameters({"state"})
    @AfterClass
    public void testClose(@Optional("") String state)
    {
        //_myHelper.closeApp();
    }
}
