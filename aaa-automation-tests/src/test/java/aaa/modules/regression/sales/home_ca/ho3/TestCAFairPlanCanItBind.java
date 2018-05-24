package aaa.modules.regression.sales.home_ca.ho3;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import aaa.modules.regression.sales.template.AbstractFAIRPlanTestMethods;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCAFairPlanCanItBind extends AbstractFAIRPlanTestMethods {
    static TestData DEFAULTPOLICYDATA;
    static HelperCommon myHelper;

    /**
     * @Scenario - During Quote Fireline returns >= 5. FPCECA Added. Will Bind.
     * @Runtime - 2min
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC1_Quote_HighFL_FPCECA_Bind(@Optional("") String state) {
        performTest("ApplicantTab_FL7", "ReportsTab_NoMembership", DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC2_Quote_FL3_WoodRoof_Bind(@Optional("") String state) {
        performTest("ApplicantTab_FL3", "ReportsTab_NoMembership", "PropertyInfoTab_RoofWood", DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC3_Quote_ZipMatch_FPCECA_Bind(@Optional("") String state) {
        performTest("ApplicantTab_ZipMatch", "ReportsTab_NoMembership", "PropertyInfoTab_RoofWood", DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class);
    }

    private void performTest(String applicantTabTD, String reportsTabTD, TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2) {
        // Assemble Test Data
        defaultPolicyData = myHelper.adjustApplicantAndReportsTD(defaultPolicyData, applicantTabTD, reportsTabTD);

        initiateHO3Quote(defaultPolicyData, tabClassTo1);

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement("ho3");

        completeFillAndVerifySignature(defaultPolicyData, tabClassTo1, tabClassTo2, "ho3");
    }

    private void performTest(String applicantTabTD, String reportsTabTD, String propInfoTD, TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2) {
        // Assemble Test Data
        defaultPolicyData = myHelper.adjustApplicantReportsAndPropInfoTD(defaultPolicyData, applicantTabTD, reportsTabTD, propInfoTD);

        initiateHO3Quote(defaultPolicyData, tabClassTo1);

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement("ho3");

        completeFillAndVerifySignature(defaultPolicyData, tabClassTo1, tabClassTo2, "ho3");
    }
}
