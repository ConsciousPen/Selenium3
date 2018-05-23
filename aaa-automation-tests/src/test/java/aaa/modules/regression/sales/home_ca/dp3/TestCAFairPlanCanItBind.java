package aaa.modules.regression.sales.home_ca.dp3;

import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.modules.policy.HomeCaDP3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCAFairPlanCanItBind extends HomeCaDP3BaseTest {
    static TestData DEFAULTPOLICYDATA;

    /**
     * @Scenario - During Quote Fireline returns >= 5. FPCECA Added. Will Bind.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void AC1_Quote_HighFL_FPCECADP_Bind(@Optional("") String state) {
        // Assemble Test Data
        DEFAULTPOLICYDATA = buildTestData("ApplicantTab_FL7", "ReportsTab_NoMembership");

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getTestSpecificTD("HO3PolicyData"));

        policy.initiate();
        policy.getDefaultView().fillUpTo(DEFAULTPOLICYDATA, EndorsementTab.class, false);

        // Click FPCECA Endorsement
        addEndorsement();

        // Continue Fill Until Documents Tab.
        policy.getDefaultView().fillFromTo(DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class, true);
        // Sign Document
        new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECADP).setValue("Physically Signed");
        policy.getDefaultView().fillFromTo(DEFAULTPOLICYDATA, DocumentsTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }

    /**
     * @Scenario - During Endorsement Fireline returns < 5. FPCECA Added. Will NOT Bind.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void AC2_Endorsement_HighFL_FPCECADP_Purchase(@Optional("") String state) {

        // Assemble Test Data
        TestData endorsementTestData = getTestSpecificTD("Endorsement_FL7");
        DEFAULTPOLICYDATA = buildTestData("ApplicantTab_FL3", "ReportsTab_NoMembership");

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getTestSpecificTD("HO3PolicyData"));

        createPolicy(DEFAULTPOLICYDATA);
        policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(endorsementTestData, EndorsementTab.class, false);

        // Click FPCECA Endorsement
        addEndorsement();

        // Continue Fill Until Documents Tab.
        policy.getDefaultView().fillFromTo(DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class, true);
        // Sign Document
        new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECADP).setValue("Physically Signed");
        policy.getDefaultView().fillFromTo(DEFAULTPOLICYDATA, DocumentsTab.class, BindTab.class, true);
        new BindTab().submitTab();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void AC3_Quote_FL3_WoodRoof_Bind(@Optional("") String state) {
        // Assemble Test Data
        DEFAULTPOLICYDATA = buildTestDataWithPropInfo("ApplicantTab_FL3", "ReportsTab_NoMembership", "PropertyInfoTab_RoofWood_RentalInfo");
        DEFAULTPOLICYDATA.adjust("PremiumsAndCoveragesQuoteTab", getTestSpecificTD("PremiumsAndCoveragesQuoteTab_AC3"));
        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getTestSpecificTD("HO3PolicyData"));

        policy.initiate();
        policy.getDefaultView().fillUpTo(DEFAULTPOLICYDATA, EndorsementTab.class, false);

        // Click FPCECA Endorsement
        addEndorsement();

        // Continue Fill Until Documents Tab.
        policy.getDefaultView().fillFromTo(DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class, true);
        // Sign Document
        new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECADP).setValue("Physically Signed");
        policy.getDefaultView().fillFromTo(DEFAULTPOLICYDATA, DocumentsTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void AC4_Quote_ZipMatch_FPCECADP_Bind(@Optional("") String state) {
        // Assemble Test Data
        DEFAULTPOLICYDATA = buildTestDataWithPropInfo("ApplicantTab_ZipMatch", "ReportsTab_NoMembership", "PropertyInfoTab_RoofWood_RentalInfo");
        DEFAULTPOLICYDATA.adjust("PremiumsAndCoveragesQuoteTab", getTestSpecificTD("PremiumsAndCoveragesQuoteTab_AC3"));

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(DEFAULTPOLICYDATA, EndorsementTab.class, false);

        // Click FPCECA Endorsement
        addEndorsement();

        // Continue Fill Until Documents Tab.
        policy.getDefaultView().fillFromTo(DEFAULTPOLICYDATA, EndorsementTab.class, DocumentsTab.class, true);
        // Sign Document
        new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECADP).setValue("Physically Signed");
        policy.getDefaultView().fillFromTo(DEFAULTPOLICYDATA, DocumentsTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }

    private void addEndorsement() {
        // Click FPCECA Endorsement
        EndorsementTab endorsementTab = new EndorsementTab();
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECADP.getLabel()).click();

        // Verify Endorsement Confirmation Appears
        Page.dialogConfirmation.confirm();
        endorsementTab.btnSaveForm.click();
    }

    private TestData buildTestData(String ApplicantTabTDName, String ReportsTabTDName) {
        // Assemble Test Data
        DEFAULTPOLICYDATA = getPolicyTD();
        TestData adjustedApplicantTab = getTestSpecificTD(ApplicantTabTDName);
        TestData adjustedReportsTab = getTestSpecificTD(ReportsTabTDName);
        DEFAULTPOLICYDATA.adjust("ApplicantTab", adjustedApplicantTab);
        DEFAULTPOLICYDATA.adjust("ReportsTab", adjustedReportsTab);

        return DEFAULTPOLICYDATA;
    }

    private TestData buildTestDataWithPropInfo(String ApplicantTabTDName, String ReportsTabTDName, String PropInfoTDName) {
        // Assemble Test Data
        DEFAULTPOLICYDATA = getPolicyTD();
        TestData adjustedApplicantTab = getTestSpecificTD(ApplicantTabTDName);
        TestData adjustedReportsTab = getTestSpecificTD(ReportsTabTDName);
        TestData adjustedPropertyTab = getTestSpecificTD(PropInfoTDName);
        DEFAULTPOLICYDATA.adjust("ApplicantTab", adjustedApplicantTab);
        DEFAULTPOLICYDATA.adjust("ReportsTab", adjustedReportsTab);
        DEFAULTPOLICYDATA.adjust("PropertyInfoTab", adjustedPropertyTab);

        return DEFAULTPOLICYDATA;
    }
}
