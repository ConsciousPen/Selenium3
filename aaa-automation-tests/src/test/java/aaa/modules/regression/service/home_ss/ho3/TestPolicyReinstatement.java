package aaa.modules.regression.service.home_ss.ho3;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test Policy Reinstatement
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Create new HSS policy.
 * 3. Make Cancellation action (with reason Rewrite Accommodation). 
 * 4. Verify policy status is Cancelled. 
 * 6. Make Reinstatement action.  
 * 7. Verify policy status is Active.
 * @details
 */

public class TestPolicyReinstatement extends HomeSSHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void testPolicyReinstatement() {
		mainApp().open();

		getCopiedPolicy();
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		TestData td_cancellation = getTestSpecificTD("TestData_Cancellation");
		TestData td_reinstate = getTestSpecificTD("TestData_Reinstatement");
		
		policy.cancel().perform(td_cancellation);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		log.info("TEST: HSS Policy #" + policyNumber + "is cancelled");

		policy.reinstate().perform(td_reinstate);
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		log.info("TEST: HSS Policy #" + policyNumber + "is reinstated");

	}

}
