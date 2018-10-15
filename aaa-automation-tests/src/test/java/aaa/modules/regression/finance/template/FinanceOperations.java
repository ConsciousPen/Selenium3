package aaa.modules.regression.finance.template;

import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public abstract class FinanceOperations extends PolicyBaseTest {

    BillingAccount billingAccount = new BillingAccount();
    TestData tdBilling = testDataManager.billingAccount;

    /**
     * @author Reda Kazlauskiene
     * @name Test Escheatment transaction creation
     * @scenario 1. Create Annual Policy
     * 2. Pay $25 more than full with check
     * 3. Refund 25$ with check - run *aaaRefundGenerationAsyncJob*
     * 4. Run *aaaRefundDisbursementAsyncJob* to make refund status to issued
     * 5. Turn time for more than a year of Refund
     * 6. Run Esheatment async job at the beginning of the month:  *aaaEscheatmentProcessAsyncJob*
     * 7. Navigate to BA
     */

    protected String createEscheatmentTransaction() {
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Check"), new Dollar(25));

        LocalDateTime paymentDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime refundDate = getTimePoints().getRefundDate(paymentDate);
        TimeSetterUtil.getInstance().nextPhase(refundDate);
        JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
        JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);

        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusMonths(13));

        JobUtils.executeJob(Jobs.aaaEscheatmentProcessAsyncJob);

        return policyNumber;
    }

    /**
     * @author Reda Kazlauskiene
     * @name Run earnedPremiumPostingAsyncTaskGenerationJob and shift to next month until provided date
     */
    protected LocalDateTime runEPJobUntil(LocalDateTime jobDate, LocalDateTime until) {
        while (until.isAfter(jobDate)) {
            TimeSetterUtil.getInstance().nextPhase(jobDate);
            JobUtils.executeJob(Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
            jobDate = jobDate.plusMonths(1).withDayOfMonth(1);
        }
        return jobDate;
    }

    /**
     * @author Reda Kazlauskiene
     * @name Create Endorsement with specific TesDate and Effective date
     */
    protected void createEndorsement(LocalDateTime effectiveDate, String testDataName) {
        policy.endorse().performAndFill(getTestSpecificTD(testDataName)
                .adjust(getPolicyTD("Endorsement", "TestData")).resolveLinks()
                .adjust("EndorsementActionTab|Endorsement Date",
                        effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
    }

    protected void createEndorsement(int daysToEffective, String testDataName) {
        createEndorsement(TimeSetterUtil.getInstance().getCurrentTime().plusDays(daysToEffective), testDataName);
    }

    /**
     * @author Reda Kazlauskiene
     * @name Create Endorsement with specific TesDate and Effective date
     */
    protected void rollBackEndorsement(LocalDateTime effectiveDate) {
        policy.rollBackEndorsement().perform(getPolicyTD("EndorsementRollBack", "TestData")
                .adjust("RollBackEndorsementActionTab|Endorsement Roll Back Date",
                        effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
    }

    /**
     * @author Maksim Piatrouski
     * @name Cancel Policy with specific Effective date
     */
    protected void cancelPolicy(LocalDateTime effectiveDate) {
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
                .adjust("CancellationActionTab|Cancel Date",
                        effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
    }

    protected void cancelPolicy(int daysToEffective) {
        cancelPolicy(TimeSetterUtil.getInstance().getCurrentTime().plusDays(daysToEffective));
    }

    /**
     * @author Maksim Piatrouski
     * @name Reinstate Policy with specific Effective date
     */

    protected void reinstatePolicy(LocalDateTime effectiveDate) {
        policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData")
                .adjust("ReinstatementActionTab|Reinstate Date",
                        effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
    }

    protected void reinstatePolicy(int daysToEffective) {
        reinstatePolicy(TimeSetterUtil.getInstance().getCurrentTime().plusDays(daysToEffective));
    }
}
