/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.docgen.home_ss.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.modules.BaseTest;
import aaa.modules.conversion.manual.ConvHomeSsDP3BaseTest;
import aaa.modules.conversion.manual.ManualConversionHelper;
import aaa.modules.docgen.template.functional.TestMaigConversionAbstract;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMaigConversion extends ConvHomeSsDP3BaseTest implements TestMaigConversionAbstract {

	private final Tab bindTab = new BindTab();

	@Override
	public ManualConversionHelper getManualConversionHelper() {
		return this;
	}

	@Override
	public BaseTest getBaseTest() {
		return this;
	}

	@Override
	public Tab getBindTab() {
		return bindTab;
	}

	@Override
	public void initiateManualConversionForTest() {
		initiateManualConversion(getManualConversionInitiationTd());
	}

	@Override
	public IPolicy getPolicy() {
		return policy;
	}

	@Override
	public TestData getPolicyTD() {
		return super.getPolicyTD();
	}

	@Override
	public PolicyType getPolicyType() {
		return super.getPolicyType();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-2305"})
	public void pas2305_preRenewalLetterHSPRNXX(@Optional("AZ") String state) {
		pas2305_preRenewalLetterHSPRNXXProductSpecific(state);
	}

}
