package aaa.modules.bct.service.home_ss;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.ReinstatementActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class ReinstatePolicy extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	private ReinstatementActionTab reinstatementTab = new ReinstatementActionTab();
	private TestData tdPolicy = testDataManager.policy.get(PolicyType.HOME_SS_HO3);

	/**
	 * @author Deloite
	 * @name Reinstatement - Cancelled with Lapse
	 * @scenario
	 * 1. User retrieves the policy which is in "Cancelled" state with following reasons:
	 * 2. Policy has cancelled due to insured request reason / UW cancel reason.
	 * 3. User tries to reinstate - System determines that Reinstate Date' is within the last 48 days of the policy term and displays the
	 * following message:"Policy will be reinstated with lapse. Do you wish to continue?".
	 * 4. User selects 'Yes' and policy reinstated with lapse
	 * 5. On policy consolidated view the status of Policy is active and  term includes lapse in policy.
	 * 6. Under transaction history, reinstatement without lapase is logged.
	 * Check:
	 * 1. A Cancelled policy need to be Retrieved
	 * 2. User need to reinstate the policy with RD>CED
	 * 3. Reinstatement with Lapse flag need to validated on Policy Consolidated view
	 * 4. Transaction history need to be updated with The reinstatement reason
	 *
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_010_ReinstatePolicy(@Optional("AZ") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.reinstate().start();

		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(reinstatementTab.getAssetList()
				.getAsset(HomeSSMetaData.ReinstatementActionTab.CANCELLATION_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);

		String reinstatementDate = cancellationDate.plusDays(48).format(DateTimeUtils.MM_DD_YYYY);
		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), HomeSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());

		reinstatementTab.fillTab(getStateTestData(tdPolicy, "Reinstatement", "TestData").adjust(reinstatementKey, reinstatementDate));
		Tab.buttonOk.click();
		assertThat(Page.dialogConfirmation.labelMessage).valueContains("Policy will be reinstated with a lapse");
		Page.dialogConfirmation.confirm();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyLapseExistFlagPresent();

		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Reinstatement with Lapse");
	}

	/**
	 * @author Deloite
	 * @name Reinstatement - Cancelled without Lapse
	 * @scenario
	 * 1. Retrieve an Active policy and make a flat cancellation
	 * 2. On cancelling policy-  Payments & Other transactions section display an entry for cancellation.
	 * 3. Policy is reinstated with reinstatement date equal to cancellation effective date, and Total due is no more zero.
	 * 4. Under transaction history, reinstatement without lapase is logged.
	 * Check:
	 * 1. AN Active policy need to be retrieved
	 * 2. Cancellation reason should have an entry in Billing page
	 * 3. Policy need to be reinstated with RD=CED
	 * 4. Transaction history need to be updated with The reinstatement reason
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_011_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		deletePendingTransaction(policy);

		policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation
				.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.CANCELLATION_EFFECTIVE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY);

		String reinstatementKey = TestData.makeKeyPath(reinstatementTab.getMetaKey(), HomeSSMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel());

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData").adjust(reinstatementKey, cancellationDate.format(DateTimeUtils.MM_DD_YYYY)));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.labelLapseExist).isPresent(false);
		PolicySummaryPage.buttonTransactionHistory.click();

		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Reinstatement");
	}

}