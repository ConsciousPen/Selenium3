package aaa.modules.policy.home_ca_ho4;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO4BaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Alexander Tinkovan
 * @name Test Create Home California Policy with HO4 Full
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Quote
 * 3. Verify quote status is 'Premium Calculated'
 * 4. Issue Policy
 * 5. Verify Policy status is 'Policy Active'
 * @details
 */
public class TestPolicyCreateHo4Full extends HomeCaHO4BaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testQuoteIssueHo4Full() {
		CustomAssert.assertTrue("NOT COMPLETED TEST: add missed test data from \"AZ_HSS_Smoke.xls\" e.g. QuoteEndorsementHS0614 form", false);
		mainApp().open();
		createCustomerIndividual();
		createPolicy(tdPolicy.getTestData("DataGather", "TestData_Full"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}
