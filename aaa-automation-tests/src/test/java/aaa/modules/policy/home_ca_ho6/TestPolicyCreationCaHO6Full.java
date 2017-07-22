package aaa.modules.policy.home_ca_ho6;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO6BaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Kazarnovskiy Lev
 * @name Test Add Billing Account on Hold
 * @scenario:
 * 1. Create new or open existent Customer;
 * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO6;
 * 3. Fill all mandatory fields;
 * 4. Add Endorsement form
 * 5. Calculate premium;
 * 6. Issue policy;
 * 7. Check Policy status is Active.
 *
 * @details
 */
public class TestPolicyCreationCaHO6Full extends HomeCaHO6BaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyCreation() throws InterruptedException {
		CustomAssert.assertTrue("NOT COMPLETED TEST: add missed test data from \"CA_HSS_Smoke.xls\" e.g. QuoteEndorsementHO210 form, etc.", false);
		mainApp().open();
		createCustomerIndividual();
		createPolicy(tdPolicy.getTestData("DataGather", "TestDataFull"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}

