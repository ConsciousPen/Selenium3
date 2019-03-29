package aaa.modules.regression.sales.auto_ss.functional;

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

    @DataProvider(name = "ThresholdTestData")
    public static Object[][] data() {
        return new Object[][] {{"AZ", eThresholdTests.BEFORE, 4}, {"AZ", eThresholdTests.ON, 5}, {"AZ", eThresholdTests.AFTER, 6}};
    }

    Integer _thresholdValue = 5;
    LocalDateTime thresholdMaxDate = null;

    @Parameters({"state"})
    @Test(dataProvider = "ThresholdTestData")
    public void STG1_TestThreshold(@Optional String state, eThresholdTests typeOfThresholdTest, Integer daysAfterNB) {
        // Creating Policy using Default Test Data
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getPolicyDefaultTD());

        // Saving policy data from summary page and closing App.
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        thresholdMaxDate = policyEffectiveDate.plusDays(_thresholdValue);
        mainApp().close();

        // Moving JVM to NB+15+testedDay and assert system is inside of Catch-Up window.
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(15 + daysAfterNB)); //moving jvm to NB+15+4.
        LocalDateTime rightNow = TimeSetterUtil.getInstance().getCurrentTime();
        switch(typeOfThresholdTest){
            case BEFORE:
                CustomAssertions.assertThat(rightNow).isBefore(thresholdMaxDate);
                System.out.println(String.format("QALOGS -> Completed batch execution for %s on %s. FIND IN LOGS.", policyNumber, rightNow));
                break;
            case ON:
                System.out.println(String.format("QALOGS -> Completed batch execution for %s on %s. FIND IN LOGS.", policyNumber, rightNow));
                CustomAssertions.assertThat(rightNow).isEqualToIgnoringHours(thresholdMaxDate);
                break;
            case AFTER:
                System.out.println(String.format("QALOGS -> Completed batch execution for %s on %s. NOT IN LOGS", policyNumber, rightNow));
                CustomAssertions.assertThat(rightNow).isAfter(thresholdMaxDate);
                break;
            default:
                break;
        }

        // Execute NB+15 / NB+30 Membership Validation Jobs
        JobUtils.executeJob(Jobs.membershipValidationJob);

        // Validate policyNumber is picked up by batch job at STG1.
        // Manually open job logs and search for policyNumber in newly generated log.
    }

    // Use this to capture a screenshot in the event of an error, or at will.
    private void doScreenshot(String testName, String fileName, String extraNotes){
        ScreenshotManager.getInstance().makeScreenshot(String.format("%s_%s_%s", testName, fileName, extraNotes));
    }
}
