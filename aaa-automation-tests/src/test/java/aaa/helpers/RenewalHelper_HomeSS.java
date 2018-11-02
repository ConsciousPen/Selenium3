package aaa.helpers;

import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;

import java.time.LocalDateTime;

public class RenewalHelper_HomeSS extends HomeSSHO3BaseTest
{
    String _policyNumber;
    LocalDateTime _policyExpiraitonDate;
    LocalDateTime _policyStage1Date;
    LocalDateTime _policyStage2Date;
    LocalDateTime _policyStage3Date;
    LocalDateTime _policyStage4Date;
    LocalDateTime _renewalImageGenDate;
    LocalDateTime _renewalBillGenDate;
    Long _timeDifferenceInDays_Stage3ToExpiration;
    Long _timeDifferenceInDays_Stage4ToExpiration;

    public RenewalHelper_HomeSS(LocalDateTime in_policyExpiraitonDate, Long timePoint1_AsRMinus, Long timePoint2_AsRMinus, boolean outputDataToLogs){
        _policyNumber = PolicySummaryPage.getPolicyNumber();
        _policyExpiraitonDate = in_policyExpiraitonDate;
        _timeDifferenceInDays_Stage3ToExpiration = timePoint1_AsRMinus;
        _timeDifferenceInDays_Stage4ToExpiration = timePoint2_AsRMinus;
        _policyStage3Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage3ToExpiration);
        _policyStage4Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage4ToExpiration);
        _renewalImageGenDate = getTimePoints().getRenewImageGenerationDate(_policyExpiraitonDate);
        _renewalBillGenDate = getTimePoints().getBillGenerationDate(_policyExpiraitonDate);
        _policyStage1Date = PolicySummaryPage.getEffectiveDate().plusDays(15l);
        _policyStage2Date = PolicySummaryPage.getEffectiveDate().plusDays(30l);

        if (outputDataToLogs){
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: Policy Expiration Date = '%s' </QA-LOG-DEBUG>", _policyExpiraitonDate.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: NB15(Stage1) = '%s' </QA-LOG-DEBUG>", _policyStage1Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: NB30(Stage2) = '%s' </QA-LOG-DEBUG>", _policyStage2Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: Membership Timepoint 1 (Stage3) = '%s' </QA-LOG-DEBUG>", _policyStage3Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: Membership Timepoint 2 (Stage4) = '%s' </QA-LOG-DEBUG>", _policyStage4Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: Renewal Image Generation Date = '%s' </QA-LOG-DEBUG>", _renewalImageGenDate.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: Renewal Bill Generation Date = '%s' </QA-LOG-DEBUG>", _renewalBillGenDate.toString()));
        }
    }

    public void moveToGivenTerm(Integer desiredTerm){
        Integer currentTerm = 0; //A value of zero represents New Business
        while(currentTerm < desiredTerm){
            moveThroughStage1();
            moveThroughStage2();
            moveThroughStage3();
            moveThroughStage4();
            handleRenewBillPay();
            moveToNewTermAndSetNewTimepoints();

            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: Renewal Term '%s' completed. </QA-LOG-DEBUG>", currentTerm));
            currentTerm++;
            if(currentTerm > 10) // <-- SANITY CHECK. LIMITS RENEWALS TO 10.
                break;
        }
    }

    public void moveThroughStage1() {
        if(_policyExpiraitonDate != null) {
            moveToNB15();
        }
        else {
            log.error(String.format(System.lineSeparator() + "<QA-LOG-ERROR> RenewalHelper: ERROR - No Expiration Date Set By Constructor! </QA-LOG-ERROR>" + System.lineSeparator(), _policyExpiraitonDate.toString()));
        }
    }

    public void moveThroughStage2() {
        if(_policyExpiraitonDate != null) {
            moveToNB30();
        }
        else {
            log.error(String.format(System.lineSeparator() + "<QA-LOG-ERROR> RenewalHelper: ERROR - No Expiration Date Set By Constructor! </QA-LOG-ERROR>" + System.lineSeparator(), _policyExpiraitonDate.toString()));
        }
    }

    public void moveThroughStage3() {
        if(_policyExpiraitonDate != null) {
            generateRenewalImage();
            moveToMembershipTimepoint1();
        }
        else {
            log.error(String.format(System.lineSeparator() + "<QA-LOG-ERROR> RenewalHelper: ERROR - No Expiration Date Set By Constructor! </QA-LOG-ERROR>" + System.lineSeparator(), _policyExpiraitonDate.toString()));
        }
    }

    public void moveThroughStage4() {
        if(_policyExpiraitonDate != null) {
            moveToMembershipTimepoint2();
        }
        else {
            log.error(String.format(System.lineSeparator() + "<QA-LOG-ERROR> RenewalHelper: ERROR - No Expiration Date Set By Constructor! </QA-LOG-ERROR>" + System.lineSeparator(), _policyExpiraitonDate.toString()));
        }
    }

    public void handleRenewBillPay() {
        TimeSetterUtil.getInstance().nextPhase(_renewalBillGenDate);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
        JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);


        mainApp().open();
        //DEBUGBREAKPOINT MANUALLY MAKE PAYMENT IN UI.
        SearchPage.openBilling(_policyNumber);
        new BillingAccount().generateFutureStatement().perform();
        TestData check_payment = testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Check");
        new BillingAccount().acceptPayment().perform(check_payment, new Dollar(200));

        mainApp().close();
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob); //POLICY SHOULD BE RENEWED NOW.
    }

    public void moveToNewTermAndSetNewTimepoints() {
        TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.plusDays(1l));

        mainApp().open();
        SearchPage.openPolicy(_policyNumber);
        captureTimepoints();
        mainApp().close();
    }


    private void moveToNB15() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage1Date);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.membershipValidationJob);
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to NB+15, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
    }

    private void moveToNB30() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage2Date);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.membershipValidationJob);
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to NB+30, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
    }

    private void generateRenewalImage() {
        TimeSetterUtil.getInstance().nextPhase(_renewalImageGenDate);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: Renewal Image Generated on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
    }

    /**
     * Moves to STG3 and handles STG3 Jobs. (E.g.= AZ HomeSS -> R-63, R-60, R-57 jobs.
     */
    private void moveToMembershipTimepoint1() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage3Date);
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to TimePoint1, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

        TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(57));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaCreditDisclosureNoticeJob);
        JobUtils.executeJob(Jobs.renewalValidationAsyncTaskJob); // Add to UI
        JobUtils.executeJob(Jobs.aaaRenewalDataRefreshAsyncJob);
    }

    private void moveToMembershipTimepoint2() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage4Date);
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to TimePoint2, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);

        TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(45l));
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to STG4 Rating, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);

        TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(35l));
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to STG4 Proposal, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalOfferAsyncTaskJob);

        TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(34l));
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to STG4 Proposal, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyDoNotRenewAsyncJob);
        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);
    }

    private void captureTimepoints(){
        _policyNumber = PolicySummaryPage.getPolicyNumber();
        _policyExpiraitonDate = PolicySummaryPage.getExpirationDate();
        _policyStage3Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage3ToExpiration);
        _policyStage4Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage4ToExpiration);
        _renewalImageGenDate = getTimePoints().getRenewImageGenerationDate(_policyExpiraitonDate);
        _renewalBillGenDate = getTimePoints().getBillGenerationDate(_policyExpiraitonDate);
        _policyStage1Date = PolicySummaryPage.getEffectiveDate().plusDays(15l);
        _policyStage2Date = PolicySummaryPage.getEffectiveDate().plusDays(30l);
    }
}
