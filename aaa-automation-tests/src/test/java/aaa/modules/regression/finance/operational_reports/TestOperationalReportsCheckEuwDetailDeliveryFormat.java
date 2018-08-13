package aaa.modules.regression.finance.operational_reports;

import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.BaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestOperationalReportsCheckEuwDetailDeliveryFormat extends BaseTest {

    private OperationalReportsTab orTab = new OperationalReportsTab();

    /**
     * @author Maksim Piatrouski
     * Objectives: check following Report formats are presented and enabled: 'Excel', 'PDF', 'CSV'
     * TC Steps:
     * 1. Navigate Reports -> Operational Reports;
     * 2. Fill Category, Type, Name;
     * 3. Check "Delivery Format" options
     */

    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-15537")
    public void pas15537_testOperationalReportsCheckEuwDetailDeliveryFormat() {
        opReportApp().open();

        orTab.fillTab(getTestSpecificTD("TestData"));

        assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.REPORT_FORMAT))
                .containsAllOptions(getTestSpecificTD("TestData_Check").getList("ReportFormats"));
    }
}
