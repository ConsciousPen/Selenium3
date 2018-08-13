package aaa.modules.regression.finance.operational_reports;

import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.BaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.CheckBox;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestOperationalReportsCheckEuwDetailScheduleReportParameters extends BaseTest {

    private OperationalReportsTab orTab = new OperationalReportsTab();

    /**
     * @author Maksim Piatrouski
     * Objectives : Check "Do you want to schedule?" checkbox and controls in "Scheduling Parameters" section
     * TC Steps:
     * 1. Navigate Reports -> Operational Reports;
     * 2. Fill Category, Type, Name;
     * 3. Check "Do you want to schedule?" checkbox is presented and enabled;
     * 4. Select "Do you want to schedule?" checkbox;
     * 5. Check controls in "Scheduling Parameters" section.
     */

    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-16021")
    public void pas16021_testOperationalReportsCheckEuwDetailScheduleReportParameters() {
        opReportApp().open();
        orTab.fillTab(getTestSpecificTD("TestData"));

        CheckBox schedule = orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.SCHEDULE);

        assertSoftly(softly -> {
                    softly.assertThat(schedule).isPresent();
                    softly.assertThat(schedule).isEnabled();
                });


        schedule.setValue(true);

        assertSoftly(softly -> {
            softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.REPORT_NAME)).isPresent();
            softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.REPORT_NAME)).isEnabled();

            softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.SCHEDULE_TYPE)).isPresent();
            softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.SCHEDULE_TYPE)).isEnabled();

            softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.SCHEDULE_INTERVAL)).isPresent();
            softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.SCHEDULE_INTERVAL)).isEnabled();

            softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.SCHEDULE_INTERVAL_TYPE)).isPresent();
            softly.assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.SCHEDULE_INTERVAL_TYPE)).isEnabled();
        });
    }
}
