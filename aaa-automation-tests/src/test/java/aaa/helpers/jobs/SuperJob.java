package aaa.helpers.jobs;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SuperJob {

    /**
     * Determines how to calculate job offset date.
     */
    public enum JobOffsetType {
        Add_Days, Subtract_Days, Job_Not_Applicable
    }

    public JobOffsetType offsetType;

    public final Job job;

    /**
     * A select few jobs actually do run on weekends. Setting true will ignore the weekend check.
     */
    private boolean supportsWeekend = false;

    /**
     * Uses JobOffsetType to add or substract number of days.
     */
    protected int jobOffsetDays = 0;

    /**
     * A list of all jobs this job depends on.
     */
    protected SuperJob[] sameDayDependencies;


    public SuperJob(Job jobToSchedule, JobOffsetType jobOffsetOperationType, int jobOffsetByDays, SuperJob ... jobSameDayDependancies){

        job = jobToSchedule;

        if (jobOffsetByDays == SuperJobs.jobNotApplicableValue){
            offsetType = JobOffsetType.Job_Not_Applicable;
        } else {
            offsetType = jobOffsetOperationType;
        }
        jobOffsetDays = jobOffsetByDays;
        sameDayDependencies = jobSameDayDependancies;
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

            if (simulateOutputOnly) {
                output.add("Simulate | Timeshift - " + outputTimeFormat.format(targetDate));
            }
            else {
                output.add("Execute | Timeshift - " + outputTimeFormat.format(targetDate));
                TimeSetterUtil.getInstance().nextPhase(targetDate);
            }

            ArrayList<SuperJob> todaysJobs = jobSchedule.getJobScheduleMap().get(daysOffset);

            // Execute all jobs for current time point
            for (SuperJob superJob : todaysJobs){
                if (simulateOutputOnly) {

                    output.add("Simulate | Job Execute " + outputTimeFormat.format(targetDate) + " " + superJob.job.getJobName());
                }
                else {
                    output.add("Execute | Job Execute " + outputTimeFormat.format(targetDate) + " " + superJob.job.getJobName());
                    JobUtils.executeJob(superJob.job);
                }
            }
        }
        return output;
    }

    /**
     * Gets a list of all required jobs to perform an auto renewal for both CA and SS.
     * @param state is used to set correct time offsets and filter out N/A jobs
     * @return ArrayList of Jobs that can be used to build a schedule for Auto Renewals
     */
    public static ArrayList<SuperJob> getAutoRenewalJobList(
            String state, SuperJobs.PolicyTerm policyTerm, boolean makePayment){

        ArrayList<SuperJob> jobList = new ArrayList<>();

        //Initiate Renewal
        jobList.add(SuperJobs.policyAutomatedRenewalAsyncTaskGenerationJob(state));

        //UBI SafetyScore
        jobList.addAll(SuperJobs.getTelematicSafetyScoreJobs(state, policyTerm));

        //Order Membership
        jobList.add(SuperJobs.aaaMembershipRenewalBatchOrderAsyncJob(state, SuperJobs.TimePoint.First));

        //Order Insurance Score
        SuperJob aaaInsuranceScoreRenewalBatchOrderAsyncJob =
                SuperJobs.aaaInsuranceScoreRenewalBatchOrderAsyncJob(state, SuperJobs.TimePoint.First);

        jobList.add(aaaInsuranceScoreRenewalBatchOrderAsyncJob);

        jobList.add(SuperJobs.aaaInsuranceScoreRenewalBatchReceiveAsyncJob(state,
                SuperJobs.TimePoint.First, aaaInsuranceScoreRenewalBatchOrderAsyncJob));

        //Order MVR/CLUE
        jobList.add(SuperJobs.aaaMvrRenewBatchOrderAsyncJob(state));
        jobList.add(SuperJobs.aaaMvrRenewAsyncBatchReceiveJob(state));
        jobList.add(SuperJobs.aaaClueRenewBatchOrderAsyncJob(state));
        jobList.add(SuperJobs.aaaClueRenewAsyncBatchReceiveJob(state));

        //Order Internal Claims
        jobList.addAll(SuperJobs.getRenewalClaimOrderAsyncJobs(state));

        //Membership Revalidation
        jobList.add(SuperJobs.aaaMembershipRenewalBatchOrderAsyncJob(state, SuperJobs.TimePoint.Second));

        //Renewal Image Available to all users
        jobList.add(SuperJobs.renewalImageRatingAsyncTaskJob(state, SuperJobs.TimePoint.First));

        //Premium Calculate
        jobList.add(SuperJobs.renewalImageRatingAsyncTaskJob(state, SuperJobs.TimePoint.Second));

        //Non-Renewal Notice
        jobList.add(SuperJobs.policyDoNotRenewAsyncJob(state));

        //Propose/Renewal Offer
        jobList.add(SuperJobs.renewalOfferAsyncTaskJob(state));
        jobList.add(SuperJobs.aaaPreRenewalNoticeAsyncJob(state));

        //Renewal Bill
        jobList.add(SuperJobs.aaaRenewalNoticeBillAsyncJob(state));

        if (makePayment) {
            //Special Make Payment job
        }

        //Renewal Reminder
            //aaaRenewalNoticeBillAsyncJob

        //R+1 Update Status (policyStatusUpdateJob)

        return jobList;
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
}
