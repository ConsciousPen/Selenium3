package aaa.modules.regression.finance.operational_reports;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Test;
import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.BaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.TextBox;

public class TestOperationalReportsCheckEuwDetailAsOfAccountingDate extends BaseTest {

	private OperationalReportsTab orTab = new OperationalReportsTab();

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
	 * TC Steps:
	 * 1. Navigate Reports -> Operational Reports;
	 * 2. Fill Category, Type, Name;
	 * 3. Check "As of Accounting Date” is displayed and enabled, has value equal to the sub ledger account date
	 */

	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-15534")
	public void pas15534_testOperationalReportsCheckAsOfAccountingDate() {
		opReportApp().open();

		orTab.fillTab(getOperationalReportsTD("DataGather", "TestData_EUW_Detail"));

		//Check "As of Accounting Date” is displayed and enabled;
		TextBox asOfAccountingDate = orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.AS_OF_ACCOUNTING_DATE);

		assertSoftly(softly -> {
			softly.assertThat(asOfAccountingDate).isPresent();
			softly.assertThat(asOfAccountingDate).isEnabled();
			softly.assertThat(asOfAccountingDate).hasValue(getTestSpecificTD("TestData_Check").getValue("AsOfDate"));
		});
	}
}
