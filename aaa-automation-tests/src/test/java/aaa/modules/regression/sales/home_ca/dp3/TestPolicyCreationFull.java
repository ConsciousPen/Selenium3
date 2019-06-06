package aaa.modules.regression.sales.home_ca.dp3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * <p> Created by lkazarnovskiy on 8/7/2017.
 * <b> TestPolicyCreation CA DP3 Full </b>
 * <p> Steps::
 * <p> 1. Create new or open existent Customer;
 * <p> 2. Initiate CA DP3 quote creation, set effective date to today, set Policy Form=HO6;
 * <p> 3. Fill all mandatory fields;
 * <p> 4. Add Endorsement form
 * <p> 5. Calculate premium;
 * <p> 6. Issue policy;
 * <p> 7. Check Policy status is Active.
 */
public class TestPolicyCreationFull extends HomeCaDP3BaseTest {

    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void testQuoteCreation(@Optional("CA") String state) {
        mainApp().open();

        createCustomerIndividual();
        createPolicy(getTestSpecificTD("TestDataFull"));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: CA DP3 Full is passed; Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
    }
}
