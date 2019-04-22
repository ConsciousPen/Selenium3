package aaa.helpers.jobs;

import aaa.common.enums.Constants;
import aaa.modules.BaseTest;
import org.apache.commons.lang.NotImplementedException;

import java.time.temporal.ChronoUnit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SchedulableJobs are Jobs with additional metadata to be able to call a list of jobs and have them run at the appropriate
 * times automatically to streamline running many jobs.
 */
public class SchedulableJobs {

    public static final String defaultStateKey = "default";
    public static final int jobNotApplicableValue = -1;

    public enum PolicyTerm {Annual, SixMonth}

    public enum PaymentPlan {Full, Monthly}

    public enum TimePoint {First, Second, NotApplicable}

    public enum ProductType {Home, Auto}

    /**
     * Returns both TelematicSafetyScore jobs.
     *
     * @param state      to set timepoints for
     * @param policyTerm that the policy uses as dates change
     * @return an ArrayList containing both TelematicSafetyScore jobs
     */
    public static ArrayList<SchedulableJob> getTelematicSafetyScoreJobs(String state, PolicyTerm policyTerm) {
        ArrayList<SchedulableJob> jobs = new ArrayList<>();
        jobs.add(aaaTelematicSafetyScoreOrderAsyncJob(state, policyTerm, TimePoint.First));
        jobs.add(aaaTelematicSafetyScoreOrderAsyncJob(state, policyTerm, TimePoint.Second));
        return jobs;
    }

