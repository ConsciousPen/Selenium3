package aaa.modules.regression.conversions.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;

public class TestCappingForSubsequentRenewals extends HomeSSHO3BaseTest {
	private static String policyNumber;
	private static LocalDateTime policyEffectiveDate;
	private static LocalDateTime policyExpirationDate;
	private static LocalDateTime renewImageGenDate;
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private ProductOfferingTab productOfferingTab = new ProductOfferingTab();

	private TestData td;

	/**
	 * @author R. Kazlauskiene
	 * @name Test View And Override Capping Details
	 * @scenario
	 * Preconditions: policy qualifies for capping and user have the capping privilege
	 * 1. Create Individual Customer / Account
	 * 2. Create converted policy.
	 * 3. Initiate Data Gathering in Renewal Screen
	 * 4. Fill all information and Calculate the Premiums: Capping should be applied correctly
	 * 5. Active Renewal
	 * 6. Create subsequent renewal
	 * 7. Verify both capping factors
	 *
	 **/
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3, testCaseId = "PAS-11383")
	public void testCappingForSubsequentRenewals(@Optional("AZ") String state) {

		td = initTestData();
		TestData initiateRenewalEntry = initInitiateRenewalEntry();
		mainApp().open();
		createCustomerIndividual();
		//Initiate Renewal manual entry
		customer.initiateRenewalEntry().perform(initiateRenewalEntry);
		//Fill Conversion Policy
		policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);

		//View Capping Factor
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();
		String cappingFactor = PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("Applied Capping Factor").replaceAll("%", "");
		Double cappingFactorValue = Double.valueOf(cappingFactor);

		//Finish Conversion Policy
		finishConversionPolicy();
		//Activate Renewal
		activateRenewal();

		//InitiateSecondRenewal
		initiateRenewal();

		//View Second Renewal Capping Factor
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();
		String cappingFactor2 = PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("Applied Capping Factor").replaceAll("%", "");
		Double cappingFactorValue2 = Double.valueOf(cappingFactor2);

		//Verify Capping Factor
		assertThat(cappingFactorValue > cappingFactorValue2).isTrue();
	}

	private void finishConversionPolicy() {
		//Finish Conversion Policy
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.VIEW_CAPPING_DETAILS_DIALOG).getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.ViewCappingDetailsDialog.BUTTON_TO_PREMIUM_AND_COVERAGES.getLabel(), Button.class).click();
		PremiumsAndCoveragesQuoteTab.btnContinue.click();
		policy.getDefaultView().fillFromTo(td, MortgageesTab.class, BindTab.class, true)
				.getTab(BindTab.class).btnPurchase.click();
		Page.dialogConfirmation.confirm();
	}

	private void activateRenewal() {
		if (PolicySummaryPage.tableRenewals.isPresent()) {
			Tab.buttonBack.click();
		}
		policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate().plusYears(1);
		policyExpirationDate = PolicySummaryPage.getExpirationDate().plusYears(1);
		renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyEffectiveDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate);

		mainApp().reopen();
		SearchPage.openBilling(policyNumber);
		if (PolicySummaryPage.tableRenewals.isPresent()) {
			SearchPage.openBilling(policyNumber);
		}
		Dollar totDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies
				.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM, policyNumber)
				.getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount
				.getTestData("AcceptPayment", "TestData_Cash"), totDue);
	}

	private void initiateRenewal() {
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	private TestData initTestData() {
		return getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(),
				HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel()), testDataManager.getDefault(TestConversionViewAndOverrideCappingDetails.class)
				.getTestData("TestData").getTestData("GeneralTab").getValue("Immediate prior carrier"));
	}

	private TestData initInitiateRenewalEntry() {
		return getManualConversionInitiationTd()
				.adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(),
						CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_POLICY_PREMIUM.getLabel()),
						testDataManager.getDefault(TestConversionViewAndOverrideCappingDetails.class).getTestData("TestData")
								.getTestData("InitiateRenewalEntryActionTab").getValue("Renewal Policy Premium"));
	}
}
