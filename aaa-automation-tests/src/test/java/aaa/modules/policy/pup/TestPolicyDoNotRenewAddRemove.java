package aaa.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Add and Remove 'Do Not Renew' for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella  Policy
 * 3. Set Do Not Renew for Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * 6. Remove Do Not Renew for Policy
 * 7. Verify Policy status is 'Policy Active'
 * 8. Verify 'Do Not Renew' flag isn't displayed in the policy overview header
 * @details
 */

public class TestPolicyDoNotRenewAddRemove extends PersonalUmbrellaBaseTest{
	  @Test
	  @TestInfo(component = "Policy.PUP.DoNotRenew")
	  public void testPolicyDoNotRenewAddRemove() {
		    mainApp().open();

	        createCustomerIndividual();

	        createPolicy();	        
	        		    		    
		    String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
	        log.info("TEST: Do Not Renew for Policy #" + policyNumber);

	        policy.doNotRenew().perform(tdPolicy.getTestData("DoNotRenew", "TestData"));
	        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	        PolicySummaryPage.labelDoNotRenew.verify.present();

	        log.info("TEST: Remove Do Not Renew for Policy #" + policyNumber);
	        policy.removeDoNotRenew().perform(new SimpleDataProvider());

	        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	        PolicySummaryPage.labelDoNotRenew.verify.present(false);
	  }

}
