package aaa.helpers.jobs;

import aaa.modules.BaseTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Wraps a hashmap with a label to avoid confusion. Map represents Offset date with list of jobs for each date.
 */
public class JobSchedule {
    // Offset Date / Jobs to run on date
    private TreeMap<Integer, ArrayList<SuperJob>> jobScheduleMap = new TreeMap<>();

    public final LocalDateTime scheduledTargetDate;

    public JobSchedule(ArrayList<SuperJob> jobList, LocalDateTime targetDate){
        sharedConstructor(jobList, targetDate, false);
        scheduledTargetDate = targetDate;
    }

    public JobSchedule(ArrayList<SuperJob> jobList, LocalDateTime targetDate, boolean allowWeekendDates){

        sharedConstructor(jobList, targetDate, allowWeekendDates);
        scheduledTargetDate = targetDate;
    }

    private void sharedConstructor(ArrayList<SuperJob> jobList, LocalDateTime targetDate, boolean allowWeekendDates){

        for (SuperJob superJob : jobList) {

            // Eliminate Job_Not_Applicable
            if (superJob.offsetType == SuperJob.JobOffsetType.Job_Not_Applicable) {
                continue;
            }

            // True Offset = 0 represents target date, negative values being subtract, and positive values being add.
            int trueOffset = 0;

            if (superJob.offsetType == SuperJob.JobOffsetType.Add_Days) {
                trueOffset = superJob.jobOffsetDays;
            } else {
                trueOffset = superJob.jobOffsetDays * -1;
            }

            // Weekend adjustment
            if (!superJob.getSupportsWeekends() && !allowWeekendDates){
                trueOffset = adjustForWeekend(trueOffset, targetDate, superJob.job.getJobName());
            }

            // Add new KVP when needed
            if (!jobScheduleMap.containsKey(trueOffset)) {
                jobScheduleMap.put(trueOffset, new ArrayList<SuperJob>());
            }

            // Dependency check.
            for (SuperJob dependentJob : superJob.sameDayDependencies) {
                dependencyCheck(jobScheduleMap.get(trueOffset),dependentJob.job.getJobName(), superJob.job.getJobName());
            }

            // Only add a same day job if it does not exist.
            if (!isJobScheduled(jobScheduleMap.get(trueOffset), superJob.job.getJobName())) {
                jobScheduleMap.get(trueOffset).add(superJob);
            }
        }
    }

    /**
     * Validates that jobs with dependant jobs listed have all the dependancies scheduled to run before it.
     * @param todaysJobs Collection of jobs already scheduled to run on same day.
     * @param dependantJobName Name of the job being verified.
     * @param currentJob Job that is having it's dependancies evaluated.
     * @throws IllegalArgumentException when a required job is not already scheduled before current job.
     */
    private void dependencyCheck(ArrayList<SuperJob> todaysJobs, String dependantJobName, String currentJob)
            throws IllegalArgumentException {

        boolean containsJob = false;

        for ( SuperJob superJob : todaysJobs ) {
            if (superJob.job.getJobName().equals(dependantJobName)){
                containsJob = true;
                break;
            }
        }

        if(!containsJob){
            String msg = "Job '" + dependantJobName  +
                    "' must be added *BEFORE* '" + currentJob +
                    "' is added. Check your SuperJob list order.";

            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Checks whether job is already present in list.
     * @param todaysJobs Collection of jobs already scheduled to run on same day.
     * @param currentJob Job that will be evaluated against already scheduled jobs for the same day.
     * @throws IllegalArgumentException when a required job is not already scheduled before current job.
     */
    private boolean isJobScheduled(ArrayList<SuperJob> todaysJobs, String currentJob){

        boolean containsJob = false;

        for ( SuperJob superJob : todaysJobs ) {
            if (superJob.job.getJobName().equals(currentJob)){
                containsJob = true;
                break;
            }
        }

        return containsJob;
    }

    /**
     * If date lands on weekend, sets offset to nearest weekday date to mimic prod.
     * @param offset Days away from the target date.
     * @param targetDate Date to adjust from
     * @return Corrected for weekend offset.
     */
    private int adjustForWeekend(int offset, LocalDateTime targetDate, String jobName){

        LocalDateTime adjustedDate = targetDate.plusDays(offset);

        Logger log = LoggerFactory.getLogger(BaseTest.class);

        int adjustedOffset;

        switch (adjustedDate.getDayOfWeek()){
            case SATURDAY:
                adjustedOffset = offset - 1;
                log.info("Scheduled Job | " + jobName + " adjusted from Saturday to Previous Friday. Offset: " + offset
                        + " | Adjusted Offset: " + adjustedOffset);
                return adjustedOffset;

            case SUNDAY:
                adjustedOffset = offset + 1;
                log.info("Scheduled Job | " + jobName + " adjusted from Sunday to Following Monday. Offset: " + offset
                        + " | Adjusted Offset: " + adjustedOffset);
                return adjustedOffset;

            default:
                return offset;
        }
    }

    public TreeMap<Integer, ArrayList<SuperJob>> getJobScheduleMap(){
        return jobScheduleMap;
    }
}
