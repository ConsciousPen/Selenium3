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
        return new Object[][] {{"AZ", eThresholdTests.BEFORE, 15, THRESHOLD_VALUE - 1}, {"AZ", eThresholdTests.ON, 15, THRESHOLD_VALUE}, {"AZ", eThresholdTests.AFTER, 15, THRESHOLD_VALUE + 1}};
    }

    @DataProvider(name = "ThresholdTestData_STG2")
    public static Object[][] ThresholdTestData_STG2() {
        return new Object[][] {{"AZ", eThresholdTests.BEFORE, 30, THRESHOLD_VALUE - 1}, {"AZ", eThresholdTests.ON, 30, THRESHOLD_VALUE}, {"AZ", eThresholdTests.AFTER, 30, THRESHOLD_VALUE + 1}};
    }

    @DataProvider(name = "ThresholdTestData_STG1_NoCatchup")
    public static Object[][] ThresholdTestData_STG1_NoCatchup() {
        return new Object[][] {{"AZ", eThresholdTests.ON, 15, 0}};
    }

    @DataProvider(name = "ThresholdTestData_STG2_NoCatchup")
    public static Object[][] ThresholdTestData_STG2_NoCatchup() {
        return new Object[][] {{"AZ", eThresholdTests.ON, 30, 0}};
    }

    // Combines STG1 and STG2 test data for ability to test everything using this one method.
    @DataProvider(name = "ThresholdTestData_STG1_STG2")
    public static Object[][] ThresholdTestData_STG1_STG2() {
        List<Object[]> all = Lists.newArrayList();
        all.addAll(Arrays.asList(ThresholdTestData_STG1()));
        all.addAll(Arrays.asList(ThresholdTestData_STG2()));
        return all.toArray(new Object[all.size()][]);
    }

    @Parameters({"state"})
    @Test(dataProvider = "ThresholdTestData_STG1_STG2")
    public void STG1orSTG2_TestThreshold(@Optional String state, eThresholdTests typeOfThresholdTest, Integer nb15or30, Integer daysAfterNB) {
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

        // Moving JVM to NB+15+testedDay and assert system is inside of Catch-Up window.
        TimeSetterUtil.getInstance().nextPhase(nbDate.plusDays(daysAfterNB)); //moving jvm to NB+15+n.
        LocalDateTime rightNow = TimeSetterUtil.getInstance().getCurrentTime();
        switch(typeOfThresholdTest){
            case BEFORE:
                CustomAssertions.assertThat(rightNow).isAfter(nbDate);
                CustomAssertions.assertThat(rightNow).isBefore(thresholdMaxDate);
                System.out.println(String.format("QALOGS -> Completed batch execution for %s on %s (NB+%s+%s). FIND IN LOGS.", policyNumber, rightNow, nb15or30.toString(), daysAfterNB.toString()));
                break;
            case ON:
                System.out.println(String.format("QALOGS -> Completed batch execution for %s on %s (NB+%s+%s). FIND IN LOGS.", policyNumber, rightNow, nb15or30.toString(), daysAfterNB.toString()));
                CustomAssertions.assertThat(rightNow).isEqualToIgnoringHours(thresholdMaxDate);
                break;
            case AFTER:
                System.out.println(String.format("QALOGS -> Completed batch execution for %s on %s (NB+%s+%s). SHOULD NOT BE IN LOGS", policyNumber, rightNow, nb15or30.toString(), daysAfterNB.toString()));
                CustomAssertions.assertThat(rightNow).isAfter(thresholdMaxDate);
                break;
            default:
                CustomAssertions.fail("Unexpected value for 'typeOfThresholdTest'. Force failing test.");
                break;
        }

        // If doing NB+30, force aaaBestMembershipStatus to FOUND
        if(nb15or30==30){
            AAAMembershipQueries.updateAAABestMembershipStatusInSQL(policyNumber, AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG1);
        }

        // Execute NB+15 / NB+30 Membership Validation Jobs
        JobUtils.executeJob(Jobs.membershipValidationJob);

        // Validate policyNumber is picked up by batch job at STG1.
        CustomAssertions.assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber).toString()).isEqualToIgnoringCase(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG1.toString());
    }

    // Use this to capture a screenshot in the event of an error, or at will.
    private void doScreenshot(String testName, String fileName, String extraNotes){
        ScreenshotManager.getInstance().makeScreenshot(String.format("%s_%s_%s", testName, fileName, extraNotes));
    }
}
