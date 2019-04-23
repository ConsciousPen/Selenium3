package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.db.queries.AAAMultiPolicyDiscountQueries;
import aaa.helpers.db.queries.AAAMultiPolicyDiscountQueries.*;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMPDSTG2Catchup extends AutoSSBaseTest {
    String populateData = "CUSTOMER_GBY@yeah.com";
    String populateDataForBatch_NotFound = "NOT_FOUND@yeah.com";
    String populatefound = "REFRESH_P@yeah.com";
    String populateerror = "MPD_ERROR@yeah.com";
    String cancelledMS = "3111111111111121";
    String activeMS = "4290074030137505";

    /**
     * @author Rajesh Nayak, Robert Boles
     * @name PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time (AC#1)
     * @scenario Precondition: Bind Product Eligible for Membership
     * 1. Create Customer.
     * 2. Create AUTO_SS Policy without Membership
     * 3. Run Membership Validation Batch Jobs at Renewal Time point 1 and Time point 2
     * 4. Retrieve Renewal image and verify that Membership discount is provided
     * @details
     */
    //Membership: Cancelled
    //MPD: QUOTED at bind
    //BMS: NOTFOUND_STG2 --Endorse image (cancelledMS)
    //MPDVS: FOUND_STG2 --Endorse image (populateData)
    //Not picked up due to outside of time point window
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: System to validate companion products at NB+30 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-1")
    public void pas28452_MS_Can_MPD_FOUND_STG2_MinusOne(@Optional("AZ") String state) {
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateData,cancelledMS,-1, "Yes")).orElse("Null"));
        assertThat(responseMPD).isEqualTo("null");
    }
    //Membership: Cancelled
    //MPD: QUOTED at bind
    //BMS: NOTFOUND_STG2 --Endorse image (cancelledMS)
    //MPDVS: FOUND_STG2 --Endorse image (populateData)
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: System to validate companion products at NB+30 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-1")
    public void pas28452_MS_Can_MPD_FOUND_STG2(@Optional("AZ") String state) {
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateData,cancelledMS,0, "Yes")).orElse("Null"));
        assertThat(responseMPD).isEqualTo("FOUND_STG2");
    }

    //Membership:Cancelled
    //MPD: QUOTED at bind
    //BMS:NOTFOUND_STG2 Endorse image (cancelledMS)
    //MPDVS:NOTFOUND_STG2 Endorse image (populateDataForBatch_NotFound)
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: System to validate companion products at NB+30 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-1")
    public void pas28452_MS_Can_MPD_NOT_FOUND_STG21(@Optional("AZ") String state) {
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateDataForBatch_NotFound,cancelledMS,0, "Yes")).orElse("Null"));
        assertThat(responseMPD).isEqualTo("NOTFOUND_STG2");
    }

    //Membership: Active
    //MPD: QUOTED at bind
    //BMS: NOTFOUND_STG2 --Endorse image (cancelledMS)
    //MPDVS: FOUND_STG2 --Endorse image (populateData)
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: System to validate companion products at NB+30 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-1")
    public void pas28452_MS_Act_MPD_FOUND_STG21(@Optional("AZ") String state) {
        //java.util.Optional<String> responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateData,cancelledMS,0, "Yes")));
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populatefound,activeMS,0, "Yes")).orElse("Null"));
        assertThat(responseMPD).isEqualTo("FOUND_STG2");
    }

    //Membership: Active
    //MPD: QUOTED at bind
    //BMS: FOUND_STG2 Endorse image (cancelledMS)
    //MPDVS: FOUND_STG2 Endorse image (populateDataForBatch_NotFound)
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: System to validate companion products at NB+30 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-1")
    public void pas28452_MS_Act_MPD_NOT_FOUND_STG21(@Optional("AZ") String state) {
        String responseMPD = (AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateDataForBatch_NotFound,activeMS,0, "Yes")).orElse("Null"));
        assertThat(responseMPD).isEqualTo("NOTFOUND_STG2");
    }



    //Membership:Cancelled
    //MPD: QUOTED
    //BMS:NOTFOUND_STG2 Endorse image
    //MPDVS:ERROR_STG2 Endorse image
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: System to validate companion products at NB+30 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-1")
    public void pas28452_MPD_STG2_Error_1(@Optional("KY") String state) {
        String response = String.valueOf(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateerror,cancelledMS,0, "Yes")));
        assertThat(response).isEqualTo("ERROR_STG2");
    }

