package aaa.helpers.renewal;

import aaa.helpers.jobs.*;
import aaa.modules.BaseTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static aaa.helpers.db.queries.AAAMembershipQueries.getPolicyEffectivLocalDateTimeFromSQL;
import static aaa.helpers.db.queries.AAAMembershipQueries.getPolicyExpirationLocalDateTimeFromSQL;

public class SchedulableJobRenewalHelper {

    // Set to true for fast debugging (Output only). Can't date future renewals due to expiration date not changing in DB.
    private static boolean simulateOnly = false;

    /**
     * Performs all jobs that production does including 15/30 day jobs for requested number of renewals.
     * @param baseTest Needed for any jobs that have to run selenium based automation (payment job).
     * @param policyNumber Policy to schedule against.
     * @param paymentPlan Monthly or Full
     * @param makeFinalRenewalPayment If false, will not make last payment.
     * @param numberOfRenewals How many renewals to perform.
     * @return Output Summary of what was scheduled when.
     */
    public static ArrayList<String> performFullAutoRenewal(BaseTest baseTest, String policyNumber,
                                                           SchedulableJobs.PaymentPlan paymentPlan,
                                                           boolean makeFinalRenewalPayment, int numberOfRenewals){

        JobSchedule newBusiness15_30Schedule = getNewBusinessPlus15_30Schedule(policyNumber);

        ArrayList<String> output = SchedulableJob.executeJobSchedule(newBusiness15_30Schedule, simulateOnly);

        output.addAll(performRenewals(SchedulableJobs.ProductType.Auto, baseTest, policyNumber, paymentPlan,
                makeFinalRenewalPayment, numberOfRenewals));

        return output;
    }

    /**
     * Performs all jobs that production does including 15/30 day jobs for requested number of renewals.
     * @param baseTest Needed for any jobs that have to run selenium based automation (payment job).
     * @param policyNumber Policy to schedule against.
     * @param paymentPlan Monthly or Full
     * @param makeFinalRenewalPayment If false, will not make last payment.
     * @param numberOfRenewals How many renewals to perform.
     * @return Output Summary of what was scheduled when.
     */
    public static ArrayList<String> performFullHomeRenewal(BaseTest baseTest, String policyNumber,
                                                           SchedulableJobs.PaymentPlan paymentPlan,
                                                           boolean makeFinalRenewalPayment, int numberOfRenewals){

        JobSchedule newBusiness15_30Schedule = getNewBusinessPlus15_30Schedule(policyNumber);

        ArrayList<String> output = SchedulableJob.executeJobSchedule(newBusiness15_30Schedule, simulateOnly);

        output.addAll(performRenewals(SchedulableJobs.ProductType.Home, baseTest, policyNumber, paymentPlan,
                makeFinalRenewalPayment, numberOfRenewals));

        return output;
    }

    /**
     * Performs the renewal loop with the full list for appropriate renewal jobs.
     * @param productType Product type to run against
     * @param baseTest Needed for any jobs that have to run selenium based automation (payment job).
     * @param policyNumber Policy to schedule against.
     * @param paymentPlan Monthly or Full
     * @param makeFinalRenewalPayment If false, will not make last payment.
     * @param numberOfRenewals How many renewals to perform.
     * @return Output Summary of what was scheduled when.
     */
    private static ArrayList<String> performRenewals(SchedulableJobs.ProductType productType, BaseTest baseTest, String policyNumber,
                                                     SchedulableJobs.PaymentPlan paymentPlan,
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

            if (productType == SchedulableJobs.ProductType.Auto) {
                jobSchedule = new JobSchedule(getAutoRenewalJobList(baseTest, policyNumber, paymentPlan, makeFinalPayment), expirationDate);
            }
            else {
                jobSchedule = new JobSchedule(getHomeRenewalJobList(baseTest, policyNumber, paymentPlan, makeFinalPayment), expirationDate);
            }

            output.addAll(SchedulableJob.executeJobSchedule(jobSchedule, simulateOnly));
        }

