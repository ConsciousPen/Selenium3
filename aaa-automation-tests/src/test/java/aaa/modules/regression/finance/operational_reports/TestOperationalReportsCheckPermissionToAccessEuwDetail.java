package aaa.modules.regression.finance.operational_reports;

import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.OperationalReportsConstants;
import aaa.modules.BaseTest;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestOperationalReportsCheckPermissionToAccessEuwDetail extends BaseTest {

    private OperationalReportsTab orTab = new OperationalReportsTab();

    /**
     * @author Maksim Piatrouski
     * Objectives : check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
     * when L41 or C32 privelege selected
     * TC Steps:
     * 1. Login app with L41/C32 privelege (Role C32)
     * 2. Navigate Reports -> Operational Reports;
     * 3. Fill Category, Type
     * 4. Check "View Earn, Unearned, Written Premium (EUW) - Detail" displayed in the drop down section
     */

    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-17040")
    public void pas17040_testOperationalReportsCheckPermissionToAccessEuwDetail_C32() {
        TestData loginTD = initiateLoginTD().adjust("Groups", "C32");
        opReportApp().open(loginTD);

        orTab.fillTab(getTestSpecificTD("TestData"));
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

    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "PAS-17040")
    public void pas17040_testOperationalReportsCheckPermissionToAccessEuwDetail_E34() {
        TestData loginTD = initiateLoginTD().adjust("Groups", "E34");
        opReportApp().open(loginTD);

        orTab.fillTab(getTestSpecificTD("TestData"));
        assertThat(orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.NAME))
                .doesNotContainOption(OperationalReportsConstants.EUW_DETAIL);
    }
}
