package aaa.modules.regression.finance.operational_reports;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Test;
import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.BaseTest;
import toolkit.utils.TestInfo;

public class TestOperationalReportsEuwSummaryCheckPolicyNumberParameter extends BaseTest {

	private OperationalReportsTab orTab = new OperationalReportsTab();

	/**
	 * @author Maksim Piatrouski
	 * Objectives: check that "Policy Number” in "Earned/Unearned/Written (EUW) Report – Summary" report is NOT displayed
	 * TC Steps:
	 * 1. Navigate Reports -> Operational Reports;
	 * 2. Fill Category, Type, Name;
	 * 3. Check that "Policy Number” is NOT displayed
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-17631")
	public void pas17631_testOperationalReportsEuwSummaryCheckPolicyNumberParameter() {
		opReportApp().open();

		orTab.fillTab(getOperationalReportsTD("DataGather", "TestData_EUW_Summary"));
		assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.POLICY_NUMBER)).isPresent(false);
	}
}
