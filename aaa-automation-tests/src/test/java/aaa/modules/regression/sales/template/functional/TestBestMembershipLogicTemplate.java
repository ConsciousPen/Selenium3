package aaa.modules.regression.sales.template.functional;

import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang3.NotImplementedException;
import toolkit.datax.TestData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestBestMembershipLogicTemplate extends PolicyBaseTest {

    // Global test fields //

    // Must be mocked in the Elastic Search response but point to a valid Stub Retrieve Member Summary member number.
    public final String DefaultBMLResponseMemberNumber = "9436258506738011";
    // Must be a valid Stub Retrieve Member Summary member number but not the same as what comes back from Elastic Mock.
    public  final String DefaultFallbackMemberNumber = "9999994444444440";

    /**
     * Create Default Policy using Default Fallback Member Number.
     * @return Policy Number
     */
    protected String createDefaultFallbackPolicy(){
        return createFallbackPolicy(DefaultFallbackMemberNumber);
    }

    /**
     * Create Policy with a Fallback to specific number.
     * @param fallbackMemberNumber Specifies an initial member number to use.
     * @return Policy Number
     */
    protected String createFallbackPolicy(String fallbackMemberNumber){
        return createBMLPolicy(fallbackMemberNumber);
    }

    /**
     * Create Default Policy using Default Fallback Member Number then move to NB+15
     * @return Policy Number
     */
    protected String createDefaultFallbackPolicyAndMoveToNB15(){
        return createFallbackPolicyAndMoveToNB15(DefaultFallbackMemberNumber);
    }

    /**
     * Create Policy with a Fallback to specific number then move to NB+15
     * @param fallbackMemberNumber Specifies an initial member number to use.
     * @return Policy Number
     */
    protected String createFallbackPolicyAndMoveToNB15(String fallbackMemberNumber){
        String policyNumber = createFallbackPolicy(fallbackMemberNumber);
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        moveJVMNumberOfDaysFromEffectiveDate(policyEffectiveDate, 15);
        executeSTG1STG2Job();
        return policyNumber;
    }

    /**
     * Move Policy with a Fallback to NB+30 <br>
     * Fallback is Insuredcd = Yes with a provided Member Number.
     * @return Policy Number
     */
    protected LocalDateTime moveFallbackPolicyToNB30(String policyNumber){

        setErrorStatus(policyNumber);

        AAAMembershipQueries.updateAAABestMembershipStatusInSQL(policyNumber,
                AAAMembershipQueries.AAABestMembershipStatus.ERROR_STG1);

        String dbPolicyEffectiveDate =
                AAAMembershipQueries.getPolicyEffectiveDateFromSQL(policyNumber).orElse("Null Value");

        LocalDateTime policyEffectiveDateTime =
                LocalDateTime.parse(dbPolicyEffectiveDate, AAAMembershipQueries.SQLDateTimeFormatter);

        moveJVMNumberOfDaysFromEffectiveDate(policyEffectiveDateTime, 30);

        executeSTG1STG2Job();

        return policyEffectiveDateTime;
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
            setErrorStatus(policyNumber);
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
     * Used to set a DB Error Status for RMS response so BML will attempt to override provided number.
     * @param policyNumber The policy number you want to update to an error status.
     */
    private void setErrorStatus(String policyNumber){
        AAAMembershipQueries.updateAAAMembershipStatusInSQL(policyNumber, AAAMembershipQueries.AAAMembershipStatus.Error);

        // Clear cache to avoid BML result getting overridden by the original number.
        //adminApp().open();
        //new CacheManager().goClearCacheManagerTable();
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
     * Executes the required jobs for BML at STG1 (NB+15) or STG2 (NB+30).
     */
    private void executeSTG1STG2Job() {
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
        JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
        JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
        JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
        JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
        //JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
    }
}
