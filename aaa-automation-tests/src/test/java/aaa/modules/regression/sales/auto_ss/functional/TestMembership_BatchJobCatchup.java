package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.screenshots.ScreenshotManager;
import toolkit.verification.CustomAssertions;

import java.time.LocalDateTime;

public class TestMembership_BatchJobCatchup extends AutoSSBaseTest {
    enum eThresholdTests {BEFORE, ON, AFTER}

    @DataProvider(name = "ThresholdTestData_STG1")
    public static Object[][] data1() {
        return new Object[][] {{"AZ", eThresholdTests.BEFORE, 15, 4}, {"AZ", eThresholdTests.ON, 15, 5}, {"AZ", eThresholdTests.AFTER, 15, 6}};
    }

    @DataProvider(name = "ThresholdTestData_STG2")
    public static Object[][] data2() {
        return new Object[][] {{"AZ", eThresholdTests.BEFORE, 30, 4}, {"AZ", eThresholdTests.ON, 30, 5}, {"AZ", eThresholdTests.AFTER, 30, 6}};
    }

    Integer _thresholdValue = 5;
    LocalDateTime thresholdMaxDate = null;

    @Parameters({"state"})
    @Test(dataProvider = "ThresholdTestData_STG1")
    public void STG1orSTG2_TestThreshold(@Optional String state, eThresholdTests typeOfThresholdTest, Integer nb15or30, Integer daysAfterNB) {
        // Creating Policy using Default Test Data
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getPolicyDefaultTD());

        // Saving policy data from summary page and closing App.
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        LocalDateTime nbDate = policyEffectiveDate.plusDays(nb15or30);
        thresholdMaxDate = nbDate.plusDays(_thresholdValue);
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
                break;
        }

        // Execute NB+15 / NB+30 Membership Validation Jobs
        JobUtils.executeJob(Jobs.membershipValidationJob);

        // Validate policyNumber is picked up by batch job at STG1.
        CustomAssertions.assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber).toString()).isEqualToIgnoringCase("FOUND");
        // Manually open job logs and search for policyNumber in newly generated log.
    }

    // Use this to capture a screenshot in the event of an error, or at will.
    private void doScreenshot(String testName, String fileName, String extraNotes){
        ScreenshotManager.getInstance().makeScreenshot(String.format("%s_%s_%s", testName, fileName, extraNotes));
    }
}
