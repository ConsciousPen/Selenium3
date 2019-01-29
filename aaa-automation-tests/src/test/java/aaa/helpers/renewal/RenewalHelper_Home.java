package aaa.helpers.renewal;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.verification.CustomAssertions;

import java.time.LocalDateTime;

/**
 * This class is used to simplify renewal testing. <br>
 * Create a new class object while viewing the Policy Summary Page of a bound policy.
 * @author Tyrone Jemison
 * It should be okay for this class to extend from HO3BaseTest, as the delta should be the state and not HomeSS vs HomeCA.
 * Policy creation happens outside of this class, so getTimepoints() should return similar data as HO4, DP3, etc.
 * This means the class should workd for other Home product types by alternating between HomeCaHO3BaseTest and HomeSSHO3BaseTest
 */
public class RenewalHelper_Home extends HomeSSHO3BaseTest
{
    // Class-level variables to hold data between various methods/terms
    String _policyNumber;
    String _policyState;
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
    boolean _bPrintDebugInfo = false;
    final String AAARENEWALTIMELINEIND_VALUETOSET = "3";


    /**
     * This constructor is used to simplify renewal testing and to facilitate testing into future terms. <br>
     * Create a new RenewalHelper type object upon creation of a new policy, while on/viewing the Policy Summary Page.
     * @param state The state the policy was created for/in.
     * @param outputDataToLogs Print debug statements to log.debug?
     */
    public RenewalHelper_Home(String state, boolean outputDataToLogs){
        //Upon construction initialize and calculate all timepoints for moving through term.
        _policyNumber = PolicySummaryPage.getPolicyNumber();
        String _policyExpiraitonDateAsString = AAAMembershipQueries.getPolicyExpirationDateFromSQL(_policyNumber).orElse("NULL RESPONSE!");
        _policyExpiraitonDate = LocalDateTime.parse(_policyExpiraitonDateAsString, AAAMembershipQueries.SQLDateTimeFormatter);
        _policyState = state;

        // Determine Membership Timepoints by State
        resolveMembershipTimepointsFromState();

        _policyStage3Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage3ToExpiration);
        _policyStage4Date = _policyExpiraitonDate.minusDays(_timeDifferenceInDays_Stage4ToExpiration);
        _renewalImageGenDate = getTimePoints().getRenewImageGenerationDate(_policyExpiraitonDate);
        _renewalBillGenDate = getTimePoints().getBillGenerationDate(_policyExpiraitonDate);
        _policyStage1Date = PolicySummaryPage.getEffectiveDate().plusDays(15l);
        _policyStage2Date = PolicySummaryPage.getEffectiveDate().plusDays(30l);

        //Output debug logs?
        _bPrintDebugInfo = outputDataToLogs;

