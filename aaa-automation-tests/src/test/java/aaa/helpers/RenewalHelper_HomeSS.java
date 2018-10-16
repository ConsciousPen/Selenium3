package aaa.helpers;

import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import java.time.LocalDateTime;

public class RenewalHelper_HomeSS extends HomeSSHO3BaseTest
{
    LocalDateTime _policyExpiraitonDate;
    LocalDateTime _policyStage1Date;
    LocalDateTime _policyStage2Date;
    LocalDateTime _policyStage3Date;
    LocalDateTime _policyStage4Date;
    LocalDateTime _renewalImageGenDate;
    Long _timeDifferenceInDays_Stage3ToExpiration;
    Long _timeDifferenceInDays_Stage4ToExpiration;

    public RenewalHelper_HomeSS(LocalDateTime in_policyExpiraitonDate, Long timePoint1_AsRMinus, Long timePoint2_AsRMinus, boolean outputDataToLogs){
        _policyExpiraitonDate = in_policyExpiraitonDate;
        _timeDifferenceInDays_Stage3ToExpiration = timePoint1_AsRMinus;
        _timeDifferenceInDays_Stage4ToExpiration = timePoint2_AsRMinus;
        _policyStage3Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage3ToExpiration);
        _policyStage4Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage4ToExpiration);
        _renewalImageGenDate = getTimePoints().getRenewImageGenerationDate(_policyExpiraitonDate);
        _policyStage1Date = PolicySummaryPage.getEffectiveDate().plusDays(15l);
        _policyStage2Date = PolicySummaryPage.getEffectiveDate().plusDays(30l);

        if (outputDataToLogs){
            log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: Policy Expiration Date = '%s' </QA-LOG-DEBUG>", _policyExpiraitonDate.toString()));
            log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: NB15(Stage1) = '%s' </QA-LOG-DEBUG>", _policyStage1Date.toString()));
            log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: NB30(Stage2) = '%s' </QA-LOG-DEBUG>", _policyStage2Date.toString()));
            log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: Membership Timepoint 1 (Stage3) = '%s' </QA-LOG-DEBUG>", _policyStage3Date.toString()));
            log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: Membership Timepoint 2 (Stage4) = '%s' </QA-LOG-DEBUG>", _policyStage4Date.toString()));
            log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: Renewal Image Generation Date = '%s' </QA-LOG-DEBUG>", _renewalImageGenDate.toString()));
        }
    }

    public void moveToGivenTerm(Integer desiredTerm){
        Integer currentTerm = 0; //A value of zero represents New Business
        while(currentTerm < desiredTerm){
            moveThroughStage1();
            moveThroughStage2();
            moveThroughStage3();
            moveThroughStage4();

            log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: Renewal Term '%1' completed. Moving to Renewal Term '%2' </QA-LOG-DEBUG>", currentTerm, currentTerm+1));
            currentTerm++;
            if(currentTerm > 10) // <-- SANITY CHECK
                break;
        }
    }

    public void moveThroughStage1() {
        if(_policyExpiraitonDate != null) {
            moveToNB15();
        }
        else {
            log.error(String.format("<QA-LOG-DEBUG> RenewalHelper: ERROR - No Expiration Date Set By Constructor! </QA-LOG-DEBUG>", _policyExpiraitonDate.toString()));
        }
    }

    public void moveThroughStage2() {
        if(_policyExpiraitonDate != null) {
            moveToNB30();
        }
        else {
            log.error(String.format("<QA-LOG-DEBUG> RenewalHelper: ERROR - No Expiration Date Set By Constructor! </QA-LOG-DEBUG>", _policyExpiraitonDate.toString()));
        }
    }

    public void moveThroughStage3() {
        if(_policyExpiraitonDate != null) {
            generateRenewalImage();
            moveToMembershipTimepoint1();
        }
        else {
            log.error(String.format("<QA-LOG-DEBUG> RenewalHelper: ERROR - No Expiration Date Set By Constructor! </QA-LOG-DEBUG>", _policyExpiraitonDate.toString()));
        }
    }

    public void moveThroughStage4() {
        if(_policyExpiraitonDate != null) {
            generateRenewalImage();
            moveToMembershipTimepoint2();
        }
        else {
            log.error(String.format("<QA-LOG-DEBUG> RenewalHelper: ERROR - No Expiration Date Set By Constructor! </QA-LOG-DEBUG>", _policyExpiraitonDate.toString()));
        }
    }

    private void moveToNB15() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage1Date);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: JVM moved to NB+15, on '%s' </QA-LOG-DEBUG>", TimeSetterUtil.getInstance().getCurrentTime().toString()));
    }

    private void moveToNB30() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage2Date);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: JVM moved to NB+30, on '%s' </QA-LOG-DEBUG>", TimeSetterUtil.getInstance().getCurrentTime().toString()));
    }

    private void generateRenewalImage() {
        TimeSetterUtil.getInstance().nextPhase(_renewalImageGenDate);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);
        log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: Renewal Image Generated on '%s' </QA-LOG-DEBUG>", TimeSetterUtil.getInstance().getCurrentTime().toString()));
    }

    private void moveToMembershipTimepoint1() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage3Date);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: JVM moved to TimePoint1, on '%s' </QA-LOG-DEBUG>", TimeSetterUtil.getInstance().getCurrentTime().toString()));
    }

    private void moveToMembershipTimepoint2() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage4Date);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        log.debug(String.format("<QA-LOG-DEBUG> RenewalHelper: JVM moved to TimePoint2, on '%s' </QA-LOG-DEBUG>", TimeSetterUtil.getInstance().getCurrentTime().toString()));
    }
}
