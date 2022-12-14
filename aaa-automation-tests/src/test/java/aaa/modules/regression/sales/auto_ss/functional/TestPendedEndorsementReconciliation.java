package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

/**
 * This test class is intended for testing the removal of Pended Endorsements before a variety of discount removals. <br>
 *     Discount removal occurs at NB+15 (STG1) and NB+30 (STG2).
 */
public class TestPendedEndorsementReconciliation extends AutoSSBaseTest {

    // Static data used throughout test class.
    private enum eTimepoints {STG1, STG2, STG3, STG4}
    private enum eThresholdTest {BEFORE, ON, AFTER}
    private enum eMembershipType {ACTIVE, CANCELLED}
    private static int CATCHUP_TIMEFRAME_VALUE = 4;

    private GeneralTab _generalTab = new GeneralTab();
    private PurchaseTab _purchaseTab = new PurchaseTab();

    // TEST CASES:

    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_Membership_STG1_BeforeThreshold(@Optional("AZ") String state) {
        doMembershipTest(eThresholdTest.BEFORE, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE - 1, true);
    }

    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_Membership_STG1_OnThreshold(@Optional("AZ") String state) {
        doMembershipTest(eThresholdTest.ON, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE, true);
    }

    @Parameters({"state"})
    @Test(enabled = false, groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_Membership_STG1_AfterThreshold(@Optional("AZ") String state) {
        doMembershipTest(eThresholdTest.AFTER, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE + 1, false);
    }

    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_Membership_STG2_BeforeThreshold(@Optional("AZ") String state) {
        doMembershipTest(eThresholdTest.BEFORE, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE - 1, true);
    }

    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_Membership_STG2_OnThreshold(@Optional("AZ") String state) {
        doMembershipTest(eThresholdTest.ON, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE, true);
    }

    @Parameters({"state"})
    @Test(enabled = false, groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_Membership_STG2_AfterThreshold(@Optional("AZ") String state) {
        doMembershipTest(eThresholdTest.AFTER, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE + 1, false);
    }

    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.MPD, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_MPD_BeforeThreshold(@Optional("UT")String state) {
        doMPDTest(eThresholdTest.BEFORE, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE - 1, true);
    }

    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.MPD, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_MPD_OnThreshold(@Optional("UT")String state) {
        doMPDTest(eThresholdTest.ON, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE, true);
    }

    @Parameters({"state"})
    @Test(enabled = false, groups = { Groups.CIO, Groups.MPD, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_MPD_AfterThreshold(@Optional("UT")String state) {
        doMPDTest(eThresholdTest.AFTER, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE + 1, false);
    }

    /**
     * Stand-Alone functional test used to regress that an Active Membership is not processed at Stage 1 or Stage 2.
     */
    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.MPD, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void ActiveMembershipNotProcessed_STG1(@Optional("AZ") String state) {
        doActiveMembershipNotPickedUpTest(eThresholdTest.BEFORE, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE - 1, false);
    }

    /**
     * Stand-Alone functional test used to regress that an Active Membership is not processed at Stage 1 or Stage 2.
     */
    @Parameters({"state"})
    @Test(groups = { Groups.CIO, Groups.MPD, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void ActiveMembershipNotProcessed_STG2(@Optional("AZ") String state) {
        doActiveMembershipNotPickedUpTest(eThresholdTest.BEFORE, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE - 1, false);
    }

    @Test(enabled = false, groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void NoDuplicateEndorsements_MembershipSTG2(@Optional String state, eTimepoints stg_x, Integer daysAfterNB) {
        // TODO: Once fix is in, add regression coverage to ensure no duplicate endorsements are created at STG2 by re-running membership validation job n the same day.
    }

    // TEST METHODS:

    private TestData prepareTestData(){
        //Create TestData that has AAA Membership == NO initially.
        TestData createdTD = getPolicyDefaultTD();
        createdTD = TestDataHelper.adjustTD(createdTD, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), "Yes");
        createdTD = TestDataHelper.adjustTD(createdTD, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "3111111111111121");

        return createdTD;
    }

    /**
     * Initiate the test. Open the app, create a customer and initiate a quote.
     */
    private void prepareForPolicyCreation(TestData in_td){
        createQuoteAndFillUpTo(in_td, GeneralTab.class, true);
    }

    /**
     * This method should begin when the web-driver is viewing the GeneralTab.
     * @param bHasMembership Should the test add a AAA Membership?
     * @param bHasMPD Should the test add MPD?
     */
    private String handlePolicyCreation(TestData in_td, Boolean bHasMembership, Boolean bHasMPD, eMembershipType membershipType){
        if(bHasMembership){
            addMembershipToGeneralTab(membershipType);
        }

        if(bHasMPD){
            addCompanionPolicyToGeneralTab();
        }

        // Continue to next page (Driver's Tab). Then complete policy bind.
        Tab.buttonNext.click();
        policy.getDefaultView().fillFromTo(in_td, DriverTab.class, PurchaseTab.class, false);

        // Override unexpected errors.
        new ErrorTab().overrideAllErrors();
        _purchaseTab.fillTab(in_td);
        _purchaseTab.submitTab();
        return PolicySummaryPage.getPolicyNumber();
    }

    private void addPendedEndorsement(){
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
        _generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME).setValue("Doug");
        _generalTab.saveAndExit();
    }

    private void validatePendedEndorsementPresent(){
        // If this is not true, there's no point in continuing the test.
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled();
    }

    private void advanceJVMToTimepoint(eTimepoints STG_N, Integer in_daysAfterNB, eThresholdTest in_typeOfBoundryTest, LocalDateTime policyEffDate){
        // Move JVM to appropriate Stage.
        LocalDateTime date = null;
        switch(STG_N){
            case STG1:
                date = policyEffDate.plusDays(15 + in_daysAfterNB);
                break;
            case STG2:
                date = policyEffDate.plusDays(30 + in_daysAfterNB);
                break;
            default:
                CustomAssertions.fail("STG_N has an unexpected/unhandled value: " + STG_N);
                break;
        }

        // Handle the fact that the job doesn't run on the weekends.
        if(date.getDayOfWeek()== DayOfWeek.SATURDAY){
            if (in_daysAfterNB == CATCHUP_TIMEFRAME_VALUE + 1) {
                date = date.plusDays(2);
            } else {
                date = date.minusDays(1);
            }
            log.debug(String.format("QALOGS -> Moving to new date = %s", date.toString()));
        }
        if(date.getDayOfWeek()== DayOfWeek.SUNDAY){
            if (in_daysAfterNB == CATCHUP_TIMEFRAME_VALUE) {
                date = date.minusDays(2);
            } else {
                date = date.plusDays(1);
            }
            log.debug(String.format("QALOGS -> Moving to new date = %s", date.toString()));
        }

        // Get System date. Is JVM within desired timeframe for test?
        LocalDateTime nbDate = null;
        if (STG_N == eTimepoints.STG1){
            nbDate = policyEffDate.plusDays(15);
        }
        if (STG_N == eTimepoints.STG2){
            nbDate = policyEffDate.plusDays(30);
        }

        LocalDateTime thresholdMaxDate = nbDate.plusDays(CATCHUP_TIMEFRAME_VALUE);

        switch(in_typeOfBoundryTest){
            case BEFORE:
                assertThat(date).isAfter(nbDate);
                assertThat(date).isBeforeOrEqualTo(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER %s and BEFORE the threshold cut off on %s.",
                        date.toLocalDate().toString(), nbDate.toString(), thresholdMaxDate.toString()));
                break;
            case ON:
                assertThat(date).isBeforeOrEqualTo(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER %s and ON the threshold cut off on %s.",
                        date.toLocalDate().toString(), nbDate.toString(), thresholdMaxDate.toString()));
                break;
            case AFTER:
                assertThat(date).isAfter(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER %s and AFTER the threshold cut off on %s.",
                        date.toLocalDate().toString(), nbDate.toString(), thresholdMaxDate.toString()));
                break;
            default:
                CustomAssertions.fail("Unexpected value for 'typeOfThresholdTest'. Force failing test.");
                break;
        }

        TimeSetterUtil.getInstance().nextPhase(date);

    }

    private void runBatchJobs(eTimepoints STG_N, boolean bSetMembershipActive, String policyNumber){
        if(bSetMembershipActive){
            AAAMembershipQueries.updateLatestNewBusinessAAAMembershipStatusInSQL(policyNumber, AAAMembershipQueries.AAAMembershipStatus.ACTIVE);
        }

        switch(STG_N){
            case STG1:
                JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
                JobUtils.executeJob(BatchJob.membershipValidationJob);
                break;
            case STG2:
                JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
                JobUtils.executeJob(BatchJob.membershipValidationJob);
                break;
            default:
                CustomAssertions.fail("STG_N has an unexpected/unhandled value: " + STG_N);
                break;
        }
    }

    private void queryDBForNumberOfPendingEndorsements(Boolean bShouldEndorsementsBeDeleted, String policyNumber){
        Integer numberOfRowsReturnedFromQuery = AAAMembershipQueries.getNumberOfPendedEndorsements(policyNumber);

        if(bShouldEndorsementsBeDeleted){
            assertThat(numberOfRowsReturnedFromQuery).isEqualTo(0);
        }
        else{
            assertThat(numberOfRowsReturnedFromQuery).isEqualTo(1);
        }
    }

    private void assertPolicyProcessedStatus(String policyNumber, Boolean expectingPolicyToBeProcessed){
        java.util.Optional<AAAMembershipQueries.AAABestMembershipStatus> dbResponse = AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber);
        if(expectingPolicyToBeProcessed){

            assertThat(dbResponse.toString()).containsIgnoringCase("FOUND");
        }
        else{
            assertThat(dbResponse.toString()).doesNotContain("FOUND");
        }
        log.debug(String.format(System.lineSeparator() + "<QALOGS> Ran 'assertPolicyProcessedStatus();' DB RESPONSE = ", dbResponse) + System.lineSeparator());
    }

    private void addMembershipToGeneralTab(eMembershipType membershipType){
        _generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Yes");
        switch(membershipType){
            case ACTIVE:
                _generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("9999999999999995");
                break;
            case CANCELLED:
                _generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER).setValue("3111111111111121");
                break;
            default:
                _generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("No");
                break;
        }
    }

