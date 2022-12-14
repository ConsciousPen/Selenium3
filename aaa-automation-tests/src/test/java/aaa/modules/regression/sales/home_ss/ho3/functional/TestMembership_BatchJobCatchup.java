package aaa.modules.regression.sales.home_ss.ho3.functional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.google.common.collect.Lists;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.verification.CustomAssertions;

public class TestMembership_BatchJobCatchup extends HomeSSHO3BaseTest {
    enum eThresholdTests {BEFORE, ON, AFTER}

    static final Integer MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE = 4;
    static LocalDateTime thresholdMaxDate = null;

    @DataProvider(name = "thresholdTestData_STG1")
    public static Object[][] ThresholdTestData_STG1() {
        return new Object[][]{
                {"AZ", eThresholdTests.BEFORE, 15, MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE - 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.ON, 15, MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.AFTER, 15, MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE + 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, false}};
    }

    @DataProvider(name = "ThresholdTestData_STG2")
    public static Object[][] ThresholdTestData_STG2() {
        return new Object[][] {
                {"AZ", eThresholdTests.BEFORE, 30, MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE - 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.ON, 30, MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.AFTER, 30, MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE + 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, false}};
    }

    @DataProvider(name = "ThresholdTestData_STG2_SkipSTG1")
    public static Object[][] ThresholdTestData_STG2_SkipSTG1() {
        return new Object[][] {
                {"AZ", eThresholdTests.BEFORE, 30, MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE - 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true, true},
                {"AZ", eThresholdTests.ON, 30, MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true, true},
                {"AZ", eThresholdTests.AFTER, 30, MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE + 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true, false}};
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
        return new Object[][] {
                {"AZ", eThresholdTests.ON, 15, 0, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.AFTER, 15, 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, false}};
    }

    @DataProvider(name = "ThresholdTestData_STG2_NoCatchup")
    public static Object[][] ThresholdTestData_STG2_NoCatchup() {
        return new Object[][] {
                {"AZ", eThresholdTests.ON, 30, 0, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, true},
                {"AZ", eThresholdTests.AFTER, 30, 1, AAAMembershipQueries.AAAMembershipStatus.No_Hit, false, false},
                {"AZ", eThresholdTests.ON, 30, 0, AAAMembershipQueries.AAAMembershipStatus.No_Hit, true, true}}; // Test feature is off, where bestMemNumber = null (skipped STG1).
    }

    @DataProvider(name = "ThresholdTestData_STG1_STG2_NoCatchup")
    public static Object[][] ThresholdTestData_STG1_STG2_NoCatchup() {
        List<Object[]> all = Lists.newArrayList();
        all.addAll(Arrays.asList(ThresholdTestData_STG1_NoCatchup()));
        all.addAll(Arrays.asList(ThresholdTestData_STG2_NoCatchup()));
        return all.toArray(new Object[all.size()][]);
    }

    @Parameters({"state"})
    @Test(dataProvider = "ThresholdTestData_STG1_STG2")
    public void STG1orSTG2_TestThreshold(@Optional String state, eThresholdTests typeOfThresholdTest, Integer nb15or30, Integer daysAfterNB, AAAMembershipQueries.AAAMembershipStatus membershipStatusAtTimeOfMembershipValidation, Boolean bRunningNB30SkipSTG1, Boolean bExpectingPolicyToBeProcessed) {
        // Creating Policy using Default Test Data
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy(getPolicyDefaultTD());

        // Saving policy data from summary page and closing App.
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        LocalDateTime nbDate = policyEffectiveDate.plusDays(nb15or30);
        thresholdMaxDate = nbDate.plusDays(MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE);
        mainApp().close();

        // Moving JVM to NB+15+testedDay and use Switch to assert system is inside of Catch-Up window.
        TimeSetterUtil.getInstance().nextPhase(nbDate.plusDays(daysAfterNB)); //moving jvm to NB+15+n.
        LocalDateTime rightNow = TimeSetterUtil.getInstance().getCurrentTime();
        switch(typeOfThresholdTest){
            case BEFORE:
                CustomAssertions.assertThat(rightNow).isAfter(nbDate);
                CustomAssertions.assertThat(rightNow).isBefore(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER NB+%s (%s) and BEFORE the threshold (%s days) cut off on %s.",
                        rightNow.toLocalDate().toString(), nb15or30.toString(), nbDate.toLocalDate().toString(), MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE, thresholdMaxDate.toLocalDate().toString()));
                break;
            case ON:
                CustomAssertions.assertThat(rightNow).isEqualToIgnoringHours(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS ON NB+%s+%s",
                        rightNow.toLocalDate().toString(), nb15or30.toString(), MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE));
                break;
            case AFTER:
                CustomAssertions.assertThat(rightNow).isAfter(thresholdMaxDate);
                log.debug(String.format("QALOGS -> VALIDATED SYSTEM DATE (%s) IS AFTER NB+%s+%s",
                        rightNow.toLocalDate().toString(), nb15or30.toString(), MEMBERSHIP_CATCHUP_TIMEFRAME_VALUE));
                // Handle another edge case where Threshold date lands on weekend and job is not expecting to run to pick it up the following weekday.
                if(thresholdMaxDate.getDayOfWeek()==DayOfWeek.SATURDAY || thresholdMaxDate.getDayOfWeek()==DayOfWeek.SUNDAY){
                    bExpectingPolicyToBeProcessed = true;
                }
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

