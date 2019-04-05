package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMembershipSTG3Catchup extends HomeSSHO3BaseTest {


    /**
     * @author Rajesh Nayak, Robert Boles
     * @name PAS-27611 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time (AC#1)
     * @scenario Precondition: Bind Product Eligible for Membership
     * 1. Create Customer.
     * 2. Create Home SS Policy without Membership
     * 3. Run Membership Validation Batch Jobs at Renewal Time point 1 and Time point 2
     * //TODO: we are not doing any MS discount validation - can this be done via DB?
     * 4. Retrieve Renewal image and verify that Membership discount is provided
     * @details
     */
    //TODO: WORKING
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27611 ")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
    public void pas27611_MembershipValidationNoMinus1(@Optional("") String state) {
        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_No").resolveLinks());
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(testData, -1))).isEmpty();

    }
    //TODO: WORKING
//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
//    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
//    public void pas27611_MembershipValidationNo_Zero(@Optional("") String state) {
//        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_No").resolveLinks());
//        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(testData,0)))
//                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG3);
//    }
    //TODO: WORKING
//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
//    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
//    public void pas27611_MembershipValidationNo_One(@Optional("") String state) {
//        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_No").resolveLinks());
//        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(testData,1)))
//                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG3);
//    }
    //TODO: WORKING
//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
//    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
//    public void pas27611_MembershipValidationNo_Four(@Optional("") String state) {
//        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_No").resolveLinks());
//        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(testData,4)))
//                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG3);
//    }
//
    //TODO: WORKING
//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
//    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
//    public void pas27611_MembershipValidationNo_Five(@Optional("") String state) {
//        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_No").resolveLinks());
//        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(testData,5))).isEmpty();
//    }

    //TODO: WORKING
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27611 ")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
    public void pas27611_MembershipValidationYesMinus1(@Optional("") String state) {
        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_Override_Life").resolveLinks());
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(testData, -1))).isEmpty();
    }
    //TODO:  WORKING
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
    public void pas27611_MembershipValidationYes_Zero(@Optional("") String state) {
        //TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_Override_Life").resolveLinks());
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getPolicyTD(),0)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG3);
    }
    //TODO: WORKING
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
    public void pas27611_MembershipValidationYes_One(@Optional("") String state) {
        //TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_Override_Life").resolveLinks());
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getPolicyTD(),1)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG3);
    }
    //TODO: WORKING
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
    public void pas27611_MembershipValidationYes_Four(@Optional("") String state) {
        //TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG1_Yes").resolveLinks());
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getPolicyTD(),4)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG3);
    }
    //TODO: WORKING
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
    public void pas27611_MembershipValidationYes_Five(@Optional("") String state) {
        //TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG1_Yes").resolveLinks());
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getPolicyTD(),5))).isEmpty();
    }
//TODO: need override?
//
//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
//    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-3")
//    public void pas6654_MembershipValidationOverride_Life(@Optional("AZ") String state) {
//
//        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_Override_Life").resolveLinks());
//        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(MembershipSTG3(testData,0)))
//          .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG3);
//    }
//TODO: need pending?

//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
//    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-3")
//    public void pas14048_AC1_Pending_STG3(@Optional("AZ") String state) {
//
//        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_Membership_Pending").resolveLinks());
//        MembershipSTG3STG4(testData,0);
//
//TODO: Nonworking STG4 test

