package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

/**
 * @author Lina Li
 * @name Test Midterm Endorsement for Policy
 * @scenario 1. Create Customer
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

		createCustomerIndividual();
		createPolicy(getBackDatedPolicyTD());

		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("TEST: MidTerm Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

		TestData endorsementTd = getTestSpecificTD("TestData");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));

		assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(policyPremium).isNotEqualTo(PolicySummaryPage.TransactionHistory.getEndingPremium());
	}
}
