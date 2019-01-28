package aaa.modules.regression.sales.template.functional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.apache.commons.lang3.NotImplementedException;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.db.queries.TimePointQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssertions;

/**
 * This template is used to test Best Membership Logic (BML) operations. BML is only used in cases where either No
 * Membership was provided during quote time (AAA Membership Numbers provided during quoting are hereafter referred to
 * as fallback member numbers) or the fallback member number couldn't be validated (Status Error or Inactive).  <br>
 *                                                                                                              <br>
 * The function of BML when used is to Find the Best Active Status member number from Elastic Search responses. <br>
 *                                                                                                              <br>
 * BML evaluates the Elastic Response for the first Active membership after Policy Effective Date for new business or
 * Renewal Date for existing policies. Elastic responses are evaluated using the following sequence:            <br>
 * 1-Membership Effective Date                                                                                  <br>
 * 2-Member Startdate Primary                                                                                   <br>
 * 3-Member Effectivedate Primary                                                                               <br>
 * 4-Member Startdate Resident Adult                                                                            <br>
 * 5-Member Effectivedate Resident Adult                                                                        <br>
 * 6-Member Startdate Dependant Associate                                                                       <br>
 * 7-Member Effectivedate Dependant Associate                                                                   <br>
 * <br>
 * Note: The Only difference between Fallback and Non-Fallback policies is whether or not a Membership number was
 * entered during quote time. From code, when dealing with STG1, we also have to change the AAA Membership Status to
 * Error for BML to re-evaluate the input member number.
 */
public class TestBestMembershipLogicTemplate extends PolicyBaseTest {

    ////////////////////////
    // Global test fields //
    ////////////////////////

    // Must be mocked in the Elastic Search response but point to a valid Stub Retrieve Member Summary member number.
    public final String DefaultBMLResponseMemberNumber = "9436258506738011";
    // Must be a valid Stub Retrieve Member Summary member number but not the same as what comes back from Elastic Mock.
    public  final String DefaultFallbackMemberNumber = "9999994444444440";
    // Used to drive validations in doValidations() method.
    public enum MembershipStatus {YES, NO, PENDING, OVERRIDE_TERM, OVERRIDE_LIFE}
    // Used to drive validations in doValidations() method.
    public enum RMSStatus {Active, Error, Inactive, NA}
    public final String ACTIVE_MEMBERSHIP_NUMBER = "3166230129507213";
    public final String ACTIVE_BML_MEMBERSHIP_NUMBER = "9999995555555551";
    public final String INACTIVE_BML_MEMBERSHIP_NUMBER = "4290020000051010";

    /**
     * Create Policy with a Fallback to specific number.
     * @param fallbackMemberNumber Specifies an initial member number to use.
     * @return Policy Number
     */
    protected String createYesAAAMembershipPolicy(String fallbackMemberNumber){

        TestData testData = getAAAMemberPolicyTestData(fallbackMemberNumber);

        String policyNumber = createBMLPolicy(testData);

        setAAAMembershipErrorStatus(policyNumber);

        return policyNumber;
    }

    /**
     * Create Default Policy using Default Fallback Member Number.
     * @return Policy Number
     */
    protected String createDefaultYesAAAMembershipPolicy(){
        return createYesAAAMembershipPolicy(DefaultFallbackMemberNumber);
    }

    /**
     * Create a Policy without a Fallback and Current AAA Member = No
     * @return Policy Number
     */
    protected String createNoAAAMembershipPolicy(){

        TestData testData = getNonAAAMemberPolicyTestData();

        String policyNumber = createBMLPolicy(testData);

        return policyNumber;
    }

    /**
     * Move Policy to STG1 NB+15 <br>
     * @param policyNumber The Policy Number you want to move to STG1 / NB+15.
     * @return LocalDateTime representing the Policy Effective Date
     */
    protected LocalDateTime movePolicyToSTG1NB15(String policyNumber){

        LocalDateTime policyEffectiveDate = getPolicyEffectiveDate(policyNumber);

        moveJVMNumberOfDaysFromEffectiveDate(policyEffectiveDate, 15);

        executeSTG1STG2Jobs();

        return policyEffectiveDate;
    }