//    @Parameters({"state"})
//    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-6654 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
//    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6654-1")
//    public void pas27611_MembershipValidationNo_STG4_Four(@Optional("") String state) {
//        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_TestMembershipSTG3_No").resolveLinks());
//        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(testData, 2)))
//                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG4);
//    }
//
//    }

    private String membershipSTG3(TestData testData, int catchup) {
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy(testData);

        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());

        //init R-73
        LocalDateTime init = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        log.info(" init R-73 Policy Renewal Image Generation Date " + init);
        TimeSetterUtil.getInstance().nextPhase(init);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);
        log.info("XXXXXXXXXXXXXXXXXXXX current BMS " + AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber));
        log.info("XXXXXXXXXXXXXXXXXXXX current MS # " + AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber));

        //R-63 Membership Tp1
        LocalDateTime membershipTp1 = getTimePoints().getMembershipTp1(policyExpirationDate).plusDays(catchup);
        log.info("R-63 Membership Tp1 " + membershipTp1);
        TimeSetterUtil.getInstance().nextPhase(membershipTp1);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);

        return policyNumber;
    }

    private String membershipSTG4(TestData testData, int catchup) {
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy(testData);

        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());

        //init R-73
        LocalDateTime init = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        log.info(" init R-73 Policy Renewal Image Generation Date " + init);
        TimeSetterUtil.getInstance().nextPhase(init);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

        //reportServices = R-48 Membership Tp2
        LocalDateTime membershipTp2 = getTimePoints().getMembershipTp2(policyExpirationDate).plusDays(catchup);
        log.info("R-63 Membership Tp1 " + membershipTp2);
        TimeSetterUtil.getInstance().nextPhase(membershipTp2);
        log.info("R-48 Membership Tp2 " + membershipTp2);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);

        //rate R-45
//        LocalDateTime ratePolicy = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
//        log.info("rate R-45" + ratePolicy);
//        TimeSetterUtil.getInstance().nextPhase(ratePolicy);
//        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
//        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);
//
//        //offerIssue R-35
//        LocalDateTime offerIssue = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
//        TimeSetterUtil.getInstance().nextPhase(offerIssue);
//        log.info("offerIssue R-35" + offerIssue);
//        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
//        JobUtils.executeJob(Jobs.renewalOfferAsyncTaskJob);
//        //Adjust Time to current time.
//        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
//        log.info("policyNumber"+policyNumber);
        return policyNumber;
    }

    private String MembershipSTG3STG4(TestData testData, int catchup) {
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy(testData);

        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());

        //init R-73
        LocalDateTime init = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        log.info(" init R-73 Policy Renewal Image Generation Date " + init);
        TimeSetterUtil.getInstance().nextPhase(init);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

        //R-63 Membership Tp1
        LocalDateTime membershipTp1 = getTimePoints().getMembershipTp1(policyExpirationDate).plusDays(catchup);
        log.info("R-63 Membership Tp1 " + membershipTp1);
        TimeSetterUtil.getInstance().nextPhase(membershipTp1);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);

        //rate R-45
        //TODO: STG4 has to have a catchup logic for 2 days
        LocalDateTime ratePolicy = getTimePoints().getMembershipTp1(policyExpirationDate).plusDays(catchup);
        //.getMembershipTp1(policyExpirationDate).plusDays(catchup);
        //.getRenewPreviewGenerationDate(policyExpirationDate);
        log.info("rate R-45" + ratePolicy);
        TimeSetterUtil.getInstance().nextPhase(ratePolicy);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);

//        //offerIssue R-35
//        LocalDateTime offerIssue = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
//        TimeSetterUtil.getInstance().nextPhase(offerIssue);
//        log.info("offerIssue R-35" + offerIssue);
//        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
//        JobUtils.executeJob(Jobs.renewalOfferAsyncTaskJob);
//        //Adjust Time to current time.
//        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
//        log.info("policyNumber"+policyNumber);

        return policyNumber;
    }


//    public void handleBillGenerationAndPayment() {
//        // Do below chunk if state is not California
//        if (!_policyState.equalsIgnoreCase("CA")){
//            TimeSetterUtil.getInstance().nextPhase(_renewalBillGenDate);
//            JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
//            JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
//            JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
//
//            mainApp().open();
//            SearchPage.openBilling(_policyNumber);
//            new BillingAccount().acceptPayment().start();
//            new AcceptPaymentActionTab().setCheckNumber(123);
//            Tab.buttonOk.click();
//            mainApp().close();
//        }
//
//        // Move forward two days. Update status.
//        TimeSetterUtil.getInstance().nextPhase(_renewalBillGenDate.plusDays(2));
//        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
//        JobUtils.executeJob(Jobs.policyStatusUpdateJob); //POLICY SHOULD BE RENEWED NOW.
//
//        // Manually ZERO OUT aaaTimelineRenewalInd
//        AAAMembershipQueries.updateAaaRenewalTimelineIndicatorValue(_policyNumber, "0");
//    }


}