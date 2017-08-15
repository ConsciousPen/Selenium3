package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test Policy Reinstatement
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Create new CAH policy.
 * 3. Make Cancellation action (with reason Rewrite Accommodation). 
 * 4. Verify policy status is Cancelled. 
 * 6. Make Reinstatement action.  
 * 7. Verify policy status is Active.
 * @details
 */

public class TestPolicyReinstatement extends HomeCaHO3BaseTest {

	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyReinstatement() {
		mainApp().open();

		getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		String tdName = this.getClass().getSimpleName();

		new HomeCaPolicyActions.Cancel().perform(getPolicyTD(tdName, "TestData_Cancellation"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		log.info("TEST: HSS Policy #" + policyNumber + "is cancelled");

		new HomeCaPolicyActions.Reinstate().perform(getPolicyTD(tdName, "TestData_Reinstatement"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		log.info("TEST: HSS Policy #" + policyNumber + "is reinstated");

	}

}