        if (_bPrintDebugInfo){
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
     * CALL THIS METHOD TO DRIVE CLASS THROUGH DESIRED NUMBER OF TERMS! <br>
     * This loop was given a sanity limit of 10 renewals, expecting never to test beyond that threshold without manual edits.
     * @param desiredTerm Quote/Initial Policy = 0. First Renewal = 1;
     */
    public void moveToGivenTerm(Integer desiredTerm, RenewalHelper_Profile profileOption, boolean bIncludeBillPay){
        Integer currentTerm = 0; //A value of zero represents New Business

        while(currentTerm < desiredTerm){
            // Only doing STG1 and STG2 if profile is 'All'
            if(profileOption.equals(RenewalHelper_Profile.All) || profileOption.equals(RenewalHelper_Profile.IncludeSTG1and2)){
                moveThroughStage1();
                moveThroughStage2();
            }

            moveThroughStage3(profileOption);
            moveThroughStage4(profileOption);
            if(bIncludeBillPay == true) {
                handleBillGenerationAndPayment();
                moveToNewTermAndSetNewTimepoints();
            }
            currentTerm++;
            if(currentTerm > 10 || bIncludeBillPay == false) // SANITY CHECK. Also If we don't pay the bill, we can't move to term++.
                break;
        }
    }

    /**
     * Simply moves a policy to the end of a single term. The Boolean controls if bill pay should be conducted or not.
     * @param bPayBill
     */
    public void moveToEndOfFirstTerm(Boolean bPayBill){
        moveThroughStage1();
        moveThroughStage2();
        moveThroughStage3();
        moveThroughStage4();
        if (bPayBill){
            handleBillGenerationAndPayment();
        }else{
            handleBillGeneration();
        }
    }

    /**
     * Handles all Stage 1 Jobs and Processes.
     * This includes: New Business + 15 Membership Validations.
     */
    public void moveThroughStage1() {

        if (_bPrintDebugInfo){
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: STAGE 1 <QA-LOG-DEBUG>"));
        }

        nbPlus15_MembershipValidation();
    }

    /**
     * Handles all Stage 2 Jobs and Processes.
     * This includes: New Business + 30 Membership Validations.
     */
    public void moveThroughStage2() {
        if (_bPrintDebugInfo){
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: STAGE 2 <QA-LOG-DEBUG>"));
        }

        nbPlus30_MembershipValidation();
    }

    public void moveThroughStage3() {
        if (_bPrintDebugInfo){
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: STAGE 3 <QA-LOG-DEBUG>"));
        }
        rMinus73_generateRenewalImage();
        rMinus63_MembershipTimepoint1();
    }

    /**
     * Handles all Stage 3 Jobs and Processes. <br>
     * This includes: Renewal Image Generation, Membership Timepoint 1.
     */
    public void moveThroughStage3(RenewalHelper_Profile profileOption) {
        if (_bPrintDebugInfo){
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: STAGE 3 <QA-LOG-DEBUG>"));
        }
        rMinus73_generateRenewalImage();
        rMinus63_MembershipTimepoint1();

        // Only perform if doing ALL jobs.
        if(profileOption.equals(RenewalHelper_Profile.All)){
            rMinus60_SendCreditDiscolsure();
            rMinus57_RunUnderWriterRules();
        }
    }

    public void moveThroughStage4() {
        if (_bPrintDebugInfo){
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: STAGE 4 <QA-LOG-DEBUG>"));
        }
        rMinus48_MembershipTimepoint2();

        // Only perform if doing ALL jobs.
    }

    /**
     * Handles all Stage 4 Jobs and Processes. <br>
     * This includes: Membership Timepoint 2.
     */
    public void moveThroughStage4(RenewalHelper_Profile profileOption) {
        if (_bPrintDebugInfo){
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: STAGE 4 <QA-LOG-DEBUG>"));
        }
        rMinus48_MembershipTimepoint2();

        // Only perform if doing ALL jobs.
        if(profileOption.equals(RenewalHelper_Profile.All)) {
            rMinus45_PremiumCalculation();
            rMinus36_LastDayToNonRenew();
            rMinus35_ProposePolicy();
        }
    }

    /**
     * Handles generating the renewal bill and making its payment via UI. <br>
     * This includes: aaaRenewalNoticeBillAsyncJob ran twice, to better ensure bill generation. <br>
     * This also includes advancing +2 days after bill is paid and updating policy status. <br>
     * aaaRenewalNoticeBillAsyncJob is run twice, to better ensure bill generation.
     */
    public void handleBillGenerationAndPayment() {
        // Do below chunk if state is not California
        if (!_policyState.equalsIgnoreCase("CA")){
            TimeSetterUtil.getInstance().nextPhase(_renewalBillGenDate);
            JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
            JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
            JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);

            mainApp().open();
            SearchPage.openBilling(_policyNumber);
            new BillingAccount().acceptPayment().start();
            new AcceptPaymentActionTab().setCheckNumber(123);
            Tab.buttonOk.click();
            mainApp().close();
        }

        // Move forward two days. Update status.
        TimeSetterUtil.getInstance().nextPhase(_renewalBillGenDate.plusDays(2));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob); //POLICY SHOULD BE RENEWED NOW.

