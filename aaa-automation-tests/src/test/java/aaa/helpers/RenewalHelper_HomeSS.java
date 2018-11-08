package aaa.helpers;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssertions;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Tyrone Jemison
 * TODO: Add State Variants to Each Timepoint Job Collection, to better account for State Deltas and make class more complete.
 * TODO: Add query to verify 'aaaRenewaltimelineIndicator' >= 2 before attempting renewalImageratingAsynctaskjob.
 */
public class RenewalHelper_HomeSS extends HomeSSHO3BaseTest
{
    String _policyNumber;
    LocalDateTime _policyExpiraitonDate;
    LocalDateTime _policyEffectiveDate;
    LocalDateTime _policyStage1Date;
    LocalDateTime _policyStage2Date;
    LocalDateTime _policyStage3Date;
    LocalDateTime _policyStage4Date;
    LocalDateTime _renewalImageGenDate;
    LocalDateTime _renewalBillGenDate;
    Long _timeDifferenceInDays_Stage3ToExpiration;
    Long _timeDifferenceInDays_Stage4ToExpiration;
    final String AAARENEWALTIMELINEIND_VALUETOSET = "3";

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
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper Constructor: Policy Expiration Date = '%s' </QA-LOG-DEBUG>", _policyExpiraitonDate.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper Constructor: NB15(Stage1) = '%s' </QA-LOG-DEBUG>", _policyStage1Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper Constructor: NB30(Stage2) = '%s' </QA-LOG-DEBUG>", _policyStage2Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper Constructor: Membership Timepoint 1 (Stage3) = '%s' </QA-LOG-DEBUG>", _policyStage3Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper Constructor: Membership Timepoint 2 (Stage4) = '%s' </QA-LOG-DEBUG>", _policyStage4Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper Constructor: Renewal Image Generation Date = '%s' </QA-LOG-DEBUG>", _renewalImageGenDate.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper Constructor: Renewal Bill Generation Date = '%s' </QA-LOG-DEBUG>", _renewalBillGenDate.toString()));
        }
    }

    /**
     * This method houses the primary loop logic for traversing through multiple policies. <br>
     * This loop was given a sanity limit of 10 renewals, expecting never to test beyond that threshold without manual edits.
     * @param desiredTerm Quote/Initial Policy = 0. First Renewal = 1;
     */
    public void moveToGivenTerm(Integer desiredTerm){
        Integer currentTerm = 0; //A value of zero represents New Business
        while(currentTerm < desiredTerm){
            moveThroughStage1();
            moveThroughStage2();
            moveThroughStage3();
            moveThroughStage4();
            handleRenewBillPay();
            moveToNewTermAndSetNewTimepoints();

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
            // R-73
            generateRenewalImage();
            // R-63
            moveToMembershipTimepoint1();
            // R-60
            // R-57
        }
        else {
            log.error(String.format(System.lineSeparator() + "<QA-LOG-ERROR> RenewalHelper: ERROR - No Expiration Date Set By Constructor! </QA-LOG-ERROR>" + System.lineSeparator(), _policyExpiraitonDate.toString()));
        }
    }

    public void moveThroughStage4() {
        if(_policyExpiraitonDate != null) {
            // R-48
            moveToMembershipTimepoint2();
            // R-45
            // R-36
            // R-35
            // R-20
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
        new BillingAccount().acceptPayment().start();
        new AcceptPaymentActionTab().setCheckNumber(123);
        Tab.buttonOk.click();
        mainApp().close();

        // Move forward one day. Update status. Move forward another day.
        TimeSetterUtil.getInstance().nextPhase(_renewalBillGenDate.plusDays(2));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob); //POLICY SHOULD BE RENEWED NOW.

        // Manually ZERO OUT aaaTimelineRenewalInd
        AAAMembershipQueries.updateAaaRenewalTimelineIndicatorValue(_policyNumber, "0");
    }

    public void moveToNewTermAndSetNewTimepoints() {
        TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.plusDays(10));

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
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);

        // R-57
        TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(57l));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalValidationAsyncTaskJob);
        JobUtils.executeJob(Jobs.aaaRenewalDataRefreshAsyncJob);

        // Manually set aaaRenewalTimelineInd
        AAAMembershipQueries.updateAaaRenewalTimelineIndicatorValue(_policyNumber, AAARENEWALTIMELINEIND_VALUETOSET);

        String results = AAAMembershipQueries.getAaaRenewalTimelineIndicatorValue(_policyNumber);
        CustomAssertions.assertThat(results.toString()).isEqualToIgnoringCase(AAARENEWALTIMELINEIND_VALUETOSET);
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
    }

    private void captureTimepoints(){
        _policyNumber = PolicySummaryPage.getPolicyNumber();
        String _policyEffectiveDateAsString = AAAMembershipQueries.getPolicyEffectiveDateFromSQL(_policyNumber).orElse("NULL RESPONSE!");
        _policyEffectiveDate = LocalDateTime.parse(_policyEffectiveDateAsString, AAAMembershipQueries.SQLDateTimeFormatter);
        _policyStage1Date = _policyEffectiveDate.plusDays(15l);
        _policyStage2Date = _policyEffectiveDate.plusDays(30l);

        String _policyExpiraitonDateAsString = AAAMembershipQueries.getPolicyExpirationDateFromSQL(_policyNumber).orElse("NULL RESPONSE!");
        _policyExpiraitonDate = LocalDateTime.parse(_policyExpiraitonDateAsString, AAAMembershipQueries.SQLDateTimeFormatter);

        _policyStage3Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage3ToExpiration);
        _policyStage4Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage4ToExpiration);

        _renewalImageGenDate = getTimePoints().getRenewImageGenerationDate(_policyExpiraitonDate);
        _renewalBillGenDate = getTimePoints().getBillGenerationDate(_policyExpiraitonDate);

    }
}
