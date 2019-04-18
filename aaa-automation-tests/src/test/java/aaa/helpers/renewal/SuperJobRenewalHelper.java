package aaa.helpers.renewal;

import aaa.helpers.jobs.*;
import aaa.modules.BaseTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static aaa.helpers.db.queries.AAAMembershipQueries.getPolicyEffectivLocalDateTimeFromSQL;
import static aaa.helpers.db.queries.AAAMembershipQueries.getPolicyExpirationLocalDateTimeFromSQL;

public class SuperJobRenewalHelper {

    // Set to true for fast debugging.
    static boolean simulateOnly = true;

    public static ArrayList<String> performFullAutoRenewal(BaseTest baseTest, String policyNumber,
                                                           SuperJobs.PolicyTerm policyTerm,
                                                           SuperJobs.PaymentPlan paymentPlan,
                                                           boolean makeFinalRenewalPayment, int numberOfRenewals){

        JobSchedule newBusiness15_30Schedule = getNewBusinessPlus15_30Schedule(policyNumber);

        ArrayList<String> output = SuperJob.executeJobSchedule(newBusiness15_30Schedule, simulateOnly);

        output.addAll(performRenewals(SuperJobs.ProductType.Auto, baseTest, policyNumber, policyTerm, paymentPlan,
                makeFinalRenewalPayment, numberOfRenewals));

        return output;
    }

    public static ArrayList<String> performFullHomeRenewal(BaseTest baseTest, String policyNumber,
                                                           SuperJobs.PolicyTerm policyTerm,
                                                           SuperJobs.PaymentPlan paymentPlan,
                                                           boolean makeFinalRenewalPayment, int numberOfRenewals){

        JobSchedule newBusiness15_30Schedule = getNewBusinessPlus15_30Schedule(policyNumber);

        ArrayList<String> output = SuperJob.executeJobSchedule(newBusiness15_30Schedule, simulateOnly);

        output.addAll(performRenewals(SuperJobs.ProductType.Home, baseTest, policyNumber, policyTerm, paymentPlan,
                makeFinalRenewalPayment, numberOfRenewals));

        return output;
    }

