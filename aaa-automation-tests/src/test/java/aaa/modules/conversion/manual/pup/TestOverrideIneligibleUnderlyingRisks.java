/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.conversion.manual.pup;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.modules.conversion.manual.ConvPUPBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestOverrideIneligibleUnderlyingRisks extends ConvPUPBaseTest {

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


		createCustomerIndividual();

		TestData testdata = getConversionPolicyDefaultTD();

		testdata.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]",
				PersonalUmbrellaMetaData.PrefillTab.NamedInsured.LLC.getLabel()), "Yes")
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]",
						PersonalUmbrellaMetaData.PrefillTab.NamedInsured.TRUSTEE.getLabel()), "Yes");

		initiateManualConversion();
		policy.getDefaultView().fillUpTo(testdata, BindTab.class);
		new BindTab().saveAndExit();


		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_PUP_SS7160072);
		errorTab.overrideAllErrors();
		errorTab.override();
		policy.getDefaultView().fillFromTo(testdata, BindTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();
	}
}


