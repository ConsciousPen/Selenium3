package aaa.helpers.jobs;

import aaa.common.enums.Constants;
import aaa.modules.BaseTest;
import org.apache.commons.lang.NotImplementedException;
import java.time.temporal.ChronoUnit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SuperJobs are Jobs with additional metadata to be able to call a list of jobs and have them run at the appropriate
 *  times automatically to streamline running many jobs.
 */
public class SuperJobs {

    public static final String defaultStateKey = "default";
    public static final int jobNotApplicableValue = -1;
    public enum PolicyTerm { Annual, SixMonth }
    public enum PaymentPlan { Full, Monthly }
    public enum TimePoint { First, Second }

    /**
     * Returns both TelematicSafetyScore jobs.
     * @param state to set timepoints for
     * @param policyTerm that the policy uses as dates change
     * @return an ArrayList containing both TelematicSafetyScore jobs
     */
    public static ArrayList<SuperJob> getTelematicSafetyScoreJobs(String state, PolicyTerm policyTerm) {
        ArrayList<SuperJob> jobs = new ArrayList<>();
        jobs.add(aaaTelematicSafetyScoreOrderAsyncJob(state, policyTerm, TimePoint.First));
        jobs.add(aaaTelematicSafetyScoreOrderAsyncJob(state, policyTerm, TimePoint.Second));
        return jobs;
    }

    /**
     * Gets a specific Telematics timepoint for a specific state.
     * @param state to set timepoint for
     * @param policyTerm that the policy uses as dates change
     * @param timePoint for the state provided
     * @return one job that represents the state, term type, and timepoint requested
     */
    public static SuperJob aaaTelematicSafetyScoreOrderAsyncJob(String state, PolicyTerm policyTerm, TimePoint timePoint){

        Job baseJob = Jobs.aaaTelematicSafetyScoreOrderAsyncJob;
        HashMap<PolicyTerm, HashMap<TimePoint, StateOffset>> timePointMap = getMultiTermMultiTimePointMap();

        // Annual First
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 95);

