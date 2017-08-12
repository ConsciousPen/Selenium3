package aaa.modules.regression.service.template;

import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.pup.TestPolicyBackdated;

import com.exigen.ipb.etcsa.utils.Dollar;

/**
 * @author Lina Li
 * @name Test Midterm Endorsement for Policy
 * @scenario
 * 1. Create Customer
 * 2. Create a Backdated Policy 
 * 3. Create endorsement
 * 4. Verify 'Pended Endorsement' button is disabled
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Ending Premium is changed
 * @details
 */

public abstract class PolicyEndorsementMidTerm extends PolicyBaseTest {

	protected void testPolicyEndorsementMidTerm() {
		mainApp().open();

		if (getPolicyType().equals(PolicyType.PUP)) {
			new TestPolicyBackdated().testPolicyBackdated();
		} else {
			createCustomerIndividual();
			createPolicy(getBackDatedPolicyTD());
		}
		
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("TEST: MidTerm Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

		TestData endorsementTd = getTestSpecificTD("TestData");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
		
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
	    PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	    CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
	}
}