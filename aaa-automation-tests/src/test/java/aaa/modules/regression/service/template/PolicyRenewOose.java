package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;


import aaa.common.Tab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Jelena Dembovska
 * @scenario
 * Common functionality from template:
 * 1. Create Customer
 * 2. Create Policy
 * 3. Create manual renew image -> change payment plan
 * 4. Bind endorsement without conflicting change - change some coverage (payment plan remains the same)
 * 5. Bind second endorsement with conflicting change - change payment plan
 * @details
 */

public class PolicyRenewOose extends PolicyBaseTest {

    public void testPolicyRenewOOSE() {
			
	        mainApp().open();

	        
	        createCustomerIndividual();
	        createPolicy();
	        
	        //change payment plan value
	        policy.renew().performAndFill(getTestSpecificTD("TestData_Renew").resolveLinks());
			Tab.buttonSaveAndExit.click();
	         
	        //change value for some coverage
	        policy.createEndorsement(getTestSpecificTD("TestData_endorsement_without_conflict").adjust(getPolicyTD("Endorsement", "TestData")));
	        
	        //check: new renewal version is created automatically
	        PolicySummaryPage.buttonRenewalQuoteVersion.click();
	        assertThat(PolicySummaryPage.tableTransactionHistory).hasRows(2);
	        PolicySummaryPage.buttonQuoteOverview.click();
	        
	        
	        //bind endorsement with conflicting change - change payment plan
	        policy.createEndorsement(getTestSpecificTD("TestData_endorsement_with_conflict").adjust(getPolicyTD("Endorsement", "TestData")));
	        
	        //here will be comparison screen when OOSE feature will be turned on
	        //rollOnChangesOnDifferencesTab();
	        policy.rollOn().perform(true);
	        
	        //check: one more renewal version is created automatically
	        PolicySummaryPage.buttonRenewalQuoteVersion.click();
	        assertThat(PolicySummaryPage.tableTransactionHistory).hasRows(3);
	        
	        //product's specific checking will follow in test for concrete product
	        
	    }
}
