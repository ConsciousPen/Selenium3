package aaa.modules.regression.finance.billing.home_ss.ho6;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMinDueIsRecalculatedAfterRenewals extends FinanceOperations {

	/**
	 * @author Vilnis Liepins
	 * Objectives : Min Due Is Recalculated After Renewals
	 * 1. Create Customer
	 * 2. Create Home SS policy with Eleven Pay Standard plan
	 * 3. Pay in full
	 * 4. Create Renewal offer
	 * 5. At time point R-17 initiate a new Renewal version
	 * 6. Navigate to P&C tab and process a Renewal that causes an additional premium (for example increase coverages)
	 * 7. Navigate to Bind tab and Propose the Renewal version
	 * 8. Navigate to Billing tab and check that Min due is not increased
	 * 9. At time point R-16 initiate a new Renewal version
	 * 10. Navigate to P&C tab and process a Renewal that causes a return premium that is greiter than the additional premium (for example decrease coverages)
	 * 11. Navigate to Bind tab and Propose the Renewal version
	 * 12. Go to Billing tab and review the Min due and check if Renewal Bill was discarded
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-22575")

	public void pas22575_testMinDueIsRecalculatedAfterRenewals(@Optional("NJ") String state) {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust(TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.ELEVEN_PAY);
		String policyNumber = openAppAndCreatePolicy(td);
		LocalDateTime policyExpDate = PolicySummaryPage.getExpirationDate();
		payTotalAmtDue(policyNumber);

		//Initiate Renewal Offer
		renewalImageGeneration(policyNumber, policyExpDate);
		renewalPreviewGeneration(policyNumber, policyExpDate);
		renewalOfferGeneration(policyNumber, policyExpDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(20));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(17));

		// Save Min Due for Renewal Proposal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar minDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue());
		BillingSummaryPage.openPolicy(1);
		renewalAndChangeCoverages("$2,000,000", "$100");

		// Check that Renewal Proposal Min Due did not change
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(minDue);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(16));
		searchForPolicy(policyNumber);
		renewalAndChangeCoverages("$100,000", "$10,000");

		// Check that Renewal Proposal Min Due did change and Renewal Bill was discarded
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isNotEqualTo(minDue);
		assertThat(BillingSummaryPage.tableBillsStatements.getValuesFromRows(BillingConstants.BillingBillsAndStatmentsTable.TYPE)).contains(BillingConstants.BillsAndStatementsType.DISCARDED_BILL);
	}

	private void renewalAndChangeCoverages(String CoverageEAmount, String DeductibleAmount) {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).setValueContains(CoverageEAmount);
		new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).setValueContains(DeductibleAmount);
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().submitTab();
	}
}