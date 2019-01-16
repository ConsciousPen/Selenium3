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

    private void sharedConstructor(ArrayList<SuperJob> jobList, LocalDateTime targetDate, boolean allowWeekendDates)
            throws IllegalArgumentException {

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
            if (!allowWeekendDates){
                trueOffset = adjustForWeekend(trueOffset, targetDate, superJob.job.getJobName());
            }

            // Add new KVP when needed
            if (!jobScheduleMap.containsKey(trueOffset)) {
                jobScheduleMap.put(trueOffset, new ArrayList<SuperJob>());
            }

            // Dependency check.
            for (SuperJob dependentJob : superJob.sameDayDependencies) {

                if (!jobScheduleMap.get(trueOffset).contains(dependentJob)) {

                    String msg = "Job '" + dependentJob.job.getJobName() +
                            "' must be added *BEFORE* '" + superJob.job.getJobName() +
                            "' is added. Check your list order.";

                    throw new IllegalArgumentException(msg);
                }
            }

            // Only add a same day job if it does not exist.
            if (!jobScheduleMap.get(trueOffset).contains(superJob)) {
                jobScheduleMap.get(trueOffset).add(superJob);
            }
        }
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
