package aaa.modules.bct.service.auto_ss;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.ReinstatementActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class ReinstatePolicy extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	private TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_SS);
	private ReinstatementActionTab reinstatementTab = new ReinstatementActionTab();

	/**
	 * @author Deloite
	 * @name Reinstatement - Cancelled with Lapse
	 * @scenario
	 * 1. User retrieves the policy which is in "Cancelled" state with following reasons:
	 * Check: A Cancelled policy need to be Retrieved
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_006_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		verifyReinstateCase(false, policyNumber);
	}

	/**
	 * @author Deloite
	 * @name Reinstatement - Cancelled without Lapse
	 * @scenario
	 * 1. Retrieve an Active policy and make a flat cancellation
	 * Check: AN Active policy need to be retrieved
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_007_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		verifyReinstateCase(true, policyNumber);
	}

	private void verifyReinstateCase(boolean isActivePolicyFound, String policyNumber) {
		if (!isActivePolicyFound) {
			SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		} else {
			SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_ACTIVE);

			deletePendingTransaction(policy);
			policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		}

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation
				.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.CANCELLATION_EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);
		String reinstatementDate = cancellationDate.plusDays(48).format(DateTimeUtils.MM_DD_YYYY);
		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), AutoSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData")
				.adjust(reinstatementKey, reinstatementDate));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Reinstatement with Lapse");
	}
}