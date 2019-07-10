package aaa.modules.regression.conversions.template;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.apache.commons.lang3.Range;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestPolicyRmeNyLegacyTierFieldTemplate extends PolicyBaseTest {

	private static String policyNumber;
	private static LocalDateTime policyEffectiveDate;
	private static LocalDateTime policyExpirationDate;
	private static LocalDateTime renewImageGenDate;

	protected static String LEGACY_TIER_REQUIRED_MESSAGE = "Legacy Tier is required";
	protected static String LEGACY_TIER_IS_OUT_OF_RANGE_MESSAGE = "Legacy Tier is out of range if the field is populated with anything other than the numbers 1-50";

	private InitiateRenewalEntryActionTab initiateRenewalEntryActionTab = new InitiateRenewalEntryActionTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	/**
	 * @author Sushil Sivaram, Rokas Lazdauskas
	 * @name Test Policy RME Legacy Tier field
	 * @scenario
	 * 1. Create Individual Customer / Account
	 * 2. Select RME Action with HSS product
	 * 3. Fill everything in RME screen except "Legacy Tier"
	 * 4. Verify "Legacy  Tier" field is exist on RME screen
	 * 5. Submit tab and check "Legacy Tier Is Required" message
	 * 6. Try filling alphabetical, special charecter or numeric value which is not in range 1-50 and submiting tab
	 * 7. Check that "Legacy tier message is out of range" message appears.
	 * 8. Fill "Legacy Tier" field with correct value
	 * 9. Check that user is able to proceed.
	 */
	protected void testPolicyRmeLegacyTier() {
		mainApp().open();
		createCustomerIndividual();

		customer.initiateRenewalEntry().start();


		initiateRenewalEntryActionTab.fillTab(getManualConversionInitiationTd()
				.mask(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(),
						CustomerMetaData.InitiateRenewalEntryActionTab.LEGACY_TIER.getLabel())));

		//Verify "Legacy Tier" Text Box is exist on RME screen
		assertThat(initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER)).isPresent();

		initiateRenewalEntryActionTab.submitTab();
		assertThat(initiateRenewalEntryActionTab.getAssetList().getWarning(CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER)).hasValue(LEGACY_TIER_REQUIRED_MESSAGE);

		checkLegacyTierIsOutOfRangeErrorMessage("a");
		checkLegacyTierIsOutOfRangeErrorMessage("$");
		checkLegacyTierIsOutOfRangeErrorMessage("-1");
		checkLegacyTierIsOutOfRangeErrorMessage("51");

		initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER).setValue("50");
		initiateRenewalEntryActionTab.submitTab();

		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get());


	}

	protected void testPolicyLegacyTierMapping(int tierValue) {

		Range<String> rangeMarketTier = Range.between("A", "J");
		String propertyInfoMessage = "* Market Tier may be a locked value from prior term";
		String marketTier = "Market tier *";



	TestData policyTd = getConversionPolicyDefaultTD();
		PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

		mainApp().open();
		createCustomerIndividual();

		customer.initiateRenewalEntry().start();

		initiateRenewalEntryActionTab.fillTab(getManualConversionInitiationTd()
				.mask(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(),
						CustomerMetaData.InitiateRenewalEntryActionTab.LEGACY_TIER.getLabel())));

		initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER).setValue(String.valueOf(tierValue));
		initiateRenewalEntryActionTab.submitTab();

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get());

		getPolicyType().get().getDefaultView().fillUpTo(policyTd, ProductOfferingTab.class,true);
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();

		PropertyQuoteTab.RatingDetailsView.open();

		// Market Tier is in range of A-J. Validate  Market Tier  & property Info Msg and Save Market Tier Value
		assertThat(rangeMarketTier.contains(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey(marketTier))).isTrue();
		String marketTierValue = PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey(marketTier);
		assertThat(PropertyQuoteTab.RatingDetailsView.propertyInfoMessage.getValue()).contains(propertyInfoMessage);

		PropertyQuoteTab.RatingDetailsView.close();
		premiumsAndCoveragesQuoteTab.submitTab();

		//bind the policy
		policy.getDefaultView().fillFromTo(policyTd, MortgageesTab.class, BindTab.class, true)
				.getTab(BindTab.class).btnPurchase.click();
		Page.dialogConfirmation.confirm();


		//Activate Renewal
		activateRenewal();

		//InitiateSecondRenewal
		initiateRenewal();

		//View Second Renewal view rating details
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();

		PropertyQuoteTab.RatingDetailsView.open();

		// Validate that Tier is not getting changed at 2R.
		assertThat(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey(marketTier)).isEqualTo(marketTierValue);

		PropertyQuoteTab.RatingDetailsView.close();
		mainApp().close();



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



	private void checkLegacyTierIsOutOfRangeErrorMessage(String value) {
		initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER).setValue(value);
		initiateRenewalEntryActionTab.submitTab();
		assertThat(initiateRenewalEntryActionTab.getAssetList().getWarning(CustomerMetaData
				.InitiateRenewalEntryActionTab.LEGACY_TIER)).hasValue(LEGACY_TIER_IS_OUT_OF_RANGE_MESSAGE);
	}
}