package aaa.helpers.renewal;

import aaa.helpers.jobs.*;
import aaa.modules.BaseTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static aaa.helpers.db.queries.AAAMembershipQueries.getPolicyEffectivLocalDateTimeFromSQL;
import static aaa.helpers.db.queries.AAAMembershipQueries.getPolicyExpirationLocalDateTimeFromSQL;

public class SuperJobRenewalHelper {

    // Set to true for fast debugging.
    static boolean simulateOnly = false;

    public static ArrayList<String> performFullAutoRenewal(BaseTest baseTest, String policyNumber,
                                                           SuperJobs.PolicyTerm policyTerm,
                                                           SuperJobs.PaymentPlan paymentPlan,
                                                           boolean makeFinalRenewalPayment, int numberOfRenewals){

        ArrayList<String> output = new ArrayList<>();

        JobSchedule newBusiness15_30Schedule = getNewBusinessPlus15_30Schedule(policyNumber);

        output.addAll(SuperJob.executeJobSchedule(newBusiness15_30Schedule, simulateOnly));

        // Loop every policy term expiration.
        for (int i = 0; i < numberOfRenewals; i++){
            LocalDateTime expirationDate = getPolicyExpirationLocalDateTimeFromSQL(policyNumber);

            JobSchedule jobSchedule;

            if (paymentPlan == SuperJobs.PaymentPlan.Monthly)
            {
                jobSchedule = new JobSchedule(getAutoRenewalJobList(baseTest, policyNumber, policyTerm, paymentPlan, makeFinalRenewalPayment), expirationDate);
            }

            else {

                boolean makeFinalPayment = true;

                // Only evaluate for the last renewal
                if (i == numberOfRenewals - 1) {
                    makeFinalPayment = makeFinalRenewalPayment;
                }

                jobSchedule = new JobSchedule(getAutoRenewalJobList(baseTest, policyNumber, policyTerm, paymentPlan, makeFinalPayment), expirationDate);
            }
            output.addAll(SuperJob.executeJobSchedule(jobSchedule, simulateOnly));
        }

        return output;
    }

    /**
     * Returns a job schedule validating new business at 15 and 30 days after policy effective date. (NB+15/30)
     * @param policyNumber is used to get the effective date of the policy
     * @return a job schedule that can be executed with SuperJob.executeJobSchedule()
     */
    public static JobSchedule getNewBusinessPlus15_30Schedule(String policyNumber){
        return new JobSchedule(getNewBusinessPlus15_30JobList(), getPolicyEffectivLocalDateTimeFromSQL(policyNumber));
    }

    /**
     * Gets a list of all jobs for validating new business at 15 and 30 days after policy effective date. (NB+15/30)
     * @return ArrayList of Jobs that can be used to build a schedule for any new policy.
     */
    public static ArrayList<SuperJob> getNewBusinessPlus15_30JobList(){

        ArrayList<SuperJob> jobList = new ArrayList<>();

        // Add all New Business (NB) jobs at TimePoint 1 & 2. TP1 = NB+15 TP2 = NB+30
        jobList.addAll(newBusinessSTG1STG2Jobs(SuperJobs.TimePoint.First));
        jobList.addAll(newBusinessSTG1STG2Jobs(SuperJobs.TimePoint.Second));

        return jobList;
    }

    /**
     * All the jobs needed at New Business + 15 and New Business + 30.
     * @param timePoint is what Time Point to use. TimePoint.First = NB+15. TimePoint.Second = NB+30
     * @return ArrayList of Jobs for one of the timepoints.
     */
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

    /**
     * Gets a list of all required jobs to perform an auto renewal for both CA and SS.
     *
     * @param baseTest     Needed so app can open to make a payment. Pass this keyword from calling test.
     * @param policyNumber Also needed during making a payment through the app.
     * @param policyTerm   What length of policy is.
     * @param paymentPlan  What type of payment plan was created.
     * @param makePayment  Determines whether to make payment for this renewal. Useful for testing non-payment scenarios.
     * @return ArrayList of Jobs that can be used to build a schedule for Auto Renewals
     */
    public static ArrayList<SuperJob> getAutoRenewalJobList(
            BaseTest baseTest, String policyNumber, SuperJobs.PolicyTerm policyTerm, SuperJobs.PaymentPlan paymentPlan, boolean makePayment) {

        String state = BaseTest.getState();

        ArrayList<SuperJob> jobList = new ArrayList<>();

        // Payment jobs are first so they will be scheduled first on their respective days.
        if (paymentPlan == SuperJobs.PaymentPlan.Monthly){
            LocalDateTime expirationDate = getPolicyExpirationLocalDateTimeFromSQL(policyNumber);
            jobList.addAll(SuperJobs.makeMonthlyPayments(baseTest,policyNumber,policyTerm, expirationDate, makePayment));
        }
        else if (paymentPlan == SuperJobs.PaymentPlan.Monthly && makePayment) {
            //Special Make Payment job
            jobList.add(SuperJobs.makeLumpSumPayment(state, baseTest, policyNumber));
        }

        //Initiate Renewal
        jobList.add(SuperJobs.policyAutomatedRenewalAsyncTaskGenerationJob(state));

        //UBI SafetyScore
        jobList.addAll(SuperJobs.getTelematicSafetyScoreJobs(state, policyTerm)); //GDN

        //Order Membership
        jobList.add(SuperJobs.aaaMembershipRenewalBatchOrderAsyncJob(state, SuperJobs.TimePoint.First));

        //Order Insurance Score
        SuperJob aaaInsuranceScoreRenewalBatchOrderAsyncJob =
                SuperJobs.aaaInsuranceScoreRenewalBatchOrderAsyncJob(state, SuperJobs.TimePoint.First);

        jobList.add(aaaInsuranceScoreRenewalBatchOrderAsyncJob);

        jobList.add(SuperJobs.aaaInsuranceScoreRenewalBatchReceiveAsyncJob(state,
                SuperJobs.TimePoint.First, aaaInsuranceScoreRenewalBatchOrderAsyncJob));

        //Order MVR/CLUE
        jobList.add(SuperJobs.aaaMvrRenewBatchOrderAsyncJob(state));    // GDN
        jobList.add(SuperJobs.aaaMvrRenewAsyncBatchReceiveJob(state));  // GDN
        jobList.add(SuperJobs.aaaClueRenewBatchOrderAsyncJob(state));   // GDN
        jobList.add(SuperJobs.aaaClueRenewAsyncBatchReceiveJob(state)); // GDN

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



        //Renewal Reminder
        jobList.add(SuperJobs.preRenewalReminderGenerationAsyncJob(state));

        //R+1 Update Status
        jobList.add(SuperJobs.policyStatusUpdateJob(SuperJob.JobOffsetType.Add_Days, 1));

        return jobList;
    }
}
