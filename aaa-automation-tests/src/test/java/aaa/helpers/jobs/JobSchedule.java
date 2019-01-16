package aaa.helpers.jobs;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Wraps a hashmap with a label to avoid confusion. Map represents Offset date with list of jobs for each date.
 */
public class JobSchedule {
    // Offset Date / Jobs to run on date
    private TreeMap<Integer, ArrayList<SuperJob>> jobScheduleMap = new TreeMap<>();

    public JobSchedule(ArrayList<SuperJob> jobList) throws IllegalArgumentException {

        for (SuperJob job : jobList) {

            // Eliminate Job_Not_Applicable
            if (job.offsetType == SuperJob.JobOffsetType.Job_Not_Applicable) {
                continue;
            }

            // True Offset = 0 represents target date, negative values being subtract, and positive values being add.
            int trueOffset = 0;

            if (job.offsetType == SuperJob.JobOffsetType.Add_Days) {
                trueOffset = job.jobOffsetDays;
            } else {
                trueOffset = job.jobOffsetDays * -1;
            }

            // Add new KVP when needed
            if (!jobScheduleMap.containsKey(trueOffset)) {
                jobScheduleMap.put(trueOffset, new ArrayList<SuperJob>());
            }

            // Dependency check.
            for (SuperJob dependentJob : job.sameDayDependencies) {

                if (!jobScheduleMap.get(trueOffset).contains(dependentJob)) {

                    String msg = "Job '" + dependentJob.getJobName() +
                            "' must be added *BEFORE* '" + job.getJobName() +
                            "' is added. Check your list order.";

                    throw new IllegalArgumentException(msg);
                }
            }

            // Only add a same day job if it does not exist.
            if (!jobScheduleMap.get(trueOffset).contains(job)) {
                jobScheduleMap.get(trueOffset).add(job);
            }
        }
    }

    public TreeMap<Integer, ArrayList<SuperJob>> getJobScheduleMap(){
        return jobScheduleMap;
    }
}
