/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.conversions.home_ss.ho4.functional;


import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO4BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestConversionNoClueISO360InspectionReports extends HomeSSHO4BaseTest {

	/**
	 * @author Dominykas Razgunas
	 * @name Home Conversion with no Clue and ISO360 ordered
	 * @scenario
	 * 1. Create Conversion Policy
	 * 2. Propose Policy without ordering Inspection Report, CLUE, ISO360 reports
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-7211")
	public void pas7211_ConversionNoClueISO360InspectionReports(@Optional("") String state) {

		String reportTabInfo = new ReportsTab().getMetaKey();

		TestData testdata = getConversionPolicyDefaultTD();

		TestData reportTab = testdata.getTestData(reportTabInfo);
		testdata.adjust(reportTabInfo, reportTab);

		reportTab.mask(HomeSSMetaData.ReportsTab.ISO360_REPORT.getLabel(), HomeSSMetaData.ReportsTab.CLUE_REPORT.getLabel())
				.removeAdjustment(HomeSSMetaData.ReportsTab.INSURANCE_SCORE_REPORT.getLabel());

		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		getPolicyType().get().getDefaultView().fill(testdata);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}


