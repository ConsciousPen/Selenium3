package aaa.modules.regression.sales.home_ca.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @Author - Tyrone Jemison
 * @Scenario - During Quote Fireline returns >= 5. FPCECA Added. Will Bind.
 */
@StateList(states = Constants.States.CA)
public class TestCAFairPlanCanItBind extends HomeCaDP3BaseTest {
    static TestData DEFAULTPOLICYDATA;
    static HelperCommon myHelper;

    /**
     * @Scenario - During Quote Fireline returns >= 5. FPCECA Added. Will Bind.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Update Fireline underwriting eligibility rules to enable Bind with FPCECA/FPCECADP Endorsement DP3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-13214")
    public void AC1_Quote_HighFL_FPCECADP_Bind(@Optional("") String state) {
        performTest("ApplicantTab_FL7", "ReportsTab_NoMembership", DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class);
    }

    /**
     * @Scenario - During Quote Fireline returns < 5. FPCECA Added. Will Bind.
     * @Runtime - 2min
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Update Fireline underwriting eligibility rules to enable Bind with FPCECA/FPCECADP Endorsement DP3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "13214")
    public void AC2_Quote_FL3_WoodRoof_Bind(@Optional("") String state) {
        performTest("ApplicantTab_FL3", "ReportsTab_NoMembership", "PropertyInfoTab_RoofWood_RentalInfo", DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class);
    }

    /**
     * @Scenario - During Quote Fireline returns zip matched address. FPCECA Added. Will Bind.
     * @Runtime - 2min
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Update Fireline underwriting eligibility rules to enable Bind with FPCECA/FPCECADP Endorsement DP3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-13214")
    public void AC3_Quote_ZipMatch_FPCECADP_Bind(@Optional("") String state) {
        performTest("ApplicantTab_ZipMatch", "ReportsTab_NoMembership", "PropertyInfoTab_RoofWood_RentalInfo", DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class);
    }

    private void performTest(String applicantTabTD, String reportsTabTD, TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2) {
        myHelper = new HelperCommon();

        // Assemble Test Data
        defaultPolicyData = adjustApplicantAndReportsTD(defaultPolicyData, applicantTabTD, reportsTabTD);

        initiateDP3Quote(defaultPolicyData, tabClassTo1, tabClassTo2);

        // Click FPCECA Endorsement
        HelperCommon.addFAIRPlanEndorsement(getPolicyType().getShortName());

        HelperCommon.completeFillAndVerifyFAIRPlanSign(policy, defaultPolicyData, tabClassTo1, tabClassTo2, getPolicyType().getShortName());
    }

    private void performTest(String applicantTabTD, String reportsTabTD, String propInfoTD, TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2) {
        myHelper = new HelperCommon();

        // Assemble Test Data
        defaultPolicyData = adjustApplicantAndReportsTD(defaultPolicyData, applicantTabTD, reportsTabTD, propInfoTD);
        defaultPolicyData.adjust(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), getTestSpecificTD("PremiumsAndCoveragesQuoteTab_AC3"));

        initiateDP3Quote(defaultPolicyData, tabClassTo1, tabClassTo2);

        // Click FPCECA Endorsement
        HelperCommon.addFAIRPlanEndorsement(getPolicyType().getShortName());

        HelperCommon.completeFillAndVerifyFAIRPlanSign(policy, defaultPolicyData, tabClassTo1, tabClassTo2, getPolicyType().getShortName());
    }

    public void initiateDP3Quote(TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2) {
        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getTestSpecificTD("HO3PolicyData"));

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