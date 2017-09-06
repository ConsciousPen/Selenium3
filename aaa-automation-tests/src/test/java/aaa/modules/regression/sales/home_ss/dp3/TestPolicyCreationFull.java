package aaa.modules.regression.sales.home_ss.dp3;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSDP3BaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * Created by lkazarnovskiy on 8/24/2017.
 * @name TestPolicyCreation SS DP3 Full
 * @scenario:
 * 1. Create new or open existent Customer;
 * 2. Initiate Hss DP3 quote creation, set effective date to today, set Policy Form=HO6;
 * 3. Fill all mandatory fields;
 * 4. Add Endorsement form
 * 5. Calculate premium;
 * 6. Issue policy;
 * 7. Check Policy status is Active.
 * */
public class TestPolicyCreationFull extends HomeSSDP3BaseTest {

	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3)
	public void testQuoteCreation() {
		mainApp().open();

		createCustomerIndividual();
		policy.createPolicy(getTestSpecificTD("TestDataFull"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Hss DP3 Full is passed; Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
	}
}