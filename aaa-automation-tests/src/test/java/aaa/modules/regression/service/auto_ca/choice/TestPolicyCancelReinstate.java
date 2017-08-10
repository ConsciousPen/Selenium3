package aaa.modules.regression.service.auto_ca.choice;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Policy Cancel Reinstate
 * @scenario
 * 1. Find customer or create new if customer does not exist;
 * 2. Create new CA Choice Policy;
 * 3. Cancel the policy and check the policy status
 * 4. Reinstate the cancelled policy
 * 5. Check the policy status
 * @details
 */

public class TestPolicyCancelReinstate extends AutoCaChoiceBaseTest {
	
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE )
	public void testPolicyCancelReinstate() {
		mainApp().open();
		
		createCustomerIndividual();
		createPolicy();
	    String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		
	    log.info("Cancelling Policy #" + policyNumber);		 
	    policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
	    PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);


	    log.info("TEST: Reinstate Policy #" + policyNumber);
        policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	    
	}
	

}
