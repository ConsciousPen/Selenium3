package base.modules.platform.admin.reports.operationalreports;

import org.testng.annotations.Test;

import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.IOperationalReport;
import aaa.admin.modules.reports.operationalreports.OperationalReportType;
import aaa.admin.pages.reports.OperationalReportSummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Andrey Shashenka
 * @name Test for Operational Report scheduling
 * @scenario
 * 1. Open Operational Reports
 * 2. Fill operational report mandatory fields
 * 3. Set 'Do you want to schedule?' = true
 * 4. Fill scheduling fields
 * 5. Schedule report
 * 6. Verify that new trigger added
 * @details
 */

public class TestOperationalReportScheduling extends BaseTest {

    private OperationalReportType reportType = OperationalReportType.OPERATIONAL_REPORT;
    private IOperationalReport report = reportType.get();
    private TestData tdReport = testDataManager.operationalReports.get(reportType);

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testOperationalReportScheduling() {

        opReportApp().open();

        String reportName = tdReport.getValue("DataGather", "TestData_Schedule",
                OperationalReportsMetaData.OperationalReportsTab.class.getSimpleName(),
                OperationalReportsMetaData.OperationalReportsTab.REPORT_NAME.getLabel());

        report.schedule(tdReport.getTestData("DataGather", "TestData_Schedule"));

        OperationalReportSummaryPage.tableTriggers.getRow(1, reportName).verify.present("Trigger is not added as expected.");
    }

}
