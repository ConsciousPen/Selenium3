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
     * @param simulateOutputOnly when true does not shift time or run the jobs. Useful for debugging.
     * @return Output of timeshifts as well as job executions.
     */
    public static ArrayList<String> executeScheduledJobMap(LocalDateTime baseDateTime,
                                                           TreeMap<Integer, ArrayList<SuperJob>> scheduledJobMap,
                                                           boolean simulateOutputOnly){

        // Prepare to Iterate treemap
        Set set = scheduledJobMap.entrySet();

        Iterator iter = set.iterator();

        ArrayList<String> output = new ArrayList<String>();

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

            ArrayList<SuperJob> todaysJobs = scheduledJobMap.get(daysOffset);

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
     * Builds a Treemap where key represents timeoffsets and value is jobs to run on that offset/day.
     * @param jobList which jobs you want to create a schedule from. N/A states are filtered out at this stage.
     * @return Treemap representing a job schedule
     * @throws IllegalArgumentException if a job is scheduled before it's required job is scheduled
     */
    public static TreeMap<Integer, ArrayList<SuperJob>> getScheduledJobsList(ArrayList<SuperJob> jobList)
            throws IllegalArgumentException{

        TreeMap<Integer, ArrayList<SuperJob>> jobScheduleMap = new TreeMap<>();

        for (SuperJob job : jobList) {

            // Eliminate Job_Not_Applicable
            if (job.offsetType == JobOffsetType.Job_Not_Applicable){
                continue;
            }

            // True Offset = 0 represents target date, negative values being subtract, and positive values being add.
            int trueOffset = 0;

            if (job.offsetType == JobOffsetType.Add_Days){
                trueOffset = job.jobOffsetDays;
            }
            else {
                trueOffset = job.jobOffsetDays * -1;
            }

            // Add new KVP when needed
            if ( !jobScheduleMap.containsKey(trueOffset)){
                jobScheduleMap.put(trueOffset, new ArrayList<SuperJob>());
            }

            // Dependency check.
            for ( SuperJob dependentJob : job.sameDayDependencies) {

                if ( !jobScheduleMap.get(trueOffset).contains(dependentJob)){

                    String msg = "Job '" + dependentJob.getJobName() +
                            "' must be added *BEFORE* '" + job.getJobName() +
                            "' is added. Check your list order.";

                    throw new IllegalArgumentException(msg);
                }
            }

            // Only add a same day job if it does not exist.
            if ( !jobScheduleMap.get(trueOffset).contains(job) ){
                jobScheduleMap.get(trueOffset).add(job);
            }
        }

        return jobScheduleMap;
    }

    /**
     * Gets a list of all required jobs to perform an auto renewal for both CA and SS.
     * @param state is used to set correct time offsets and filter out N/A jobs
     * @return ArrayList of Jobs that can be used to build a schedule for Auto Renewals
     */
    public static ArrayList<SuperJob> getAutoRenewalJobList(String state, boolean makePayment){
        ArrayList<SuperJob> jobList = new ArrayList<>();

        //Initiate Renewal
        jobList.add(SuperJobs.policyAutomatedRenewalAsyncTaskGenerationJob(state));

        //UBI SafetyScore
        jobList.addAll(SuperJobs.getTelematicSafetyScoreJobs(state, SuperJobs.PolicyTerm.Annual));

        //Order Membership
        jobList.add(SuperJobs.aaaMembershipRenewalBatchOrderAsyncJob(state, SuperJobs.TimePoint.First));

        //Order Insurance Score
        SuperJob aaaInsuranceScoreRenewalBatchOrderAsyncJob =
                SuperJobs.aaaInsuranceScoreRenewalBatchOrderAsyncJob(state, SuperJobs.TimePoint.First);

        jobList.add(aaaInsuranceScoreRenewalBatchOrderAsyncJob);

        jobList.add(SuperJobs.aaaInsuranceScoreRenewalBatchReceiveAsyncJob(state,
                SuperJobs.TimePoint.First, aaaInsuranceScoreRenewalBatchOrderAsyncJob));

        //Order MVR/CLUE

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