        return output;
    }

    /**
     * Returns a job schedule validating new business at 15 and 30 days after policy effective date. (NB+15/30)
     * @param policyNumber is used to get the effective date of the policy
     * @return a job schedule that can be executed with SchedulableJob.executeJobSchedule()
     */
    public static JobSchedule getNewBusinessPlus15_30Schedule(String policyNumber){
        return new JobSchedule(getNewBusinessPlus15_30JobList(), getPolicyEffectivLocalDateTimeFromSQL(policyNumber));
    }

    /**
     * Gets a list of all jobs for validating new business at 15 and 30 days after policy effective date. (NB+15/30)
     * @return ArrayList of Jobs that can be used to build a schedule for any new policy.
     */
    public static ArrayList<SchedulableJob> getNewBusinessPlus15_30JobList(){

        ArrayList<SchedulableJob> jobList = new ArrayList<>();

        // Add all New Business (NB) jobs at TimePoint 1 & 2. TP1 = NB+15 TP2 = NB+30
        jobList.addAll(newBusinessSTG1STG2Jobs(SchedulableJobs.TimePoint.First));
        jobList.addAll(newBusinessSTG1STG2Jobs(SchedulableJobs.TimePoint.Second));

        return jobList;
    }

    /**
     * All the jobs needed at New Business + 15 and New Business + 30.
     * @param timePoint is what Time Point to use. TimePoint.First = NB+15. TimePoint.Second = NB+30
     * @return ArrayList of Jobs for one of the timepoints.
     */
    private static ArrayList<SchedulableJob> newBusinessSTG1STG2Jobs(SchedulableJobs.TimePoint timePoint) {
        ArrayList<SchedulableJob> jobList = new ArrayList<>();
        jobList.add(SchedulableJobs.aaaBatchMarkerJob(timePoint));
        jobList.add(SchedulableJobs.aaaAutomatedProcessingInitiationJob(timePoint));
        jobList.add(SchedulableJobs.automatedProcessingRatingJob(timePoint));
        jobList.add(SchedulableJobs.automatedProcessingRunReportsServicesJob(timePoint));
        jobList.add(SchedulableJobs.automatedProcessingIssuingOrProposingJob(timePoint));
        jobList.add(SchedulableJobs.automatedProcessingStrategyStatusUpdateJob(timePoint));

        return jobList;
    }

    /**
     * Gets a list of all required jobs to perform an auto renewal for both CA and SS.
     *
     * @param baseTest     Needed so app can open to make a payment. Pass this keyword from calling test.
     * @param policyNumber Also needed during making a payment through the app.
     * @param paymentPlan  What type of payment plan was created.
     * @param makePayment  Determines whether to make payment for this renewal. Useful for testing non-payment scenarios.
     * @return ArrayList of Jobs that can be used to build a schedule for Auto Renewals
     */
    public static ArrayList<SchedulableJob> getAutoRenewalJobList(
            BaseTest baseTest, String policyNumber, SchedulableJobs.PaymentPlan paymentPlan, boolean makePayment) {

        // Payment schedule used can be located in wiki:
        // https://csaaig.atlassian.net/wiki/spaces/TC/pages/848855097/Renewal+Timelines+and+Batch+Jobs+-+Property+and+Auto

        String state = BaseTest.getState();
        SchedulableJobs.PolicyTerm policyTerm = SchedulableJobs.GetPolicyTerm(policyNumber);

        // Schedule payment jobs first so they always run first
        ArrayList<SchedulableJob> jobList = getPaymentJobList(baseTest,policyNumber, paymentPlan, makePayment);

        //Initiate Renewal
        jobList.add(SchedulableJobs.policyAutomatedRenewalAsyncTaskGenerationJob(SchedulableJobs.ProductType.Auto, state));

        //UBI SafetyScore
        jobList.addAll(SchedulableJobs.getTelematicSafetyScoreJobs(state, policyTerm)); //GDN

        //Order Membership
        jobList.add(SchedulableJobs.aaaMembershipRenewalBatchOrderAsyncJob(SchedulableJobs.ProductType.Auto, state, SchedulableJobs.TimePoint.First));

        //Order Insurance Score
        jobList.add(SchedulableJobs.aaaInsuranceScoreRenewalBatchOrderAsyncJob(SchedulableJobs.ProductType.Auto, state, SchedulableJobs.TimePoint.First));

        jobList.add(SchedulableJobs.aaaInsuranceScoreRenewalBatchReceiveAsyncJob(SchedulableJobs.ProductType.Auto, state,
                SchedulableJobs.TimePoint.First));

        //Order MVR/CLUE
        jobList.add(SchedulableJobs.aaaMvrRenewBatchOrderAsyncJob(state));
        jobList.add(SchedulableJobs.aaaMvrRenewAsyncBatchReceiveJob(state));

        jobList.add(SchedulableJobs.aaaClueRenewBatchOrderAsyncJob(SchedulableJobs.ProductType.Auto, state));
        jobList.add(SchedulableJobs.aaaClueRenewAsyncBatchReceiveJob(SchedulableJobs.ProductType.Auto, state));

        //Order Internal Claims
        jobList.addAll(SchedulableJobs.getRenewalClaimOrderAsyncJobs(state));

        //Membership Revalidation
        jobList.add(SchedulableJobs.aaaMembershipRenewalBatchOrderAsyncJob(SchedulableJobs.ProductType.Auto, state, SchedulableJobs.TimePoint.Second));

        //Renewal Image Available to all users
        jobList.add(SchedulableJobs.renewalImageRatingAsyncTaskJob(state, SchedulableJobs.TimePoint.First));

        //Premium Calculate
        jobList.add(SchedulableJobs.renewalImageRatingAsyncTaskJob(state, SchedulableJobs.TimePoint.Second));

        //Non-Renewal Notice
        jobList.add(SchedulableJobs.policyDoNotRenewAsyncJob(SchedulableJobs.ProductType.Auto, state));

        //Propose/Renewal Offer
        jobList.add(SchedulableJobs.renewalOfferAsyncTaskJob(SchedulableJobs.ProductType.Auto, state));
        jobList.add(SchedulableJobs.aaaPreRenewalNoticeAsyncJob(state));

        //Renewal Bill
        jobList.add(SchedulableJobs.aaaRenewalNoticeBillAsyncJob(SchedulableJobs.ProductType.Auto, state));

        //Renewal Reminder
        jobList.add(SchedulableJobs.preRenewalReminderGenerationAsyncJob(SchedulableJobs.ProductType.Auto, state));

        //R+1 Update Status
        jobList.add(SchedulableJobs.policyStatusUpdateJob(SchedulableJob.JobOffsetType.Add_Days, 1));
        return jobList;
    }


    /**
     * Gets a list of all required jobs to perform an property(home) renewal for both CA and SS.
     *
     * @param baseTest     Needed so app can open to make a payment. Pass this keyword from calling test.
     * @param policyNumber Also needed during making a payment through the app.
     * @param paymentPlan  What type of payment plan was created.
     * @param makePayment  Determines whether to make payment for this renewal. Useful for testing non-payment scenarios.
     * @return ArrayList of Jobs that can be used to build a schedule for Auto Renewals
     */
    public static ArrayList<SchedulableJob> getHomeRenewalJobList(BaseTest baseTest, String policyNumber,
                                                                  SchedulableJobs.PaymentPlan paymentPlan, boolean makePayment) {

        // Payment schedule used can be located in wiki:
        // https://csaaig.atlassian.net/wiki/spaces/TC/pages/848855097/Renewal+Timelines+and+Batch+Jobs+-+Property+and+Auto

        String state = BaseTest.getState();

        // Schedule payment jobs first so they always run first
        ArrayList<SchedulableJob> jobList = getPaymentJobList(baseTest,policyNumber, paymentPlan, makePayment);

        // [Initiate renewal]
        jobList.add(SchedulableJobs.policyAutomatedRenewalAsyncTaskGenerationJob(SchedulableJobs.ProductType.Home, state));

        // [Order Reports & Prefill]
            // Batch Orders

        jobList.add(SchedulableJobs.isoRenewalBatchOrderJob(state));

        jobList.add(SchedulableJobs.aaaMembershipRenewalBatchOrderAsyncJob(SchedulableJobs.ProductType.Home, state, SchedulableJobs.TimePoint.First));

        jobList.add(SchedulableJobs.aaaInsuranceScoreRenewalBatchOrderAsyncJob(SchedulableJobs.ProductType.Home, state, SchedulableJobs.TimePoint.NotApplicable));

        jobList.add(SchedulableJobs.aaaClueRenewBatchOrderAsyncJob(SchedulableJobs.ProductType.Home, state));

            // Batch Receives

        jobList.add(SchedulableJobs.aaaIsoRenewAsyncBatchReceiveJob(state));

        jobList.add(SchedulableJobs.aaaInsuranceScoreRenewalBatchReceiveAsyncJob(SchedulableJobs.ProductType.Home, state,
                SchedulableJobs.TimePoint.NotApplicable));

        jobList.add(SchedulableJobs.aaaClueRenewAsyncBatchReceiveJob(SchedulableJobs.ProductType.Home, state));

        jobList.add(SchedulableJobs.aaaPolicyAutomatedRenewalAsyncTaskGenerationJob(SchedulableJobs.ProductType.Home, state, SchedulableJobs.TimePoint.First));

        // [Send Credit Disclosure]
        jobList.add(SchedulableJobs.aaaCreditDisclosureNoticeJob(state));

        // [Run Rules]
        jobList.add(SchedulableJobs.renewalValidationAsyncTaskJob(state));
        jobList.add(SchedulableJobs.aaaRenewalDataRefreshAsyncJob(state));

        // This job is a hack due to mocked responses missing some of the information in Stub.
        // Another workaround would be to open the renewal image in UI, rate and propose it. Does not affect Auto.
        //jobList.add(SchedulableJobs.updateRenewalTimelineIndicator(state, policyNumber));

        // [Membership Revalidation]
        jobList.add(SchedulableJobs.aaaMembershipRenewalBatchOrderAsyncJob(SchedulableJobs.ProductType.Home, state,
                SchedulableJobs.TimePoint.Second));

        // Premium Calculate
        jobList.add(SchedulableJobs.renewalImageRatingAsyncTaskJob(state));

        // [Propose]
        jobList.add(SchedulableJobs.renewalOfferAsyncTaskJob(SchedulableJobs.ProductType.Home, state));


        // [Do not renew alert]
        jobList.add(SchedulableJobs.policyDoNotRenewAsyncJob(SchedulableJobs.ProductType.Home, state));

        jobList.add(SchedulableJobs.aaaPolicyAutomatedRenewalAsyncTaskGenerationJob(SchedulableJobs.ProductType.Home, state,
                SchedulableJobs.TimePoint.Second));

        // [Renewal Bill]
        jobList.add(SchedulableJobs.aaaRenewalNoticeBillAsyncJob(SchedulableJobs.ProductType.Home, state));

        // [1st Renewal Reminder, if Mortgagee Bill (R-5) (CA) and 1st Renewal Reminder, if Mortgagee Bill (R+10) (SS)]
        // Disabling due to not needed except in special circumstances and causes time changes in non-CA to be set after renewal
        // jobList.add(SchedulableJobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob(state, SchedulableJobs.TimePoint.First));

        // [1st Renewal Reminder, if non- Mortgagee Bill (R-10)]
        jobList.add(SchedulableJobs.preRenewalReminderGenerationAsyncJob(SchedulableJobs.ProductType.Home, state));

        // [1st Renewal Reminder, if Mortgagee Bill (R+10) (SS)]
        // Placeholder where SS would actually be if not scheduled above.

        // [2nd Renewal Reminder, if Mortgagee Bill (R+40)]
        //jobList.add(SchedulableJobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob(state, SchedulableJobs.TimePoint.Second));
        // aaaMortgageeRenewalReminderAndExpNoticeAsyncJob

        // [Policy Lapse  after R+30, if non-Mortgagee Bill]
        // policyLapsedRenewalProcessAsyncJob

        return jobList;
    }

    /**
     * Gets a job list that will perform payments.
     * @param baseTest     Needed so app can open to make a payment. Pass this keyword from calling test.
     * @param policyNumber Also needed during making a payment through the app.
     * @param paymentPlan  What type of payment plan was created.
     * @param makePayment  Determines whether to make final payment for this renewal. Useful for testing non-payment scenarios.
     * @return ArrayList of Jobs that can be used to build a schedule for Auto Renewals
     */
    public static ArrayList<SchedulableJob> getPaymentJobList(BaseTest baseTest, String policyNumber,
                                                              SchedulableJobs.PaymentPlan paymentPlan, boolean makePayment){
        String state = BaseTest.getState();
        SchedulableJobs.PolicyTerm policyTerm = SchedulableJobs.GetPolicyTerm(policyNumber);
        ArrayList<SchedulableJob> jobList = new ArrayList<>();

        if (paymentPlan == SchedulableJobs.PaymentPlan.Monthly) {
            LocalDateTime expirationDate = getPolicyExpirationLocalDateTimeFromSQL(policyNumber);
            jobList.addAll(SchedulableJobs.makeMonthlyPayments(baseTest, policyNumber, policyTerm, expirationDate, makePayment));
        }
        else if (paymentPlan == SchedulableJobs.PaymentPlan.Full && makePayment) {
            //Special Make Payment job
            jobList.add(SchedulableJobs.makeLumpSumPayment(state, baseTest, policyNumber));
        }

        return jobList;
    }
}
