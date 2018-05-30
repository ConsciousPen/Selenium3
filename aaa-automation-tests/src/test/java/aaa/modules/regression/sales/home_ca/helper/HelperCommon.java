package aaa.modules.regression.sales.home_ca.helper;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
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
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.table.Table;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

    public void seniorDiscountDependencyOnEffectiveDate(String policyNumber, int seniorDiscountApplicabilityAgeYears, int effectiveDateDaysDelta, String seniorDiscountName) {
        if (!generalTab.getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE).isPresent()) {
            NavigationPage.toViewTab(NavigationEnum.HomeCaTab.GENERAL.get());
        }
        generalTab.getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(effectiveDateDaysDelta).format(DateTimeUtils.MM_DD_YYYY));

        seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears);
        PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).verify.contains(seniorDiscountName);
        seniorDiscountViewRatingDetailsCheck(seniorDiscountName, "Yes");

        seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, -1 + effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears - 1);
        CustomAssert.assertFalse(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).getValue().contains(seniorDiscountName));
        seniorDiscountViewRatingDetailsCheck(seniorDiscountName, "No");

        seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, 1 + effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears);
        PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).verify.contains(seniorDiscountName);
        seniorDiscountViewRatingDetailsCheck(seniorDiscountName, "Yes");
    }


    public void seniorDiscountViewRatingDetailsCheck(String seniorDiscountName, String seniorDiscountValue) {
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        //BUG QC 44971 Regression: Senior discount is not displayed in rating details dialog
        switch(seniorDiscountName) {
            case "Senior":
                CustomAssert.assertEquals(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Senior Discount"), seniorDiscountValue);
                break;
            case "Mature Policy Holder":
                CustomAssert.assertEquals(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Mature policy holder"), seniorDiscountValue);
                break;
        }
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
    }


    public void seniorDiscountAppliedAndAgeCheck(String policyNumber, int seniorDiscountApplicabilityAgeYears, int dateOfBirthDaysDelta, int ageInDbYears) {
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        String seniorDiscountApplicabilityAge = TimeSetterUtil.getInstance().getCurrentTime().minusYears(seniorDiscountApplicabilityAgeYears).minusDays(dateOfBirthDaysDelta).format(DateTimeUtils.MM_DD_YYYY);
        applicantTab.getAssetList().getAsset(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiAssetList.class).getAsset(HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue(seniorDiscountApplicabilityAge);
        premiumsAndCoveragesQuoteTab.calculatePremium();
        int ageFromDb = Integer.parseInt(DBService.get().getValue(String.format(AGE_VERIFICATION_SQL, policyNumber)).get());
        CustomAssert.assertEquals(ageFromDb, ageInDbYears);
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
            case "ho3":
                endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();
                break;
            case "dp3":
                endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECADP.getLabel()).click();
                break;
        }

        // Handle Endorsement Confirmation
        Page.dialogConfirmation.confirm();
        endorsementTab.btnSaveForm.click();
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

    public TestData adjustApplicantReportsAndPropInfoTD(TestData in_td, String ApplicantTabTDName, String ReportsTabTDName, String PropInfoTDName) {
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
        assertThat(tableForms.getRowContains(columnName, endorsementToFind)).isNotNull();
    }

    public void verifyFPCECAEndorsementAvailable(String policyType) {
        switch (policyType) {
            case "HOME_CA_HO3":
                verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms,
                        PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECA");
                break;
            case "HOME_CA_DP3":
                verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms,
                        PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECADP");
                break;
        }
    }

    public void setupHO3Policy(TestData inputHO3TestData) {

            // Open App, Create Customer and Initiate Quote
            mainApp().open();
            createCustomerIndividual();
            inputHO3TestData = getTestSpecificTD("HO3PolicyData");
            createPolicy(inputHO3TestData);
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

    public void completeFillAndVerifyFAIRPlanSign(TestData defaultPolicyData, Class<? extends Tab> tabClassTo1, Class<? extends Tab> tabClassTo2, String policyType) {
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