    private static ArrayList<String> performRenewals(SuperJobs.ProductType productType, BaseTest baseTest, String policyNumber,
                                              SuperJobs.PolicyTerm policyTerm, SuperJobs.PaymentPlan paymentPlan,
                                              boolean makeFinalRenewalPayment, int numberOfRenewals){

        ArrayList<String> output = new ArrayList<>();

        // Loop every policy term expiration.
        for (int i = 0; i < numberOfRenewals; i++){
            LocalDateTime expirationDate = getPolicyExpirationLocalDateTimeFromSQL(policyNumber);

            JobSchedule jobSchedule;

            // Update output when a new renewal starts
            output.add("==[Renewal Number " + (i + 1) + "]==");

            // Only use value for make final payment if we are on the last renewal.
            boolean makeFinalPayment = (i != numberOfRenewals - 1) || makeFinalRenewalPayment;

            if (productType == SuperJobs.ProductType.Auto) {
                jobSchedule = new JobSchedule(getAutoRenewalJobList(baseTest, policyNumber, policyTerm, paymentPlan, makeFinalPayment), expirationDate);
            }
            else {
                jobSchedule = new JobSchedule(getHomeRenewalJobList(baseTest, policyNumber, policyTerm, paymentPlan, makeFinalPayment), expirationDate);
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

        // Schedule payment jobs first so they always run first
        ArrayList<SuperJob> jobList = getPaymentJobList(baseTest,policyNumber, policyTerm, paymentPlan, makePayment);

        //Initiate Renewal
        jobList.add(SuperJobs.policyAutomatedRenewalAsyncTaskGenerationJob(SuperJobs.ProductType.Auto, state));

        //UBI SafetyScore
        jobList.addAll(SuperJobs.getTelematicSafetyScoreJobs(state, policyTerm)); //GDN

        //Order Membership
        jobList.add(SuperJobs.aaaMembershipRenewalBatchOrderAsyncJob(SuperJobs.ProductType.Auto, state, SuperJobs.TimePoint.First));

        //Order Insurance Score
        jobList.add(SuperJobs.aaaInsuranceScoreRenewalBatchOrderAsyncJob(SuperJobs.ProductType.Auto, state, SuperJobs.TimePoint.First));

        jobList.add(SuperJobs.aaaInsuranceScoreRenewalBatchReceiveAsyncJob(SuperJobs.ProductType.Auto, state,
                SuperJobs.TimePoint.First));

        //Order MVR/CLUE
        jobList.add(SuperJobs.aaaMvrRenewBatchOrderAsyncJob(state));
        jobList.add(SuperJobs.aaaMvrRenewAsyncBatchReceiveJob(state));

        jobList.add(SuperJobs.aaaClueRenewBatchOrderAsyncJob(SuperJobs.ProductType.Auto, state));
        jobList.add(SuperJobs.aaaClueRenewAsyncBatchReceiveJob(SuperJobs.ProductType.Auto, state));

        //Order Internal Claims
        jobList.addAll(SuperJobs.getRenewalClaimOrderAsyncJobs(state));

        //Membership Revalidation
        jobList.add(SuperJobs.aaaMembershipRenewalBatchOrderAsyncJob(SuperJobs.ProductType.Auto, state, SuperJobs.TimePoint.Second));

        //Renewal Image Available to all users
        jobList.add(SuperJobs.renewalImageRatingAsyncTaskJob(state, SuperJobs.TimePoint.First));

        //Premium Calculate
        jobList.add(SuperJobs.renewalImageRatingAsyncTaskJob(state, SuperJobs.TimePoint.Second));

        //Non-Renewal Notice
        jobList.add(SuperJobs.policyDoNotRenewAsyncJob(SuperJobs.ProductType.Auto, state));

        //Propose/Renewal Offer
        jobList.add(SuperJobs.renewalOfferAsyncTaskJob(SuperJobs.ProductType.Auto, state));
        jobList.add(SuperJobs.aaaPreRenewalNoticeAsyncJob(state));

        //Renewal Bill
        jobList.add(SuperJobs.aaaRenewalNoticeBillAsyncJob(SuperJobs.ProductType.Auto, state));

        //Renewal Reminder
        jobList.add(SuperJobs.preRenewalReminderGenerationAsyncJob(SuperJobs.ProductType.Auto, state));

        //R+1 Update Status
        jobList.add(SuperJobs.policyStatusUpdateJob(SuperJob.JobOffsetType.Add_Days, 1));

        return jobList;
    }


    /**
     * Gets a list of all required jobs to perform an property(home) renewal for both CA and SS.
     *
     * @param baseTest     Needed so app can open to make a payment. Pass this keyword from calling test.
     * @param policyNumber Also needed during making a payment through the app.
     * @param policyTerm   What length of policy is.
     * @param paymentPlan  What type of payment plan was created.
     * @param makePayment  Determines whether to make payment for this renewal. Useful for testing non-payment scenarios.
     * @return ArrayList of Jobs that can be used to build a schedule for Auto Renewals
     */
    public static ArrayList<SuperJob> getHomeRenewalJobList(BaseTest baseTest, String policyNumber, SuperJobs.PolicyTerm policyTerm,
                                                            SuperJobs.PaymentPlan paymentPlan, boolean makePayment) {

        String state = BaseTest.getState();

        // Schedule payment jobs first so they always run first
        ArrayList<SuperJob> jobList = getPaymentJobList(baseTest,policyNumber, policyTerm, paymentPlan, makePayment);

        // [Initiate renewal]
        jobList.add(SuperJobs.policyAutomatedRenewalAsyncTaskGenerationJob(SuperJobs.ProductType.Home, state));

        // [Order Reports & Prefill]
            // Batch Orders

        jobList.add(SuperJobs.isoRenewalBatchOrderJob(state));

        jobList.add(SuperJobs.aaaMembershipRenewalBatchOrderAsyncJob(SuperJobs.ProductType.Home, state, SuperJobs.TimePoint.First));

        //SuperJob aaaInsuranceScoreRenewalBatchOrderAsyncJob =
        jobList.add(SuperJobs.aaaInsuranceScoreRenewalBatchOrderAsyncJob(SuperJobs.ProductType.Home, state, SuperJobs.TimePoint.NotApplicable));

        jobList.add(SuperJobs.aaaClueRenewBatchOrderAsyncJob(SuperJobs.ProductType.Home, state));

            // Batch Receives

        jobList.add(SuperJobs.aaaIsoRenewAsyncBatchReceiveJob(state));

        jobList.add(SuperJobs.aaaInsuranceScoreRenewalBatchReceiveAsyncJob(SuperJobs.ProductType.Home, state,
                SuperJobs.TimePoint.NotApplicable));

        jobList.add(SuperJobs.aaaClueRenewAsyncBatchReceiveJob(SuperJobs.ProductType.Home, state));

        jobList.add(SuperJobs.aaaPolicyAutomatedRenewalAsyncTaskGenerationJob(SuperJobs.ProductType.Home, state, SuperJobs.TimePoint.First));

        // [Send Credit Disclosure]
        jobList.add(SuperJobs.aaaCreditDisclosureNoticeJob(state));

        // [Run Rules]
        jobList.add(SuperJobs.renewalValidationAsyncTaskJob(state));
        jobList.add(SuperJobs.aaaRenewalDataRefreshAsyncJob(state));
        jobList.add(SuperJobs.updateRenewalTimelineIndicator(state, policyNumber));

        // [Membership Revalidation]
        jobList.add(SuperJobs.aaaMembershipRenewalBatchOrderAsyncJob(SuperJobs.ProductType.Home, state,
                SuperJobs.TimePoint.Second));

        // Premium Calculate
        jobList.add(SuperJobs.renewalImageRatingAsyncTaskJob(state));

        // [Propose]
        jobList.add(SuperJobs.renewalOfferAsyncTaskJob(SuperJobs.ProductType.Home, state));

        // [Do not renew alert]
        jobList.add(SuperJobs.policyDoNotRenewAsyncJob(SuperJobs.ProductType.Home, state));

        jobList.add(SuperJobs.aaaPolicyAutomatedRenewalAsyncTaskGenerationJob(SuperJobs.ProductType.Home, state,
                SuperJobs.TimePoint.Second));

        // [Renewal Bill]
        jobList.add(SuperJobs.aaaRenewalNoticeBillAsyncJob(SuperJobs.ProductType.Home, state));

        // [1st Renewal Reminder, if Mortgagee Bill (R-5) (CA) and 1st Renewal Reminder, if Mortgagee Bill (R+10) (SS)]
        // Disabling due to not needed except in special circumstances and causes time changes in non-CA to be set after renewal
        // jobList.add(SuperJobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob(state, SuperJobs.TimePoint.First));

        // [1st Renewal Reminder, if non- Mortgagee Bill (R-10)]
        jobList.add(SuperJobs.preRenewalReminderGenerationAsyncJob(SuperJobs.ProductType.Home, state));

        // [1st Renewal Reminder, if Mortgagee Bill (R+10) (SS)]
        // Placeholder where SS would actually be if not scheduled above.

        // [2nd Renewal Reminder, if Mortgagee Bill (R+40)]
        //jobList.add(SuperJobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob(state, SuperJobs.TimePoint.Second));
        // aaaMortgageeRenewalReminderAndExpNoticeAsyncJob

        // [Policy Lapse  after R+30, if non-Mortgagee Bill]
        // policyLapsedRenewalProcessAsyncJob

        return jobList;
    }

    public static ArrayList<SuperJob> getPaymentJobList(BaseTest baseTest, String policyNumber, SuperJobs.PolicyTerm policyTerm,
                                                 SuperJobs.PaymentPlan paymentPlan, boolean makePayment){
        String state = BaseTest.getState();
        ArrayList<SuperJob> jobList = new ArrayList<>();

        // BONDTODO: This code needs review. I believe it's supposed to differentiate between monthly and lump some.
        if (paymentPlan == SuperJobs.PaymentPlan.Monthly) {
            LocalDateTime expirationDate = getPolicyExpirationLocalDateTimeFromSQL(policyNumber);
            jobList.addAll(SuperJobs.makeMonthlyPayments(baseTest, policyNumber, policyTerm, expirationDate, makePayment));
        }
        else if (paymentPlan == SuperJobs.PaymentPlan.Full && makePayment) {
            //Special Make Payment job
            jobList.add(SuperJobs.makeLumpSumPayment(state, baseTest, policyNumber));
        }

        return jobList;
    }
}
