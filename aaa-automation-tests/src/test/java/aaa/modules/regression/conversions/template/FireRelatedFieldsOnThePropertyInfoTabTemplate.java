package aaa.modules.regression.conversions.template;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

public class FireRelatedFieldsOnThePropertyInfoTabTemplate extends PolicyBaseTest {

	private static final String FIRE_DEPARTMENT_TYPE = "S - Subscription based";
	private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();

	public void verifyFireRelatedFieldsOnThePropertyInfoTab() {
		mainApp().open();
		createCustomerIndividual();

		//Initiate Renewal manual entry
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());

		//Fill Quote
		policy.getDefaultView().fillUpTo(getConversionPolicyDefaultTD(), PropertyInfoTab.class, true);

		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_DEPARTMENT_TYPE).setValueContains(FIRE_DEPARTMENT_TYPE);

		//Check Public protection class (PPC) fields
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_DEPARTMENT_TYPE)).isEnabled();
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS)).isEnabled();
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.DISTANCE_TO_FIRE_HYDRANT)).isEnabled();
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_PROTECTION_AREA)).isEnabled();
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.SUBSCRIPTION_TO_FIRE_DEPARTMENT_STATION)).isEnabled();
	}

	public void verifyFireRelatedFieldsOnThePropertyInfoTabSecondRenewal() {
		mainApp().open();
		createCustomerIndividual();

		String policyNumber = createConversionPolicy();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate().plusYears(1);
		LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);

		activeFirstRenewal(renewImageGenDate, policyExpirationDate, policyNumber);
		initiateSecondRenewal(policyExpirationDate, policyNumber);

		if (PolicySummaryPage.buttonRenewals.isPresent()) {
			PolicySummaryPage.buttonRenewals.click();
		}
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());

		//Check Public protection class (PPC) fields
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_DEPARTMENT_TYPE)).isDisabled();
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS)).isEnabled(true);
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.DISTANCE_TO_FIRE_HYDRANT)).isDisabled();
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_PROTECTION_AREA)).isDisabled();
	}

	private void activeFirstRenewal(LocalDateTime renewImageGenDate, LocalDateTime policyExpirationDate, String policyNumber) {
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);

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

	private void initiateSecondRenewal(LocalDateTime policyExpirationDate, String policyNumber) {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

}