//Membership:Active
    //MPD: QUOTED
    //BMS:Not eligible for Validation
    //MPDVS:ERROR_STG2 Endorse Original image

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: System to validate companion products at NB+30 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-1")
    public void pas28452_MPD_STG2_Error_2(@Optional("UT") String state) {
        String response = AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateerror, "4290023667710001", 0, "Yes")).orElse("Null");
        assertThat(response).isEqualTo("ERROR_STG2");
//        assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateerror, "4290023667710001", 0, "Yes")).orElse("Null"))
//                .isEqualTo(AAAMultipolicyDiscountStatus.ERROR_STG2);
    }

    //Membership:CANCELLED
    //MPD: QUOTED
    //BMS: ERROR_STG2 Error from RMS Endorse Image
    //MPDVS:ERROR_STG2 Endorse image

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-28452 MPD Validation Phase 3: System to validate companion products at NB+30 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28452-1")
    public void pas28452_MPD_STG2_Error_3(@Optional("MD") String state) {
        String response = AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2_update_mem(populateerror, "4290074030137505", 0, "Yes")).orElse("Null");
        assertThat(response).isEqualTo("ERROR_STG2");
                //.isEqualTo(AAAMultipolicyDiscountStatus.ERROR_STG2);
    }


    private String mpdSTG2(String wiremockValue, String membershipNumber, int catchup, String insurercd) {
        GeneralTab generalTab = new GeneralTab();
        ErrorTab errorTab = new ErrorTab();
        //TestData testData = getPolicyTD().adjust(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL.getLabel(),populateData);
        TestData testData = getPolicyTD();
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, GeneralTab.class,true);
        //Set Membership to Yes or No
        generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue(insurercd);
        //Set the membership number to either cancelled or active
        generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue(membershipNumber);
        //puts quoted products into the MPD table with REFRESH_Q
        generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue(populateData);
        generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                .click(Waiters.AJAX);
        //Sets the value to REFRESH_P so when the job see quoted products from REFRESH_Q, picks it up and refreshes it will see REFRESH_P and put the active products there
        generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue(wiremockValue);
        generalTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), DriverTab.class, PurchaseTab.class, true);
        if (errorTab.tableErrors.isPresent()) {
            errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_MVR_order_validation_SS);
            errorTab.override();
            PurchaseTab purchaseTab = new PurchaseTab();
            purchaseTab.submitTab();
        } else {
            PurchaseTab purchaseTab = new PurchaseTab();
            purchaseTab.submitTab();
        }

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        log.info("Effectivedate"+policyEffectiveDate);
        log.info("policyNumber"+PolicySummaryPage.getPolicyNumber());
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(30).plusDays(catchup));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
        JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
        JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
        JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
        JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
        JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
        setTimeToToday();
        return policyNumber;
    }

    private String mpdSTG2_update_mem(String wiremockValue, String membershipNumber, int catchup, String insurercd) {
        GeneralTab generalTab = new GeneralTab();
        ErrorTab errorTab = new ErrorTab();
        //TestData testData = getPolicyTD().adjust(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL.getLabel(),populateData);
        TestData testData = getPolicyTD();
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, GeneralTab.class,true);
        generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue(insurercd);
        generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue(membershipNumber);
        generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue(populateData);
        generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                .click(Waiters.AJAX);
        generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue(wiremockValue);
        generalTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), DriverTab.class, PurchaseTab.class, true);
        if (errorTab.tableErrors.isPresent()) {
            errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_MVR_order_validation_SS);
            errorTab.override();
            PurchaseTab purchaseTab = new PurchaseTab();
            purchaseTab.submitTab();
        } else {
            PurchaseTab purchaseTab = new PurchaseTab();
            purchaseTab.submitTab();
        }

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        AAAMembershipQueries.updateAAAMembershipNumberInSQL(policyNumber,membershipNumber.substring(0,14));
        AAAMembershipQueries.updatePriorAAAMembershipNumberInSQL(policyNumber,membershipNumber.substring(0,14));
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        log.info("Effectivedate"+policyEffectiveDate);
        log.info("policyNumber"+PolicySummaryPage.getPolicyNumber());
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(30).plusDays(catchup));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
        JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
        JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
        JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
        JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
        JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
        setTimeToToday();
        return policyNumber;
    }

    public void setTimeToToday() {
        log.info("Current application date: " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        TimeSetterUtil.getInstance().adjustTime();
    }
}