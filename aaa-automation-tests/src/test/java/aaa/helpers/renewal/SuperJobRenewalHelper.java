package aaa.helpers.renewal;

import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.jobs.SuperJob;
import aaa.helpers.jobs.SuperJobs;
import aaa.modules.BaseTest;

import java.util.ArrayList;

public class SuperJobRenewalHelper {

    /**
     * Gets a list of all required jobs to perform an auto renewal for both CA and SS.
     *
     * @param baseTest     Needed so app can open to make a payment. Pass this keyword from calling test.
     * @param policyNumber Also needed during making a payment through the app.
     * @param policyTerm   What length of policy is.
     * @param makePayment  Determines whether to make payment for this renewal. Useful for testing non-payment scenarios.
     * @return ArrayList of Jobs that can be used to build a schedule for Auto Renewals
     */
    public static ArrayList<SuperJob> getAutoRenewalJobList(
            BaseTest baseTest, String policyNumber, SuperJobs.PolicyTerm policyTerm, boolean makePayment) {

        String state = BaseTest.getState();

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
            jobList.add(SuperJobs.makePayment(state, baseTest, policyNumber));
        }

        //Renewal Reminder
        jobList.add(SuperJobs.preRenewalReminderGenerationAsyncJob(state));

        //R+1 Update Status
        jobList.add(SuperJobs.policyStatusUpdateJob(SuperJob.JobOffsetType.Add_Days, 1));

        return jobList;
    }

    public static ArrayList<SuperJob> getNewBusinessPlus15_30JobList(){

        ArrayList<SuperJob> jobList = new ArrayList<>();

        // Add all New Business (NB) jobs at TimePoint 1 & 2. TP1 = NB+15 TP2 = NB+30
        jobList.addAll(newBusinessSTG1STG2Jobs(SuperJobs.TimePoint.First));
        jobList.addAll(newBusinessSTG1STG2Jobs(SuperJobs.TimePoint.Second));

        return jobList;
    }

    private static ArrayList<SuperJob> newBusinessSTG1STG2Jobs(SuperJobs.TimePoint timePoint) {
        ArrayList<SuperJob> jobList = new ArrayList<>();
        jobList.add(SuperJobs.aaaBatchMarkerJob(timePoint));
        jobList.add(SuperJobs.aaaAutomatedProcessingInitiationJob(timePoint));
        jobList.add(SuperJobs.automatedProcessingRatingJob(timePoint));
        jobList.add(SuperJobs.automatedProcessingRunReportsServicesJob(timePoint));
        jobList.add(SuperJobs.automatedProcessingIssuingOrProposingJob(timePoint));
        jobList.add(SuperJobs.automatedProcessingStrategyStatusUpdateJob(timePoint));

        return jobList;
    }
}
