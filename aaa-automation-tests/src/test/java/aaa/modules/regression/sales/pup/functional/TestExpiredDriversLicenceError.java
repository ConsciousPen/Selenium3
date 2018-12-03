/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.pup.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;

public class TestExpiredDriversLicenceError extends PersonalUmbrellaBaseTest {

	private ErrorTab errorTab = policy.getDefaultView().getTab(ErrorTab.class);
	private UnderlyingRisksAutoTab underlyingRisksAutoTab = new UnderlyingRisksAutoTab();
	private PremiumAndCoveragesQuoteTab premiumAndCoveragesQuoteTab = new PremiumAndCoveragesQuoteTab();


	/**
	 * @author Dominykas Razgunas
	 * @name Expired Driver Licence Error
	 * @scenario
	 * 1. Create PUP Policy
	 * 2. Add Home Policy with PPC = 8
	 * 3. Add Driver with expired licence
	 * 4. Calculate Premium
	 * 5. Verify Error
	 * 6. Navigate to change driver with valid licence status
	 * 7. Calculate Premium
	 * 8. Submit Premium and Coverages Page
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-4270")
	public void pas4270_VerifyExpiredDriversLicenceError(@Optional("VA") String state) {

		mainApp().open();
		createCustomerIndividual(getTdCustomer());

		PolicyType.HOME_SS_HO3.get().createPolicy(getTdHome());

		// Create Test Data
		TestData tdOtherActive = getTestSpecificTD("TestData_ActiveUnderlyingPolicies")
				.adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy Number"), PolicySummaryPage.getPolicyNumber());
		TestData tdPUP = getPolicyTD()
				.adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel()), tdOtherActive)
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.class.getSimpleName()), getTestSpecificTD("TestData_UnderlyingAuto"));


		PolicyType.PUP.get().initiate();
		policy.getDefaultView().fillUpTo(tdPUP, PremiumAndCoveragesQuoteTab.class);
		premiumAndCoveragesQuoteTab.calculatePremium();

		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_PUP_SS2260177);
		errorTab.cancel();
		assertThat(premiumAndCoveragesQuoteTab.getAssetList().getAsset("Payment Plan")).isPresent();

		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
		NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());

		underlyingRisksAutoTab.getAssetList().getAsset("Drivers", MultiInstanceAfterAssetList.class).getAsset("License Status", ComboBox.class).setValue("Licensed (US)");

		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());

		premiumAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumAndCoveragesQuoteTab.getAssetList().getAsset("Payment Plan")).isPresent();

	}


	// TD for home policy
	private TestData getTdHome() {
		return getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather"), "TestData_VA")
				.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel()), "8")
				.mask(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()));
	}

	// TD for Customer
	private TestData getTdCustomer() {
		return getCustomerIndividualTD("DataGather", "TestData_VA")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()), "22205")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ADDRESS_LINE_1.getLabel()), "2934 BASELINE RD");
	}





}


