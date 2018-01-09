/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.conversion.manual.pup;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.conversion.manual.ConvPUPBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestOverrideLiabilityLosses extends ConvPUPBaseTest {

	private ErrorTab errorTab = policy.getDefaultView().getTab(ErrorTab.class);

	/**
	 * @author Dominykas Razgunas
	 * @name
	 * @scenario
	 * 1. Create Conversion PUP Policy
	 * 2. Propose Policy
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "")
	public void pas6977_ConversionMAIGpup(@Optional("NJ") String state) {

		mainApp().open();
//		createCustomerIndividual();
//
//     // Create Underlying policy
//		PolicyType.HOME_SS_HO3.get().createPolicy(getTdHome());
//		String HoPolicyNumber = PolicySummaryPage.getPolicyNumber();

		TestData testdata =  getPupConversionTdNoPolicyCreation();

		testdata.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel() + "[0]",
				PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_NUMBER.getLabel()), "NJH3926232034")
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.ClaimsTab.class.getSimpleName()), getTestSpecificTD("TestData_ClaimsTab"))
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.class.getSimpleName()), getTestSpecificTD("TestData_UnderlyingAuto"));



		initiateManualConversion(getManualConversionInitiationTd().adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_EFFECTIVE_DATE.getLabel()), "$<today+30d>"));
		policy.getDefaultView().fillUpTo(testdata, BindTab.class);
		new BindTab().submitTab();


		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_PUP_SS7160072);
		errorTab.overrideAllErrors();
		errorTab.override();

		policy.getDefaultView().fillFromTo(testdata, BindTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

	}

	// Create Testdata for an underlying policy
	private TestData getTdHome() {
		return getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather"), "TestData_NJ")
				.mask(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()));
	}


//	private TestData getTdAuto() {
//		return getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData_NJ");
//	}
}


