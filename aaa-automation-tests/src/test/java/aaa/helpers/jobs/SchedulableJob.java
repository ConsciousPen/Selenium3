package aaa.helpers.jobs;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SchedulableJob {

    /**
     * Determines how to calculate job offset date.
     */
    public enum JobOffsetType {
        Add_Days, Subtract_Days, Job_Not_Applicable
    }

    public JobOffsetType offsetType;

    public String jobName;

    public final Job job;

    private static SoapJobActions service = new SoapJobActions();

    /**
     * A select few jobs actually do run on weekends. Setting true will ignore the weekend check.
     */
    private boolean supportsWeekend = false;

    /**
     * Uses JobOffsetType to add or substract number of days.
     */
    protected int jobOffsetDays = 0;


    public SchedulableJob(Job jobToSchedule, JobOffsetType jobOffsetOperationType, int jobOffsetByDays){

        job = jobToSchedule;

        if (!service.isJobExist(JobGroup.fromSingleJob(job.getJobName()))) {
            service.createJob(JobGroup.fromSingleJob(job.getJobName()));
        }

        if (jobOffsetByDays == SchedulableJobs.jobNotApplicableValue){
            offsetType = JobOffsetType.Job_Not_Applicable;
        } else {
            offsetType = jobOffsetOperationType;
        }
        jobOffsetDays = jobOffsetByDays;
        jobName = job.getJobName();
    }

    /**
     * Executes the job schedule.
     * @param jobSchedule is the job schedule to execute against.
     * @return Output of timeshifts as well as job executions.
     */
    public static ArrayList<String> executeJobSchedule(JobSchedule jobSchedule){
        return executeJobSchedule(jobSchedule,false);
    }

    /**
     * Executes the job schedule with ability to simulate only.
     * @param jobSchedule is the job schedule to execute against.
     * @param simulateOutputOnly when true does not shift time or run the jobs. Useful for debugging.
     * @return Output of timeshifts as well as job executions.
     */
    public static ArrayList<String> executeJobSchedule(JobSchedule jobSchedule, boolean simulateOutputOnly){

        // Prepare to Iterate treemap
        Set set = jobSchedule.getJobScheduleMap().entrySet();

        Iterator iter = set.iterator();

        ArrayList<String> output = new ArrayList<>();

        DateTimeFormatter outputTimeFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        // Iterate treemap
        while(iter.hasNext()) {

            Map.Entry me = (Map.Entry)iter.next();

            Integer daysOffset = (Integer) me.getKey();

            LocalDateTime targetDate = jobSchedule.scheduledTargetDate.plusDays(daysOffset);

            output.add("Execute | Timeshift - " + outputTimeFormat.format(targetDate));

            if (!simulateOutputOnly) {
                TimeSetterUtil.getInstance().nextPhase(targetDate);
            }

            ArrayList<SchedulableJob> todaysJobs = jobSchedule.getJobScheduleMap().get(daysOffset);

            // Execute all jobs for current time point
            for (SchedulableJob schedulableJob : todaysJobs){

                output.add("Execute | Job Execute " + outputTimeFormat.format(targetDate) + " " + schedulableJob.jobName);

                if (!simulateOutputOnly) {
                    schedulableJob.executeJob();
                }
            }
        }
        return output;
    }

    /**
     * Executes the job using the job runner.
     */
    public void executeJob(){
        JobUtils.executeJob(this.job);
    }



    /**
     * Job can be overridden to support weekends even if JobSchedule(allowWeekendDates = false).
     * This is false by default since most jobs do not.
     * @param jobSupportsWeekends what setting to use.
     */
    public void setSupportsWeekend(boolean jobSupportsWeekends){
        supportsWeekend = jobSupportsWeekends;
    }

    /**
     * Accessor to check weekend support even if JobSchedule(allowWeekendDates = false).
     * @return whether or not job supports running on the weekends.
     */
    public boolean getSupportsWeekends(){
        return supportsWeekend;
    }


    /**
     * Extended to support running automation on execute rather than running a job.
     */
    public class PaymentSchedulableJob extends SchedulableJob {

        private BaseTest _baseTest;
        private String _policyNumber;

        public PaymentSchedulableJob(BaseTest baseTest, String policyNumber, Job jobToSchedule,
                                     JobOffsetType jobOffsetOperationType, int jobOffsetByDays){
            super(jobToSchedule, jobOffsetOperationType, jobOffsetByDays);

            _baseTest = baseTest;
            _policyNumber = policyNumber;
            jobName = "SchedulableJob.PaymentSchedulableJob";
        }

        @Override
        public void executeJob(){
            //MainApplication mainApplication = CSAAApplicationFactory.get().mainApp();
            // Make a payement
            _baseTest.mainApp().open();
            SearchPage.openBilling(_policyNumber);
            new BillingAccount().acceptPayment().start();
            new AcceptPaymentActionTab().setCheckNumber(123);
            Tab.buttonOk.click();
            _baseTest.mainApp().close();
        }
    }

    /**
     * This is required because of differences in VDM vs Prod. This is only needed for Home/Property renewals.
     * Runs at Run Rules window (R-57). Full name updRenewTimelineIndicatorSchedulableJob
     */
    public class updRenewTimelineIndicatorSchedulableJob extends SchedulableJob {

        private String _policyNumber;

        public updRenewTimelineIndicatorSchedulableJob(String policyNumber, Job jobToSchedule, JobOffsetType jobOffsetOperationType, int jobOffsetByDays, SchedulableJob... jobSameDayDependancies){
            super(jobToSchedule, jobOffsetOperationType, jobOffsetByDays);
            _policyNumber = policyNumber;
            jobName = "SchedulableJob.updRenewTimelineIndicatorSchedulableJob";
        }

        @Override
        public void executeJob(){
            // Manually set aaaRenewalTimelineInd
            AAAMembershipQueries.updateAaaRenewalTimelineIndicatorValue(_policyNumber, "3");
        }
    }
}
