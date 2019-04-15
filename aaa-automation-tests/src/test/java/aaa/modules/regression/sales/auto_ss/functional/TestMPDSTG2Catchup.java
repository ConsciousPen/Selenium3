package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
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
    String cancelledMS = "3111111111111121";

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
    //TODO: Not WORKING
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-858 MPD Validation Phase 3: System to validate companion products at NB+15 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-858-1")
    public void pas858_MPD_FOUND_STG1_MinusOne(@Optional("") String state) {
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateDataForBatch_NotFound,cancelledMS, -1)).isEmpty());
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-858 MPD Validation Phase 3: System to validate companion products at NB+15 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-858-1")
    public void pas858_MPD_FOUND_STG1_Zero(@Optional("") String state) {
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateDataForBatch_NotFound,cancelledMS,0))).isEqualTo(AAAMultipolicyDiscountStatus.NOTFOUND_STG1);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-858 MPD Validation Phase 3: System to validate companion products at NB+15 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-858-1")
    public void pas858_MPD_FOUND_STG1_One(@Optional("") String state) {
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(mpdSTG2(populateDataForBatch_NotFound,cancelledMS,0)).contains("STG1"));
        //assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(policyNumber).contains("0"));
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-858 MPD Validation Phase 3: System to validate companion products at NB+15 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-858-1")
    public void pas858_MPD_FOUND_STG1_Five(@Optional("") String state) {
        TestData testData = getPolicyTD();
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, GeneralTab.class,true);
        GeneralTab generalTab = new GeneralTab();
        generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue("CUSTOMER_GBY@yeah.com");
        generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                .click(Waiters.AJAX);
        generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue("NOT_FOUND@yeah.com");
        generalTab.submitTab();
        policy.getDefaultView().fillFromTo(testData, DriverTab.class, PurchaseTab.class, true);
        PurchaseTab purchaseTab = new PurchaseTab();
        purchaseTab.submitTab();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        mpdSTG2(populateDataForBatch_NotFound,cancelledMS, 0);
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(policyNumber).isEmpty());
        //assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(policyNumber).contains("STG1"));
        //assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(policyNumber).contains("0"));
    }

    private String mpdSTG2(String wiremockValue, String membershipNumber, int catchup) {
        GeneralTab generalTab = new GeneralTab();
        ErrorTab errorTab = new ErrorTab();
        //TestData testData = getPolicyTD().adjust(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL.getLabel(),populateData);
        TestData testData = getPolicyTD();
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, GeneralTab.class,true);
        generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
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