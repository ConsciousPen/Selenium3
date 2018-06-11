package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.common.Tab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @Author - Tyrone Jemison
 */
public class TestCAFairPlanCanItBind extends HomeCaHO3BaseTest {
    static TestData DEFAULTPOLICYDATA;
    static HelperCommon myHelper;

    /**
     * @Scenario - During Quote Fireline returns >= 5. FPCECA Added. Will Bind.
     * @Runtime - 3min
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Update Fireline underwriting eligibility rules to enable Bind with FPCECA/FPCECADP Endorsement HO3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-13214")
    public void AC1_Quote_HighFL_FPCECA_Bind(@Optional("") String state) {
        myHelper = new HelperCommon();
        performTest("ApplicantTab_FL7", "ReportsTab_NoMembership", DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class);
    }

    /**
     * @Scenario - During Quote Fireline returns < 5. FPCECA Added. Will Bind.
     * @Runtime - 2min
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Update Fireline underwriting eligibility rules to enable Bind with FPCECA/FPCECADP Endorsement HO3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-13214")
    public void AC2_Quote_FL3_WoodRoof_Bind(@Optional("") String state) {
        myHelper = new HelperCommon();
        performTest("ApplicantTab_FL3", "ReportsTab_NoMembership", "PropertyInfoTab_RoofWood", DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class);
    }

    /**
     * @Scenario - During Quote Fireline returns zip matched address. FPCECA Added. Will Bind.
     * @Runtime - 2min
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Update Fireline underwriting eligibility rules to enable Bind with FPCECA/FPCECADP Endorsement HO3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-13214")
    public void AC3_Quote_ZipMatch_FPCECA_Bind(@Optional("") String state) {

        performTest("ApplicantTab_ZipMatch", "ReportsTab_NoMembership", "PropertyInfoTab_RoofWood", DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class);
    }

    private void performTest(String applicantTabTD, String reportsTabTD, TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2) {
        myHelper = new HelperCommon();

        // Assemble Test Data
        defaultPolicyData = adjustApplicantAndReportsTD(defaultPolicyData, applicantTabTD, reportsTabTD);

        initiateHO3Quote(defaultPolicyData, tabClassTo1);

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement(getPolicyType().getShortName());

        myHelper.completeFillAndVerifyFAIRPlanSign(defaultPolicyData, tabClassTo1, tabClassTo2, getPolicyType().getShortName());
    }

    private void performTest(String applicantTabTD, String reportsTabTD, String propInfoTD, TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2) {
        myHelper = new HelperCommon();

        // Assemble Test Data
        defaultPolicyData = adjustApplicantAndReportsTD(defaultPolicyData, applicantTabTD, reportsTabTD, propInfoTD);

        initiateHO3Quote(defaultPolicyData, tabClassTo1);

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement(getPolicyType().getShortName());

        myHelper.completeFillAndVerifyFAIRPlanSign(defaultPolicyData, tabClassTo1, tabClassTo2, getPolicyType().getShortName());
    }

    public void initiateHO3Quote(TestData defaultPolicyData, Class<? extends Tab> tabClassTo1) {
        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, tabClassTo1, false);
    }

    public TestData adjustApplicantAndReportsTD(TestData in_td, String ApplicantTabTDName, String ReportsTabTDName) {
        // Assemble Test Data
        in_td = getPolicyTD();
        TestData adjustedApplicantTab = getTestSpecificTD(ApplicantTabTDName);
        TestData adjustedReportsTab = getTestSpecificTD(ReportsTabTDName);
        in_td.adjust(ApplicantTab.class.getSimpleName(), adjustedApplicantTab);
        in_td.adjust(ReportsTab.class.getSimpleName(), adjustedReportsTab);

        return in_td;
    }

    public TestData adjustApplicantAndReportsTD(TestData in_td, String ApplicantTabTDName, String ReportsTabTDName, String PropInfoTDName) {
        // Assemble Test Data
        in_td = getPolicyTD();
        TestData adjustedApplicantTab = getTestSpecificTD(ApplicantTabTDName);
        TestData adjustedReportsTab = getTestSpecificTD(ReportsTabTDName);
        TestData adjustedPropertyTab = getTestSpecificTD(PropInfoTDName);
        in_td.adjust(ApplicantTab.class.getSimpleName(), adjustedApplicantTab);
        in_td.adjust(ReportsTab.class.getSimpleName(), adjustedReportsTab);
        in_td.adjust(PropertyInfoTab.class.getSimpleName(), adjustedPropertyTab);

        return in_td;
    }
}