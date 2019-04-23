package aaa.modules.regression.finance.billing.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMinDueIsNotRecalculatedAfterThirdEndorsement extends FinanceOperations {

	/**
 * @author Vilnis Liepins
 * Objectives : Min Due Is Not Recalculated After Three Endersements
 * 1. Create Customer
 * 2. Create Auto SS policy with Monthly Standard plan
 * 3. Pay in full
 * 4. Create initial Renewal Offer
 * 5. At time point R-17 initiate an Endorsement
 * 6. Navigate to P&C tab and process an Endorsement that causes a return premium(for example decrease coverages)
 * 7. Navigate to Bind tab and Bind the Endorsement
 * 8. Navigate to Billing tab, check that Min due is decreased and save the Min due value
 * 9. At time point R-16 initiate an Endorsement
 * 10. Navigate to P&C tab and process an Endorsement that causes an additional premium (for example increase coverages)
 * 11. Navigate to Bind tab and Bind the Endorsement
 * 12. Navigate to Billing tab and check that Min due is not increased
 * 13. At time point R-15 initiate an Endorsement
 * 14. Navigate to P&C tab and process an Endorsement that causes a return premium that is greater than the additional premium (for example decrease coverages)
 * 15. Navigate to Bind tab and Bind the Endorsement
 * 16. Go to Billing tab and review the Min due and check Renewal Offer
 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
	@Parameters({"state"})
	@StateList(statesExcept = {States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-22575")

	public void pas22575_testMinDueIsNotRecalculatedAfterThirdEndorsement(@Optional("KY") String state) {
		TestData tdPolicy = getPolicyTD()
				.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.AUTO_ELEVEN_PAY);
		String policyNumber = openAppAndCreatePolicy(tdPolicy);
		LocalDateTime policyExpDate = PolicySummaryPage.getExpirationDate();
		payTotalAmtDue(policyNumber);

		//Create Initial Renewal Offer and Generate Bill
		renewalImageGeneration(policyNumber, policyExpDate);
		renewalPreviewGeneration(policyNumber, policyExpDate);
		renewalOfferGeneration(policyNumber, policyExpDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(20));
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(17));

		// Create the First Endorsement (RP)
		mainApp().open();
		searchForPolicy(policyNumber);
		endorseAndChangeBodilyInjury("$25,000/");

		// Check That Initial Renewal Offer is discarded and Save Min Due value
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(16));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		assertThat(BillingSummaryPage.tableBillsStatements.getValuesFromRows(BillingConstants.BillingBillsAndStatmentsTable.TYPE)).contains(BillingConstants.BillsAndStatementsType.DISCARDED_BILL);
		Dollar minDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue());

		// Create the Second Endorsement (AP)
		BillingSummaryPage.openPolicy(1);
		endorseAndChangeBodilyInjury("$1,000,000/");

		// Check That Renewal Proposal Min Due did not change
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(15));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(minDue);

		// Create the Third Endorsement (RP)
		BillingSummaryPage.openPolicy(1);
		endorseAndChangeBodilyInjury("$50,000/");

		// Check That Renewal Proposal Min Due did not change
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(minDue);
	}

	private void endorseAndChangeBodilyInjury(String bodilyInjuryAmount){
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValueContains(bodilyInjuryAmount);
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().submitTab();
	}
}