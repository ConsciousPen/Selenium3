package aaa.modules.regression.sales.auto_ss.functional;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.AZ)
public class TestMembershipRenewalConsideration extends AutoSSBaseTest
{
    /**
     * * @author Tyrone Jemison
     * @name Test Membership Renewal Consideration
     * @scenario
     * Preconditions: 1.Open App, 2.Create Customer, 3.Create Policy, 4.Close App
     * Test Steps: 1.Advance JVM to R-63
     * 2. Run Renewal Batch Job Part1
     * 3. Advance JVM to R-48
     * 4. Run Renewal Batch Job Part2
     * 5. Verify Policy was not considered again at R-48
     * @details Clean Path. Expected Result is that Policy will NOT be considered again at R-48.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7458")
    public void PAS3788_TestCase1(@Optional("") String state) {

        //Preconditions
        mainApp().open();
        createCustomerIndividual();
        createPolicy();
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        mainApp().close();

        LocalDateTime timePoint1 = getTimePoints().getRenewImageGenerationDate(policyExpirationDate).minusDays(63);
        LocalDateTime timePoint2 = getTimePoints().getRenewImageGenerationDate(policyExpirationDate).minusDays(48);

        //Main Test
        TimeSetterUtil.getInstance().nextPhase(timePoint1);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
        HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);

        TimeSetterUtil.getInstance().nextPhase(timePoint2);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
        HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);
    }

    /**
     * * @author Tyrone Jemison
     * @name Test Membership Renewal Consideration
     * @scenario
     * Preconditions: 1.Open App, 2.Create Customer, 3.Create Policy, 4.Close App
     * Test Steps: 1.Advance JVM to R-63
     * 2. Run Renewal Batch Job Part1
     * 3. Advance JVM to R-48
     * 4. Run Renewal Batch Job Part2
     * 5. Verify Policy was considered again at R-48
     * @details Dirty Path. Expected Result is that Policy will NOT be considered again at R-48.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7458")
    public void PAS3788_TestCase2(@Optional("") String state) {
        LocalDateTime expirationDate;

        //Preconditions
        expirationDate = handlePreconditions();

        LocalDateTime timePoint1 = getTimePoints().getRenewImageGenerationDate(expirationDate).minusDays(63);
        LocalDateTime timePoint2 = getTimePoints().getRenewImageGenerationDate(expirationDate).minusDays(48);

        //Main Test
        TimeSetterUtil.getInstance().nextPhase(timePoint1);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
        HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);

        TimeSetterUtil.getInstance().nextPhase(timePoint2);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
        HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_MEMBERSHIP_SUMMARY_BATCH);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);
    }

    public LocalDateTime handlePreconditions(){
        mainApp().open();
        createCustomerIndividual();
        TestData adjustedData = getTestSpecificTD("TestData_Adjusted");
        createPolicy(getPolicyTD().adjust(adjustedData)); //This policy should not be "Active" at TimePoint1
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        mainApp().close();
        return policyExpirationDate;
    }
}