        // Manually ZERO OUT aaaTimelineRenewalInd
        AAAMembershipQueries.updateAaaRenewalTimelineIndicatorValue(_policyNumber, "0");
    }

    public void handleBillGeneration() {
        // Do below chunk if state is not California
        if (!_policyState.equalsIgnoreCase("CA")){
            TimeSetterUtil.getInstance().nextPhase(_renewalBillGenDate);
            JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
            JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
            JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
        }
    }

    /**
     * Handles moving from an old term to a new term after paying the previous bill. <br>
     * This simply advances us to NB+10 and captures timepoint values from the DB. <br>
     * Policy number is intentionally not updated to avoid opening the UI.
     */
    public void moveToNewTermAndSetNewTimepoints() {
        TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.plusDays(10));
        captureTimepoints();
    }

    /**
     * Handles NB+15 Jobs and Processes - Membership Validation. <br>
     * This includes: membershipValidationJob.
     */
    private void nbPlus15_MembershipValidation() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage1Date);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.membershipValidationJob);

        if (_bPrintDebugInfo){
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to NB+15, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        }
    }

    /**
     * Handles NB+30 Jobs and Processes - Membership Validation. <br>
     * This includes: membershipValidationJob.
     */
    private void nbPlus30_MembershipValidation() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage2Date);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.membershipValidationJob);

        if (_bPrintDebugInfo) {
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to NB+30, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        }
    }

    /**
     * Handles R-73 Jobs and Processes, generating a renewal image for the policy. <br>
     * This includes: renewalImageRatingAsyncTaskJob. <br>
     * This method accounts for State Deltas.
     */
    private void rMinus73_generateRenewalImage() {
        switch (_policyState) {
            case "MD":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(88l));
                break;
            case "CT":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(101l));
                break;
            case "KY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(108l));
                break;
            case "MT":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(85l));
                break;
            case "WY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(85l));
                break;
            case "NY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(88l));
                break;
            case "WA":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(85l));
                break;
            case "CA":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(83l));
                break;
            default:
                TimeSetterUtil.getInstance().nextPhase(_renewalImageGenDate);
                break;
        }

        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);

        if (_bPrintDebugInfo) {
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: Renewal Image Generated on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        }
    }

    /**
     * Handles R-63 Jobs and Processes - Timepoint 1 Membership Validation. <br>
     * This includes: aaaMembershipRenewalBatchOrderAsyncJob, policyAutomatedRenewalAsyncTaskGenerationJob.
     */
    private void rMinus63_MembershipTimepoint1() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage3Date);

        if (_bPrintDebugInfo) {
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to TimePoint1, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        }

        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);
    }

    /**
     * Handles R-60 Jobs and Processes. <br>
     * This only seems to apply to State='WV' and runs aaaCreditDisclosureNoticeJob.
     */
    private void rMinus60_SendCreditDiscolsure(){
        if (_policyState.equalsIgnoreCase("WV")) {
            JobUtils.executeJob(Jobs.aaaCreditDisclosureNoticeJob);
        }

        if (_bPrintDebugInfo) {
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM Moves ro R-60, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        }
    }

    /**
     * Handles R-57 Jobs and Processes. <br>
     * This includes: RenewalValidationAsyncTaskJob, aaaRenewalDataRefreshAsyncJob. <br>
     * This also includes a manual update query to Oracle DB, updating aaaRenewalTimelineInd. <br>
     * This extra query prepares the policy for rating in Stage 4. <br>
     * This method accounts for State Deltas.
     */
    private void rMinus57_RunUnderWriterRules(){
        switch (_policyState) {
            case "MD":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(72l));
                break;
            case "CT":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(85l));
                break;
            case "KY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(92l));
                break;
            case "MT":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(69l));
                break;
            case "WY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(69l));
                break;
            case "NY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(72l));
                break;
            case "CA":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(67l));
                break;
            default:
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(57l));
                break;
        }

        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalValidationAsyncTaskJob);
        JobUtils.executeJob(Jobs.aaaRenewalDataRefreshAsyncJob);

        // Manually set aaaRenewalTimelineInd
        AAAMembershipQueries.updateAaaRenewalTimelineIndicatorValue(_policyNumber, AAARENEWALTIMELINEIND_VALUETOSET);

        // Verify the DB took our update with an assertion on a SELECT query.
        String results = AAAMembershipQueries.getAaaRenewalTimelineIndicatorValue(_policyNumber);
        CustomAssertions.assertThat(results.toString()).isEqualToIgnoringCase(AAARENEWALTIMELINEIND_VALUETOSET);
    }

    /**
     * Handles R-48 Jobs and Processes. <br>
     * This includes: aaaMembershipRenewalBatchOrderAsyncJob. <br>
     */
    private void rMinus48_MembershipTimepoint2() {
        TimeSetterUtil.getInstance().nextPhase(_policyStage4Date);
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to TimePoint2, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
    }

    /**
     * Handles R-45 Jobs and Processes. <br>
     * This includes: renewalImageRatingAsyncTaskJob. <br>
     * This method accounts for State Deltas.
     */
    private void rMinus45_PremiumCalculation(){
        switch (_policyState) {
            case "MD":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(60l));
                break;
            case "CT":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(73l));
                break;
            case "KY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(80l));
                break;
            case "MT":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(57l));
                break;
            case "WY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(57l));
                break;
            case "NY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(60l));
                break;
            case "WA":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(57l));
                break;
            case "CA":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(58l));
                break;
            default:
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(45l));
                break;
        }
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to STG4 Rating, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);
    }

    /**
     * Handles R-36 Jobs and Processes. <br>
     * THIS DOES NOTHING AT THE MOMENT. TO BE IMPLEMENTED IF REQUIRED.
     */
    private void rMinus36_LastDayToNonRenew(){

    }

    /**
     * Handles R-45 Jobs and Processes. <br>
     * This includes: renewalOfferAsyncTaskJob. <br>
     */
    private void rMinus35_ProposePolicy(){
        switch (_policyState) {
            case "MD":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(50l));
                break;
            case "MT":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(47l));
                break;
            case "WY":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(47l));
                break;
            case "CA":
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(48l));
                break;
            default:
                TimeSetterUtil.getInstance().nextPhase(_policyExpiraitonDate.minusDays(35l));
                break;
        }
        log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper: JVM moved to STG4 Proposal, on '%s' </QA-LOG-DEBUG>" + System.lineSeparator(), TimeSetterUtil.getInstance().getCurrentTime().toString()));
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalOfferAsyncTaskJob);
    }

    /**
     * Handles capturing and calculating all the timepoints from a provided policy. <br>
     * This method captures Effective Date and Expiration Date from Oracle DB, then calculates all timepoints using these two values.
     */
    private void captureTimepoints(){
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

        if (_bPrintDebugInfo){
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper NEW TERM VALUES: Policy Expiration Date = '%s' </QA-LOG-DEBUG>", _policyExpiraitonDate.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper NEW TERM VALUES: NB15(Stage1) = '%s' </QA-LOG-DEBUG>", _policyStage1Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper NEW TERM VALUES: NB30(Stage2) = '%s' </QA-LOG-DEBUG>", _policyStage2Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper NEW TERM VALUES: Membership Timepoint 1 (Stage3) = '%s' </QA-LOG-DEBUG>", _policyStage3Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper NEW TERM VALUES: Membership Timepoint 2 (Stage4) = '%s' </QA-LOG-DEBUG>", _policyStage4Date.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper NEW TERM VALUES: Renewal Image Generation Date = '%s' </QA-LOG-DEBUG>", _renewalImageGenDate.toString()));
            log.debug(String.format(System.lineSeparator() + "<QA-LOG-DEBUG> RenewalHelper NEW TERM VALUES: Renewal Bill Generation Date = '%s' </QA-LOG-DEBUG>", _renewalBillGenDate.toString()));
        }
    }

    /**
     * This method will evaluate Membership Timepoint 1 and 2 values, using the _statePolicy class variable. <br>
     * Once the appropriate STG3 and STG4 timepoints are determined, they are set as class variables.
     */
    private void resolveMembershipTimepointsFromState(){
        switch(_policyState){
            case "MD":
                _timeDifferenceInDays_Stage3ToExpiration = 78l;
                _timeDifferenceInDays_Stage4ToExpiration = 63l;
                break;
            case "CT":
                _timeDifferenceInDays_Stage3ToExpiration = 91l;
                _timeDifferenceInDays_Stage4ToExpiration = 76l;
                break;
            case "KY":
                _timeDifferenceInDays_Stage3ToExpiration = 98l;
                _timeDifferenceInDays_Stage4ToExpiration = 83l;
                break;
            case "MT":
                _timeDifferenceInDays_Stage3ToExpiration = 75l;
                _timeDifferenceInDays_Stage4ToExpiration = 60l;
                break;
            case "WY":
                _timeDifferenceInDays_Stage3ToExpiration = 75l;
                _timeDifferenceInDays_Stage4ToExpiration = 60l;
                break;
            case "NY":
                _timeDifferenceInDays_Stage3ToExpiration = 78l;
                _timeDifferenceInDays_Stage4ToExpiration = 63l;
                break;
            case "WA":
                _timeDifferenceInDays_Stage3ToExpiration = 63l;
                _timeDifferenceInDays_Stage4ToExpiration = 60l;
                break;
            case "CA":
                _timeDifferenceInDays_Stage3ToExpiration = 73l;
                _timeDifferenceInDays_Stage4ToExpiration = 59l;
                break;
            default:
                _timeDifferenceInDays_Stage3ToExpiration = 63l; // <-- R-63 and R-48 being default timepoints for HomeSS policies.
                _timeDifferenceInDays_Stage4ToExpiration = 48l;
                break;
        }
    }

    public String getCreatedPolicyNumber(){
        return _policyNumber;
    }
}