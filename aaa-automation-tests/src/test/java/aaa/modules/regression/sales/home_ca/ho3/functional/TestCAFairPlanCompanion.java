package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.xml.XmlParser;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Tyrone C Jemison
 * @name Test CA Fair Plan Companion
 */
public class TestCAFairPlanCompanion extends HomeCaHO3BaseTest {
    // Class Variables
    TestData defaultPolicyData;
    HelperCommon myHelper = new HelperCommon();

    /**
     * @scenario
     * 1. Initiate a HO3 Quote.
     * 2. Observe FPCECA is visible.
     * 3. After adding FPCECA, FPCECA removed from Optional Endorsements
     * 4. After Adding FPCECA, FPCECA visible in Documents Tab and Quote Tab.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Add FAIR Plan Companion endorsement HO3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-13210")
    public void AC1AC4_Quote_VisibleFPCECA(@Optional("") String state) {
        defaultPolicyData = getPolicyTD();

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, EndorsementTab.class, false);

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement(getPolicyType().getShortName());

        // Verify FPCECA now present on Documents Tab & Quote Tab
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        myHelper.verifyFPCECAEndorsementAvailable(getPolicyType().getShortName());

        // Verify Document Tab populates Endorsement
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
        assertThat(new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECA.getLabel()).isPresent()).isTrue();
        mainApp().close();
    }

    /**
     * @scenario
     * 1. Create a HO3 Quote.
     * 2. Bind Quote.
     * 3. Initiate Mid-Term Endorsement.
     * 4. Observe FPCECA is visible.
     * 5. After opting to add FPCECA, a message is displayed.
     * 6. Verify message will match mock data.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Add FAIR Plan Companion endorsement HO3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-13210")
    public void AC2AC5_Endorsement_VisibleFPCECA(@Optional("") String state) {

        defaultPolicyData = getPolicyTD();
        TestData endorsementTestData = getTestSpecificTD("Endorsement_AC2AC5").resolveLinks();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        createPolicy(defaultPolicyData);
        policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(endorsementTestData, EndorsementTab.class, false);
        myHelper.verifyFPCECAEndorsementAvailable(getPolicyType().getShortName());

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement(getPolicyType().getShortName());
        mainApp().close();
    }

    /**
     * @scenario
     * 1. Create a HO3 Quote.
     * 2. Bind Quote.
     * 3. Advance JVM to TP1, run renewal jobs.
     * 4. Advance JVM to TP2, run renewal jobs.
     * 5. Retrieve Renewal Image
     * 6. Observe FPCECA is visible.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Add FAIR Plan Companion endorsement HO3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-13210")
    public void AC3_Renewal_VisibleFPCECA(@Optional("") String state) {
        defaultPolicyData = getPolicyTD();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        createPolicy(defaultPolicyData);
        myHelper.handleRenewalTesting(defaultPolicyData);
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_AC3"), EndorsementTab.class, false);

        myHelper.verifyFPCECAEndorsementAvailable("ho3");

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement("homeca_ho3");
        mainApp().close();
    }

    /**
     * @scenario
     * 1. Create a HO3 policy w/ FPCECA Endorsement
     * 2. On Policy Summary Page, select "Take Action" > "OnDemandDocs"
     * 3. On Doc Selection Page, select 62 65000 CA 05012013 doc.
     * 4. Verify document contains correct FP verbage with DB query.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Send FAIR Plan data to DCS when rendering EOI document")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-14675")
    public void PAS_14675_IsFPCECAInEOI(@Optional("") String state) {

        final String EXPECTED_NAME = "FairPlanYN";
        defaultPolicyData = getPolicyTD();

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, EndorsementTab.class, false);

        // Add FPCECA Endorsement and complete Policy
        myHelper.addFAIRPlanEndorsement(getPolicyType().getShortName());
        myHelper.completeFillAndVerifyFAIRPlanSign(policy, defaultPolicyData, EndorsementTab.class, DocumentsTab.class, getPolicyType().getShortName());

        String policyNumber = PolicySummaryPage.getPolicyNumber();

        // Generate EOI Documents
        policy.policyDocGen().start();
        PolicyDocGenActionTab documentActionTab = policy.policyDocGen().getView().getTab(PolicyDocGenActionTab.class);
        documentActionTab.generateDocuments(DocGenEnum.Documents._62_6500);

        // Pick Up File Generated
        myHelper.validatePdfFromDb(policyNumber, DocGenEnum.Documents._62_6500,
                AaaDocGenEntityQueries.EventNames.ADHOC_DOC_ON_DEMAND_GENERATE, EXPECTED_NAME, "Y");
        mainApp().close();
    }

}