    private void addCompanionPolicyToGeneralTab(){
        _generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME).setValue("REFRESH_PQ");
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH).click();
    }

    private void doMembershipTest(eThresholdTest in_typeOfBoundryTest, eTimepoints in_stg_x, Integer in_daysAfterNB, Boolean expectingPolicyToBeProcessed){
        TestData testLevelTD = prepareTestData();
        prepareForPolicyCreation(testLevelTD);
        String policyNumber = handlePolicyCreation(testLevelTD, true, false, eMembershipType.CANCELLED);
        LocalDateTime policyEffDate = PolicySummaryPage.getEffectiveDate();
        addPendedEndorsement();
        validatePendedEndorsementPresent();
        advanceJVMToTimepoint(in_stg_x, in_daysAfterNB, in_typeOfBoundryTest, policyEffDate);
        runBatchJobs(in_stg_x, false, policyNumber);
        assertPolicyProcessedStatus(policyNumber, expectingPolicyToBeProcessed);
        queryDBForNumberOfPendingEndorsements(expectingPolicyToBeProcessed, policyNumber);
    }

    private void doMPDTest(eThresholdTest in_typeOfBoundryTest, eTimepoints in_stg_x, Integer in_daysAfterNB, Boolean expectingPolicyToBeProcessed){
        TestData testLevelTD = prepareTestData();
        prepareForPolicyCreation(testLevelTD);
        String policyNumber = handlePolicyCreation(testLevelTD, true, true, eMembershipType.CANCELLED);
        LocalDateTime policyEffDate = PolicySummaryPage.getEffectiveDate();
        addPendedEndorsement();
        validatePendedEndorsementPresent();
        advanceJVMToTimepoint(in_stg_x, in_daysAfterNB, in_typeOfBoundryTest, policyEffDate);
        runBatchJobs(in_stg_x, false, policyNumber);
        assertPolicyProcessedStatus(policyNumber, expectingPolicyToBeProcessed);
        queryDBForNumberOfPendingEndorsements(expectingPolicyToBeProcessed, policyNumber);
    }

    private void doActiveMembershipNotPickedUpTest(eThresholdTest in_typeOfBoundryTest, eTimepoints in_stg_x, Integer in_daysAfterNB, Boolean expectingPolicyToBeProcessed){
        TestData testLevelTD = prepareTestData();
        prepareForPolicyCreation(testLevelTD);
        String policyNumber = handlePolicyCreation(testLevelTD, true, false, eMembershipType.ACTIVE);
        LocalDateTime policyEffDate = PolicySummaryPage.getEffectiveDate();
        advanceJVMToTimepoint(in_stg_x, in_daysAfterNB, in_typeOfBoundryTest, policyEffDate);
        runBatchJobs(in_stg_x, true, policyNumber);
        assertPolicyProcessedStatus(policyNumber, expectingPolicyToBeProcessed);
    }
}
