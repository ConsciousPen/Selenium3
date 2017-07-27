package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestPolicyCopy extends HomeCaHO3BaseTest {
	/**
	 * @author Jurij Kuznecov
	 * @name Test Policy Copy
	 * @scenario
	 * 1. Create new or open existent Customer Individual;
	 * 2. Initiate CAH quote creation, set effective date to today;
	 * 3. Fill all mandatory fields and purchase policy;
	 * 4. Copy policy and purchase;
	 * 5. Verify that new policy number is not the same as initial policy number;
	 * */

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyCopy() {

		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.policyCopy().perform(tdPolicy.getTestData("CopyFromPolicy", "TestData"));
		policy.calculatePremiumAndPurchase(tdPolicy.getTestData("DataGather", "TestData").mask("ReportsTab"));
		CustomAssert.assertFalse("Copied policy number is the same as initial policy number " + policyNumber,
				policyNumber.equals(PolicySummaryPage.labelPolicyNumber.getValue()));
	}

}
