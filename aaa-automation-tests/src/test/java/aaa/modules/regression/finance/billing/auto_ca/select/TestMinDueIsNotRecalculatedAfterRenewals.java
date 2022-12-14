package aaa.modules.regression.finance.billing.auto_ca.select;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMinDueIsNotRecalculatedAfterRenewals extends FinanceOperations {

	/**
	 * @author Vilnis Liepins
	 * Objectives : Min Due Is Not Recalculated After Renewals
	 * 1. Create Customer
	 * 2. Create CA Select Auto policy with Standard Monthly plan
	 * 3. Pay in full
	 * 4. Create renewal offer
	 * 5. At time point R-17 initiate a new Renewal version
	 * 6. Navigate to P&C tab and process a Renewal that causes an additional premium (for example increase coverages)
	 * 7. Navigate to Bind tab and Propose the Renewal version
	 * 8. Navigate to Billing tab and check that Min due is not increased
	 * 9. At time point R-16 initiate a new Renewal version
	 * 10. Navigate to P&C tab and process a Renewal that causes a return premium that is smaller than the additional premium (for example decrease coverages)
	 * 11. Navigate to Bind tab and Propose the Renewal version
	 * 12. Go to Billing tab and review the Min due and check if Renewal Offer was not discarded
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-22575")

	public void pas22575_testMinDueIsNotRecalculatedAfterRenewals(@Optional("CA") String state) {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
		String policyNumber = openAppAndCreatePolicy(td);
		LocalDateTime policyExpDate = PolicySummaryPage.getExpirationDate();
		payTotalAmtDue(policyNumber);

		//Initiate Renewal Offer
		createInitialReviewOffer(policyExpDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(17));

		// Save Min Due for Renewal Proposal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar minDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains
				(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED)
				.getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue());
		BillingSummaryPage.openPolicy(1);
		renewalAndChangeBodilyInjury("$1,000,000/$1,000,000");

		// Check that Renewal Proposal Min Due did not change
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(
				BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED)
				.getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(minDue);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(16));
		searchForPolicy(policyNumber);
		renewalAndChangeBodilyInjury("$500,000/$1,000,000");

		// Check that Renewal Proposal Min Due did not change and Offer was not discarded
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(
				BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED)
				.getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isNotEqualTo(minDue);
		assertThat(BillingSummaryPage.tableBillsStatements.getValuesFromRows(BillingConstants.BillingBillsAndStatmentsTable.TYPE))
				.doesNotContain(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER);
	}

	private void renewalAndChangeBodilyInjury(String bodilyInjuryAmount) {
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValueContains(bodilyInjuryAmount);
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
	}
}