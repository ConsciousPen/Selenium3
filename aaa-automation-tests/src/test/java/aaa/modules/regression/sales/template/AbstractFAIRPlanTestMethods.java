package aaa.modules.regression.sales.template;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import toolkit.datax.TestData;

import java.time.LocalDateTime;

public abstract class AbstractFAIRPlanTestMethods extends PolicyBaseTest {

    HelperCommon myHelper = new HelperCommon();

    public TestData adjustDP3TestData(TestData inputDP3TestData) {

        // Must adjust DP3 Data on Applicant Tab.
        inputDP3TestData = getPolicyTD();
        TestData adjustedDP3ApplicantData = getTestSpecificTD("ApplicantTab_DP3").resolveLinks();
        TestData adjustedDP3ReportsData = getTestSpecificTD("ReportsTab_DP3").resolveLinks();
        inputDP3TestData.adjust(ApplicantTab.class.getSimpleName(), adjustedDP3ApplicantData);
        inputDP3TestData.adjust(ReportsTab.class.getSimpleName(), adjustedDP3ReportsData);

        return inputDP3TestData;
    }

    public void setupHO3policy(TestData inputHO3TestData) {

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        inputHO3TestData = getTestSpecificTD("HO3PolicyData");
        createPolicy(inputHO3TestData);
    }

    public void initiateDP3Quote(TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2) {
        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getTestSpecificTD("HO3PolicyData"));

        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, tabClassTo1, false);
    }

    public void initiateHO3Quote(TestData defaultPolicyData, Class<? extends Tab> tabClassTo1) {
        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, tabClassTo1, false);
    }

    public void verifyEndorsementAvailable(String policyType ) {

        String formattedInput = policyType.toLowerCase();
        switch (formattedInput) {
            case "ho3":
                myHelper.verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms,
                        PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECA");
                break;
            case "dp3":
                myHelper.verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms,
                        PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECADP");
                break;
        }
    }

    public void handleRenewalTesting(TestData defaultPolicyData) {

        // Open App, Create Customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = myHelper.createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        LocalDateTime policyExpirationDate  = PolicySummaryPage.getExpirationDate();
        mainApp().close();

        // Determine Time Values
        LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);

        // Move JVM to TP1 (R-73) & run Renewal jobs
        myHelper.moveJVMToDateAndRunRenewalJobs(renewImageGenDate, 1);
        // Move JVM to TP2 (R-59) & run Renewal jobs
        myHelper.moveJVMToDateAndRunRenewalJobs(renewPreviewGenDate, 2);

        // Open App, get renewal image.
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        policy.renew().start().submit();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_AC3"), EndorsementTab.class, false);
    }

    public void completeFillAndVerifySignature(TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2, String policyType) {
        // Continue Fill Until Documents Tab.
        policy.getDefaultView().fillFromTo(defaultPolicyData, tabClassTo1, tabClassTo2, true);

        // Sign Document
        String formattedInput = policyType.toLowerCase();
        switch (formattedInput) {
            case "ho3":
                new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECA).setValue("Physically Signed");
                break;
            case "dp3":
                new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECADP).setValue("Physically Signed");
                break;
        }
        policy.getDefaultView().fillFromTo(defaultPolicyData, tabClassTo2, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }
}
