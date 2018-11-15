package aaa.modules.regression.sales.home_ca.dp3.functional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * @Author - Tyrone C Jemison
 * @Description -
 */
@StateList(states = Constants.States.CA)
public class TestCAFairPlanMutualExclusions extends HomeCaDP3BaseTest {

    static TestData dp3PolicyData;
    static TestData ho3PolicyData;
    static HelperCommon myHelper;
    static String capturedPolicyNumber;

    /**
     * @Author - Tyrone C Jemison
     * @Description - Mixing and matching mutually exclusive endorsements. Should not be on same policy together.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Mutually Exclusive Endorsements DP3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-13445")
    public void TS1_AC1ToAC5_MutuallyExclusiveEndorsementCombos(@Optional("") String state) {
        // Verify Endorsements are not present.
        Table INCLUDED_ENDORSEMENTS_TABLE = new EndorsementTab().tblIncludedEndorsements;
        String FORM_ID = PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID;
        ArrayList<String> allExtraEndorsementsByLabel = convertStringsToArrayList(
                HomeCaMetaData.EndorsementTab.DP_04_18.getLabel(),
                HomeCaMetaData.EndorsementTab.DP_04_75.getLabel(),
                HomeCaMetaData.EndorsementTab.DW_09_25.getLabel()
        );
        // Initiate DP3 Policy. Fill up to P&C Endorsements.
        startDP3Quote(dp3PolicyData);

        // Add DP 09 25, DP 04 18, DP 04 47. Remove it With FairPLAN
        addEndorsementsRemoveWithFAIRPlan(FORM_ID, INCLUDED_ENDORSEMENTS_TABLE, allExtraEndorsementsByLabel);

        // Add DP 09 25. Remove it With FairPLAN
        addEndorsementsRemoveWithFAIRPlan(FORM_ID, INCLUDED_ENDORSEMENTS_TABLE, HomeCaMetaData.EndorsementTab.DW_09_25.getLabel());

        // Add DP 04 18. Remove it With FairPLAN
        addEndorsementsRemoveWithFAIRPlan(FORM_ID, INCLUDED_ENDORSEMENTS_TABLE, HomeCaMetaData.EndorsementTab.DP_04_18.getLabel());

        // Add DP 04 75. Remove it With FairPLAN
        addEndorsementsRemoveWithFAIRPlan(FORM_ID, INCLUDED_ENDORSEMENTS_TABLE, HomeCaMetaData.EndorsementTab.DP_04_75.getLabel());

        // Add Endorsement, Cancel Pop-Up Message
        addEndorsementsCancelFAIRPlan(FORM_ID, INCLUDED_ENDORSEMENTS_TABLE, HomeCaMetaData.EndorsementTab.DP_04_18.getLabel());

    }

    /**
     * @Author - Tyrone C Jemison
     * @Description - Adding FPCECADP during renewal will remove the auto-applied DW 04 75 endorsement.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Mutually Exclusive Endorsements DP3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-13445")
    public void TS2_AC6_RenewalBatch_AddingFPCECADP(@Optional("") String state) {
        Table INCLUDED_ENDORSEMENTS_TABLE = new EndorsementTab().tblIncludedEndorsements;
        String FORM_ID = PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID;
        ArrayList<String> allExtraEndorsementsByLabel = convertStringsToArrayList(
                HomeCaMetaData.EndorsementTab.DP_04_18.getLabel(),
                HomeCaMetaData.EndorsementTab.DP_04_75.getLabel(),
                HomeCaMetaData.EndorsementTab.DW_09_25.getLabel()
        );

        // Create HO3 Base Policy
        createHO3Policy(ho3PolicyData);
        // Begin DP3 Quote
        doDP3QuoteWithPropInfo(dp3PolicyData, false);
        LocalDateTime policyExpirationDate  = PolicySummaryPage.getExpirationDate();
        capturedPolicyNumber = PolicySummaryPage.getPolicyNumber();

        // Close App, Move JVM, Get Renewal Image
        advanceJVM(policyExpirationDate, capturedPolicyNumber);

        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_DP3"), EndorsementTab.class, false);
        HelperCommon.addFAIRPlanEndorsement(getPolicyType().getShortName());
        HelperCommon.verifyEndorsementsNotVisible(INCLUDED_ENDORSEMENTS_TABLE, FORM_ID, allExtraEndorsementsByLabel);
    }

    /**
     * @Author - Tyrone C Jemison
     * @Description - A renewal image with the FPCECADP endorsement will not automatically add the
     *  DW 04 75 endorsement.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "18.5: CA FAIR Plan: Mutually Exclusive Endorsements DP3")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-13445")
    public void TS2_AC7_RenewalBatch_NoDW0475(@Optional("") String state) {
        Table INCLUDED_ENDORSEMENTS_TABLE = new EndorsementTab().tblIncludedEndorsements;
        String FORM_ID = PolicyConstants.PolicyIncludedAndSelectedEndorsementsTable.FORM_ID;
        ArrayList<String> allExtraEndorsementsByLabel = convertStringsToArrayList(
                HomeCaMetaData.EndorsementTab.DP_04_18.getLabel(),
                HomeCaMetaData.EndorsementTab.DP_04_75.getLabel(),
                HomeCaMetaData.EndorsementTab.DW_09_25.getLabel()
        );

        // Create HO3 Base Policy
        createHO3Policy(ho3PolicyData);

        // Begin DP3 Quote
        doDP3QuoteWithPropInfo(dp3PolicyData, true);
        LocalDateTime policyExpirationDate  = PolicySummaryPage.getExpirationDate();
        capturedPolicyNumber = PolicySummaryPage.getPolicyNumber();

        // Close App, Move JVM, Get Renewal Image
        advanceJVM(policyExpirationDate, capturedPolicyNumber);

        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_DP3"), EndorsementTab.class, false);
        HelperCommon.verifyEndorsementsNotVisible(INCLUDED_ENDORSEMENTS_TABLE, FORM_ID, allExtraEndorsementsByLabel);
    }

    public void createHO3Policy(TestData inputHO3TestData) {

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        inputHO3TestData = getTestSpecificTD("HO3PolicyData");
        createPolicy(inputHO3TestData);
    }

    public void startDP3Quote(TestData defaultPolicyData) {
        defaultPolicyData = getPolicyTD();
        defaultPolicyData.adjust(ApplicantTab.class.getSimpleName(), getTestSpecificTD("ApplicantTab_DP3"));
        defaultPolicyData.adjust(ReportsTab.class.getSimpleName(), getTestSpecificTD("ReportsTab_DP3"));
        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, EndorsementTab.class, false);
    }

    public void doDP3QuoteWithPropInfo(TestData defaultPolicyData, boolean addFAIRPlan) {
        defaultPolicyData = getPolicyTD();
        defaultPolicyData.adjust(ApplicantTab.class.getSimpleName(), getTestSpecificTD("ApplicantTab_DP3"));
        defaultPolicyData.adjust(ReportsTab.class.getSimpleName(), getTestSpecificTD("ReportsTab_DP3"));
        defaultPolicyData.adjust(PropertyInfoTab.class.getSimpleName(), getTestSpecificTD("PropertyInfo_OldHouse"));
        defaultPolicyData.adjust(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), getTestSpecificTD("PremiumsAndCoveragesQuoteTab_DP3"));
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, EndorsementTab.class, false);

        if (addFAIRPlan) {
            HelperCommon.addFAIRPlanEndorsement(getPolicyType().getShortName());
        }

        policy.getDefaultView().fillFromTo(defaultPolicyData, EndorsementTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }

    public ArrayList<String> convertStringsToArrayList(String... allGiven) {
        ArrayList<String> createdList = new ArrayList<String>();
        for (String val : allGiven) {
            createdList.add(val);
        }

        return createdList;
    }

    public static void addMultipleEndorsements(ArrayList<String> endorsementLabels) {
        // Click FPCECA Endorsement
        EndorsementTab endorsementTab = new EndorsementTab();
        for (String label : endorsementLabels)
        {
            endorsementTab.getAddEndorsementLink(label).click();

            if (label.equalsIgnoreCase(HomeCaMetaData.EndorsementTab.DP_04_18.getLabel())) {
                endorsementTab.getAssetList().getAsset(HomeCaMetaData.EndorsementTab.DP_04_18).getAsset(HomeCaMetaData.EndorsementTab.EndorsementDP0418.COVERAGE_LIMIT).setValue("1000.00");
            }

            if (label.equalsIgnoreCase(HomeCaMetaData.EndorsementTab.DW_09_25.getLabel())) {
                endorsementTab.getAssetList().getAsset(HomeCaMetaData.EndorsementTab.DW_09_25).getAsset(HomeCaMetaData.EndorsementTab.EndorsementDW0925.REASON_FOR_VACANCY).setValue("None");
                endorsementTab.getAssetList().getAsset(HomeCaMetaData.EndorsementTab.DW_09_25).getAsset(HomeCaMetaData.EndorsementTab.EndorsementDW0925.LENGTH_OF_VACANCY).setValue("32");
            }
            endorsementTab.btnSaveForm.click();
        }
    }

    public void addEndorsementsRemoveWithFAIRPlan(String FORM_ID, Table INCLUDED_ENDORSEMENTS_TABLE, String... args) {
        // Add endorsements DP 04 18, DP 04 75, and  DW 09 25.
        ArrayList<String> allExtraEndorsementsByLabel = new ArrayList<String>();
        for (String label : args) {
            allExtraEndorsementsByLabel.add(label);
        }
        addMultipleEndorsements(allExtraEndorsementsByLabel);
        HelperCommon.addFAIRPlanEndorsement(getPolicyType().getShortName());
        HelperCommon.verifyEndorsementsNotVisible(INCLUDED_ENDORSEMENTS_TABLE, FORM_ID, allExtraEndorsementsByLabel);
        HelperCommon.removeFAIRPlanEndorsement(getPolicyType().getShortName());
    }

    public void addEndorsementsRemoveWithFAIRPlan(String FORM_ID, Table INCLUDED_ENDORSEMENTS_TABLE, ArrayList<String> args) {
        // Add endorsements DP 04 18, DP 04 75, and  DW 09 25.
        ArrayList<String> allExtraEndorsementsByLabel = new ArrayList<String>();
        for (String label : args) {
            allExtraEndorsementsByLabel.add(label);
        }
        addMultipleEndorsements(allExtraEndorsementsByLabel);
        //TODO: Pick up here - i think its a bug
        HelperCommon.addFAIRPlanEndorsement("homeca_dp3");
        HelperCommon.verifyEndorsementsNotVisible(INCLUDED_ENDORSEMENTS_TABLE, FORM_ID, allExtraEndorsementsByLabel);
        HelperCommon.removeFAIRPlanEndorsement(getPolicyType().getShortName());
    }

    public void addEndorsementsCancelFAIRPlan(String FORM_ID, Table INCLUDED_ENDORSEMENTS_TABLE, String... args) {
        // Add endorsements DP 04 18, DP 04 75, and  DW 09 25.
        ArrayList<String> allExtraEndorsementsByLabel = new ArrayList<String>();
        for (String label : args) {
            allExtraEndorsementsByLabel.add(label);
        }
        addMultipleEndorsements(allExtraEndorsementsByLabel);
        HelperCommon.addFAIRPlanThenCancelPopUp(getPolicyType().getShortName());
    }

    public void advanceJVM(LocalDateTime policyExpirationDate, String policyToRetrieve) {
        mainApp().close();
        // Determine Time Values
        LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);

        // Move JVM to TP1 (R-73) & run Renewal jobs
        HelperCommon.moveJVMToDateAndRunRenewalJobs(renewImageGenDate, 2);
        HelperCommon.moveJVMToDateAndRunRenewalJobs(renewPreviewGenDate, 1);
        mainApp().open();

        SearchPage.openPolicy(policyToRetrieve);

        policy.renew().start().submit();
    }

}