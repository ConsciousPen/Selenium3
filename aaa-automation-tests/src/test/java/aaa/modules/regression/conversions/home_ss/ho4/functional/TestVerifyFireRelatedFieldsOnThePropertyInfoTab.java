package aaa.modules.regression.conversions.home_ss.ho4.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO4BaseTest;
import toolkit.utils.TestInfo;

public class TestVerifyFireRelatedFieldsOnThePropertyInfoTab extends HomeSSHO4BaseTest {

	PropertyInfoTab propertyInfoTab = new PropertyInfoTab();

	private static final String FIRE_DEPARTMENT_TYPE = "S - Subscription based";
	private static final String PUBLIC_PROTECTION_CLASS = "10W";
	private static final String DISTANCE_TO_FIRE_HYDRANT = ">1000ft";
	private static final String FIRE_PROTECTION_AREA = "Pennsylvania";

	private static String policyNumber;
	private static LocalDateTime policyEffectiveDate;
	private static LocalDateTime policyExpirationDate;
	private static LocalDateTime renewImageGenDate;

	/**
	 * @author R. Kazlauskiene
	 * @name Test Verify fire related fields on the Property info tab
	 * @scenario
	 * 1. Create Individual Customer / Account
	 * 2. Create converted SS home policy
	 * 3. Navigate to Property Information tab > Public protection class (PPC)
	 * 4. Fields should be editable and enabled:
	 * Fire department type
	 * Distance to fire hydrant
	 * Fire protection area
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO4, testCaseId = "PAS-10703")
	public void testVerifyFireRelatedFieldsOnThePropertyInfoTab(@Optional("NJ") String state) {

		mainApp().open();
		createCustomerIndividual();

		//Initiate Renewal manual entry
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());

		//Fill Quote
		policy.getDefaultView().fillUpTo(getConversionPolicyDefaultTD(), PropertyInfoTab.class, true);

		//Check Public protection class (PPC) fields
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_DEPARTMENT_TYPE).setValueContains(FIRE_DEPARTMENT_TYPE);
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.SUBSCRIPTION_TO_FIRE_DEPARTMENT_STATION).setValue("Yes");
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS).setValueContains(PUBLIC_PROTECTION_CLASS);
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.DISTANCE_TO_FIRE_HYDRANT).setValueContains(DISTANCE_TO_FIRE_HYDRANT);
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_PROTECTION_AREA).setValue(FIRE_PROTECTION_AREA);

// Ignored due to https://csaaig.atlassian.net/browse/PAS-12159
//		propertyInfoTab.saveAndExit();
//		policy.dataGather().start();
//		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
//
//		//Check Public protection class (PPC) fields
//		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
//				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_DEPARTMENT_TYPE.getLabel())).equals(FIRE_DEPARTMENT_TYPE);
//		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
//						.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.SUBSCRIPTION_TO_FIRE_DEPARTMENT_STATION.getLabel())).equals("Yes");
//		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
//				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel())).equals(PUBLIC_PROTECTION_CLASS);
//		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
//				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.DISTANCE_TO_FIRE_HYDRANT.getLabel())).equals(DISTANCE_TO_FIRE_HYDRANT);
//		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
//				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_PROTECTION_AREA.getLabel())).equals(FIRE_PROTECTION_AREA);
	}

	/**
	 * @author R. Kazlauskiene
	 * @name Test Verify fire related fields on the Property info tab - Second Renewal
	 * @scenario
	 * 1. Create Individual Customer / Account
	 * 2. Create converted SS home policy
	 * 3. Initiate the Second Renewal
	 * 4. Navigate to Property Information tab > Public protection class (PPC)
	 * 5. Fields should be disabled:
	 * Fire department type
	 * Distance to fire hydrant
	 * Fire protection area
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO4, testCaseId = "PAS-10703")
	public void testVerifyFireRelatedFieldsOnThePropertyInfoTabSecondRenewal(@Optional("PA") String state) {

		mainApp().open();
		createCustomerIndividual();

		policyNumber = createConversionPolicy();

		activeFirstRenewal();
		initiateSecondRenewal();

		if (PolicySummaryPage.buttonRenewals.isPresent()) {
			PolicySummaryPage.buttonRenewals.click();
		}
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());

		//Check Public protection class (PPC) fields
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_DEPARTMENT_TYPE)).isEnabled();
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS)).isEnabled(true);
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.DISTANCE_TO_FIRE_HYDRANT)).isEnabled();
		assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS)
				.getAsset(HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.FIRE_PROTECTION_AREA)).isEnabled();
	}

	private void activeFirstRenewal() {
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyEffectiveDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate);

		mainApp().reopen();
		SearchPage.openBilling(policyNumber);
		Dollar totDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies
				.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM, policyNumber)
				.getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount
				.getTestData("AcceptPayment", "TestData_Cash"), totDue);
	}

	private void initiateSecondRenewal() {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}
}
