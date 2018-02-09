/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestOverrideLiabilityLosses extends PersonalUmbrellaBaseTest {

	private ErrorTab errorTab = policy.getDefaultView().getTab(ErrorTab.class);
	private PurchaseTab purchaseTab = policy.getDefaultView().getTab(PurchaseTab.class);
	private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);


	/**
	 * @author Dominykas Razgunas
	 * @name Liability losses bind rules can now be overridden
	 * @scenario
	 * 1. Create PUP Policy
	 * 2. Add Liability
	 * 3. Override Bind Error
	 * 4. Issue Policy
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6963")
	public void pas6963_OverrideLiabilityLosses(@Optional("VA") String state) {

		mainApp().open();
		createCustomerIndividual();

		TestData testdata = getPolicyDefaultTD();

		testdata.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.ClaimsTab.class.getSimpleName()), getTestSpecificTD("TestData_ClaimsTab"))
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.class.getSimpleName()), getTestSpecificTD("TestData_UnderlyingAuto"));

		PolicyType.PUP.get().initiate();
		policy.getDefaultView().fillUpTo(testdata, BindTab.class);
		overrideAndBind();

	}

	//Override rule
private void overrideAndBind() {
	bindTab.submitTab();
	errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_PUP_SS1263335);
	errorTab.overrideAllErrors();
	errorTab.override();
	bindTab.submitTab();

	if(!PolicySummaryPage.labelPolicyNumber.isPresent()){
		purchaseTab.fillTab(getPolicyTD());
		purchaseTab.submitTab();
	}
	PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
}


}


