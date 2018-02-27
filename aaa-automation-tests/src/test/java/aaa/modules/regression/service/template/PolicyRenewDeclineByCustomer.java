package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.verification.CustomAssert;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;


/**
 * @author Lina Li
 * @name Test renew decline by customer
 * @scenario
 * 1. Create Customer
 * 2. Create a Policy 
 * 3. Renew Policy
 * 4. Decline by customer
 * 5. Verify Policy status is 'Customer Declined'
 * @details
 */

public class PolicyRenewDeclineByCustomer extends PolicyBaseTest{
	public void testPolicyRenewDeclineByCustomer(){
		 mainApp().open();
	        
	     getCopiedPolicy();
	                
	     assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	        
	     CustomAssert.enableSoftMode();
	     log.info("TEST: Decline By Company Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
	        
	     if (getPolicyType().equals(PolicyType.AUTO_SS)||getPolicyType().equals(PolicyType.AUTO_CA_SELECT)||getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)){
	    	 policy.renew().perform(new SimpleDataProvider());
	     }	     
	     
	     else {
	    	  policy.renew().performAndExit(new SimpleDataProvider());
	     }   
	         
	     PolicySummaryPage.buttonRenewals.click();

	     policy.declineByCustomerQuote().perform(getPolicyTD("DeclineByCustomer", "TestData_Plus1Year"));
	     PolicySummaryPage.buttonRenewals.click();

	     PolicySummaryPage.tableRenewals.getRow(1).getCell(4).verify.value(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);
	     
	     CustomAssert.assertAll();
	     
	}

}
