package aaa.modules.regression.sales.home_ss.dp3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * <p> Created by lkazarnovskiy on 8/24/2017.
 * <b> TestPolicyCreation SS DP3 Full </b>
 * <p> Steps::
 * <p> 1. Create new or open existent Customer;
 * <p> 2. Initiate Hss DP3 quote creation, set effective date to today, set Policy Form=HO6;
 * <p> 3. Fill all mandatory fields;
 * <p> 4. Add Endorsement form
 * <p> 5. Calculate premium;
 * <p> 6. Issue policy;
 * <p> 7. Check Policy status is Active.
 * <p> */
public class TestPolicyCreationFull extends HomeSSDP3BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3)
	public void testPolicyCreationFull(@Optional("") String state) {
		mainApp().open();

		createCustomerIndividual();
		policy.createPolicy(getTestSpecificTD("TestDataFull"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Hss DP3 Full is passed; Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
	}
}