    /**
     * Gets a specific Telematics timepoint for a specific state.
     *
     * @param state      to set timepoint for
     * @param policyTerm that the policy uses as dates change
     * @param timePoint  for the state provided
     * @return one job that represents the state, term type, and timepoint requested
     */
    public static SchedulableJob aaaTelematicSafetyScoreOrderAsyncJob(String state, PolicyTerm policyTerm, TimePoint timePoint) {

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

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    /**
     * Returns policyAutomatedRenewalAsyncTaskGenerationJob with offset stored for specified state
     *
     * @param state to set timepoint for
     * @return one job that represents the state timepoint requested
     */
    public static SchedulableJob policyAutomatedRenewalAsyncTaskGenerationJob(ProductType productType, String state) {
        Job baseJob = Jobs.policyAutomatedRenewalAsyncTaskGenerationJob;

        StateOffset stateOffset = getStateOffsetMap();

        if (productType == ProductType.Auto) {
            // Auto
            stateOffset.stateOffsetMap.put(defaultStateKey, 96);
            stateOffset.stateOffsetMap.put(Constants.States.CA, 81);
        } else {
            // Home
            stateOffset.stateOffsetMap.put(defaultStateKey, 73);
            stateOffset.stateOffsetMap.put(Constants.States.CA, 83);
            stateOffset.stateOffsetMap.put(Constants.States.CT, 101);
            stateOffset.stateOffsetMap.put(Constants.States.KY, 108);
            stateOffset.stateOffsetMap.put(Constants.States.MD, 88);
            stateOffset.stateOffsetMap.put(Constants.States.MT, 85);
            stateOffset.stateOffsetMap.put(Constants.States.NY, 88);
            stateOffset.stateOffsetMap.put(Constants.States.WA, 85);
            stateOffset.stateOffsetMap.put(Constants.States.WY, 85);
        }

        int offset = getOffsetFromMap(stateOffset.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaMembershipRenewalBatchOrderAsyncJob(ProductType productType, String state, TimePoint timePoint) {
        Job baseJob = Jobs.aaaMembershipRenewalBatchOrderAsyncJob;

        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        if (productType == ProductType.Auto) {
            // Auto Order Membership timepoint
            timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 63);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CA, 80);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CT, 75);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.KY, 90);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MD, 75);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NJ, 75);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.PA, 75);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.SD, 75);

            // Auto Membership Revalidation timepoint
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 48);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CA, 66);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CT, 60);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.KY, 75);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MD, 60);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NJ, 60);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.PA, 60);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.SD, 60);
        } else {
            // productType = ProductType.Home

            // Home Order Membership timepoint
            timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 63);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CA, 73);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CT, 91);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.KY, 98);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MD, 78);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MT, 75);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NY, 78);
            timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.WY, 75);


            // Home Membership Revalidation timepoint
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 48);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CA, 59);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.CT, 76);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.KY, 83);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MD, 63);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.MT, 60);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.NY, 63);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.WA, 60);
            timePointMap.get(TimePoint.Second).stateOffsetMap.put(Constants.States.WY, 60);
        }

        int offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaInsuranceScoreRenewalBatchOrderAsyncJob(ProductType productType, String state, TimePoint timePoint) {
        Job baseJob = Jobs.aaaInsuranceScoreRenewalBatchOrderAsyncJob;

        int offset;

        if (productType == ProductType.Auto) {
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

            offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);
        } else {
            // Property
            StateOffset timePointMap = getStateOffsetMap();

            timePointMap.stateOffsetMap.put(defaultStateKey, 63);
            timePointMap.stateOffsetMap.put(Constants.States.MD, 78);
            timePointMap.stateOffsetMap.put(Constants.States.CT, 91);
            timePointMap.stateOffsetMap.put(Constants.States.KY, 98);
            timePointMap.stateOffsetMap.put(Constants.States.MT, 75);
            timePointMap.stateOffsetMap.put(Constants.States.WY, 75);
            timePointMap.stateOffsetMap.put(Constants.States.NY, 78);
            timePointMap.stateOffsetMap.put(Constants.States.CA, 73);

            offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);
        }

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }


    /**
     * Orders Insurance score for both Auto SS and all Property Products
     *
     * @param productType                                Which product type to use.
     * @param state                                      Which state to lookup offset from.
     * @param timePoint                                  Only pertains to Auto. This field is ignored using Home products.
     * @return SchedulableJob with correct timepoint.
     */
    public static SchedulableJob aaaInsuranceScoreRenewalBatchReceiveAsyncJob(ProductType productType, String state, TimePoint timePoint) {
        Job baseJob = Jobs.aaaInsuranceScoreRenewalBatchReceiveAsyncJob;

        int offset;

        if (productType == ProductType.Auto) {
            // Auto
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

            offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);
        } else {
            // Property
            StateOffset timePointMap = getStateOffsetMap();

            timePointMap.stateOffsetMap.put(defaultStateKey, 63);
            timePointMap.stateOffsetMap.put(Constants.States.MD, 78);
            timePointMap.stateOffsetMap.put(Constants.States.CT, 91);
            timePointMap.stateOffsetMap.put(Constants.States.KY, 98);
            timePointMap.stateOffsetMap.put(Constants.States.MT, 75);
            timePointMap.stateOffsetMap.put(Constants.States.WY, 75);
            timePointMap.stateOffsetMap.put(Constants.States.NY, 78);
            timePointMap.stateOffsetMap.put(Constants.States.CA, 73);

            offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);
        }


        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset,
                aaaInsuranceScoreRenewalBatchOrderAsyncJob(productType, state, timePoint));
    }

    public static SchedulableJob aaaMvrRenewBatchOrderAsyncJob(String state) {
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

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaMvrRenewAsyncBatchReceiveJob(String state) {
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

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset, aaaMvrRenewBatchOrderAsyncJob(state));
    }

    public static SchedulableJob aaaClueRenewBatchOrderAsyncJob(ProductType productType, String state) {
        Job baseJob = Jobs.aaaClueRenewBatchOrderAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();

        if (productType == ProductType.Auto) {
            timePointMap.stateOffsetMap.put(defaultStateKey, 63);
            timePointMap.stateOffsetMap.put(Constants.States.CT, 75);
            timePointMap.stateOffsetMap.put(Constants.States.KY, 90);
            timePointMap.stateOffsetMap.put(Constants.States.NJ, 75);
            timePointMap.stateOffsetMap.put(Constants.States.NV, 63);
            timePointMap.stateOffsetMap.put(Constants.States.PA, 75);
            timePointMap.stateOffsetMap.put(Constants.States.SD, 75);
        } else {
            timePointMap.stateOffsetMap.put(defaultStateKey, 63);
            timePointMap.stateOffsetMap.put(Constants.States.MD, 78);
            timePointMap.stateOffsetMap.put(Constants.States.CT, 91);
            timePointMap.stateOffsetMap.put(Constants.States.KY, 98);
            timePointMap.stateOffsetMap.put(Constants.States.MT, 75);
            timePointMap.stateOffsetMap.put(Constants.States.WY, 75);
            timePointMap.stateOffsetMap.put(Constants.States.NY, 78);
            timePointMap.stateOffsetMap.put(Constants.States.CA, 73);
        }

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaClueRenewAsyncBatchReceiveJob(ProductType productType, String state) {
        Job baseJob = Jobs.aaaClueRenewAsyncBatchReceiveJob;

        StateOffset timePointMap = getStateOffsetMap();

        if (productType == ProductType.Auto) {
            // Auto
            timePointMap.stateOffsetMap.put(defaultStateKey, 63);
            timePointMap.stateOffsetMap.put(Constants.States.CT, 75);
            timePointMap.stateOffsetMap.put(Constants.States.KY, 90);
            timePointMap.stateOffsetMap.put(Constants.States.NJ, 75);
            timePointMap.stateOffsetMap.put(Constants.States.NV, 63);
            timePointMap.stateOffsetMap.put(Constants.States.PA, 75);
            timePointMap.stateOffsetMap.put(Constants.States.SD, 75);
        } else {
            // Home
            timePointMap.stateOffsetMap.put(defaultStateKey, 63);
            timePointMap.stateOffsetMap.put(Constants.States.MD, 78);
            timePointMap.stateOffsetMap.put(Constants.States.CT, 91);
            timePointMap.stateOffsetMap.put(Constants.States.KY, 98);
            timePointMap.stateOffsetMap.put(Constants.States.MT, 75);
            timePointMap.stateOffsetMap.put(Constants.States.WY, 75);
            timePointMap.stateOffsetMap.put(Constants.States.NY, 78);
            timePointMap.stateOffsetMap.put(Constants.States.CA, 73);
        }

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset,
                aaaClueRenewBatchOrderAsyncJob(productType, state));
    }

    public static ArrayList<SchedulableJob> getRenewalClaimOrderAsyncJobs(String state) {
        ArrayList<SchedulableJob> jobs = new ArrayList<>();
        jobs.add(renewalClaimOrderAsyncJob(state, TimePoint.First));
        jobs.add(renewalClaimOrderAsyncJob(state, TimePoint.Second));
        return jobs;
    }

    public static SchedulableJob renewalClaimOrderAsyncJob(String state, TimePoint timePoint) {
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

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob renewalImageRatingAsyncTaskJob(String state, TimePoint timePoint) {
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

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob policyDoNotRenewAsyncJob(ProductType productType, String state) {
        Job baseJob = Jobs.policyDoNotRenewAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();

        if (productType == ProductType.Auto) {
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
        }
        else{
            // BondTODO: Chart is missing data on this one. Will do more research.
            timePointMap.stateOffsetMap.put(defaultStateKey, 34);
        }

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob renewalOfferAsyncTaskJob(ProductType productType, String state) {

        Job baseJob = Jobs.renewalOfferAsyncTaskJob;

        StateOffset timePointMap = getStateOffsetMap();

        if (productType == ProductType.Auto) {
            // Auto
            timePointMap.stateOffsetMap.put(defaultStateKey, 35);
            timePointMap.stateOffsetMap.put(Constants.States.MD, 50);
            timePointMap.stateOffsetMap.put(Constants.States.MT, 45);
        }
        else {
            // Property
            timePointMap.stateOffsetMap.put(defaultStateKey, 35);
            timePointMap.stateOffsetMap.put(Constants.States.MD, 50);
            timePointMap.stateOffsetMap.put(Constants.States.MT, 47);
            timePointMap.stateOffsetMap.put(Constants.States.WY, 47);
            timePointMap.stateOffsetMap.put(Constants.States.WA, 47);
            timePointMap.stateOffsetMap.put(Constants.States.CA, 48);
        }

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaPreRenewalNoticeAsyncJob(String state) {
        Job baseJob = Jobs.aaaPreRenewalNoticeAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 35);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 50);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 45);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaPolicyAutomatedRenewalAsyncTaskGenerationJob(ProductType productType, String state,
                                                                                 TimePoint timePoint) {
        Job baseJob = Jobs.aaaPolicyAutomatedRenewalAsyncTaskGenerationJob;

        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        // Home Timepoint 1 = Order Reports & Prefill
        timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 63);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MD, 78);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CT, 91);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.KY, 98);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.MT, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.WY, 75);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.NY, 78);
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CA, 73);

        // Home Timepoint 2 = Do not renew alert
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 34);
        // No data in the sheet other than default day for this time point.

        int offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaRenewalNoticeBillAsyncJob(ProductType productType, String state) {
        Job baseJob = Jobs.aaaRenewalNoticeBillAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();

        if (productType == ProductType.Auto) {
            // Auto
            timePointMap.stateOffsetMap.put(defaultStateKey, 20);
        }
        else {
            // Property
            timePointMap.stateOffsetMap.put(defaultStateKey, 20);
            timePointMap.stateOffsetMap.put(Constants.States.CA, jobNotApplicableValue);
        }

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob.PaymentSchedulableJob makeLumpSumPayment(String state, BaseTest baseTest, String policyNumber) {

        // The actual job is not used for this one. This is a placeholder.
        Job baseJob = Jobs.aaaBatchMarkerJob;

        StateOffset timePointMap = getStateOffsetMap();

        timePointMap.stateOffsetMap.put(defaultStateKey, 13);//20);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset).
                new PaymentSchedulableJob(baseTest, policyNumber, baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static ArrayList<SchedulableJob> makeMonthlyPayments(BaseTest baseTest, String policyNumber, PolicyTerm policyTerm,
                                                                LocalDateTime expirationDate, boolean makeFinalPayment) {

        // The actual job is not used for this one. This is a placeholder.
        Job baseJob = Jobs.aaaBatchMarkerJob;

        ArrayList<SchedulableJob> jobs = new ArrayList<>();

        int numMonths = policyTerm == PolicyTerm.SixMonth ? 6 : 12;

        // When figuring what day to run, subtracts this from end of month.
        int daysBeforeEndOfMonthPaymentOffset = 13;

        // Number of loops is equivalent to number of months.
        for (int i = 0; i < numMonths; i++) {

            // The final payment should only be scheduled if true.
            if (i == 0 && makeFinalPayment) {
                jobs.add(new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, daysBeforeEndOfMonthPaymentOffset).
                        new PaymentSchedulableJob(baseTest, policyNumber, baseJob, SchedulableJob.JobOffsetType.Subtract_Days, daysBeforeEndOfMonthPaymentOffset));
            }

            // If put in place so it will do nothing if final makeFinalPayment == false
            else if (i > 0) {

                // Figure out the proper offset factoring in months of the year.
                LocalDateTime targetDateTime = expirationDate.minusMonths(i).minusDays(daysBeforeEndOfMonthPaymentOffset);

                int dateSpread = Math.abs((int) ChronoUnit.DAYS.between(expirationDate, targetDateTime));

                jobs.add(new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, dateSpread).
                        new PaymentSchedulableJob(baseTest, policyNumber, baseJob, SchedulableJob.JobOffsetType.Subtract_Days, dateSpread));

                // BONDTODO: Will probably need to take in the renewal date for this one to generate correct payment offsets
                //           Probably can redo this to be one for loop for both if do it date based.
                //           renewal.months.subtract(iterator).days.add(13) - I think.
                // Offset by months.
            }
        }

        return jobs;
    }


    /**
     * This is required because of differences in VDM vs Prod. This is only needed for Home/Property renewals.
     * Runs at Run Rules window (R-57). Full name updRenewTimelineIndicatorSchedulableJob
     */
    public static SchedulableJob.updRenewTimelineIndicatorSchedulableJob updateRenewalTimelineIndicator(String state,
                                                                                                        String policyNumber) {

        // The actual job is not used for this one. This is a placeholder.
        Job baseJob = Jobs.aaaBatchMarkerJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 57);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 72);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 85);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 92);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 69);
        timePointMap.stateOffsetMap.put(Constants.States.WY, 69);
        timePointMap.stateOffsetMap.put(Constants.States.NY, 72);
        timePointMap.stateOffsetMap.put(Constants.States.CA, 67);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset).
                new updRenewTimelineIndicatorSchedulableJob(policyNumber, baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    /**
     * This Reminder is CA only currently. Other states are marked jobNotApplicableValue and skipped by scheduler.
     * @param productType Which product to get timepoints for.
     * @param state Should be CA. If not, the scheduler will skip.
     * @return one job that represents the state requested.
     */
    public static SchedulableJob preRenewalReminderGenerationAsyncJob(ProductType productType, String state) {
        Job baseJob = Jobs.preRenewalReminderGenerationAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();

        // Leaving this way just in case they ever change
        if (productType == ProductType.Auto) {
            // Auto
            timePointMap.stateOffsetMap.put(defaultStateKey, jobNotApplicableValue);
            timePointMap.stateOffsetMap.put(Constants.States.CA, 10);
        }
        else {
            // Property
            timePointMap.stateOffsetMap.put(defaultStateKey, jobNotApplicableValue);
            timePointMap.stateOffsetMap.put(Constants.States.CA, 10);
        }

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaBatchMarkerJob(TimePoint newBusinessTimePoint) {
        Job baseJob = Jobs.aaaBatchMarkerJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Add_Days, offset);
    }

    public static SchedulableJob aaaAutomatedProcessingInitiationJob(TimePoint newBusinessTimePoint) {
        Job baseJob = Jobs.aaaAutomatedProcessingInitiationJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Add_Days, offset);
    }

    public static SchedulableJob automatedProcessingRatingJob(TimePoint newBusinessTimePoint) {
        Job baseJob = Jobs.automatedProcessingRatingJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Add_Days, offset);
    }

    public static SchedulableJob automatedProcessingRunReportsServicesJob(TimePoint newBusinessTimePoint) {
        Job baseJob = Jobs.automatedProcessingRunReportsServicesJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Add_Days, offset);
    }

    public static SchedulableJob automatedProcessingIssuingOrProposingJob(TimePoint newBusinessTimePoint) {
        Job baseJob = Jobs.automatedProcessingIssuingOrProposingJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Add_Days, offset);
    }

    public static SchedulableJob automatedProcessingStrategyStatusUpdateJob(TimePoint newBusinessTimePoint) {
        Job baseJob = Jobs.automatedProcessingStrategyStatusUpdateJob;

        int offset = getNewBusinessPlus_15_Or_30(newBusinessTimePoint);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Add_Days, offset);
    }

    public static SchedulableJob isoRenewalBatchOrderJob(String state) {
        // Property
        Job baseJob = Jobs.isoRenewalBatchOrderJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 63);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 78);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 91);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 98);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 75);
        timePointMap.stateOffsetMap.put(Constants.States.WY, 75);
        timePointMap.stateOffsetMap.put(Constants.States.NY, 78);
        timePointMap.stateOffsetMap.put(Constants.States.CA, 73);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaCreditDisclosureNoticeJob(String state) {
        // Property
        Job baseJob = Jobs.aaaCreditDisclosureNoticeJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, jobNotApplicableValue);
        timePointMap.stateOffsetMap.put(Constants.States.WV, 60);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaIsoRenewAsyncBatchReceiveJob(String state) {
        // Property
        Job baseJob = Jobs.aaaIsoRenewAsyncBatchReceiveJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 63);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 78);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 91);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 98);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 75);
        timePointMap.stateOffsetMap.put(Constants.States.WY, 75);
        timePointMap.stateOffsetMap.put(Constants.States.NY, 78);
        timePointMap.stateOffsetMap.put(Constants.States.CA, 73);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset,
                aaaClueRenewBatchOrderAsyncJob(SchedulableJobs.ProductType.Home, state));
    }

    public static SchedulableJob renewalValidationAsyncTaskJob(String state) {
        // Property
        Job baseJob = Jobs.renewalValidationAsyncTaskJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 57);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 72);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 85);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 92);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 69);
        timePointMap.stateOffsetMap.put(Constants.States.WY, 69);
        timePointMap.stateOffsetMap.put(Constants.States.NY, 72);
        timePointMap.stateOffsetMap.put(Constants.States.CA, 67);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaRenewalDataRefreshAsyncJob(String state) {
        // Property
        Job baseJob = Jobs.aaaRenewalDataRefreshAsyncJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 57);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 72);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 85);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 92);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 69);
        timePointMap.stateOffsetMap.put(Constants.States.WY, 69);
        timePointMap.stateOffsetMap.put(Constants.States.NY, 72);
        timePointMap.stateOffsetMap.put(Constants.States.CA, 67);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob renewalImageRatingAsyncTaskJob(String state) {
        // Property
        Job baseJob = Jobs.renewalImageRatingAsyncTaskJob;

        StateOffset timePointMap = getStateOffsetMap();
        timePointMap.stateOffsetMap.put(defaultStateKey, 45);
        timePointMap.stateOffsetMap.put(Constants.States.MD, 60);
        timePointMap.stateOffsetMap.put(Constants.States.CT, 73);
        timePointMap.stateOffsetMap.put(Constants.States.KY, 80);
        timePointMap.stateOffsetMap.put(Constants.States.MT, 57);
        timePointMap.stateOffsetMap.put(Constants.States.WY, 57);
        timePointMap.stateOffsetMap.put(Constants.States.NY, 60);
        timePointMap.stateOffsetMap.put(Constants.States.WA, 57);
        timePointMap.stateOffsetMap.put(Constants.States.CA, 58);

        int offset = getOffsetFromMap(timePointMap.stateOffsetMap, state);

        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
    }

    public static SchedulableJob aaaMortgageeRenewalReminderAndExpNoticeAsyncJob(String state, TimePoint timePoint) {
        // Property
        Job baseJob = Jobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob;

        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 10);    // SS
        timePointMap.get(TimePoint.First).stateOffsetMap.put(Constants.States.CA, 5); // CA

        timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 40);

        int offset = getOffsetFromMap(timePointMap.get(timePoint).stateOffsetMap, state);

        // Timepoint.First is before renewal so subtract days.
        if (timePoint == TimePoint.First){
            return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Subtract_Days, offset);
        }
        // Timepoint.Second is after renewal so add days.
        return new SchedulableJob(baseJob, SchedulableJob.JobOffsetType.Add_Days, offset);
    }

    private static int getNewBusinessPlus_15_Or_30(TimePoint newBusinessTimePoint) {
        HashMap<TimePoint, StateOffset> timePointMap = getMultiTimePointMap();

        // Order Membership timepoint
        timePointMap.get(TimePoint.First).stateOffsetMap.put(defaultStateKey, 15);

        // Membership Revalidation timepoint
        timePointMap.get(TimePoint.Second).stateOffsetMap.put(defaultStateKey, 30);

        return getOffsetFromMap(timePointMap.get(newBusinessTimePoint).stateOffsetMap, defaultStateKey);
    }

    /**
     * Policy status update job updates policy status after renewal period. May do other things.
     *
     * @param offsetType         Whether to add or subtract days.
     * @param offsetNumberOfDays How many days to adjust based on offsetType
     * @return one job that will be run at the offsetType and offsetNumberOfDays.
     */
    public static SchedulableJob policyStatusUpdateJob(SchedulableJob.JobOffsetType offsetType, int offsetNumberOfDays) {
        Job baseJob = Jobs.policyStatusUpdateJob;
        return new SchedulableJob(baseJob, offsetType, offsetNumberOfDays);
    }


    /**
     * Checks the map passed in for the specific state otherwise returns default.
     *
     * @param stateOffsetMap
     * @param state
     * @return
     * @throws IllegalArgumentException
     */
    private static int getOffsetFromMap(HashMap<String, Integer> stateOffsetMap, String state) throws IllegalArgumentException {
        if (stateOffsetMap.isEmpty() || !stateOffsetMap.containsKey(defaultStateKey)) {
            throw new IllegalArgumentException("Arg stateOffsetMap must contain a default value.");
        }
        return stateOffsetMap.getOrDefault(state, stateOffsetMap.get("default"));
    }

    /**
     * Returns an empty HashMap suitable for jobs that have different term dates as well as are run at multiple timepoints.
     *
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
     *
     * @return
     */
    private static HashMap<TimePoint, StateOffset> getMultiTimePointMap() {
        HashMap<TimePoint, StateOffset> timePointMap = new HashMap<>();
        timePointMap.put(TimePoint.First, getStateOffsetMap());
        timePointMap.put(TimePoint.Second, getStateOffsetMap());
        return timePointMap;
    }

    /**
     * Creates a new StateOffset object for readability.
     *
     * @return
     */
    private static StateOffset getStateOffsetMap() {
        return new SchedulableJobs().new StateOffset();
    }

    /**
     * Validates the multiTerm HashMap and calls the multiTimePoint check to validate that as well.
     *
     * @param termMap    hashmap that contains a list of maps for timepoints to be mapped to policy terms.
     * @param policyTerm that the policy uses as dates change
     * @param timePoint  for the state provided
     * @param jobName    the job's name for reporting purposes
     * @throws IllegalArgumentException if no key is present for the policyTerm configured
     */
    private static void multiTermMultiTimePointMapKeyCheck(HashMap<PolicyTerm, HashMap<TimePoint, StateOffset>> termMap,
                                                           PolicyTerm policyTerm, TimePoint timePoint, String jobName)
            throws IllegalArgumentException {

        if (!termMap.containsKey(policyTerm)) {
            throw new NotImplementedException("No matching policyTerm for SchedulableJob." + jobName + " for " +
                    policyTerm.toString());
        }

        multiTimePointMapKeyCheck(termMap.get(policyTerm), timePoint, jobName);
    }

    /**
     * Validates the multiTimePoint hashmap.
     *
     * @param termMap   hashmap that contains timepoints for jobs that have multiple timepoints in the same state.
     * @param timePoint for the state provided
     * @param jobName   the job's name for reporting purposes
     * @throws IllegalArgumentException if no key is present for the timePoint configured
     */
    private static void multiTimePointMapKeyCheck(HashMap<TimePoint, StateOffset> termMap,
                                                  TimePoint timePoint, String jobName)
            throws IllegalArgumentException {

        if (!termMap.containsKey(timePoint)) {
            throw new NotImplementedException("No matching timepoint for SchedulableJob." + jobName + " for " +
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
