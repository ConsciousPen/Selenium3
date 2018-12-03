package aaa.modules.regression.finance.operational_reports;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Test;
import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.OperationalReportsConstants;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestOperationalReportsCheckPermissionToAccessEuwDetail extends BaseTest {

	private OperationalReportsTab orTab = new OperationalReportsTab();

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
	 * when L41 privelege selected
	 * TC Steps:
	 * 1. Login app with L41 privelege
	 * 2. Navigate Reports -> Operational Reports;
	 * 3. Fill Category, Type
	 * 4. Check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-17040")
	public void pas17040_testOperationalReportsCheckPermissionToAccessEuwDetail_L41() {
		TestData loginTD = getLoginTD()
				.adjust("Groups", "L41")
				.adjust("User", "g60land")
				.adjust("Password", "g60land");
		opReportApp().open(loginTD);

		orTab.fillTab(getOperationalReportsTD("DataGather", "TestData"));
		assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.NAME))
				.containsOption(OperationalReportsConstants.EUW_DETAIL);
	}

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
	 * when C32 privelege selected
	 * TC Steps:
	 * 1. Login app with C32 privelege
	 * 2. Navigate Reports -> Operational Reports;
	 * 3. Fill Category, Type
	 * 4. Check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-17040")
	public void pas17040_testOperationalReportsCheckPermissionToAccessEuwDetail_C32() {
		TestData loginTD = getLoginTD()
				.adjust("Groups", "C32")
				.adjust("User", "gac9syl")
				.adjust("Password", "gac9syl");
		opReportApp().open(loginTD);

		orTab.fillTab(getOperationalReportsTD("DataGather", "TestData"));
		assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.NAME))
				.containsOption(OperationalReportsConstants.EUW_DETAIL);
	}

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check "View Earn, Unearned, Written Premium (EUW) - Detail" not displayed in the drop down section
	 * when other (non L41/C32) privelege selected
	 * TC Steps:
	 * 1. Login app with Role E34
	 * 2. Navigate Reports -> Operational Reports;
	 * 3. Fill Category, Type
	 * 4. Check "View Earn, Unearned, Written Premium (EUW) - Detail" not displayed in the drop down section
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-17040")
	public void pas17040_testOperationalReportsCheckPermissionToAccessEuwDetail_E34() {
		TestData loginTD = getLoginTD()
				.adjust("Groups", "E34")
				.adjust("User", "gac9syl")
				.adjust("Password", "gac9syl");

		opReportApp().open(loginTD);

		orTab.fillTab(getOperationalReportsTD("DataGather", "TestData"));

		assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.NAME))
				.doesNotContainOption(OperationalReportsConstants.EUW_DETAIL);
	}
}
