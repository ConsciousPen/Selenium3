package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.*;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Tyrone Jemison
 * @name Validate Membership Error Message
 * @scenario
 * 1. GIVEN an agent binding a transaction of NB, Endorsement, Manual Renewal...
 * 2. GIVEN member number is 'Active'.
 * 3. WHEN there is NO match in FIRST or LAST or DOB between RMS and Member/Driver on Quote/Policy.
 * 4. THEN UW Eligibility rules fire and display Error Message:
 * 'Membership Validation Failed. Please review the Membership Report and confirm Member details. Rule can be overridden with Level 4 Authority'
 */
public class ValidateMembershipErrorMsg extends HomeSSHO3BaseTest
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
        TestData _tdCustomer = getTestSpecificTD("TestData_Customer");
        _customerNumber = createCustomerIndividual(_tdCustomer);
        log.info(">Completed Test TC01_createCustomer()<");
        log.info("Customer Number = " + _customerNumber);
    }

    @Parameters({"state"})
    @Test
    @TestInfo(component= ComponentConstant.Sales.AUTO_SS)
    public void TC02_initiatePolicy(@Optional("") String state)
    {
        log.info(">Running Test TC02_initiatePolicy()...<");
        TestData _td = getTestSpecificTD("TestData");
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
        TestData _td = getTestSpecificTD("TestData");
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
