package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.CheckBox;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * This test class is intended for testing the removal of Pended Endorsements before a variety of discount removals. <br>
 *     Discount removal occurs at NB+15 (STG1) and NB+30 (STG2).
 */
public class TestPendedEndorsementReconciliation extends AutoSSBaseTest {

    // Static data used throughout test class.
    private enum eTimepoints {STG1, STG2, STG3, STG4}
    private enum eThresholdTest {BEFORE, ON, AFTER}
    private enum eMembershipType {ACTIVE, CANCELLED}
    static private int CATCHUP_TIMEFRAME_VALUE = 4;
    static private boolean _bExpectingPolicyToBeProcessed = true;

    static private String _storedPolicyNumber = null;
    static private LocalDateTime _policyEffectiveDate = null;
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
    @Test(groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
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
    @Test(groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
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
    @Test(groups = { Groups.CIO, Groups.MPD, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
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
    private void prepareForPolicyCreation(TestData in_td, boolean in_bExpectingPolicyToBeProcessed){
        createQuoteAndFillUpTo(in_td, GeneralTab.class, true);
        _bExpectingPolicyToBeProcessed = in_bExpectingPolicyToBeProcessed;
    }

    /**
     * This method should begin when the web-driver is viewing the GeneralTab.
     * @param bHasMembership Should the test add a AAA Membership?
     * @param bHasMPD Should the test add MPD?
     */
    private void handlePolicyCreation(TestData in_td, Boolean bHasMembership, Boolean bHasMPD, eMembershipType membershipType){
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
        _storedPolicyNumber = PolicySummaryPage.getPolicyNumber();
        _policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
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

    private void advanceJVMToTimepoint(eTimepoints STG_N, Integer in_daysAfterNB, eThresholdTest in_typeOfBoundryTest){
        // Move JVM to appropriate Stage.
        LocalDateTime date = null;
        switch(STG_N){
            case STG1:
                date = _policyEffectiveDate.plusDays(15 + in_daysAfterNB);
                break;
            case STG2:
                date = _policyEffectiveDate.plusDays(30 + in_daysAfterNB);
                break;
            default:
                CustomAssertions.fail("STG_N has an unexpected/unhandled value: " + STG_N.toString());
                break;
        }
        TimeSetterUtil.getInstance().nextPhase(date);

        // Get System date. Is JVM within desired timeframe for test?
        LocalDateTime rightNow = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime nbDate = null;
        if (STG_N == eTimepoints.STG1){
            nbDate = _policyEffectiveDate.plusDays(15);
        }
        if (STG_N == eTimepoints.STG2){
            nbDate = _policyEffectiveDate.plusDays(30);
        }

        LocalDateTime thresholdMaxDate = nbDate.plusDays(CATCHUP_TIMEFRAME_VALUE);

        switch(in_typeOfBoundryTest){
            case BEFORE:
                CustomAssertions.assertThat(rightNow).isAfter(nbDate);
                CustomAssertions.assertThat(rightNow).isBefore(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER %s and BEFORE the threshold cut off on %s.",
                        rightNow.toLocalDate().toString(), nbDate.toString(), thresholdMaxDate.toString()));
                break;
            case ON:
                CustomAssertions.assertThat(rightNow).isEqualToIgnoringHours(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER %s and ON the threshold cut off on %s.",
                        rightNow.toLocalDate().toString(), nbDate.toString(), thresholdMaxDate.toString()));
                break;
            case AFTER:
                CustomAssertions.assertThat(rightNow).isAfter(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER %s and AFTER the threshold cut off on %s.",
                        rightNow.toLocalDate().toString(), nbDate.toString(), thresholdMaxDate.toString()));
                // Handle another edge case where Threshold date lands on weekend and job is not expecting to run to pick it up the following weekday.
                if(rightNow.getDayOfWeek()==DayOfWeek.SATURDAY || rightNow.getDayOfWeek()==DayOfWeek.SUNDAY){
                    log.debug(String.format("QALOGS -> Threshold Date = %s (%s). Updated Result Expectations.", thresholdMaxDate.toString(), thresholdMaxDate.getDayOfWeek()));
                    _bExpectingPolicyToBeProcessed = true;
                }
                break;
            default:
                CustomAssertions.fail("Unexpected value for 'typeOfThresholdTest'. Force failing test.");
                break;
        }

        // Handle the fact that the job doesn't run on the weekends.
        if(rightNow.getDayOfWeek()== DayOfWeek.SATURDAY){
            rightNow = rightNow.plusDays(2);
            log.debug(String.format("QALOGS -> Moving to new date = %s", rightNow.toString()));
            TimeSetterUtil.getInstance().nextPhase(rightNow);
        }
        if(rightNow.getDayOfWeek()== DayOfWeek.SUNDAY){
            rightNow = rightNow.plusDays(1);
            log.debug(String.format("QALOGS -> Moving to new date = %s", rightNow.toString()));
            TimeSetterUtil.getInstance().nextPhase(rightNow);
        }
    }

    private void runBatchJobs(eTimepoints STG_N, boolean bSetMembershipActive){
        if(bSetMembershipActive){
            AAAMembershipQueries.updateLatestNewBusinessAAAMembershipStatusInSQL(_storedPolicyNumber, AAAMembershipQueries.AAAMembershipStatus.ACTIVE);
        }

        switch(STG_N){
            case STG1:
                JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
                JobUtils.executeJob(Jobs.membershipValidationJob);
                break;
            case STG2:
                JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
                JobUtils.executeJob(Jobs.membershipValidationJob);
                break;
            default:
                CustomAssertions.fail("STG_N has an unexpected/unhandled value: " + STG_N.toString());
                break;
        }
    }

    private void queryDBForNumberOfPendingEndorsements(Boolean bShouldEndorsementsBeDeleted){
        Integer numberOfRowsReturnedFromQuery = AAAMembershipQueries.getNumberOfPendedEndorsements(_storedPolicyNumber);

        if(bShouldEndorsementsBeDeleted){
            CustomAssertions.assertThat(numberOfRowsReturnedFromQuery).isEqualTo(0);
        }
        else{
            CustomAssertions.assertThat(numberOfRowsReturnedFromQuery).isEqualTo(1);
        }
    }

    private void assertPolicyProcessedStatus(){
        java.util.Optional<AAAMembershipQueries.AAABestMembershipStatus> dbResponse = AAAMembershipQueries.getAAABestMembershipStatusFromSQL(_storedPolicyNumber);
        if(_bExpectingPolicyToBeProcessed){

            CustomAssertions.assertThat(dbResponse.toString()).containsIgnoringCase("FOUND");
        }
        else{
            CustomAssertions.assertThat(dbResponse.toString()).doesNotContain("FOUND");
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

    public void setTimeToToday() {
        log.info("Current application date: " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        TimeSetterUtil.getInstance().adjustTime();
    }

    private void doMembershipTest(eThresholdTest in_typeOfBoundryTest, eTimepoints in_stg_x, Integer in_daysAfterNB, Boolean in_bExpectingPolicyToBeProcessed){
        eThresholdTest typeOfBoundryTest = in_typeOfBoundryTest;
        eTimepoints stg_x = in_stg_x;
        Integer daysAfterNB = in_daysAfterNB;
        Boolean bExpectingPolicyToBeProcessed = in_bExpectingPolicyToBeProcessed;

        TestData testLevelTD = prepareTestData();
        prepareForPolicyCreation(testLevelTD, bExpectingPolicyToBeProcessed);
        handlePolicyCreation(testLevelTD, true, false, eMembershipType.CANCELLED);
        addPendedEndorsement();
        validatePendedEndorsementPresent();
        advanceJVMToTimepoint(stg_x, daysAfterNB, typeOfBoundryTest);
        runBatchJobs(stg_x, false);
        assertPolicyProcessedStatus();
        queryDBForNumberOfPendingEndorsements(_bExpectingPolicyToBeProcessed);
    }

    private void doMPDTest(eThresholdTest in_typeOfBoundryTest, eTimepoints in_stg_x, Integer in_daysAfterNB, Boolean in_bExpectingPolicyToBeProcessed){
        eThresholdTest typeOfBoundryTest = in_typeOfBoundryTest;
        eTimepoints stg_x = in_stg_x;
        Integer daysAfterNB = in_daysAfterNB;
        Boolean bExpectingPolicyToBeProcessed = in_bExpectingPolicyToBeProcessed;

        TestData testLevelTD = prepareTestData();
        prepareForPolicyCreation(testLevelTD, bExpectingPolicyToBeProcessed);
        handlePolicyCreation(testLevelTD, true, true, eMembershipType.CANCELLED);
        addPendedEndorsement();
        validatePendedEndorsementPresent();
        advanceJVMToTimepoint(stg_x, daysAfterNB, typeOfBoundryTest);
        runBatchJobs(stg_x, false);
        assertPolicyProcessedStatus();
        queryDBForNumberOfPendingEndorsements(_bExpectingPolicyToBeProcessed);
        setTimeToToday();
    }

    private void doActiveMembershipNotPickedUpTest(eThresholdTest in_typeOfBoundryTest, eTimepoints in_stg_x, Integer in_daysAfterNB, Boolean in_bExpectingPolicyToBeProcessed){
        eThresholdTest typeOfBoundryTest = in_typeOfBoundryTest;
        eTimepoints stg_x = in_stg_x;
        Integer daysAfterNB = in_daysAfterNB;
        Boolean bExpectingPolicyToBeProcessed = in_bExpectingPolicyToBeProcessed;

        TestData testLevelTD = prepareTestData();
        prepareForPolicyCreation(testLevelTD, bExpectingPolicyToBeProcessed);
        handlePolicyCreation(testLevelTD, true, false, eMembershipType.ACTIVE);
        advanceJVMToTimepoint(stg_x, daysAfterNB, typeOfBoundryTest);
        runBatchJobs(stg_x, true);
        assertPolicyProcessedStatus();
    }
}
