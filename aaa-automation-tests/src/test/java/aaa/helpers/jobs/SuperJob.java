package aaa.helpers.jobs;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SuperJob extends Job{

    /**
     * Determines how to calculate job offset date.
     */
    public enum JobOffsetType {
        Add_Days, Subtract_Days, Job_Not_Applicable
    }

    public JobOffsetType offsetType;

    /**
     * Uses JobOffsetType to add or substract number of days.
     */
    protected int jobOffsetDays = 0;

    /**
     * A list of all jobs this job depends on.
     */
    protected SuperJob[] sameDayDependencies;

    public SuperJob(String jobName){
        super(jobName);
    }

    public SuperJob(String jobName, List<String> jobFolders){
        super(jobName, jobFolders);
    }

    public SuperJob(String jobName, JobOffsetType jobOffsetOperationType, int jobOffsetByDays, SuperJob ... jobSameDayDependancies){
        super(jobName);

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
     * @param baseDateTime is the time before offsets applied. Usually New Business or Renewal date
     * @param scheduledJobMap is the job schedule to execute against.
     * @return Output of timeshifts as well as job executions.
     */
    public static ArrayList<String> executeScheduledJobMap(LocalDateTime baseDateTime, JobSchedule scheduledJobMap){
        return executeScheduledJobMap(baseDateTime,scheduledJobMap,false);
    }

    /**
     * Executes the job schedule with ability to simulate only.
     * @param baseDateTime is the time before offsets applied. Usually New Business or Renewal date
     * @param scheduledJobMap is the job schedule to execute against.
     * @param simulateOutputOnly when true does not shift time or run the jobs. Useful for debugging.
     * @return Output of timeshifts as well as job executions.
     */
    public static ArrayList<String> executeScheduledJobMap(LocalDateTime baseDateTime,JobSchedule scheduledJobMap,
                                                           boolean simulateOutputOnly){

        // Prepare to Iterate treemap
        Set set = scheduledJobMap.getJobScheduleMap().entrySet();

        Iterator iter = set.iterator();

        ArrayList<String> output = new ArrayList<>();

        DateTimeFormatter outputTimeFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        // Iterate treemap
        while(iter.hasNext()) {

            Map.Entry me = (Map.Entry)iter.next();

            Integer daysOffset = (Integer) me.getKey();

            LocalDateTime targetDate = baseDateTime.plusDays(daysOffset);

            if (simulateOutputOnly) {
                output.add("Simulate | Timeshift - " + outputTimeFormat.format(targetDate));
            }
            else {
                output.add("Execute | Timeshift - " + outputTimeFormat.format(targetDate));
                TimeSetterUtil.getInstance().nextPhase(targetDate);
            }

            ArrayList<SuperJob> todaysJobs = scheduledJobMap.getJobScheduleMap().get(daysOffset);

            // Execute all jobs for current time point
            for (SuperJob job : todaysJobs){
                if (simulateOutputOnly) {

                    output.add("Simulate | Job Execute " + outputTimeFormat.format(targetDate) + " " + job.getJobName());
                }
                else {
                    output.add("Execute | Job Execute " + outputTimeFormat.format(targetDate) + " " + job.getJobName());
                    JobUtils.executeJob(job);
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

        //Order Internal Claims

        //Membership Revalidation

        //Renewal Image Available to all users

        //Premium Calculate

        //Non-Renewal Notice

        //Propose/Renewal Offer

        //Renewal Bill

        if (makePayment) {
            //Special Make Payment job
        }

        //Renewal Reminder

        //R+1 Update Status (policyStatusUpdateJob)

        return jobList;
    }
}
