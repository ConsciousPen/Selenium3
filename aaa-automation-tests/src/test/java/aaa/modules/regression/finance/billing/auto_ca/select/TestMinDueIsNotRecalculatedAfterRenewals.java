package aaa.modules.regression.finance.billing.auto_ca.select;

import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestMinDueIsNotRecalculatedAfterRenewals extends FinanceOperations {

	/**
	 * @author Vilnis Liepins
	 * @name Test Min Due Is Not Recalculated After Renewals
	 * @scenario
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
	 * 12. Go to Billing tab and review the Min due
	 * @details
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-22575")

	public void pas22575_testMinDueIsNotRecalculatedAfterRenewals (@Optional("CA") String state) {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
		.adjust(TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.STANDARD_MONTHLY);
		String policyNumber = openAppAndCreatePolicy(td);
		LocalDateTime policyExpDate = PolicySummaryPage.getExpirationDate();
		payTotalAmtDue(policyNumber);

		//Initiate Renewal Offer
		renewalImageGeneration(policyNumber, policyExpDate);
		renewalPreviewGeneration(policyNumber, policyExpDate);
		renewalOfferGeneration(policyNumber, policyExpDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(17));

		// Save Min Due for Renewal Proposal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar minDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue());
		BillingSummaryPage.openPolicy(1);
		renewalAndChangeBodilyInjury("$1,000,000/$1,000,000");

		// Check that Renewal Proposal Min Due did not change
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(minDue);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(16));
		searchForPolicy(policyNumber);
		renewalAndChangeBodilyInjury("$500,000/$1,000,000");

		// Check that Renewal Proposal Min Due did not change and Bill was not discarded
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(minDue);
		assertThat(BillingSummaryPage.tableBillsStatements.getValuesFromRows(BillingConstants.BillingBillsAndStatmentsTable.TYPE)).doesNotContain(BillingConstants.BillsAndStatementsType.DISCARDED_BILL);
	}

	private void renewalAndChangeBodilyInjury(String bodilyInjuryAmount){
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValueContains(bodilyInjuryAmount);
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
	}
}