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
    private PremiumAndCoveragesTab pncTab = new PremiumAndCoveragesTab();
    private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

    @DataProvider(name = "reconcilePendedEndorsements_MembershipTestData")
    public static Object[][] reconcilePendedEndorsements_MembershipTestData() {
        return new Object[][]{
                {"AZ", eThresholdTest.BEFORE, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE - 1, true},
                {"AZ", eThresholdTest.ON, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE, true},
                {"AZ", eThresholdTest.AFTER, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE + 1, false},
                {"AZ", eThresholdTest.BEFORE, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE - 1, true},
                {"AZ", eThresholdTest.ON, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE, true},
                {"AZ", eThresholdTest.AFTER, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE + 1, false}
        };
    }

    @DataProvider(name = "reconcilePendedEndorsements_EValueTestData")
    public static Object[][] reconcilePendedEndorsements_EValueTestData() {
        return new Object[][]{
                {"DC", eThresholdTest.BEFORE, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE - 1, true},
                {"DC", eThresholdTest.ON, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE, true},
                {"DC", eThresholdTest.AFTER, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE + 1, false},
                {"DC", eThresholdTest.BEFORE, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE - 1, true},
                {"DC", eThresholdTest.ON, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE, true},
                {"DC", eThresholdTest.AFTER, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE + 1, false}
        };
    }

    @DataProvider(name = "reconcilePendedEndorsements_MPDTestData")
    public static Object[][] reconcilePendedEndorsements_MPDTestData() {
        return new Object[][]{
                {"UT", eThresholdTest.BEFORE, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE - 1, true},
                {"UT", eThresholdTest.ON, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE, true},
                {"UT", eThresholdTest.AFTER, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE + 1, false}};
    }

    @DataProvider(name = "reconcilePendedEndorsements_ActiveNotProcessed")
    public static Object[][] reconcilePendedEndorsements_ActiveNotProcessed() {
        return new Object[][]{
                {"AZ", eThresholdTest.BEFORE, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE - 1, true},
                {"AZ", eThresholdTest.ON, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE, true},
                {"AZ", eThresholdTest.AFTER, eTimepoints.STG1, CATCHUP_TIMEFRAME_VALUE + 1, false},
                {"AZ", eThresholdTest.BEFORE, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE - 1, true},
                {"AZ", eThresholdTest.ON, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE, true},
                {"AZ", eThresholdTest.AFTER, eTimepoints.STG2, CATCHUP_TIMEFRAME_VALUE + 1, false}
        };
    }

    // TEST CASES:

    @Parameters({"state"})
    @Test(dataProvider = "reconcilePendedEndorsements_MembershipTestData", groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_Membership(@Optional String state, eThresholdTest typeOfBoundryTest, eTimepoints stg_x, Integer daysAfterNB, Boolean bExpectingPolicyToBeProcessed) {
        TestData testLevelTD = prepareTestData();
        prepareForPolicyCreation(testLevelTD, bExpectingPolicyToBeProcessed);
        handlePolicyCreation(testLevelTD, true, false, eMembershipType.CANCELLED);
        addPendedEndorsement();
        validatePendedEndorsementPresent();
        advanceJVMToTimepoint(stg_x, daysAfterNB, typeOfBoundryTest);
        runBatchJobs(stg_x, false);
        assertPolicyProcessedStatus();
        queryDBForNumberOfPendingEndorsements(_bExpectingPolicyToBeProcessed);
        setTimeToToday();
    }

    @Parameters({"state"})
    @Test(dataProvider = "reconcilePendedEndorsements_EValueTestData", groups = { Groups.CIO, Groups.EVALUE, Groups.FUNCTIONAL }, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_Evalue(@Optional String state, eThresholdTest typeOfBoundryTest, eTimepoints stg_x, Integer daysAfterNB, Boolean bExpectingPolicyToBeProcessed) {
        _bExpectingPolicyToBeProcessed = bExpectingPolicyToBeProcessed;
        createEvaluePolicy();
        addPendedEndorsement();
        validatePendedEndorsementPresent();
        validateEValueDiscountPresent(_storedPolicyNumber);
        advanceJVMToTimepoint(stg_x, daysAfterNB, typeOfBoundryTest);
        runBatchJobs(stg_x, false);
        assertPolicyProcessedStatus();
        queryDBForNumberOfPendingEndorsements(_bExpectingPolicyToBeProcessed);
        setTimeToToday();
    }

    @Parameters({"state"})
    @Test(dataProvider = "reconcilePendedEndorsements_MPDTestData", groups = { Groups.CIO, Groups.MPD, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void pas28489_reconcilePendedEndorsements_MPD(@Optional String state, eThresholdTest typeOfBoundryTest, eTimepoints stg_x, Integer daysAfterNB, Boolean bExpectingPolicyToBeProcessed) {
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

    @Parameters({"state"})
    @Test(dataProvider = "reconcilePendedEndorsements_ActiveNotProcessed", groups = { Groups.CIO, Groups.MPD, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28489")
    public void ActiveMembershipNotProcessed(@Optional String state, eThresholdTest typeOfBoundryTest, eTimepoints stg_x, Integer daysAfterNB, Boolean bExpectingPolicyToBeProcessed) {
        TestData testLevelTD = prepareTestData();
        prepareForPolicyCreation(testLevelTD, bExpectingPolicyToBeProcessed);
        handlePolicyCreation(testLevelTD, true, false, eMembershipType.ACTIVE);
        advanceJVMToTimepoint(stg_x, daysAfterNB, typeOfBoundryTest);
        runBatchJobs(stg_x, true);
        assertPolicyProcessedStatus();
    }

    @Test(enabled = false, groups = { Groups.CIO, Groups.MEMBERSHIP, Groups.FUNCTIONAL}, description = "MPD Validation Phase 3: Delete pended endorsements during post-NB MPD validations")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void NoDuplicateEndorsements_MembershipSTG2(@Optional String state, eTimepoints stg_x, Integer daysAfterNB) {
        // TODO: Add regression coverage to ensure no duplicate endorsements are created at STG2 by re-running membership validation job n the same day.
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

    private void createEvaluePolicy(){
        TestData td = getPolicyDefaultTD();
        testEValueDiscount.eValueQuoteCreation();
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        pncTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
        pncTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

        _storedPolicyNumber = PolicySummaryPage.getPolicyNumber();
        _policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
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
        //HACK BEGIN
        new ErrorTab().overrideAllErrors();
        _purchaseTab.fillTab(in_td); //HACK END
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

    private void validateEValueDiscountPresent(String policyNumber){
        String status = AAAMembershipQueries.getEValueStatus(_storedPolicyNumber);
        CustomAssertions.assertThat(status).isNotNull().isEqualTo("Active");
    }

    private void advanceJVMToTimepoint(eTimepoints STG_N, Integer in_daysAfterNB, eThresholdTest in_typeOfBoundryTest){
        // Move JVM to appropriate Stage.
        switch(STG_N){
            case STG1:
                TimeSetterUtil.getInstance().nextPhase(_policyEffectiveDate.plusDays(15 + in_daysAfterNB));
                break;
            case STG2:
                TimeSetterUtil.getInstance().nextPhase(_policyEffectiveDate.plusDays(30 + in_daysAfterNB));
                break;
            default:
                CustomAssertions.fail("STG_N has an unexpected/unhandled value: " + STG_N.toString());
                break;
        }

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
}
