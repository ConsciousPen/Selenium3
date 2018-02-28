package aaa.modules.regression.sales.home_ss.ho3.functional;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.home_ss.helper.HelperRevisedHomeTierPA;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test that the Home 'Market Tier' is changed after implementation date
 * @scenario
 * 1. Create PA HO3 Policy
 * 2. Fill All required fields and Calculate Premium
 * 3. View Rating Details
 * 4. Check that Home tier value is between A and J
 * 5. Issue Policy
 * 6. Initiate renewal
 * 7. Calculate Premium
 * 8. Check that Home tier value is between A and J
 * @details
 */


public class TestPaTierChangePropertyInformation extends HomeSSHO3BaseTest {

    private HelperRevisedHomeTierPA helper = new HelperRevisedHomeTierPA();

    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.HIGH }, description = "PA Revised Home Tier - UI Change : Property Information section")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-7025")
    public void pas7025_TestPAPropertyTierChange(@Optional("PA") String state) {

        helper.pas7025_TestPAPropertyTierChange(getPolicyType());

    }
}
