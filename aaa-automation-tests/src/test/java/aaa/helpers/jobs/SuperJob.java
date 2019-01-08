package aaa.helpers.jobs;

import java.time.LocalDateTime;
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

    public static ArrayList<SuperJob> getAutoRenewalJobList(String state){
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

        return jobList;
    }
}
