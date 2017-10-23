package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.junit.Before;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Tyrone Jemison
 * @modified N/A
 * @name
 * @scenario
 * Validate whether system does not update or refresh the existing open CLUE claims when CLUE claims report is unsuccessful.
 * 1. Determine Precondition is met.
 * 2. Automated renewal batch jobs are run for ordering Open CLUE claims.
 * 3. System validates Existing Activity 1 w/ Activity Source as 'CLUE' and Status as 'Open'- reassigned
 * from another driver.
 * 4. System validates Existing Activity 2 w/ Activity Source as 'CLUE' and Status as 'Open'. Override Activity = 'Yes'
 * 5. System validates Existing Activity 3 w/ Activity Source as 'CLUE' and Status as 'Open'- not reassigned and override = 'No'
 */
public class TestClueClaimNotUpdate extends AutoSSBaseTest
{
    TestData _td;

    @BeforeClass
    public void scenarioSetup()
    {
        _td = getPolicyTD("DataGather", "TestData");
    }

    /**
     * @author Tyrone Jemison
     * @param state
     * @scenario
     * Preconditions:
     * 1. Policy w/ 2+ drivers exist.
     * 2. Policy is in the renewal order window.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void isScenarioPreconditionMet(@Optional("") String state) {
        //
        //
        //
        mainApp().open();
        createCustomerIndividual();

        //Begin creating a policy.
        policy.initiate();
        policy.getDefaultView().fillUpTo(_td, DriverTab.class);


        //Prefill Tab
        //General Tab
        //Driver Tab
        //Rating Tab
        //Vehicle Tab
        //Forms Tab
        // P&C Tab
        // Driver Activity Report Tab
        // Documents and Bind Tab
    }

    /**
     * @author Tyrone Jemison
     * @param state
     */
//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL})
//    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
//    public void runRenewalBatch(@Optional("") String state) {
//        // Automated renewal batch jobs are run for ordering Open CLUE claims.
//    }

    /**
     * @author Tyrone Jemison
     * @param state
     */
//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL})
//    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
//    public void systemValidateExistingActivity(@Optional("") String state) {
        // Automated renewal batch jobs are run for ordering Open CLUE claims.
//    }
}
