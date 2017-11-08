package aaa.modules.functionalTests;

import aaa.helpers.QAHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.*;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class Membership_NBValidation extends AutoSSBaseTest
{
    // Scenario-Level variables to carry data throughout test.
    String _customerNumber;
    PolicyType _policyType = getPolicyType();
    Boolean _bKillAppAfterRun = false;

    @Parameters({"state"})
    @BeforeClass
    public void testSetup(@Optional("") String state)
    {
        log.info(">Running @BeforeClass testSetup()<");
        log.info(">Launching PAS Application...<");
        // Launching App
        mainApp().open();
        log.info(">Launched PAS App<");
    }

    @Parameters({"state"})
    @Test
    @TestInfo(component= ComponentConstant.Sales.AUTO_SS)
    public void TC01_createCustomer(@Optional("") String state)
    {
        log.info(">Running Test TC01_createCustomer()...<");
        // Get Test Data and create customer.
        TestData _td = getTestSpecificTD("TestDataBlock");
        _customerNumber = createCustomerIndividual(_td);
        log.info(">Completed Test TC01_createCustomer()<");
    }

    @Parameters({"state"})
    @Test
    @TestInfo(component= ComponentConstant.Sales.AUTO_SS)
    public void TC02_initiatePolicy(@Optional("") String state)
    {
        log.info(">Running Test TC02_initiatePolicy()...<");
        TestData _td = getTestSpecificTD("TestDataBlock");
        _policyType.get().initiate();
        _policyType.get().getDefaultView().fillUpTo(_td, PurchaseTab.class, true);
        log.info(">Completed Test TC02_initiatePolicy()<");
    }

    @Parameters({"state"})
    @Test
    @TestInfo(component= ComponentConstant.Sales.AUTO_SS)
    public void TC03_verifyMembershipErrorMsg(@Optional("") String state)
    {
        log.info(">Running Test TC03_verifyMembershipErrorMsg()...<");
        TestData _td = getTestSpecificTD("TestDataBlock");
        // Call Custom Method to Verify Presence of Error Message.
        log.info(">Completed Test TC03_verifyMembershipErrorMsg()<");
    }

    @Parameters({"state"})
    @AfterClass
    public void testClose(@Optional("") String state)
    {
        log.info(">Running @AfterClass testClose()<");
        if(_bKillAppAfterRun) // Control at top of code- in scenario level variables.
        {
            log.info(">Closing Main App<");
            mainApp().close();
        }
        log.info(">Completed @AfterClass testClose()<");
    }

}
