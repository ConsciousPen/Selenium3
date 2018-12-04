/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.conversions.pup.functional;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.MD, Constants.States.PA, Constants.States.DE, Constants.States.NJ, Constants.States.VA})
public class TestOverrideIneligibleUnderlyingRisks extends ConvPUPBaseTest {

	private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);

	/**
	 * @author Dominykas Razgunas
	 * @name Allow PAS to override and bind policies for Applicant(s) listed as a trustee, or LLC.  (on prefill) - PUP   Conversion
	 * @scenario
	 * 1. Create Conversion PUP Policy
	 * 2. Select LLC and Trustee as 'Yes'
	 * 3. Fill All tabs until Bind
	 * 4. Override new error
	 * 5. Bind The policy
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6977")
	public void pas6977_OverrideRulesForConversionMAIG(@Optional("NJ") String state) {

		mainApp().open();

		createCustomerIndividual();

		TestData testdata = getConversionPolicyDefaultTD();

		testdata.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]",
				PersonalUmbrellaMetaData.PrefillTab.NamedInsured.LLC.getLabel()), "Yes")
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]",
						PersonalUmbrellaMetaData.PrefillTab.NamedInsured.TRUSTEE.getLabel()), "Yes");

		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), TimeSetterUtil.getInstance().getCurrentTime().plusDays(30));
		policy.getDefaultView().fillUpTo(testdata, BindTab.class, true);
		bindTab.submitTab();
		verifyErrorsAndOverride(ErrorEnum.Errors.ERROR_AAA_PUP_SS7160072);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Allow PAS to override and bind policies for Applicant(s) listed as a trustee, or LLC.  (on prefill) - PUP
	 * @scenario
	 * 1. Create PUP Policy
	 * 2. Select LLC and Trustee as 'Yes'
	 * 3. Fill All tabs until Bind
	 * 4. Override new error
	 * 5. Bind The policy
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6977")
	public void pas6977_OverrideRulesForPup(@Optional("NJ") String state) {

		mainApp().open();

		createCustomerIndividual();

		TestData testdata = getPolicyDefaultTD();

		testdata.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]",
				PersonalUmbrellaMetaData.PrefillTab.NamedInsured.LLC.getLabel()), "Yes")
				.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]",
						PersonalUmbrellaMetaData.PrefillTab.NamedInsured.TRUSTEE.getLabel()), "Yes");

		PolicyType.PUP.get().initiate();
		policy.getDefaultView().fillUpTo(testdata, BindTab.class, true);
		bindTab.submitTab();
        verifyErrorsAndOverride(ErrorEnum.Errors.ERROR_AAA_PUP_SS7160072);
	}
}