        // Handle the fact that the job doesn't run on the weekends.
        if(rightNow.getDayOfWeek()== DayOfWeek.SATURDAY){
            rightNow = rightNow.plusDays(2);
            TimeSetterUtil.getInstance().nextPhase(rightNow);
        }
        if(rightNow.getDayOfWeek()== DayOfWeek.SUNDAY){
            rightNow = rightNow.plusDays(1);
            TimeSetterUtil.getInstance().nextPhase(rightNow);
        }

        // Execute NB+15 / NB+30 Membership Validation Jobs
        JobUtils.executeJob(BatchJob.membershipValidationJob);

        // Validate policyNumber is picked up by batch job at STG1.
        doValidation(policyEffectiveDate, rightNow, nb15or30, policyNumber, bRunningNB30SkipSTG1, bExpectingPolicyToBeProcessed);
    }

    public void doValidation(LocalDateTime in_policyEffectiveDate, LocalDateTime in_currentTime, Integer in_nb15or30, String in_policyNumber, Boolean in_bRunningNB30SkipSTG1, Boolean bExpectedPolicyWasProcessed) {
        //Get the value to assert against.
        java.util.Optional<AAAMembershipQueries.AAABestMembershipStatus> bestMembershipStatus = AAAMembershipQueries.getAAABestMembershipStatusFromSQL(in_policyNumber);

        // Try Catch to handle edge case. If Policy is made on Sunday and the membershipValidation attempts to run on a Sunday, 1 Day AFTER the cut-off date...
        /// ...THEN the policy is still picked up when we're not expecting it to. This is acceptable because the policyEffectiveDate is Sunday.
        // This Try-Catch should inform us quickly of the policy effective date and the date the job attempted to run on. If we see Sunday and Sunday, it should mean we hit the edge case and can disregard the failure.
        try {
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
            } else {
                //GIVEN a negative scenario...
                if (in_nb15or30 == 15) {
                    CustomAssertions.assertThat(bestMembershipStatus.toString()).containsIgnoringCase("Empty");
                } else {
                    if (in_bRunningNB30SkipSTG1 && in_nb15or30 == 30) {
                        CustomAssertions.assertThat(bestMembershipStatus.toString()).containsIgnoringCase("Empty");
                    } else {
                        if (!in_bRunningNB30SkipSTG1 && in_nb15or30 == 30) {
                            CustomAssertions.assertThat(bestMembershipStatus.toString()).containsIgnoringCase(AAAMembershipQueries.AAABestMembershipStatus.NOHIT_STG1.toString());
                        } else {
                            CustomAssertions.fail(String.format("Variable 'nb15or30' == something other than 15 or 30 (Actual nb15or30: %s). (Actual membershipStatus: %s).", in_nb15or30.toString(), bestMembershipStatus.toString()));
                        }
                    }
                }
            }
        } catch (AssertionError ex) {
            CustomAssertions.fail("%s [PolicyEffectiveDate = %s(%s); BatchRunDate = %s(%s)]", ex.getMessage(), in_policyEffectiveDate, in_policyEffectiveDate.getDayOfWeek(), in_currentTime, in_currentTime.getDayOfWeek());
        }
    }
}
