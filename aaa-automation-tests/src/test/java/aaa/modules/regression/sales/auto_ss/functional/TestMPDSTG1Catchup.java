package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMultiPolicyDiscountQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
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

import static org.assertj.core.api.Assertions.assertThat;

public class TestMPDSTG1Catchup extends AutoSSBaseTest {

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
    //TODO: WORKING
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-858 MPD Validation Phase 3: System to validate companion products at NB+15 ")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-858-1")
    public void pas858_MPD_NOT_FOUND_STG1(@Optional("") String state) {
        TestData testData = getPolicyTD();
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, GeneralTab.class,true);
        GeneralTab generalTab = new GeneralTab();
        generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue("CUSTOMER_GBY@yeah.com");

        generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                .click(Waiters.AJAX);
        //new aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab().getAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Membership Pending");
        //generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("No");
        generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL).setValue("NOT_FOUND@yeah.com");
        //TestData td  = getTestSpecificTD("TestData_MPD_NOT_FOUND").resolveLinks();
        generalTab.submitTab();
        policy.getDefaultView().fillFromTo(testData, DriverTab.class, PurchaseTab.class, true);
        PurchaseTab purchaseTab = new PurchaseTab();
        purchaseTab.submitTab();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        mpdSTG1(0);
        assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(policyNumber).contains("STG1"));
        //assertThat(AAAMultiPolicyDiscountQueries.getMPDVStatusandTxTypeFromDB(policyNumber).contains("0"));

    }
//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-858 MPD Validation Phase 3: System to validate companion products at NB+15 ")
//    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-858-2")
//    public void pas858_MPD_FOUND_STG1(@Optional("") String state) {
//        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_MPD_GBY").resolveLinks());
//        mainApp().open();
//        createCustomerIndividual();
//        policy.initiate();
//        policy.getDefaultView().fillUpTo(testData, GeneralTab.class,true);
//        GeneralTab generalTab = new GeneralTab();
//        generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
//                .click(Waiters.AJAX);
//        TestData td  = getTestSpecificTD("TestData_MPD_ACTIVE").resolveLinks();
//        policy.getDefaultView().fillFromTo(td,GeneralTab.class, PurchaseTab.class, true);
//        PurchaseTab purchaseTab = new PurchaseTab();
//        purchaseTab.submitTab();
//        mpdSTG1(1);
//        //   assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG1(getNonAAAMemberPolicyTestData(),0))).isEmpty();
//
//    }

    private void mpdSTG1(int catchup) {
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        log.info("Effectivedate"+policyEffectiveDate);
        log.info("policyNumber"+PolicySummaryPage.getPolicyNumber());
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(15).plusDays(catchup));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
        JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
        JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
        JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
        JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
        JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
    }
}