package aaa.modules.policy.pup;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class AAATestPolicyEndorsement extends PersonalUmbrellaBaseTest {
	
	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyEndorsement() {
		 mainApp().open();

	     createCustomerIndividual();
	       
	     TestData td = adjustWithRealPolicies(getStateTestData(tdPolicy, "DataGather", "TestData"), getPrimaryPolicies());
	     policy.createPolicy(td);

	     Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();
			
	     log.info("TEST: Flat Endorsement for PUP Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
	     
	     TestData endorsement_td = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData");
	     policy.createEndorsement(endorsement_td.adjust(tdPolicy.getTestData("Endorsement", "TestData")));
	     
	     CustomAssert.enableSoftMode();
	        
	     PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
	     PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	        
	     //PolicySummaryPage.tableOtherUnderlyingRisks.verify.rowsCount(2);
	        
	     CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
	        
	     CustomAssert.assertAll();
   
	}

}
