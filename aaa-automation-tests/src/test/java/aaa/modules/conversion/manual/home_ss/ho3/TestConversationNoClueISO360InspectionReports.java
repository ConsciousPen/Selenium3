/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.conversion.manual.home_ss.ho3;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.conversion.manual.ConvHomeSsHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestConversationNoClueISO360InspectionReports extends ConvHomeSsHO3BaseTest {

	/**
	 * @author Dominykas Razgunas
	 * @name
	 * @scenario
	 * 1. Create Conversion Policy
	 * 2. Fill TestData up to reports Tab
	 * 3. Check that Order Clue Report is disabled
	 * 4. Fill other required info for the Policy
	 * 5. Buy Policy without ordering Inspection Report
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-7211")
	public void pas7211_ConversationNoClueISO360InspectionReports(@Optional("DE") String state) {

		String generalTabInfo = new GeneralTab().getMetaKey();
		String applicantTabInfo = new ApplicantTab().getMetaKey();
		String reportTabInfo = new ReportsTab().getMetaKey();

		TestData testdata = getPolicyTD();

		TestData generalTab = testdata.getTestData(generalTabInfo);
		generalTab.adjust(getTestSpecificTD("TestData").getTestData(generalTabInfo));
		testdata.adjust(generalTabInfo, generalTab);

		TestData applicantTab = testdata.getTestData(applicantTabInfo);
		applicantTab.adjust(getTestSpecificTD("TestData").getTestData(applicantTabInfo));
		testdata.adjust(applicantTabInfo, applicantTab);

		TestData reportTab = testdata.getTestData(reportTabInfo);
		reportTab.adjust(getTestSpecificTD("TestData").getTestData(reportTabInfo));
		testdata.adjust(reportTabInfo, reportTab);

		generalTab.mask("Effective date");

		mainApp().open();

		initiateManualConversion();
		policy.getDefaultView().fillUpTo(testdata, ReportsTab.class, true);

		assertThat(new ReportsTab().tblClueReport.getRow(1).getCell(6).controls.links.getFirst().isEnabled()).isFalse();
		assertThat(new ReportsTab().tblISO360Report.getRow(1).getCell(6).controls.links.getFirst().isEnabled()).isFalse();

		new ReportsTab().submitTab();

		policy.getDefaultView().fillFromTo(testdata, PropertyInfoTab.class, PurchaseTab.class, true);

		new PurchaseTab().submitTab();

	}
}


