package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.HomeSSPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
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

		String tdName = this.getClass().getSimpleName();

		new HomeSSPolicyActions.Cancel().perform(getPolicyTD(tdName, "TestData_Cancellation"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		log.info("TEST: HSS Policy #" + policyNumber + "is cancelled");

		new HomeSSPolicyActions.Reinstate().perform(getPolicyTD(tdName, "TestData_Reinstatement"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		log.info("TEST: HSS Policy #" + policyNumber + "is reinstated");

	}

}
