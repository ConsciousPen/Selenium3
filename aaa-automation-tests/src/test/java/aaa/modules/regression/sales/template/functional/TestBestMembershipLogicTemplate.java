package aaa.modules.regression.sales.template.functional;

import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.db.queries.TimePointQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang3.NotImplementedException;
import toolkit.datax.TestData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    /**
     * Create Policy with a Fallback to specific number.
     * @param fallbackMemberNumber Specifies an initial member number to use.
     * @return Policy Number
     */
    protected String createFallbackPolicy(String fallbackMemberNumber){
        return createBMLPolicy(fallbackMemberNumber);
    }

    /**
     * Create Default Policy using Default Fallback Member Number.
     * @return Policy Number
     */
    protected String createDefaultFallbackPolicy(){
        return createFallbackPolicy(DefaultFallbackMemberNumber);
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
     * Move Policy to NB+30 <br>
     * @param policyNumber The Policy Number you want to move to STG2 / NB+30.
     * @param policyEffectiveDateTime The policy effective date for the timesetter calculations.
     */
    protected void movePolicyToSTG2NB30(String policyNumber, LocalDateTime policyEffectiveDateTime){

        setAAAMembershipErrorStatus(policyNumber);

        setAAABestMemberStatus(policyNumber, AAAMembershipQueries.AAABestMembershipStatus.ERROR_STG1);

        moveJVMNumberOfDaysFromEffectiveDate(policyEffectiveDateTime, 30);

        executeSTG1STG2Jobs();
    }


    /**
     * Move Policy to STG3 - Renewal Image, Reports, and Rating Generation. <br>
     * @param policyNumber The Policy Number you want to move to STG3.
     * @return LocalDateTime representing the Policy Expiration Date
     */
    protected LocalDateTime movePolicyToSTG3Renewal(String policyNumber){

        setAAAMembershipErrorStatus(policyNumber);

        setAAABestMemberStatus(policyNumber, AAAMembershipQueries.AAABestMembershipStatus.ERROR_STG2);

        LocalDateTime policyExpirationDate = getPolicyExpirationDate(policyNumber);

        generatePreSTG3RenewalImage(policyExpirationDate);

        executeSTG3Jobs(policyExpirationDate);

        generatePostSTG3ReportsAndRating(policyExpirationDate);

        return policyExpirationDate;
    }



    /**
     * Creates a policy with the intention of going through BML.
     * @param inputQuoteMemberNumber If blank, will set No to AAAMember during quote.
     * @return Policy Number
     * @throws NotImplementedException when evaluating policy types for keypath generation if no match.
     */
     private String createBMLPolicy(String inputQuoteMemberNumber) throws NotImplementedException{

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
                        AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

                // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
                keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                        AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel());

                // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
                keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                        AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());;
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
                String msg = "createBMLPolicy does not implement policy type: [" + getPolicyType().getShortName() +
                        "] Keypaths must be mapped for the type for CurrentAAAMembership and AAAMembership Number";
                throw new NotImplementedException(msg);
            }
        }

        // Create testData from keypaths //
        TestData testData;

        boolean hasMemberNumber = !inputQuoteMemberNumber.isEmpty();

        // When the commented out code below is ready to be used, this code must be removed.
        testData = getAAAMemberPolicyTestData(keypathCurrentMember, keypathMemberNum, inputQuoteMemberNumber);

         /* // The else statement below is an untested code path for integrating with a future story.
        if (hasMemberNumber) {
            testData = getAAAMemberPolicyTestData(keypathCurrentMember, keypathMemberNum, inputQuoteMemberNumber);
        }
        else{
            // This is an untested path put in place for integrating with another story.
            testData = getNonAAAMemberPolicyTestData(keypathCurrentMember, keypathMemberNum);
        }
        */

        // Create the policy and save policy number //
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy(testData);

        // Set error status if necessary //
        if (hasMemberNumber) {
            setAAAMembershipErrorStatus(policyNumber);
        }

        return policyNumber;
    }

    /**
     * Used to get test data when AAA Member Number provided
     * @param keypathCurrentMember Describes path to find Current Member on page. <br>
     * EX: "GeneralTab|AAAProductOwned|Current AAA Member" <br>
     * <br>
     * @param keypathMemberNum Describes path to find AAA Member Number on page. <br>
     * EX: "GeneralTab|AAAProductOwned|Membership Number" <br>
     * <br>
     * @param aaaMemberNumber The AAA Membership Number value you want to set in the PAS UI. <br>
     * EX: "9999994444444440"
     * @return TestData that ensures Current AAA Member to Yes and automation uses specific Member Number
     */
    private TestData getAAAMemberPolicyTestData
            (String keypathCurrentMember, String keypathMemberNum, String aaaMemberNumber) {

        return getPolicyTD()
                .adjust(keypathCurrentMember, "Yes")
                // Use different valid membership number so we can validate BML/Elastic response overrides this.
                .adjust(keypathMemberNum, aaaMemberNumber);
    }

    /**
     * Used to get test data when no AAA Member Number provided
     * @param keypathCurrentMember Describes path to find Current Member on page. <br>
     * EX: "GeneralTab|AAAProductOwned|Current AAA Member" <br>
     * @param keypathMemberNum Describes path to find AAA Member Number on page. <br>
     * EX: "GeneralTab|AAAProductOwned|Membership Number" <br>
     * <br>
     * @return TestData that sets Current AAA Member to No and hides any member number the default TD would have entered.
     */
    private TestData getNonAAAMemberPolicyTestData (String keypathCurrentMember, String keypathMemberNum)
            throws NotImplementedException{

        throw new NotImplementedException("This is an untested path put in place for integrating with another BML story. " +
                "It may work out of the box but has not been tested yet.");
        /*
        return getPolicyTD()
                .adjust(keypathCurrentMember, "No")
                .mask(keypathMemberNum);
        */
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

        LocalDateTime reportServices = getTimePoints().getRenewCheckUWRules(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(reportServices);

        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);
    }
}
