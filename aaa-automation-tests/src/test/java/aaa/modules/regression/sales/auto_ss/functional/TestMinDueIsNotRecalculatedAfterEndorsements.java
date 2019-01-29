package aaa.modules.regression.sales.auto_ss.functional;

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
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test Min Due Is Not Recalculated After Endorsements
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy with Eleen Pay Standard plan
 * 3. Pay in full
 * 4. At time point R-96 run renewalOfferGenerationPart2 (renewal is initiated)
 * 5. At time point R-45 run renewalOfferGenerationPart1 and renewalOfferGenerationPart2 (renewal status is ‘Premium Calculated’)
 * 6. At time point R-35 run renewalOfferGenerationPart2 (renewal status is ‘Proposed’)
 * 7. At time point R-20 run aaaRenewalNoticeBillAsyncJob to generate renewal bill
 * 8. At time point R-17 initiate endorsement
 * 9. Navigate to P&C tab and process an endorsement that causes an additional premium (for example increase coverages)
 * 10. Navigate to Bind tab and bind the endorsement
 * 11. Navigate to Billing tab and check min due (min due is not increased from AP endorsement)
 * 12. At time point R-16 initiate endorsement
 * 13. Navigate to P&C tab and process an endorsement that causes a return premium that is smaller than the additional premium (for example decrease coverages)
 * 14. Navigate to Bind tab and bind the endorsement
 * 15. Go to Billing tab and review the minimum due
 * @details
 */
public class TestMinDueIsNotRecalculatedAfterEndorsements extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = {States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-20379, PAS-22575")
	public void pas22575_testMinDueIsNotRecalculatedAfterEndorsements(@Optional("AZ") String state) {

		TestData tdPolicy = getPolicyTD()
				.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.AUTO_ELEVEN_PAY);
		String policyNumber = openAppAndCreatePolicy(tdPolicy);
		LocalDateTime policyExpDate = PolicySummaryPage.getExpirationDate();
		payTotalAmtDue(policyNumber);
		moveTimeAndRunRenewJobs(policyExpDate.minusDays(96));
		moveTimeAndRunRenewJobs(policyExpDate.minusDays(45));
		moveTimeAndRunRenewJobs(policyExpDate.minusDays(35));
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(20));
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(17));

		// Save Min Due for Renewal Proposal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar minDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue());
		BillingSummaryPage.openPolicy(1);

		endorseAndChangeBodilyInjury("$1,000,000/");

		// Check That Renewal Proposal Min Due did not change
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(minDue);

		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(16));
		searchForPolicy(policyNumber);
		endorseAndChangeBodilyInjury("$300,000/");

		// Check That Renewal Proposal Min Due did not change and Bill was not discarded
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(minDue);
		assertThat(BillingSummaryPage.tableBillsStatements.getValuesFromRows(BillingConstants.BillingBillsAndStatmentsTable.TYPE)).doesNotContain(BillingConstants.BillsAndStatementsType.DISCARDED_BILL);
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