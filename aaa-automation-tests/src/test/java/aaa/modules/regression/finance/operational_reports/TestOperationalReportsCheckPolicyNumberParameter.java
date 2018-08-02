package aaa.modules.regression.finance.operational_reports;

import aaa.admin.metadata.reports.OperationalReportsMetaData;
import aaa.admin.modules.reports.operationalreports.defaulttabs.OperationalReportsTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.BaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.TextBox;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestOperationalReportsCheckPolicyNumberParameter extends BaseTest {

    private OperationalReportsTab orTab = new OperationalReportsTab();
    private static String policyNumber = "AZ" + RandomStringUtils.randomNumeric(11);

    /**
     * @author Reda Kazlauskiene
     * Objectives: check that "Policy Number” in "View Earn, Unearned,
     Written Premium (EUW) - Detail" report is displayed and enabled
     * TC Steps:
     * 1. Navigate Reports -> Operational Reports;
     * 2. Fill Category, Type, Name;
     * 3. Check that "Policy Number” is displayed and enabled, free format policy number can be entered
     */

    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Finance.OPERATIONAL_REPORTS, testCaseId = "16020")
    public void pas16020_testOperationalReportsCheckPolicyNumberParameter() {

        opReportApp().open();

        orTab.fillTab(testDataManager.getDefault(TestOperationalReportsCheckDeliveryTypeForEuwDetail.class).getTestData("TestData"));

        //Check that "Policy Number” field is displayed, enabled and text can be entered;
        TextBox policyNumberField = orTab.getAssetList().getAsset(OperationalReportsMetaData.OperationalReportsTab.POLICY_NUMBER);
        policyNumberField.setValue(policyNumber);

        assertSoftly(softly -> {
            softly.assertThat(policyNumberField).isPresent();
            softly.assertThat(policyNumberField).isEnabled();
            softly.assertThat(policyNumberField).hasValue(policyNumber);
        });
    }
}
