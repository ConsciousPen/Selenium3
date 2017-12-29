/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.conversion.manual.home_ss.dp3;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.conversion.manual.ConvHomeSsDP3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestConversionNoClueISO360InspectionReports extends ConvHomeSsDP3BaseTest {

	/**
	 * @author Dominykas Razgunas
	 * @name
	 * @scenario
	 * 1. Create Conversion Policy
	 * 2. Propose Policy without ordering Inspection Report, CLUE, ISO360 reports
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-7211")
	public void pas7211_ConversionNoClueISO360InspectionReports(@Optional("DE") String state) {

		String reportTabInfo = new ReportsTab().getMetaKey();

		TestData testdata = getConversionPolicyDefaultTD();

		TestData reportTab = testdata.getTestData(reportTabInfo);
		testdata.adjust(reportTabInfo, reportTab);

		// If Membership report works remove mask for it and associated test data
		reportTab.mask(HomeSSMetaData.ReportsTab.ISO360_REPORT.getLabel(), HomeSSMetaData.ReportsTab.CLUE_REPORT.getLabel());

		mainApp().open();

		createConversionPolicy(testdata);

	}
}


