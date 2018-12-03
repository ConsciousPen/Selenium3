package aaa.modules.regression.sales.home_ca.helper;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.table.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.*;
import static toolkit.verification.CustomAssertions.assertThat;

public class HelperCommon extends HomeCaHO3BaseTest{
    private static final String AGE_VERIFICATION_SQL = "select ip.age from POLICYSUMMARY ps, INSUREDPRINCIPAL ip\n" +
            "where ps.POLICYDETAIL_ID = ip.POLICYDETAIL_ID \n" +
            "and ps.POLICYNUMBER = '%s'\n";
    private GeneralTab generalTab = new GeneralTab();
    private ApplicantTab applicantTab = new ApplicantTab();
    private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();


    public void seniorDiscountDwellingUsageCheck(String dwellingUsageValue) {
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
        propertyInfoTab.getInteriorAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Interior.DWELLING_USAGE).setValue(dwellingUsageValue);
        premiumsAndCoveragesQuoteTab.calculatePremium();
    }

    public void seniorDiscountDependencyOnEffectiveDate(String policyNumber, int seniorDiscountApplicabilityAgeYears, int effectiveDateDaysDelta, String seniorDiscountName, ETCSCoreSoftAssertions softly) {
        if (!generalTab.getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE).isPresent()) {
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.GENERAL.get());
        }
        generalTab.getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(effectiveDateDaysDelta).format(DateTimeUtils.MM_DD_YYYY));

        seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears);
        softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1)).valueContains(seniorDiscountName);
        seniorDiscountViewRatingDetailsCheck(seniorDiscountName, "Yes");

        seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, -1 + effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears - 1);
        softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain(seniorDiscountName);
        seniorDiscountViewRatingDetailsCheck(seniorDiscountName, "No");

        seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, 1 + effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears);
        softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1)).valueContains(seniorDiscountName);
        seniorDiscountViewRatingDetailsCheck(seniorDiscountName, "Yes");
    }


    public void seniorDiscountViewRatingDetailsCheck(String seniorDiscountName, String seniorDiscountValue) {
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        //BUG QC 44971 Regression: Senior discount is not displayed in rating details dialog
        switch(seniorDiscountName) {
            case "Senior":
                assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Senior Discount")).isEqualTo(seniorDiscountValue);
                break;
            case "Mature Policy Holder":
                assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Mature policy holder")).isEqualTo(seniorDiscountValue);
                break;
        }
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
    }


    public void seniorDiscountAppliedAndAgeCheck(String policyNumber, int seniorDiscountApplicabilityAgeYears, int dateOfBirthDaysDelta, int ageInDbYears) {
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        String seniorDiscountApplicabilityAge = TimeSetterUtil.getInstance().getCurrentTime().minusYears(seniorDiscountApplicabilityAgeYears).minusDays(dateOfBirthDaysDelta).format(DateTimeUtils.MM_DD_YYYY);
        applicantTab.getAssetList().getAsset(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiAssetList.class).getAsset(HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue(seniorDiscountApplicabilityAge);
        premiumsAndCoveragesQuoteTab.calculatePremium();
        assertThat(DBService.get().getValue(String.format(AGE_VERIFICATION_SQL, policyNumber)).map(Integer::parseInt)).hasValue(ageInDbYears);
    }

    // This creates a customer, policy and return the policy number as a String.
    public String createCustomerPolicyReturnPN(TestData in_customerData, TestData in_policyData)
    {
        createCustomerIndividual(in_customerData);
        return createPolicy(in_policyData);
    }

    // Retrieves an existing policy and initiates a generic endorsement.
    public void doSameDayEndorsement(String policyNumberToOpen, IPolicy in_policy, TestData endorsementTestData)
    {
        //Go to Created Policy.
        SearchPage.openPolicy(policyNumberToOpen);
        // Begin Endorsement. Use Default data for Endorsement Reason Page. Using Custom Data From Adjustment.
        in_policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        in_policy.getDefaultView().fillUpTo(endorsementTestData, PurchaseTab.class, false);
    }

    public static void addFAIRPlanEndorsement(String policyType) {
        // Click FPCECA Endorsement
        policyType = policyType.toLowerCase();
        EndorsementTab endorsementTab = new EndorsementTab();

        switch (policyType) {
            case "homeca_ho3":
                Map<String, String> endorsement_FPCECA = new HashMap<>();
                endorsement_FPCECA.put("Form ID", "FPCECA");
                endorsement_FPCECA.put("Name", "FAIR Plan Companion Endorsement - California");

                endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();
                endorsementTab.btnSaveEndo.click();
                assertThat(new EndorsementTab().tblIncludedEndorsements.getRowContains(endorsement_FPCECA)).isPresent();
                break;
            case "homeca_dp3":
                Map<String, String> endorsement_FPCECADP = new HashMap<>();
                endorsement_FPCECADP.put("Form ID", "FPCECADP");
                endorsement_FPCECADP.put("Name", "FAIR Plan Companion Endorsement - California");

                endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECADP.getLabel()).click();
                endorsementTab.btnSaveEndo.click();
                assertThat(new EndorsementTab().tblIncludedEndorsements.getRowContains(endorsement_FPCECADP)).isPresent();
                break;
        }
    }

    public static void addFAIRPlanThenCancelPopUp(String policyType) {
        // Click FPCECA Endorsement
        policyType = policyType.toLowerCase();
        EndorsementTab endorsementTab = new EndorsementTab();

        switch (policyType) {
            case "homeca_ho3":
                endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();
                break;
            case "homeca_dp3":
                endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECADP.getLabel()).click();
                break;
        }

        // Handle Endorsement Confirmation
        Page.dialogConfirmation.reject();
    }

    public static void removeFAIRPlanEndorsement(String policyType) {
        // Click FPCECA Endorsement
        policyType = policyType.toLowerCase();
        EndorsementTab endorsementTab = new EndorsementTab();

        switch (policyType) {
            case "homeca_ho3":
                endorsementTab.getRemoveEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel(), 1).click();
                break;
            case "homeca_dp3":
                endorsementTab.getRemoveEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECADP.getLabel(), 1).click();
                break;
        }

        // Handle Endorsement Confirmation
        Page.dialogConfirmation.confirm();
    }

    public static void moveJVMToDateAndRunRenewalJobs(LocalDateTime desiredJVMLocalDateTime, int howManyPartsToRun)
    {
        printToDebugLog(" -- Current Date = " + TimeSetterUtil.getInstance().getCurrentTime() + ". Moving JVM to input time = "
                + desiredJVMLocalDateTime.toString() + " -- ");

        // Advance JVM to Generate Renewal Image.
        TimeSetterUtil.getInstance().nextPhase(desiredJVMLocalDateTime);
        printToDebugLog("Current Date is now = " + TimeSetterUtil.getInstance().getCurrentTime());

        if (howManyPartsToRun == 1)
            JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        if (howManyPartsToRun == 2) {
            JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
            JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        }


        printToDebugLog(" -- Renewal Offer Generation Jobs Completed -- ");
    }

    public static void verifySelectedEndorsementsPresent(Table tableForms, String columnName, String endorsementToFind) {
        assertThat(tableForms.getRowContains(columnName, endorsementToFind)).exists();
    }

    public static void verifyEndorsementsNotVisible(Table tableForms, String columnName, ArrayList<String> endorsementsByLabel) {
        ArrayList<String> foundColumnNames = new ArrayList<>();
        for (int i = 1; i < tableForms.getRowsCount(); i++) {
            foundColumnNames.add(tableForms.getRow(i).getCell(columnName).getValue());
        }

        for (String name : foundColumnNames) {
            for (String label : endorsementsByLabel)
            assertThat(name).isNotEqualToIgnoringCase(label);
        }
    }

    public void verifyFPCECAEndorsementAvailable(String policyType) {
        switch (policyType) {
            case "HomeCA_HO3":
                verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms,
                        PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECA");
                break;
            case "HomeCA_DP3":
                verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms,
                        PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECADP");
                break;
        }
    }

    public void handleRenewalTesting(TestData defaultPolicyData) {

        // Open App, Create Customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        LocalDateTime policyExpirationDate  = PolicySummaryPage.getExpirationDate();
        mainApp().close();

        // Determine Time Values
        LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);

        // Move JVM to TP1 (R-73) & run Renewal jobs
        moveJVMToDateAndRunRenewalJobs(renewImageGenDate, 2);
        // Move JVM to TP2 (R-59) & run Renewal jobs
        moveJVMToDateAndRunRenewalJobs(renewPreviewGenDate, 1);

        // Open App, get renewal image.
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        policy.renew().start().submit();
    }

    public static void completeFillAndVerifyFAIRPlanSign(IPolicy policy, TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2, String policyType) {
        // Continue Fill Until Documents Tab.
        policy.getDefaultView().fillFromTo(defaultPolicyData, tabClassTo1, tabClassTo2, true);

        // Sign Document
        String formattedInput = policyType.toLowerCase();
        switch (formattedInput) {
            case "homeca_ho3":
                new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECA).setValue("Physically Signed");
                break;
            case "homeca_dp3":
                new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECADP).setValue("Physically Signed");
                break;
        }
        policy.getDefaultView().fillFromTo(defaultPolicyData, tabClassTo2, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }

    /**
     * @author Tyrone Jemison
     * @description This method is usable during any test to verify the value of a data element contained within a document on the database.
     * @param policyNumber The policy which generated the document.
     * @param docGenDoc The Document to verify.
     * @param eventName Event which Generated Document.
     * @param expectedElementName The data element, via text within a "aaan:Name" label.
     * @param expectedTagValue The expected value of the associated "aaan:DataElementChoice"
     */
    public static void validatePdfFromDb(String policyNumber, DocGenEnum.Documents docGenDoc, EventNames eventName, String expectedElementName, String expectedTagValue) {
        String actualValueFound;
        String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, docGenDoc.getIdInXml(), eventName);
        Document docToValidate = DocGenHelper.getDocument(docGenDoc, query);
        actualValueFound = DocGenHelper.getDocumentDataElemByName(expectedElementName, docToValidate).getDataElementChoice().getTextField();

        assertThat(actualValueFound.contentEquals(expectedTagValue)).isTrue();
    }
}