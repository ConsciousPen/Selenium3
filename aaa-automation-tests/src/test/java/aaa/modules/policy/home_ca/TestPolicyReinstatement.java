package aaa.modules.policy.home_ca;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaBaseTest;
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

public class TestPolicyReinstatement extends HomeCaBaseTest {
	
	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyReinstatement(){
		mainApp().open();
		
		getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		
		String tdName = this.getClass().getSimpleName();	
		
		new HomeCaPolicyActions.Cancel().perform(tdPolicy.getTestData(tdName, "TestData_Cancellation"));
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		
		log.info("TEST: HSS Policy #" + policyNumber +"is cancelled");
		
		new HomeCaPolicyActions.Reinstate().perform(tdPolicy.getTestData(tdName, "TestData_Reinstatement"));
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		log.info("TEST: HSS Policy #" + policyNumber +"is reinstated");
		
	}

}
