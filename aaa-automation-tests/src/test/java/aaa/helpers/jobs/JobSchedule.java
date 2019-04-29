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
    // Debugging switches
    boolean showWeekendAdjustmentOutput = false;

    // Offset Date / Jobs to run on date
    private TreeMap<Integer, ArrayList<SchedulableJob>> jobScheduleMap = new TreeMap<>();

    public final LocalDateTime scheduledTargetDate;

    public JobSchedule(ArrayList<SchedulableJob> jobList, LocalDateTime targetDate){
        sharedConstructor(jobList, targetDate, false);
        scheduledTargetDate = targetDate;
    }

    public JobSchedule(ArrayList<SchedulableJob> jobList, LocalDateTime targetDate, boolean allowWeekendDates){

        sharedConstructor(jobList, targetDate, allowWeekendDates);
        scheduledTargetDate = targetDate;
    }

    private void sharedConstructor(ArrayList<SchedulableJob> jobList, LocalDateTime targetDate, boolean allowWeekendDates){

        for (SchedulableJob schedulableJob : jobList) {

            // Eliminate Job_Not_Applicable
            if (schedulableJob.offsetType == SchedulableJob.JobOffsetType.Job_Not_Applicable) {
                continue;
            }

            // True Offset = 0 represents target date, negative values being subtract, and positive values being add.
            int trueOffset = 0;

            if (schedulableJob.offsetType == SchedulableJob.JobOffsetType.Add_Days) {
                trueOffset = schedulableJob.jobOffsetDays;
            } else {
                trueOffset = schedulableJob.jobOffsetDays * -1;
            }

            // Weekend adjustment
            if (!schedulableJob.getSupportsWeekends() && !allowWeekendDates){
                trueOffset = adjustForWeekend(trueOffset, targetDate, schedulableJob.job.getJobName());
            }

            // Add new KVP when needed
            if (!jobScheduleMap.containsKey(trueOffset)) {
                jobScheduleMap.put(trueOffset, new ArrayList<SchedulableJob>());
            }

            // Only add a same day job if it does not exist.
            if (!isJobScheduled(jobScheduleMap.get(trueOffset), schedulableJob.job.getJobName())) {
                jobScheduleMap.get(trueOffset).add(schedulableJob);
            }
        }
    }

    /**
     * Checks whether job is already present in list.
     * @param todaysJobs Collection of jobs already scheduled to run on same day.
     * @param currentJob Job that will be evaluated against already scheduled jobs for the same day.
     * @throws IllegalArgumentException when a required job is not already scheduled before current job.
     */
    private boolean isJobScheduled(ArrayList<SchedulableJob> todaysJobs, String currentJob){

        boolean containsJob = false;

        for ( SchedulableJob schedulableJob : todaysJobs ) {
            if (schedulableJob.job.getJobName().equals(currentJob)){
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

                if(showWeekendAdjustmentOutput) {
                    log.info("Scheduled Job | " + jobName + " adjusted from Saturday to Previous Friday. Offset: " + offset
                            + " | Adjusted Offset: " + adjustedOffset);
                }
                return adjustedOffset;

            case SUNDAY:
                adjustedOffset = offset + 1;

                if(showWeekendAdjustmentOutput) {
                    log.info("Scheduled Job | " + jobName + " adjusted from Sunday to Following Monday. Offset: " + offset
                            + " | Adjusted Offset: " + adjustedOffset);
                }
                return adjustedOffset;

            default:
                return offset;
        }
    }

    public TreeMap<Integer, ArrayList<SchedulableJob>> getJobScheduleMap(){
        return jobScheduleMap;
    }
}