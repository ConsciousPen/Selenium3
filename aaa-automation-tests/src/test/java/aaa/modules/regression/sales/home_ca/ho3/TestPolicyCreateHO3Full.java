package aaa.modules.regression.sales.home_ca.ho3;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;

public class TestPolicyCreateHO3Full extends HomeCaHO3BaseTest {

    /**
     * @author Jurij Kuznecov
     * @name Test Create CAH Policy HO3-Full
     * @scenario
     * 1. Create new or open existent Customer;
     * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO3;
     * 3. Fill all mandatory fields;
     * 4. Add Endorsement form
     * 5. Calculate premium;
     * 6. Issue policy;
     * 7. Check Policy status is Active.
     */
    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void testPolicyCreation(@Optional("CA") String state) {

        mainApp().open();
        
        createCustomerIndividual();
        createPolicy(getTestSpecificTD("TestData").adjust(getTestSpecificTD("TestData_AddForm_HO75")).resolveLinks());
        
        assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