        // Annual Second
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 45);
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.Second).stateOffsetMap.put(Constants.States.CT, 69);
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.Second).stateOffsetMap.put(Constants.States.KY, 84);
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.Second).stateOffsetMap.put(Constants.States.MD, 55);
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.Second).stateOffsetMap.put(Constants.States.MT, 55);
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.Second).stateOffsetMap.put(Constants.States.NJ, 69);
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.Second).stateOffsetMap.put(Constants.States.PA, 69);
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.Second).stateOffsetMap.put(Constants.States.SD, 69);
        timePointMap.get(PolicyTerm.Annual).get(TimePoint.Second).stateOffsetMap.put(Constants.States.VA, 52);

        // 6 Month First
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 53);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.First).stateOffsetMap.put(Constants.States.CT, 95);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.First).stateOffsetMap.put(Constants.States.KY, 95);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.First).stateOffsetMap.put(Constants.States.MD, 95);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.First).stateOffsetMap.put(Constants.States.MT, 95);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.First).stateOffsetMap.put(Constants.States.NJ, 95);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.First).stateOffsetMap.put(Constants.States.PA, 95);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.First).stateOffsetMap.put(Constants.States.SD, 95);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.First).stateOffsetMap.put(Constants.States.VA, 53);

        // 6 Month Second
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 45);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.Second).stateOffsetMap.put(Constants.States.CT, 69);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.Second).stateOffsetMap.put(Constants.States.KY, 84);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.Second).stateOffsetMap.put(Constants.States.MD, 55);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.Second).stateOffsetMap.put(Constants.States.MT, 55);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.Second).stateOffsetMap.put(Constants.States.NJ, 69);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.Second).stateOffsetMap.put(Constants.States.PA, 69);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.Second).stateOffsetMap.put(Constants.States.SD, 69);
        timePointMap.get(PolicyTerm.SixMonth).get(TimePoint.Second).stateOffsetMap.put(Constants.States.VA, 52);

        // Make sure key exists
        multiTermMultiTimePointMapKeyCheck(timePointMap, policyTerm, timePoint, baseJob.getJobName());

        int offset = getOffsetFromMap(timePointMap.get(policyTerm).get(timePoint).stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    /**
     * Returns policyAutomatedRenewalAsyncTaskGenerationJob with offset stored for specified state
     * @param state to set timepoint for
     * @return one job that represents the state timepoint requested
     */
    public static SuperJob policyAutomatedRenewalAsyncTaskGenerationJob(String state){
        Job baseJob = Jobs.policyAutomatedRenewalAsyncTaskGenerationJob;

        StateOffset stateOffset = getStateOffsetMap();
        stateOffset.stateOffsetMap.put(defaultStateKey, 96);
        stateOffset.stateOffsetMap.put(Constants.States.CA, 81);

        int offset = getOffsetFromMap(stateOffset.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob aaaMembershipRenewalBatchOrderAsyncJob(String state, TimePoint timePoint){
        Job baseJob = Jobs.aaaMembershipRenewalBatchOrderAsyncJob;

        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        // Order Membership timepoint
        timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CA, 80);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CT, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.KY, 90);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MD, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NJ, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.PA, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.SD, 75);

        // Membership Revalidation timepoint
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 48);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CA, 66);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CT, 60);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.KY, 75);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MD, 60);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NJ, 60);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.PA, 60);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.SD, 60);

        int offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob aaaInsuranceScoreRenewalBatchOrderAsyncJob(String state, TimePoint timePoint){
        Job baseJob = Jobs.aaaInsuranceScoreRenewalBatchOrderAsyncJob;

        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CA, jobNotApplicableValue);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CO, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.DE, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.KY, 90);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MD, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MT, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NJ, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NV, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NY, jobNotApplicableValue);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.OK, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.OR, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.PA, jobNotApplicableValue);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.VA, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.WV, 63);

        timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 69);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CA, jobNotApplicableValue);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CO, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.DE, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.KY, 84);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MD, 56);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MT, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NJ, 60);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NV, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NY, jobNotApplicableValue);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.OK, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.OR, 46);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.PA, jobNotApplicableValue);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.VA, 53);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.WV, 57);

        int offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob aaaInsuranceScoreRenewalBatchReceiveAsyncJob(String state, TimePoint timePoint,
                                                                        SuperJob aaaInsuranceScoreRenewalBatchOrderAsyncJob){
        Job baseJob = Jobs.aaaInsuranceScoreRenewalBatchReceiveAsyncJob;

        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CA, jobNotApplicableValue);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CO, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.DE, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.KY, 90);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MD, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MT, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NJ, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NV, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NY, jobNotApplicableValue);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.OK, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.OR, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.PA, jobNotApplicableValue);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.VA, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.WV, 63);

        timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 69);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CA, jobNotApplicableValue);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CO, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.DE, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.KY, 84);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MD, 56);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MT, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NJ, 60);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NV, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NY, jobNotApplicableValue);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.OK, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.OR, 46);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.PA, jobNotApplicableValue);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.VA, 53);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.WV, 57);

        int offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset,
                aaaInsuranceScoreRenewalBatchOrderAsyncJob);
    }

    public static SuperJob aaaMvrRenewBatchOrderAsyncJob(String state){
        Job baseJob = Jobs.aaaMvrRenewBatchOrderAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 63);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 75);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 90);
        timePointMap.stateOffsetMap.put(Constants.States.NJ, 75);
        timePointMap.stateOffsetMap.put(Constants.States.NV, 63);
        timePointMap.stateOffsetMap.put(Constants.States.PA, 75);
        timePointMap.stateOffsetMap.put(Constants.States.SD, 75);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob aaaMvrRenewAsyncBatchReceiveJob(String state){
        Job baseJob = Jobs.aaaMvrRenewAsyncBatchReceiveJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 63);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 75);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 90);
        timePointMap.stateOffsetMap.put(Constants.States.NJ, 75);
        timePointMap.stateOffsetMap.put(Constants.States.NV, 63);
        timePointMap.stateOffsetMap.put(Constants.States.PA, 75);
        timePointMap.stateOffsetMap.put(Constants.States.SD, 75);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset, aaaMvrRenewBatchOrderAsyncJob(state));
    }

    public static SuperJob aaaClueRenewBatchOrderAsyncJob(String state){
        Job baseJob = Jobs.aaaClueRenewBatchOrderAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 63);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 75);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 90);
        timePointMap.stateOffsetMap.put(Constants.States.NJ, 75);
        timePointMap.stateOffsetMap.put(Constants.States.NV, 63);
        timePointMap.stateOffsetMap.put(Constants.States.PA, 75);
        timePointMap.stateOffsetMap.put(Constants.States.SD, 75);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob aaaClueRenewAsyncBatchReceiveJob(String state){
        Job baseJob = Jobs.aaaClueRenewAsyncBatchReceiveJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 63);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 75);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 90);
        timePointMap.stateOffsetMap.put(Constants.States.NJ, 75);
        timePointMap.stateOffsetMap.put(Constants.States.NV, 63);
        timePointMap.stateOffsetMap.put(Constants.States.PA, 75);
        timePointMap.stateOffsetMap.put(Constants.States.SD, 75);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset, aaaClueRenewBatchOrderAsyncJob(state));
    }

    public static ArrayList<SuperJob> getRenewalClaimOrderAsyncJobs(String state) {
        ArrayList<SuperJob> jobs = new ArrayList<>();
        jobs.add(renewalClaimOrderAsyncJob(state, TimePoint.First));
        jobs.add(renewalClaimOrderAsyncJob(state, TimePoint.Second));
        return jobs;
    }

    public static SuperJob renewalClaimOrderAsyncJob(String state, TimePoint timePoint){
        Job baseJob = Jobs.renewalClaimOrderAsyncJob;

        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CT, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.KY, 90);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NJ, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.PA, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.SD, 75);

        timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CT, 60);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.KY, 84);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NJ, 60);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.PA, 60);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.SD, 60);

        int offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob renewalImageRatingAsyncTaskJob(String state, TimePoint timePoint){
        Job baseJob = Jobs.renewalImageRatingAsyncTaskJob;

        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        // For default states where both timepoints are same day, JobSchedule will throw out duplicate automatically.

        // Renewal Image Available to all Users (R-45)
        timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 45);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CT, 69);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.KY, 84);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MD, 55);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MT, 55);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NJ, 69);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.PA, 69);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.SD, 69);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.VA, 52);

        // Premium Calculate (R-45)
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 45);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CA, 57);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MD, 55);
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MT, 55);

        int offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob policyDoNotRenewAsyncJob(String state){
        Job baseJob = Jobs.policyDoNotRenewAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 35);
        timePointMap.stateOffsetMap.put(Constants.States.AZ, 47);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 64);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 75);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 50);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 47);
        timePointMap.stateOffsetMap.put(Constants.States.NJ, 62);
        timePointMap.stateOffsetMap.put(Constants.States.NY, 47);
        timePointMap.stateOffsetMap.put(Constants.States.PA, 62);
        timePointMap.stateOffsetMap.put(Constants.States.SD, 62);
        timePointMap.stateOffsetMap.put(Constants.States.VA, 47);
        timePointMap.stateOffsetMap.put(Constants.States.WV, 47);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob renewalOfferAsyncTaskJob(String state) {
        Job baseJob = Jobs.renewalOfferAsyncTaskJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 35);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 50);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 45);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob aaaPreRenewalNoticeAsyncJob(String state) {
        Job baseJob = Jobs.aaaPreRenewalNoticeAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 35);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 50);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 45);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob aaaRenewalNoticeBillAsyncJob(String state) {
        Job baseJob = Jobs.aaaRenewalNoticeBillAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();

        timePointMap.stateOffsetMap.put(defaultStateKey, 20);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob.PaymentSuperJob makeLumpSumPayment(String state, BaseTest baseTest, String policyNumber) {

        // The actual job is not used for this one. This is a placeholder.
        Job baseJob = Jobs.aaaBatchMarkerJob;

        StateOffset timePointMap = getStateOffsetMap();

        timePointMap.stateOffsetMap.put(defaultStateKey, 13);//20);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset).
                new PaymentSuperJob(baseTest,policyNumber, baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static ArrayList<SuperJob> makeMonthlyPayments(BaseTest baseTest, String policyNumber, PolicyTerm policyTerm,
                                                          LocalDateTime expirationDate, boolean makeFinalPayment){

        // The actual job is not used for this one. This is a placeholder.
        Job baseJob = Jobs.aaaBatchMarkerJob;

        ArrayList<SuperJob> jobs = new ArrayList<>();

        int numMonths = policyTerm == PolicyTerm.SixMonth ? 6 : 12;

        int offset = 13;

        // Number of loops is equivalent to number of months.
        for (int i = 0; i < numMonths; i++){

            // The final payment should only be scheduled if true.
            if (i == 0 && makeFinalPayment){
                jobs.add(new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset).
                        new PaymentSuperJob(baseTest,policyNumber, baseJob, SuperJob.JobOffsetType.Subtract_Days, offset));
            }

            // If put in place so it will do nothing if final makeFinalPayment == false
            else if (i > 0){

                // Figure out the proper offset factoring in months of the year.
                LocalDateTime targetDateTime = expirationDate.minusMonths(i).minusDays(offset);

                int dateSpread = Math.abs((int)ChronoUnit.DAYS.between(expirationDate, targetDateTime));

                jobs.add(new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, dateSpread).
                        new PaymentSuperJob(baseTest,policyNumber, baseJob, SuperJob.JobOffsetType.Subtract_Days, dateSpread));

                // BONDTODO: Will probably need to take in the renewal date for this one to generate correct payment offsets
                //           Probably can redo this to be one for loop for both if do it date based.
                //           renewal.months.subtract(iterator).days.add(13) - I think.
                // Offset by months.
            }
        }


        return jobs;
    }

    /**
     * This Reminder is CA only currently. Other states are marked jobNotApplicableValue and skipped by scheduler.
     * @param state Should be CA. If not, the scheduler will skip.
     * @return one job that represents the state requested.
     */
    public static SuperJob preRenewalReminderGenerationAsyncJob(String state) {
        Job baseJob = Jobs.preRenewalReminderGenerationAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, jobNotApplicableValue);
        timePointMap.stateOffsetMap.put(Constants.States.CA, 10);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SuperJob aaaBatchMarkerJob(TimePoint newBusinessTimePoint){
        Job baseJob = Jobs.aaaBatchMarkerJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Add_Days, offset);
    }

    public static SuperJob aaaAutomatedProcessingInitiationJob(TimePoint newBusinessTimePoint){
        Job baseJob = Jobs.aaaAutomatedProcessingInitiationJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Add_Days, offset);
    }

    public static SuperJob automatedProcessingRatingJob(TimePoint newBusinessTimePoint){
        Job baseJob = Jobs.automatedProcessingRatingJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Add_Days, offset);
    }

    public static SuperJob automatedProcessingRunReportsServicesJob(TimePoint newBusinessTimePoint){
        Job baseJob = Jobs.automatedProcessingRunReportsServicesJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Add_Days, offset);
    }

    public static SuperJob automatedProcessingIssuingOrProposingJob(TimePoint newBusinessTimePoint){
        Job baseJob = Jobs.automatedProcessingIssuingOrProposingJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Add_Days, offset);
    }

    public static SuperJob automatedProcessingStrategyStatusUpdateJob(TimePoint newBusinessTimePoint){
        Job baseJob = Jobs.automatedProcessingStrategyStatusUpdateJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SuperJob(baseJob, SuperJob.JobOffsetType.Add_Days, offset);
    }

    private static int getNewBusinessPlus_15_Or_30(TimePoint newBusinessTimePoint){
        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        // Order Membership timepoint
        timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 15);

        // Membership Revalidation timepoint
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 30);

        return getOffsetFromMap(timePointMap.get(newBusinessTimePoint).stateOffsetMap, defaultStateKey);
    }

    /**
     * Policy status update job updates policy status after renewal period. May do other things.
     * @param offsetType Whether to add or subtract days.
     * @param offsetNumberOfDays How many days to adjust based on offsetType
     * @return one job that will be run at the offsetType and offsetNumberOfDays.
     */
    public static SuperJob policyStatusUpdateJob(SuperJob.JobOffsetType offsetType, int offsetNumberOfDays) {
        Job baseJob = Jobs.policyStatusUpdateJob;
        return new SuperJob(baseJob, offsetType, offsetNumberOfDays);
    }



    /**
     * Checks the map passed in for the specific state otherwise returns default.
     * @param stateOffsetMap
     * @param state
     * @return
     * @throws IllegalArgumentException
     */
    private static int getOffsetFromMap(HashMap<String, Integer> stateOffsetMap, String state)throws IllegalArgumentException{
        if (stateOffsetMap.isEmpty() || !stateOffsetMap.containsKey(defaultStateKey)){
            throw new IllegalArgumentException("Arg stateOffsetMap must contain a default value.");
        }
        return stateOffsetMap.getOrDefault(state, stateOffsetMap.get("default"));
    }

    /**
     * Returns an empty HashMap suitable for jobs that have different term dates as well as are run at multiple timepoints.
     * @return
     */
    private static HashMap<PolicyTerm, HashMap<TimePoint, StateOffset>> getMultiTermMultiTimePointMap() {
        HashMap<PolicyTerm, HashMap<TimePoint, StateOffset>> termMap = new HashMap<>();
        termMap.put(PolicyTerm.Annual, getMultiTimePointMap());
        termMap.put(PolicyTerm.SixMonth, getMultiTimePointMap());
        return termMap;
    }

    /**
     * Returns an empty HashMap suitable for jobs with multiple timepoints for the same job.
     * @return
     */
    private static HashMap<TimePoint, StateOffset> getMultiTimePointMap(){
        HashMap<TimePoint, StateOffset> timePointMap = new HashMap<>();
        timePointMap.put(TimePoint.First, getStateOffsetMap());
        timePointMap.put(TimePoint.Second, getStateOffsetMap());
        return timePointMap;
    }

    /**
     * Creates a new StateOffset object for readability.
     * @return
     */
    private static StateOffset getStateOffsetMap(){
        return new SuperJobs().new StateOffset();
    }

    /**
     * Validates the multiTerm HashMap and calls the multiTimePoint check to validate that as well.
     * @param termMap hashmap that contains a list of maps for timepoints to be mapped to policy terms.
     * @param policyTerm that the policy uses as dates change
     * @param timePoint for the state provided
     * @param jobName the job's name for reporting purposes
     * @throws IllegalArgumentException if no key is present for the policyTerm configured
     */
    private static void multiTermMultiTimePointMapKeyCheck( HashMap<PolicyTerm, HashMap<TimePoint, StateOffset>> termMap,
                                                            PolicyTerm policyTerm, TimePoint timePoint, String jobName)
            throws IllegalArgumentException {

        if (!termMap.containsKey(policyTerm)){
            throw new NotImplementedException("No matching policyTerm for SuperJob." + jobName + " for " +
                    policyTerm.toString());
        }

        multiTimePointMapKeyCheck(termMap.get(policyTerm), timePoint, jobName);
    }

    /**
     * Validates the multiTimePoint hashmap.
     * @param termMap hashmap that contains timepoints for jobs that have multiple timepoints in the same state.
     * @param timePoint for the state provided
     * @param jobName the job's name for reporting purposes
     * @throws IllegalArgumentException if no key is present for the timePoint configured
     */
    private static void multiTimePointMapKeyCheck(HashMap<TimePoint, StateOffset> termMap,
                                                           TimePoint timePoint, String jobName)
            throws IllegalArgumentException {

        if (!termMap.containsKey(timePoint)){
            throw new NotImplementedException("No matching timepoint for SuperJob." + jobName + " for " +
                    timePoint.toString());
        }
    }

    /**
     * Wraps a hashmap with a label to avoid confusion. Map represents US States with their offset in days.
     */
    public class StateOffset {
        HashMap<String, Integer> stateOffsetMap = new HashMap<>();
    }
}
