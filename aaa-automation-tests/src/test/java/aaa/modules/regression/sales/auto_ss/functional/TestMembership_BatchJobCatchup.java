package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.screenshots.ScreenshotManager;
import toolkit.verification.CustomAssertions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TestMembership_BatchJobCatchup extends AutoSSBaseTest {
    enum eThresholdTests {BEFORE, ON, AFTER}

    static final Integer THRESHOLD_VALUE = 4;
    static LocalDateTime thresholdMaxDate = null;

    @DataProvider(name = "ThresholdTestData_STG1")
    public static Object[][] ThresholdTestData_STG1() {
        return new Object[][]{
                {"AZ", eThresholdTests.BEFORE, 15, THRESHOLD_VALUE - 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.ON, 15, THRESHOLD_VALUE, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.AFTER, 15, THRESHOLD_VALUE + 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, false}};
    }

    @DataProvider(name = "ThresholdTestData_STG2")
    public static Object[][] ThresholdTestData_STG2() {
        return new Object[][] {
                {"AZ", eThresholdTests.BEFORE, 30, THRESHOLD_VALUE - 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.ON, 30, THRESHOLD_VALUE, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.AFTER, 30, THRESHOLD_VALUE + 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, false}}; //FAILS. status == FOUND_STG2
    }

    @DataProvider(name = "ThresholdTestData_STG2_SkipSTG1")
    public static Object[][] ThresholdTestData_STG2_SkipSTG1() {
        return new Object[][] {
                {"AZ", eThresholdTests.BEFORE, 30, THRESHOLD_VALUE - 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true, true},
                {"AZ", eThresholdTests.ON, 30, THRESHOLD_VALUE, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true, true},
                {"AZ", eThresholdTests.AFTER, 30, THRESHOLD_VALUE + 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true, false}};
    }

    // Combines STG1 and STG2 test data for ability to test everything using this one method.
    @DataProvider(name = "ThresholdTestData_STG1_STG2")
    public static Object[][] ThresholdTestData_STG1_STG2() {
        List<Object[]> all = Lists.newArrayList();
        all.addAll(Arrays.asList(ThresholdTestData_STG1()));
        all.addAll(Arrays.asList(ThresholdTestData_STG2()));
        all.addAll(Arrays.asList(ThresholdTestData_STG2_SkipSTG1()));
        return all.toArray(new Object[all.size()][]);
    }

    @DataProvider(name = "ThresholdTestData_STG1_NoCatchup")
    public static Object[][] ThresholdTestData_STG1_NoCatchup() {
        return new Object[][] {{"AZ", eThresholdTests.ON, 15, 0, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true}};
    }

    @DataProvider(name = "ThresholdTestData_STG2_NoCatchup")
    public static Object[][] ThresholdTestData_STG2_NoCatchup() {
        return new Object[][] {{"AZ", eThresholdTests.ON, 30, 0, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true}};
    }

    @DataProvider(name = "ThresholdTestData_STG1_STG2_NoCatchup")
    public static Object[][] ThresholdTestData_STG1_STG2_NoCatchup() {
        List<Object[]> all = Lists.newArrayList();
        all.addAll(Arrays.asList(ThresholdTestData_STG1_NoCatchup()));
        all.addAll(Arrays.asList(ThresholdTestData_STG2_NoCatchup()));
        return all.toArray(new Object[all.size()][]);
    }

    @Parameters({"state"})
    @Test(dataProvider = "ThresholdTestData_STG2")
    public void STG1orSTG2_TestThreshold(@Optional String state, eThresholdTests typeOfThresholdTest, Integer nb15or30, Integer daysAfterNB, AAAMembershipQueries.AAAMembershipStatus membershipStatusAtTimeOfMembershipValidation, Boolean bRunningNB30SkipSTG1, Boolean bExpectingPolicyToBeProcessed) {
        // Creating Policy using Default Test Data
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getPolicyDefaultTD());

        // Saving policy data from summary page and closing App.
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        LocalDateTime nbDate = policyEffectiveDate.plusDays(nb15or30);
        thresholdMaxDate = nbDate.plusDays(THRESHOLD_VALUE);
        mainApp().close();

        // Moving JVM to NB+15+testedDay and use Switch to assert system is inside of Catch-Up window.
        TimeSetterUtil.getInstance().nextPhase(nbDate.plusDays(daysAfterNB)); //moving jvm to NB+15+n.
        LocalDateTime rightNow = TimeSetterUtil.getInstance().getCurrentTime();
        switch(typeOfThresholdTest){
            case BEFORE:
                CustomAssertions.assertThat(rightNow).isAfter(nbDate);
                CustomAssertions.assertThat(rightNow).isBefore(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER NB+%s (%s) and BEFORE the threshold (%s days) cut off on %s.",
                        rightNow.toLocalDate().toString(), nb15or30.toString(), nbDate.toLocalDate().toString(), THRESHOLD_VALUE, thresholdMaxDate.toLocalDate().toString()));
                break;
            case ON:
                CustomAssertions.assertThat(rightNow).isEqualToIgnoringHours(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS ON NB+%s+%s",
                        rightNow.toLocalDate().toString(), nb15or30.toString(), THRESHOLD_VALUE));
                break;
            case AFTER:
                CustomAssertions.assertThat(rightNow).isAfter(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER NB+%s+%s",
                        rightNow.toLocalDate().toString(), nb15or30.toString(), THRESHOLD_VALUE));
                break;
            default:
                CustomAssertions.fail("Unexpected value for 'typeOfThresholdTest'. Force failing test.");
                break;
        }

        // Force MembershipStatus != ACTIVE. ACTIVE policies will not be processed!
        AAAMembershipQueries.updateAAAMembershipStatusInSQL(policyNumber, membershipStatusAtTimeOfMembershipValidation);
        java.util.Optional<AAAMembershipQueries.AAAMembershipStatus> membershipStatus = AAAMembershipQueries.getAAAMembershipStatusFromSQL(policyNumber);
        CustomAssertions.assertThat(membershipStatus.toString()).isEqualToIgnoringCase(membershipStatus.toString()); //Asserting the DB received the value we just pushed to it.

        // If doing NB+30, force aaaBestMembershipStatus to NOHIT_STG1 so STG2 will process. If processed at STG1, STG2 will not process policy.
        if(!bRunningNB30SkipSTG1 && nb15or30==30){
            AAAMembershipQueries.updateAAABestMembershipStatusInSQL(policyNumber, AAAMembershipQueries.AAABestMembershipStatus.NOHIT_STG1);
        }

        // Execute NB+15 / NB+30 Membership Validation Jobs
        JobUtils.executeJob(Jobs.membershipValidationJob);

        // Validate policyNumber is picked up by batch job at STG1.
        doValidation(nb15or30, policyNumber, bRunningNB30SkipSTG1, bExpectingPolicyToBeProcessed);
    }

    public void doValidation(Integer in_nb15or30, String in_policyNumber, Boolean in_bRunningNB30SkipSTG1, Boolean bExpectedPolicyWasProcessed) {
        //Get the value to assert against.
        java.util.Optional<AAAMembershipQueries.AAABestMembershipStatus> bestMembershipStatus = AAAMembershipQueries.getAAABestMembershipStatusFromSQL(in_policyNumber);

        // Next, is this a positive or negative scenario test?
        if (bExpectedPolicyWasProcessed) {
            // GIVEN a positive scenario...
            if (in_nb15or30 == 15) {
                CustomAssertions.assertThat(bestMembershipStatus.toString()).containsIgnoringCase(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG1.toString());
            } else {
                if (in_nb15or30 == 30) {
                    CustomAssertions.assertThat(bestMembershipStatus.toString()).containsIgnoringCase(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG2.toString());
                } else {
                    CustomAssertions.fail(String.format("Variable 'nb15or30' == something other than 15 or 30 (Actual nb15or30: %s). (Actual membershipStatus: %s).", in_nb15or30.toString(), bestMembershipStatus.toString()));
                }
            }
        }else{
            //GIVEN a negative scenario...
            if (in_nb15or30 == 15) {
                CustomAssertions.assertThat(bestMembershipStatus.toString()).containsIgnoringCase("Empty");
            } else {
                if (in_bRunningNB30SkipSTG1 && in_nb15or30==30) {
                    CustomAssertions.assertThat(bestMembershipStatus.toString()).containsIgnoringCase("Empty");
                } else {
                    if (!in_bRunningNB30SkipSTG1 && in_nb15or30==30){
                        CustomAssertions.assertThat(bestMembershipStatus.toString()).containsIgnoringCase(AAAMembershipQueries.AAABestMembershipStatus.NOHIT_STG1.toString());
                    }else {
                        CustomAssertions.fail(String.format("Variable 'nb15or30' == something other than 15 or 30 (Actual nb15or30: %s). (Actual membershipStatus: %s).", in_nb15or30.toString(), bestMembershipStatus.toString()));
                    }
                }
            }
        }
    }
}
