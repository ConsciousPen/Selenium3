package aaa.modules.regression.sales.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestClueReportForCopiedPolicy extends AutoCaSelectBaseTest {

    /**
     * @author Josh Carpenter
     * @name Test that CLUE reports do not carry over on 'Copy Policy' action for CA Select Auto policies
     * @scenario
     * 1. Create Customer
     * 2. Create Auto CA Select Policy
     * 3. Create endorsement to add a driver
     * 4. Copy from policy
     * 5. Order reports on Rating Details Report tab
     * 6. Calculate premium
     * 7. Order reports on DAR page
     * 8. Verify CLUE table contains a single row with 'HouseHold' for 'Order Type'
     * 9. Verify CLUE table 'Select' radio button defaults to 'Yes'
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-8271")
    public void pas8271_testClueReportOnCopyPolicyActionCA(@Optional("") String state) {

        mainApp().open();
        createCustomerIndividual();
        createPolicy();


    }
}
