package aaa.modules.regression.finance.operational_reports;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Test;
import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.OperationalReportsConstants;
import aaa.modules.BaseTest;
import toolkit.utils.TestInfo;

public class TestOperationalReportsCheckEuwDetailDropdown extends BaseTest {

	private OperationalReportsTab orTab = new OperationalReportsTab();

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
	 * TC Steps:
	 * 1. Navigate Reports -> Operational Reports;
	 * 2. Fill Category, Type
	 * 3. Check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-14523")
	public void pas14523_testOperationalReportsCheckEuwDropdown() {
		opReportApp().open();

		orTab.fillTab(getOperationalReportsTD("DataGather", "TestData"));

		assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.NAME))
				.containsOption(OperationalReportsConstants.EUW_DETAIL);

	}
}
