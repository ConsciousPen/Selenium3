package aaa.modules.bct.service.auto_ss;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class CancelPolicy extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_005_CancelPolicy(@Optional("AZ") String state) {
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		assertSoftly(softly -> {
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			deletePendingTransaction(policy);
			policy.cancel().perform(getTestSpecificTD("Cancellation_005"));

			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		});
	}

	/**
	 * @author Deloite
	 * @name View Cancelled policy status
	 * @scenario
	 * 1. Retrieve a Policy with "Cancellation Pending" status
	 * 2. Policy should be with Cancellation pending status
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_008_CancelPolicy(@Optional("AZ") String state) {
		//TODO Test moved from Deloite's code as is, should be updated
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		mainApp().open();
		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.CANCELLATION_PENDING);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
	}

}
