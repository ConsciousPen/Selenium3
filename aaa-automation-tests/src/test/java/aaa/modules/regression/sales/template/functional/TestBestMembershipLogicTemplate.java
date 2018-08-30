package aaa.modules.regression.sales.template.functional;

import aaa.admin.modules.administration.generateproductschema.defaulttabs.CacheManager;
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
    protected String CreateDefaultFallbackPolicy(){
        return CreateFallbackPolicy(DefaultFallbackMemberNumber);
    }

    /**
     * Create Policy with a Fallback to specific number.
     * @param fallbackMemberNumber
     * @return Policy Number
     */
    protected String CreateFallbackPolicy(String fallbackMemberNumber){
        return CreateBMLPolicy(fallbackMemberNumber);
    }

    /**
     * Create Default Policy using Default Fallback Member Number then move to NB+15
     * @return Policy Number
     */
    protected String CreateDefaultFallbackPolicyAndMoveToNB15(){
        return CreateFallbackPolicyAndMoveToNB15(DefaultFallbackMemberNumber);
    }

    /**
     * Create Policy with a Fallback to specific number then move to NB+15
     * @param fallbackMemberNumber
     * @return Policy Number
     */
    protected String CreateFallbackPolicyAndMoveToNB15(String fallbackMemberNumber){
        String policyNumber = CreateFallbackPolicy(fallbackMemberNumber);
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        MoveToNB15(policyEffectiveDate);
        STG1STG2JobExecute();
        return policyNumber;
    }

    /**
     * Creates a policy with the intention of going through BML.
     * @param inputQuoteMemberNumber If blank, will set No to AAAMember during quote.
     * @return Policy Number
     */
     private String CreateBMLPolicy(String inputQuoteMemberNumber){

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
                String msg = "CreateBMLPolicy does not implement policy type: [" + getPolicyType().getShortName() +
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
            SetErrorStatus(policyNumber);
        }

        return policyNumber;
    }

    /**
     * Used to get test data when AAA Member Number provided
     * @param keypathCurrentMember
     * @param keypathMemberNum
     * @param aaaMemberNumber
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
     * @param keypathCurrentMember
     * @param keypathMemberNum
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
     * @param policyNumber
     */
    private void SetErrorStatus(String policyNumber){
        AAAMembershipQueries.UpdateAAAMembershipStatusInSQL(policyNumber, AAAMembershipQueries.AAAMembershipStatus.Error);

        // Clear cache to avoid BML result getting overridden by the original number.
        //adminApp().open();
        //new CacheManager().goClearCacheManagerTable();
    }

    /**
     * Logs out the policy effective date and Moves to New Business + 15 days.
     * @param policyEffectiveDate
     */
    private void MoveToNB15(LocalDateTime policyEffectiveDate){
        log.info("Policy Effective Date: " + policyEffectiveDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(15));
    }

    /**
     * Executes the required jobs for BML at STG1 (NB+15) or STG2 (NB+30).
     */
    private void STG1STG2JobExecute() {
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
        JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
        JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
        JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
        JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
        //JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
    }
}
