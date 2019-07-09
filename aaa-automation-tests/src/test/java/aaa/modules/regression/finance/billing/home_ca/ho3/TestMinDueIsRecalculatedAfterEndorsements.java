package aaa.modules.regression.finance.billing.home_ca.ho3;

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
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMinDueIsRecalculatedAfterEndorsements extends FinanceOperations {

	/**
	 * @author Vilnis Liepins
	 * Objectives : Min Due Is Recalculated After Endersements
	 * 1. Create Customer
	 * 2. Create CA Home policy with Monthly Standard plan
	 * 3. Pay in full
	 * 4. Create Renewal Offer
	 * 5. At time point R-17 initiate an Endorsement
	 * 6. Navigate to P&C tab and process an Endorsement that causes an additional premium (for example increase coverages)
	 * 7. Navigate to Bind tab and Bind the Endorsement
	 * 8. Navigate to Billing tab and check that Min due is not increased
	 * 9. At time point R-16 initiate an Endorsement
	 * 10. Navigate to P&C tab and process an Endorsement that causes a return premium that is greater than the additional premium (for example decrease coverages)
	 * 11. Navigate to Bind tab and Bind the Endorsement
	 * 12. Go to Billing tab and review the Min due and check Renewal Offer
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-22575")

	public void pas22575_testMinDueIsRecalculatedAfterEndorsements(@Optional("CA") String state) {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust(TestData.makeKeyPath(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.MONTHLY_STANDARD);
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
		endorseAndChangeCoverages("$1,000,000", "$100");

		// Check that Renewal Proposal Min Due did not change
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(minDue);
		TimeSetterUtil.getInstance().nextPhase(policyExpDate.minusDays(16));
		searchForPolicy(policyNumber);
		endorseAndChangeCoverages("$100,000", "$7,500");

		// Check that Renewal Proposal Min Due did change and Offer was discarded
		SearchPage.openBilling(policyNumber);
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS, ProductConstants.PolicyStatus.PROPOSED).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isNotEqualTo(minDue);
		assertThat(BillingSummaryPage.tableBillsStatements.getValuesFromRows(BillingConstants.BillingBillsAndStatmentsTable.TYPE)).contains(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER);
	}

	private void endorseAndChangeCoverages(String CoverageEAmount, String DeductibleAmount) {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).setValueContains(CoverageEAmount);
		new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE).setValueContains(DeductibleAmount);
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		new BindTab().submitTab();
	}
}