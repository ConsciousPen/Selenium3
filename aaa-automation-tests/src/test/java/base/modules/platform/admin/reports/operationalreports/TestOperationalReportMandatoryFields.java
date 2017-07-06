package base.modules.platform.admin.reports.operationalreports;

import org.testng.annotations.Test;

import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.IOperationalReport;
import aaa.admin.modules.reports.operationalreports.OperationalReportType;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.modules.BaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Andrey Shashenka
 * @name Test for Operational Report mandatory fields
 * @scenario
 * 1. Open Operational Reports
 * 2. Select first available category
 * 3. Select first available type
 * 4. Select first available name
 * 5. Delete Transaction Date From and Transaction Date To
 * 6. Check Transaction Dates error messages
 * 7. Set Transaction Date From and Transaction Date To values to string
 * 8. Check Transaction Dates error messages
 * 9. Check error message when Transaction Date From is later than Transaction Date To
 * 10. Check if Transaction Date From and Transaction Date To pop-ups opens and closes
 * 11. Checks Report button deactivation when both Type and Name values are empty
 * 12. Checks Report button deactivation when Name value is empty
 * 13. Select Delivery Type to E-Mail and leaves fields E-mail Address and Additional E-mail Address empty
 * 14. Check fields E-mail Address and Additional E-mail Address error message
 * 15. Select scheduling and Delivery Type sets to Download
 * 16. Checks error message that delivery type Download is not supported for scheduled reports
 * @details
 */

public class TestOperationalReportMandatoryFields extends BaseTest {

    private OperationalReportType reportType = OperationalReportType.OPERATIONAL_REPORT;
    private IOperationalReport report = reportType.get();

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testOperationalReportMandatoryFields() {

        opReportApp().open();

        report.create(tdSpecific.getTestData("TestData_EmptyDates"));
        validateEmptyDates();

        report.create(tdSpecific.getTestData("TestData_IncorrectDates"));
        validateIncorrectDatesFormat();

        report.create(tdSpecific.getTestData("TestData_IncorrectDatesRange"));
        validateIncorrectDatesRange();

        report.getDefaultView().fill(tdSpecific.getTestData("TestData_EmptyNameAndType"));
        OperationalReportsTab.buttonGenerateReport.verify.enabled(false);

        report.create(tdSpecific.getTestData("TestData_EmptyEmails"));
        validateEmptyEmails();

        report.getDefaultView().fill(tdSpecific.getTestData("TestData_ScheduleDownload"));
        OperationalReportsTab.buttonSchedule.click();
        validateDownloadSchedule();
    }

    private void validateEmptyDates() {
        CustomAssert.enableSoftMode();

        report.getDefaultView().getTab(OperationalReportsTab.class).getAssetList().getWarning(
                OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_FROM.getLabel()).verify.value(
                String.format("Incorrect error message for %s element",
                        OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_FROM.getLabel()),
                "Field is required");
        report.getDefaultView().getTab(OperationalReportsTab.class).getAssetList().getWarning(
                OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_TO.getLabel()).verify.value(
                String.format("Incorrect error message for %s element",
                        OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_TO.getLabel()),
                "Field is required");

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }

    private void validateIncorrectDatesFormat() {
        CustomAssert.enableSoftMode();

        report.getDefaultView().getTab(OperationalReportsTab.class).getAssetList().getWarning(
                OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_FROM.getLabel()).verify.value(
                String.format("Incorrect error message for %s element",
                        OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_FROM.getLabel()),
                "Date format should be in MM/dd/yyyy");
        report.getDefaultView().getTab(OperationalReportsTab.class).getAssetList().getWarning(
                OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_TO.getLabel()).verify.value(
                String.format("Incorrect error message for %s element",
                        OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_TO.getLabel()),
                "Date format should be in MM/dd/yyyy");

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }

    private void validateIncorrectDatesRange() {
        report.getDefaultView().getTab(OperationalReportsTab.class).getAssetList().getWarning(
                OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_TO.getLabel()).verify.value(
                String.format("Incorrect error message for %s element",
                        OperationalReportsMetaData.OperationalReportsTab.ACTIVITY_DATE_TO.getLabel()),
                "Activity end date should not be less than start date");
    }

    private void validateEmptyEmails() {
        CustomAssert.enableSoftMode();

        report.getDefaultView().getTab(OperationalReportsTab.class).getAssetList().getWarning(
                OperationalReportsMetaData.OperationalReportsTab.EMAIL_ADDRESS.getLabel()).verify.value(
                String.format("Incorrect error message for %s element",
                        OperationalReportsMetaData.OperationalReportsTab.EMAIL_ADDRESS.getLabel()),
                "Field is required");
        report.getDefaultView().getTab(OperationalReportsTab.class).getAssetList().getWarning(
                OperationalReportsMetaData.OperationalReportsTab.ADDITIONAL_EMAIL.getLabel()).verify.value(
                String.format("Incorrect error message for %s element",
                        OperationalReportsMetaData.OperationalReportsTab.ADDITIONAL_EMAIL.getLabel()),
                "Field is required");

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }

    private void validateDownloadSchedule() {
        CustomAssert.enableSoftMode();

        report.getDefaultView().getTab(OperationalReportsTab.class).getAssetList().getWarning(
                OperationalReportsMetaData.OperationalReportsTab.DELIVERY_TYPE.getLabel()).verify.value(
                String.format("Incorrect error message for %s element",
                        OperationalReportsMetaData.OperationalReportsTab.DELIVERY_TYPE.getLabel()),
                "\"Not supported\"");
        report.getDefaultView().getTab(OperationalReportsTab.class).getAssetList().getWarning(
                OperationalReportsMetaData.OperationalReportsTab.REPORT_NAME.getLabel()).verify.value(
                String.format("Incorrect error message for %s element",
                        OperationalReportsMetaData.OperationalReportsTab.REPORT_NAME.getLabel()),
                "Field is required");

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();
    }
}