    /**
     * Move Policy to STG2 NB+30 <br>
     * @param policyNumber The Policy Number you want to move to STG2 / NB+30.
     * @param policyEffectiveDateTime The policy effective date for the timesetter calculations.
     */
    protected void movePolicyToSTG2NB30(String policyNumber, LocalDateTime policyEffectiveDateTime){

        // Only set error statuses if Current AAA Member set to Yes.
        if (isAAAMemberInputAtQuoteTime(policyNumber)) {
            // Set status for BML
            setAAAMembershipErrorStatus(policyNumber);
            setAAABestMemberStatus(policyNumber, AAAMembershipQueries.AAABestMembershipStatus.ERROR_STG1);
        }

        moveJVMNumberOfDaysFromEffectiveDate(policyEffectiveDateTime, 30);

        executeSTG1STG2Jobs();
    }


    /**
     * Move Policy to STG3 - Renewal Image, Reports, and Rating Generation. <br>
     * @param policyNumber The Policy Number you want to move to STG3.
     * @return LocalDateTime representing the Policy Expiration Date
     */
    protected LocalDateTime movePolicyToSTG3Renewal(String policyNumber){

        // Only Set error status if there was a prior ordered membership number.
        if (AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber).isPresent()) {

            setAAAMembershipErrorStatus(policyNumber);
            setAAABestMemberStatus(policyNumber, AAAMembershipQueries.AAABestMembershipStatus.ERROR_STG2);
        }

        LocalDateTime policyExpirationDate = getPolicyExpirationDate(policyNumber);

        generatePreSTG3RenewalImage(policyExpirationDate);

        executeSTG3Jobs(policyExpirationDate);

        generatePostSTG3ReportsAndRating(policyExpirationDate);

