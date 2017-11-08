package aaa.helpers.team.cio;

import aaa.helpers.QAHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.*;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class QA_EndorsePolicy extends AutoSSBaseTest {
    // Must create a helper class object. Cannot be static as it uses non-static methods and objects.
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
        // Input policy to endorse below.
        String policyNumber = "";
        _myHelper.getCreatedPolicy(policyNumber);

        TestData _myEndoTD = getTestSpecificTD("EndorsementTestData_" + state);
        policy.createEndorsement(_myEndoTD.adjust(getPolicyTD("Endorsement", "TestData")));
    }

    @Parameters({"state"})
    @AfterClass
    public void testClose(@Optional("") String state)
    {
        //_myHelper.closeApp();
    }
}
