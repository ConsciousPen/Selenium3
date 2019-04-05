package aaa.modules.bct.service.home_ss.ho3;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.EndorsementTemplate;
import aaa.utils.StateList;

public class CancelPolicy extends EndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	/**
	 * @author Deloite
	 * @name View Cancelled policy status
	 * @scenario
	 * Agent retrieves 1 active policy:
	 * 	Agent selects "New Business Rescission - NSF on Down Payment" value for the dropdown field "Cancellation reason"."
	 * 	System does the following:
	 * 	Status of the policy is set as "Policy Cancelled."
	 * 	Following transaction is created and displayed in the "Payments & Other Transactions" section of the Billing Tab.
	 *      -"Type" = Premium
	 *      -"Subtype/Reason" = Cancellation - New Business Recission - NSF on Down Payment
	 *      -"Amount"= Policy Premium
	 *      -"Status" = Applied"
	 * 	"Policy need to be retrieved and selected for Flat cancellation
	 * 	Status need to be updated as "Policy Cancelled"
	 * 	On Billing page Tha cancellation Transaction need to be updated
	 */

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CA, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_009_CancelPolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		deletePendingTransaction(policy);
		policy.cancel().perform(getTestSpecificTD("Cancellation_009"));

		// Check if Status is updated to Policy Cancelled in the UI
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason("Cancellation - New Business Rescission - NSF on Down Payment")
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();
	}
}