        return policyExpirationDate;
    }

    /**
     * Move Policy to STG4 - Final Renewal jobs.
     * @param policyNumber The Policy Number you want to move to STG4.
     */
    protected void movePolicyToSTG4Renewal(String policyNumber, LocalDateTime policyExpirationDate){

        setAAAMembershipErrorStatus(policyNumber);

        setAAABestMemberStatus(policyNumber, AAAMembershipQueries.AAABestMembershipStatus.ERROR_STG3);

        executeSTG4Jobs(policyExpirationDate);

        executeSTG4Rating(policyExpirationDate);

        executeSTG4OfferIssue(policyExpirationDate);

        generateSTG4RenewalBill(policyExpirationDate);
    }

    /**
     * Creates a policy with the intention of going through BML.
     * @param testData Runs createPolicy with specified TD
     * @return Policy Number
     */
     private String createBMLPolicy(TestData testData) {
         // Create the policy and save policy number //
         mainApp().open();
         createCustomerIndividual();
         return createPolicy(testData);
    }

    /**
     * Queries DB to find out if Current AAA Membership Yes was selected at quote time.
     * @param policyNumber The policy number to check against.
     * @return True if Yes was used during quote time.
     */
    private boolean isAAAMemberInputAtQuoteTime(String policyNumber)throws NullPointerException{
         String insurerCd = AAAMembershipQueries.getAAAInsurerCdFromSQL(policyNumber).orElse("Null");

         // There is no scenario after binding where insurerCd should come back as null
        if (insurerCd.equals("Null")){
            throw new NullPointerException("InsurerCd query should never come back Null after policy binding");
        }
         return insurerCd.equals("Yes");
    }

    /**
     * Used to get test data when AAA Member Number provided
     * @param aaaMemberNumber The AAA Membership Number value you want to set in the PAS UI. <br>
     * EX: "9999994444444440"
     * @return TestData that ensures Current AAA Member to Yes and automation uses specific Member Number
     * @throws NotImplementedException when evaluating policy types for keypath generation if no match.
     */
    private TestData getAAAMemberPolicyTestData(String aaaMemberNumber) throws NotImplementedException {

        String keypathCurrentMember;
        String keypathMemberNum;

        // Set keypaths based on policy type.
        switch (getPolicyType().getShortName())
        {
            case "AutoCA": {
                // keypathTabSection Result: "GeneralTab|AAAProductOwned"
                String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.customer.defaulttabs.GeneralTab.class.getSimpleName(),
                        AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
                break;
            }

            case "AutoCAC": {
                // keypathTabSection Result: "GeneralTab|AAAProductOwned"
                String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.customer.defaulttabs.GeneralTab.class.getSimpleName(),
                        AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
                break;
            }

            case "AutoSS": {
                // keypathTabSection Result: "GeneralTab|AAAProductOwned"
                String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.customer.defaulttabs.GeneralTab.class.getSimpleName(),
                        AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());
                break;
            }

            case "HomeSS_HO3": {
                // keypathTabSection Result: "ApplicantTab|AAAMembership"
                String keypathTabSection = TestData.makeKeyPath(ApplicantTab.class.getSimpleName(),
                        HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());
                break;
            }

            case "HomeCA_HO3": {
                // keypathTabSection Result: "ApplicantTab|AAAMembership"
                String keypathTabSection = TestData.makeKeyPath(ApplicantTab.class.getSimpleName(),
                        HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());
                break;
            }

            default: {
                String msg = "getAAAMemberPolicyTestData does not implement policy type: [" + getPolicyType().getShortName() +
                        "] Keypaths must be mapped for the type for CurrentAAAMembership and AAAMembership Number";
                throw new NotImplementedException(msg);
            }
        }

        return getPolicyTD()
                .adjust(keypathCurrentMember, "Yes")
                // Use different valid membership number so we can validate BML/Elastic response overrides this.
                .adjust(keypathMemberNum, aaaMemberNumber);
    }

    /**
     * Used to get test data that will set No to Membership status.
     * @return TestData that ensures Current AAA Member to No.
     * @throws NotImplementedException when evaluating policy types for keypath generation if no match.
     */
    private TestData getNonAAAMemberPolicyTestData() throws NotImplementedException {

        String keypathCurrentMember;
        String keypathMemberNum;
        String keypathOrderMembershipReports = ""; // This is only used for HO products.


        // Set keypaths based on policy type.
        switch (getPolicyType().getShortName())
        {
            case "AutoCA": {
                // keypathTabSection Result: "GeneralTab|AAAProductOwned"
                String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.customer.defaulttabs.GeneralTab.class.getSimpleName(),
                        AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
                break;
            }

            case "AutoCAC": {
                // keypathTabSection Result: "GeneralTab|AAAProductOwned"
                String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.customer.defaulttabs.GeneralTab.class.getSimpleName(),
                        AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
                break;
            }

            case "AutoSS": {
                // keypathTabSection Result: "GeneralTab|AAAProductOwned"
                String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.customer.defaulttabs.GeneralTab.class.getSimpleName(),
                        AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());
                break;
            }

            case "HomeSS_HO3": {
                // keypathTabSection Result: "ApplicantTab|AAAMembership"
                String keypathTabSection = TestData.makeKeyPath(ApplicantTab.class.getSimpleName(),
                        HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());
                break;
            }

            case "HomeCA_HO3": {
                // keypathTabSection Result: "ApplicantTab|AAAMembership"
                String keypathTabSection = TestData.makeKeyPath(ApplicantTab.class.getSimpleName(),
                        HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());

                // keypathOrderMembershipReports Result: "ReportsTab|AAAMembershipReport"
                keypathOrderMembershipReports = TestData.makeKeyPath(HomeCaMetaData.ReportsTab.class.getSimpleName(),
                        HomeCaMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());
                break;
            }

            default: {
                String msg = "getNonAAAMemberPolicyTestData does not implement policy type: [" + getPolicyType().getShortName() +
                        "] Keypaths must be mapped for the type for CurrentAAAMembership and AAAMembership Number";
                throw new NotImplementedException(msg);
            }
        }

        TestData testData =  getPolicyTD()
                .adjust(keypathCurrentMember, "No")
                .mask(keypathMemberNum);

        // Skip ordering the membership report if a keypath provided
        if(!keypathOrderMembershipReports.isEmpty()){
            testData.mask(keypathOrderMembershipReports);
        }

        return testData;
    }

    /**
     * Gets the Policy Effective Date from the DB.
     * @param policyNumber Is the policy number to get the effective date from.
     * @return LocalDateTime representing the Policy Effective Date
     */
    private LocalDateTime getPolicyEffectiveDate(String policyNumber){
        String dbPolicyEffectiveDate =
                AAAMembershipQueries.getPolicyEffectiveDateFromSQL(policyNumber).orElse("Null Value");

        LocalDateTime policyEffectiveDateTime =
                LocalDateTime.parse(dbPolicyEffectiveDate, AAAMembershipQueries.SQLDateTimeFormatter);

        return policyEffectiveDateTime;
    }

    /**
     * Gets the Policy Expiration Date from the DB.
     * @param policyNumber Is the policy number to get the expiration date from.
     * @return LocalDateTime representing the Policy Expiration Date
     */
    private LocalDateTime getPolicyExpirationDate(String policyNumber){
        String dbPolicyExpirationDate =
                AAAMembershipQueries.getPolicyExpirationDateFromSQL(policyNumber).orElse("Null Value");

        LocalDateTime policyExpirationDateTime =
                LocalDateTime.parse(dbPolicyExpirationDate, AAAMembershipQueries.SQLDateTimeFormatter);

        return policyExpirationDateTime;
    }

    /**
     * Used to set a DB Error Status for RMS response so BML will attempt to override provided number.
     * @param policyNumber The policy number you want to update to an error status.
     */
    private void setAAAMembershipErrorStatus(String policyNumber){
        AAAMembershipQueries.updateAAAMembershipStatusInSQL(policyNumber, AAAMembershipQueries.AAAMembershipStatus.Error);

        // Clear cache to avoid BML result getting overridden by the original number.
        //adminApp().open();
        //new CacheManager().goClearCacheManagerTable();
    }

    /**
     * Used to set a DB Error Status for Best Membership Status response so BML will attempt to override provided number. <br>
     * Do not use this at STG1. BML has not been run yet so should be null.
     * @param policyNumber The policy number you want to update to an error status.
     * @param updatedStatus This is to update the last BML status, so if about to run STG2 jobs, this should be Error_STG1.
     */
    private void setAAABestMemberStatus(String policyNumber, AAAMembershipQueries.AAABestMembershipStatus updatedStatus){
        AAAMembershipQueries.updateAAABestMembershipStatusInSQL(policyNumber, updatedStatus);
    }

    /**
     * Logs out the policy effective date and Moves to specified date offset.
     * @param policyEffectiveDate The Current policy effective date to move ahead
     * @param numberOfDays Number of days from provided policy effective date to move JVM to.
     */
    private void moveJVMNumberOfDaysFromEffectiveDate(LocalDateTime policyEffectiveDate, int numberOfDays){
        log.info("Policy Effective Date: " + policyEffectiveDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(numberOfDays));
    }

    /**
     * Logs out the policy expiration date and Moves to specified date offset.
     * @param policyExpirationDate The Current policy effective date to move ahead
     * @param numberOfDays Number of days before provided policy expiration date to move JVM to.
     */
    private void moveJVMNumberOfDaysBeforeExpirationDate(LocalDateTime policyExpirationDate, int numberOfDays){
        log.info("Policy Expiration Date: " + policyExpirationDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(numberOfDays));
    }

    /**
     * Executes the required jobs for BML at STG1 (NB+15) or STG2 (NB+30).
     */
    private void executeSTG1STG2Jobs() {
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
        JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
        JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
        JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
        JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
        //JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
    }

    /**
     * Moves JVM to appropriate date and generates Renewal Image.
     * @param policyExpirationDate The Policy Expiration Date to renew off of.
     */
    private void generatePreSTG3RenewalImage(LocalDateTime policyExpirationDate){

        LocalDateTime renewalImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewalImageGenDate);

        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.policyAutomatedRenewalAsyncTaskGenerationJob);
    }

    /**
     * Moves JVM to appropriate date and executes STG3 jobs.
     * @param policyExpirationDate The Policy Expiration Date to renew off of.
     */
    private void executeSTG3Jobs(LocalDateTime policyExpirationDate){

        int timepoint1_STG3_Offset = TimePointQueries.getRenewalTimePoint1_STG3(getPolicyType(), getState());
        moveJVMNumberOfDaysBeforeExpirationDate(policyExpirationDate, timepoint1_STG3_Offset);

        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);
    }

    /**
     * Moves JVM to appropriate date and generates Renewal Image.
     * @param policyExpirationDate The Policy Expiration Date to renew off of.
     */
    private void generatePostSTG3ReportsAndRating(LocalDateTime policyExpirationDate){

        // Usually R-57
        LocalDateTime reportServices = getTimePoints().getRenewCheckUWRules(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(reportServices);

        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);
    }

    /**
     * Moves JVM to appropriate date and generates Renewal Image.
     * @param policyExpirationDate The Policy Expiration Date to renew off of.
     */
    private void executeSTG4Jobs(LocalDateTime policyExpirationDate){

        int timepoint2_STG4_Offset = TimePointQueries.getRenewalTimePoint2_STG4(getPolicyType(), getState());
        moveJVMNumberOfDaysBeforeExpirationDate(policyExpirationDate, timepoint2_STG4_Offset);

        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchOrderAsyncJob);
        JobUtils.executeJob(Jobs.aaaMembershipRenewalBatchReceiveAsyncJob);
    }

    /**
     * Moves JVM to appropriate date and executes STG4 jobs.
     * @param policyExpirationDate The Policy Expiration Date to renew off of.
     */
    private void executeSTG4Rating(LocalDateTime policyExpirationDate){

        // Usually R-45
        LocalDateTime ratePolicy = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(ratePolicy);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);
    }

    /**
     * Moves JVM to appropriate date and executes jobs to Issue Offer.
     * @param policyExpirationDate The Policy Expiration Date to renew off of.
     */
    private void executeSTG4OfferIssue(LocalDateTime policyExpirationDate){

        // Usually R-35
        LocalDateTime offerIssue = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(offerIssue);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalOfferAsyncTaskJob);
    }

    /**
     * Moves JVM to appropriate date and executes jobs to Send Renewal Bill.
     * @param policyExpirationDate The Policy Expiration Date to renew off of.
     */
    private void generateSTG4RenewalBill(LocalDateTime policyExpirationDate){

        // Usually R-20
        LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(billDate);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
    }

    public TestData getBMLPolicyTD(){
        return getPolicyTD();
    }

    /**
     * Use to simplify adjusting test data.
     * @param td The input test data to be adjusted.
     * @param tabElementIsOn Tab class object. Element adjusted to the test data lives on this tab.
     * @param elementLabel getLabel() for MetaData element being manipulated via adjustment.
     * @param value The new value he element should contain.
     * @return
     */
    public TestData adjustTD(TestData td, Class<? extends Tab> tabElementIsOn, String elementLabel, String value){
        td.adjust(TestData.makeKeyPath(tabElementIsOn.getSimpleName(), elementLabel), value);
        return td;
    }

    /**
     * Use to simplify adjusting test data.
     * @param td The input test data to be adjusted.
     * @param tabElementIsOn Tab class object. Element adjusted to the test data lives on this tab.
     * @param subChunkLabel getLabel() for AssetList that contains sub Assets.
     * @param elementLabel getLabel() for MetaData element being manipulated via adjustment.
     * @param value The new value he element should contain.
     * @return
     */
    public TestData adjustTD(TestData td, Class<? extends Tab> tabElementIsOn, String subChunkLabel, String elementLabel, String value){
        String result = TestData.makeKeyPath(tabElementIsOn.getSimpleName(), subChunkLabel, elementLabel);
        td.adjust(result ,value);
        return td;
    }

    /**
     * Use to simplify adjusting masking/removing test data.
     * @param td The input test data to be adjusted.
     * @param tabElementIsOn Tab class object. Element adjusted to the test data lives on this tab.
     * @param subChunkLabel getLabel() for AssetList that contains sub Assets.
     * @param elementLabel getLabel() for MetaData element being removed from the test data.
     * @return
     */
    public TestData maskTD(TestData td, Class<? extends Tab> tabElementIsOn, String subChunkLabel, String elementLabel){
        td.mask(TestData.makeKeyPath(tabElementIsOn.getSimpleName(), subChunkLabel, elementLabel));
        return td;
    }

    /**
     * Use to simplify adjusting masking/removing test data.
     * @param td The input test data to be adjusted.
     * @param tabElementIsOn Tab class object. Element adjusted to the test data lives on this tab.
     * @param elementLabel getLabel() for MetaData element being removed from the test data.
     * @return
     */
    public TestData maskTD(TestData td, Class<? extends Tab> tabElementIsOn, String elementLabel){
        td.mask(TestData.makeKeyPath(tabElementIsOn.getSimpleName(), elementLabel));
        return td;
    }

    /**
     * Opens a quote for creation using a default customer. Returns LocalDateTime expirationDate.
     * @param inputTestData
     */
    public LocalDateTime doTestReturnPolicyNumber(TestData inputTestData){
        log.info("==================== Beginning Policy Creation ====================");
        mainApp().open();
        createCustomerIndividual();
        createPolicy(inputTestData);
        return PolicySummaryPage.getExpirationDate();
    }

    /**
     * Simply used to scrape and return policy number form policy summary page. Closes App after.
     * @return
     */
    public String scrapePolicyNumberandExit() {
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();
        return policyNumber;
    }

    public void doValidations(String in_policyNumber, MembershipStatus memberStatus, RMSStatus rms, String productType) {
        mainApp().open();
        SearchPage.openPolicy(in_policyNumber);
        PolicySummaryPage.buttonRenewals.click();

        switch(productType){
            case "AutoSS":
                autoSSSpecificValidations(memberStatus, rms);
                break;
            case "HomeSS_HO3":
                homeSSSpecificValidations(memberStatus, rms);
                break;
            case "AutoCAC":
                CAAutoSelectSpecificValidations(memberStatus, rms);
                break;
            case "HomeCA_HO3":
                CAHomeSpecificValidations(memberStatus, rms);
                break;
            default:
                log.error("[QADEBUG] ERROR: Unsupported policy type provided to doValidations(). Type= " + productType);
                break;
        }

        mainApp().close();
    }

    private void autoSSSpecificValidations(MembershipStatus memberStatus, RMSStatus rms) {
        // Validate AAA Membership in General Tab
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        GeneralTab gt = new GeneralTab();

        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(gt.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Yes");
                if (rms.equals(RMSStatus.Inactive)) {
                    CustomAssertions.assertThat(gt.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase(INACTIVE_BML_MEMBERSHIP_NUMBER);
                }
                if (rms.equals(RMSStatus.Active)) {
                    CustomAssertions.assertThat(gt.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase(ACTIVE_BML_MEMBERSHIP_NUMBER);
                }
                break;
            case NO:
                CustomAssertions.assertThat(gt.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("No");
                break;
            case PENDING:
                CustomAssertions.assertThat(gt.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Pending");
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(gt.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Override");
                CustomAssertions.assertThat(gt.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.OVERRIDE_TYPE.getLabel()).getValue().toString()).isEqualToIgnoringCase("Life");
                break;
            case OVERRIDE_TERM:
                CustomAssertions.assertThat(gt.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Override");
                CustomAssertions.assertThat(gt.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.OVERRIDE_TYPE.getLabel()).getValue().toString()).isEqualToIgnoringCase("Term");
                break;
            default:
                break;
        }

        // Validate Membership in P&C Tab
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));
                break;
            case NO:
                CustomAssertions.assertThat(!PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));
                break;
            case PENDING:
                CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));
                break;
            case OVERRIDE_TERM:
                break;
            default:
                break;
        }

        // Validate Membership in VRD
        PremiumAndCoveragesTab.RatingDetailsView.open();

        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1).getCell(4).getValue()).isEqualToIgnoringCase("Yes");
                break;
            case NO:
                CustomAssertions.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1).getCell(4)).hasValue("None");
                break;
            case PENDING:
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1).getCell(4).getValue()).isEqualToIgnoringCase("Yes");
                break;
            case OVERRIDE_TERM:
                break;
            default:
                break;
        }
        PremiumAndCoveragesTab.RatingDetailsView.close();
    }

    private void homeSSSpecificValidations(MembershipStatus memberStatus, RMSStatus rms) {
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        ApplicantTab at = new ApplicantTab();
        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Yes");
                if (rms.equals(RMSStatus.Inactive)) {
                    CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase(INACTIVE_BML_MEMBERSHIP_NUMBER);
                }
                if (rms.equals(RMSStatus.Active)) {
                    CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase(ACTIVE_BML_MEMBERSHIP_NUMBER);
                }
                break;
            case NO:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("No");
                break;
            case PENDING:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Pending");
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Override");
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.OVERRIDE_TYPE.getLabel()).getValue().toString()).isEqualToIgnoringCase("Life");
                break;
            case OVERRIDE_TERM:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Override");
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.OVERRIDE_TYPE.getLabel()).getValue().toString()).isEqualToIgnoringCase("Term");
                break;
            default:
                break;
        }

        // Validate Membership in P&C Tab
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab pnc = new aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab();

        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(pnc.isDiscountApplied("AAA Membership")).isTrue();
                break;
            case NO:
                CustomAssertions.assertThat(pnc.isDiscountApplied("AAA Membership")).isFalse();
                break;
            case PENDING:
                CustomAssertions.assertThat(pnc.isDiscountApplied("AAA Membership")).isTrue();
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(pnc.isDiscountApplied("AAA Membership")).isTrue();
                break;
            case OVERRIDE_TERM:
                CustomAssertions.assertThat(pnc.isDiscountApplied("AAA Membership")).isTrue();
                break;
            default:
                break;
        }

        // Validate Membership in VRD
        pnc.calculatePremium();
        aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();

        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("Yes");
                break;
            case NO:
                CustomAssertions.assertThat(aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("No");
                break;
            case PENDING:
                CustomAssertions.assertThat(aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("Yes");
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("Yes");
                break;
            case OVERRIDE_TERM:
                CustomAssertions.assertThat(aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA Member indicator")).isEqualToIgnoringCase("Yes");
                break;
            default:
                break;
        }
    }

    private void CAAutoSelectSpecificValidations(MembershipStatus memberStatus, RMSStatus rms) {
        policy.renew().doAction("Data Gathering");
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();

        // First Go Reorder Membership Reports.
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.MEMBERSHIP.get());
        new MembershipTab().orderMembershipReport();

        // Validate AAA Membership in General Tab
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.GENERAL.get());
        aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab gt = new aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab();
        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(gt.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Yes");
                if (rms.equals(RMSStatus.Inactive)) {
                    CustomAssertions.assertThat(gt.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase(INACTIVE_BML_MEMBERSHIP_NUMBER);
                }
                if (rms.equals(RMSStatus.Active)) {
                    CustomAssertions.assertThat(gt.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase(ACTIVE_BML_MEMBERSHIP_NUMBER);
                }
                break;
            case NO:
                CustomAssertions.assertThat(gt.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("No");
                break;
            case PENDING:
                CustomAssertions.assertThat(gt.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Pending");
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(gt.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Override");
                CustomAssertions.assertThat(gt.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.OVERRIDE_TYPE.getLabel()).getValue().toString()).isEqualToIgnoringCase("Life");
                break;
            case OVERRIDE_TERM:
                CustomAssertions.assertThat(gt.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Override");
                CustomAssertions.assertThat(gt.getAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAProductOwned.OVERRIDE_TYPE.getLabel()).getValue().toString()).isEqualToIgnoringCase("Term");
                break;
            default:
                break;
        }

        // Validate Membership in P&C Tab
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));
                break;
            case NO:
                CustomAssertions.assertThat(!aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));
                break;
            case PENDING:
                CustomAssertions.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));
                break;
            case OVERRIDE_TERM:
                break;
            default:
                break;
        }

        // Validate Membership in VRD
        aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.RatingDetailsView.open();

        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Program").getCell(2).getValue().contains("AAA Members")).isTrue();
                break;
            case NO:
                CustomAssertions.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Program").getCell(2).getValue().contains("AAA Members")).isFalse();
                break;
            case PENDING:
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Program").getCell(2).getValue().contains("AAA Members")).isTrue();
                break;
            case OVERRIDE_TERM:
                break;
            default:
                break;
        }
        PremiumAndCoveragesTab.RatingDetailsView.close();
    }

    private void CAHomeSpecificValidations(MembershipStatus memberStatus, RMSStatus rms) {
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        ApplicantTab at = new ApplicantTab();
        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Yes");
                if (rms.equals(RMSStatus.Inactive)) {
                    CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase(INACTIVE_BML_MEMBERSHIP_NUMBER);
                }
                if (rms.equals(RMSStatus.Active)) {
                    CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase(ACTIVE_BML_MEMBERSHIP_NUMBER);
                }
                break;
            case NO:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("No");
                break;
            case PENDING:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Pending");
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Override");
                break;
            case OVERRIDE_TERM:
                CustomAssertions.assertThat(at.getAAAMembershipAssetList().getAsset(HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()).getValue().toString()).isEqualToIgnoringCase("Membership Override");
                break;
            default:
                break;
        }

        // Reorder Reports
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());
        new ReportsTab().reorderReports();

        // Validate Membership in P&C Tab
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab pnc = new PremiumsAndCoveragesQuoteTab();
        pnc.calculatePremium();
        switch(memberStatus){
            case YES:
                CustomAssertions.assertThat(pnc.isDiscountApplied("AAA Membership")).isTrue();
                break;
            case NO:
                break;
            case PENDING:
                CustomAssertions.assertThat(pnc.isDiscountApplied("AAA Membership")).isTrue();
                break;
            case OVERRIDE_LIFE:
                CustomAssertions.assertThat(pnc.isDiscountApplied("AAA Membership")).isTrue();
                break;
            case OVERRIDE_TERM:
                CustomAssertions.assertThat(pnc.isDiscountApplied("AAA Membership")).isTrue();
                break;
            default:
                break;
        }

        // Validate Membership in VRD
        pnc.calculatePremium();

		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();

        switch(memberStatus){
            case YES:
				CustomAssertions.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA member indicator")).isEqualToIgnoringCase("Yes");
                break;
            case NO:
				CustomAssertions.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA member indicator")).isEqualToIgnoringCase("No");
                break;
            case PENDING:
				CustomAssertions.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA member indicator")).isEqualToIgnoringCase("Yes");
                break;
            case OVERRIDE_LIFE:
				CustomAssertions.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA member indicator")).isEqualToIgnoringCase("Yes");
                break;
            case OVERRIDE_TERM:
				CustomAssertions.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts
                        .getValueByKey("Membership current AAA member indicator")).isEqualToIgnoringCase("Yes");
                break;
            default:
                break;
        }
    }

    /**
     * Handles common functionality shared between PAS-14048 tests extending this class, in that this calls all common methods and accepts only the varying parameters.
     * @param td
     * @param stage3MembershipStatus
     * @param stage3RMSExpectedStatus
     * @param stage4MembershipStatus
     * @param stage4RMSExpectedStatus
     */
    public void testCaseDriver(TestData td, MembershipStatus stage3MembershipStatus, RMSStatus stage3RMSExpectedStatus, MembershipStatus stage4MembershipStatus, RMSStatus stage4RMSExpectedStatus) {
        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(td);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doDBReport(_policyNumber, "3");
        doValidations(_policyNumber, stage3MembershipStatus, stage3RMSExpectedStatus, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doDBReport(_policyNumber, "4");
        doValidations(_policyNumber, stage4MembershipStatus, stage4RMSExpectedStatus, getPolicyType().getShortName());
    }

    /**
     * Query the DB to check Membership Status.
     * @param in_policyNumber
     * @param stage
     */
    private void doDBReport(String in_policyNumber, String stage) {

        Optional<AAAMembershipQueries.AAABestMembershipStatus> membershipStatus
                = AAAMembershipQueries.getAAABestMembershipStatusFromSQL(in_policyNumber);
        if(membershipStatus.isPresent()){
            log.info("<QADEBUG> Membership Status["+stage+"]: " + membershipStatus.get().name());
        }else {
            log.info("<QADEBUG> Membership Status["+stage+"]: NULL, NOTHING!!!!!!! YOU GET NOTHING GOOD DAY SIR!");
        }
    }
}
