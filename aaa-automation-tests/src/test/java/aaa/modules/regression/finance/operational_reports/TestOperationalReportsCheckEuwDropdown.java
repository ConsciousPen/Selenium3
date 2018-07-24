package aaa.modules.regression.finance.operational_reports;

import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.BaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestOperationalReportsCheckEuwDropdown extends BaseTest {

    private OperationalReportsTab orTab = new OperationalReportsTab();

    /**
     * @author Maksim Piatrouski
     * Objectives : check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
     * TC Steps:
     * 1. Navigate Reports -> Operational Reports;
     * 2. Fill Category, Type
     * 3. check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
     */

    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS)
    public void pas16695_testOperationalReportsCheckEuwDropdown() {
        opReportApp().open();
        orTab.fillTab(getTestSpecificTD("TestData"));
        assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.NAME))
                .containsOption("View Earn, Unearned, Written Premium (EUW) - Detail");

    }
}
