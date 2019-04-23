package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

public class TestMembershipSTG3Catchup extends AutoSSBaseTest {
    /**
     * @author Rajesh Nayak, Robert Boles
     * @name PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time (AC#1)
     * @scenario Precondition: Bind Product Eligible for Membership (Membership = No)
     * 1. Create Customer.
     * 2. Create Auto SS Policy
     * 3. Run Membership Validation Batch Jobs at Time point 3 (STG3)
     * 4. Retrieve BestMembershipStatus from DB to confirm policy was picked up during catchup logic (NOTFOUND_STG3)
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-1")
    public void pas27611_MembershipValidationNoMinus1(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getNonAAAMemberPolicyTestData(), -1))).isEmpty();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-2")
    public void pas27611_MembershipValidationNo_Zero(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getNonAAAMemberPolicyTestData(),0)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG3);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-3")
    public void pas27611_MembershipValidationNo_One(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getNonAAAMemberPolicyTestData(),1)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG3);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-4")
    public void pas27611_MembershipValidationNo_Four(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getNonAAAMemberPolicyTestData(),4)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG3);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-5")
    public void pas27611_MembershipValidationNo_Five(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getNonAAAMemberPolicyTestData(),5))).isEmpty();
    }

    /**
     * @author Rajesh Nayak, Robert Boles
     * @name PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time (AC#1)
     * @scenario Precondition: Bind Product Eligible for Membership (Membership = Yes)
     * 1. Create Customer.
     * 2. Create Auto SS Policy
     * 3. Run Membership Validation Batch Jobs at Time point 3 (STG3)
     * 4. Retrieve BestMembershipStatus from DB to confirm policy was picked up during catchup logic (FOUND_STG3)
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-6")
    public void pas27611_MembershipValidationYesMinus1(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getPolicyTD(), -1))).isEmpty();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-7")
    public void pas27611_MembershipValidationYes_Zero(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getPolicyTD(),0)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG3);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-8")
    public void pas27611_MembershipValidationYes_One(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getPolicyTD(),1)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG3);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-9")
    public void pas27611_MembershipValidationYes_Four(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getPolicyTD(),4)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG3);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-10")
    public void pas27611_MembershipValidationYes_Five(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG3(getPolicyTD(),5))).isEmpty();
    }

 //START OF STG4 tests
    /**
     * @author Rajesh Nayak, Robert Boles
     * @name PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time (AC#1)
     * @scenario Precondition: Bind Product NOT Eligible for Membership (Membership = No)
     * 1. Create Customer.
     * 2. Create Auto SS Policy
     * 3. Run Membership Validation Batch Jobs at Time point 4 (STG4)
     * 4. Retrieve BestMembershipStatus from DB to confirm policy was picked up during catchup logic (NOTFOUND_STG4)
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-11")
    public void pas27611_MembershipValidationNoSGT4Minus1(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getNonAAAMemberPolicyTestData(), -1))).isEmpty();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-12")
    public void pas27611_MembershipValidationNoSGT4_Zero(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getNonAAAMemberPolicyTestData(), 0)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG4);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-13")
    public void pas27611_MembershipValidationNoSGT4_One(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getNonAAAMemberPolicyTestData(), 1)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG4);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-14")
    public void pas27611_MembershipValidationNoSGT4_Two(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getNonAAAMemberPolicyTestData(), 2)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.NOTFOUND_STG4);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-15")
    public void pas27611_MembershipValidationNoSGT4_Three(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getNonAAAMemberPolicyTestData(), 3))).isEmpty();
    }
    /**
     * @author Rajesh Nayak, Robert Boles
     * @name PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time (AC#1)
     * @scenario Precondition: Bind Product Eligible for Membership (Membership = Yes)
     * 1. Create Customer.
     * 2. Create Auto SS Policy
     * 3. Run Membership Validation Batch Jobs at Time point 4 (STG4)
     * 4. Retrieve BestMembershipStatus from DB to confirm policy was picked up during catchup logic (FOUND_STG4)
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-16")
    public void pas27611_MembershipValidationYesMinus1_STG4(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getPolicyTD(), -1))).isEmpty();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-17")
    public void pas27611_MembershipValidationYes_Zero_STG4(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getPolicyTD(),0)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG4);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-18")
    public void pas27611_MembershipValidationYes_One_STG4(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getPolicyTD(),1)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG4);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-19")
    public void pas27611_MembershipValidationYes_Two_STG4(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getPolicyTD(),2)))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG4);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "PAS-27116 Update Renewal Logic so that policies without an active membership still go through the membership validation at renewal time")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27116-20")
    public void pas27611_MembershipValidationYes_Three_STG4(@Optional("") String state) {
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(membershipSTG4(getPolicyTD(),3))).isEmpty();
        setTimeToToday();
    }

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
        JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
        JobUtils.executeJob(BatchJob.policyAutomatedRenewalAsyncTaskGenerationJob);

        //R-63 Membership Tp1
        LocalDateTime membershipTp1 = getTimePoints().getMembershipTp1(policyExpirationDate).plusDays(catchup);
        log.info("R-63 Membership Tp1 " + membershipTp1);
        TimeSetterUtil.getInstance().nextPhase(membershipTp1);
        JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
        JobUtils.executeJob(BatchJob.aaaMembershipRenewalBatchOrderAsyncJob);
        setTimeToToday();
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
        JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
        JobUtils.executeJob(BatchJob.policyAutomatedRenewalAsyncTaskGenerationJob);
        log.info("Current BMS# at image creation" + AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber));
        log.info("Current MS# at image creation" + AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber));

        //reportServices = R-48 Membership Tp2
        LocalDateTime membershipTp2 = getTimePoints().getMembershipTp2(policyExpirationDate).plusDays(catchup);
        log.info("R-63 Membership Tp1 " + membershipTp2);
        TimeSetterUtil.getInstance().nextPhase(membershipTp2);
        log.info("R-48 Membership Tp2 " + membershipTp2);
        JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
        JobUtils.executeJob(BatchJob.aaaMembershipRenewalBatchOrderAsyncJob);
        setTimeToToday();
        return policyNumber;
    }

    /**
     * Used to get test data that will set No to Membership status.
     * @return TestData that ensures Current AAA Member to No.
     */
    private TestData getNonAAAMemberPolicyTestData() {

        String keypathCurrentMember;
        String keypathMemberNum;
        String keypathOrderMembershipReports = "";

                // keypathTabSection Result: "GeneralTab|AAAProductOwned"
                String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.customer.defaulttabs.GeneralTab.class.getSimpleName(),
                        AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());

        TestData testData =  getPolicyTD()
                .adjust(keypathCurrentMember, "No")
                .mask(keypathMemberNum);

        // Skip ordering the membership report if a keypath provided
        if(!keypathOrderMembershipReports.isEmpty()){
            testData.mask(keypathOrderMembershipReports);
        }
        return testData;
    }

    public void setTimeToToday() {
        log.info("Current application date: " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        TimeSetterUtil.getInstance().adjustTime();
    }
}