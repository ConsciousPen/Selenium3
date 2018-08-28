package aaa.modules.regression.sales.template.functional;

import aaa.admin.modules.administration.generateproductschema.defaulttabs.CacheManager;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestBestMembershipLogicTemplate extends PolicyBaseTest {

    // Global test fields //

    // Must be mocked in the Elastic Search response but point to a valid Stub Retrieve Member Summary member number.
    public final String ExpectedElasticResponseMemberNumber = "9436258506738011";
    // Must be a valid Stub Retrieve Member Summary member number but not the same as what comes back from Elastic Mock.
    public  final String InitialEnteredMemberNumber = "9999994444444440";

    protected String CreatePolicyAndMoveToNB15(){

        /*--Step 2--*/
        log.info("Step 2: Create policy with valid but error status membership by overwriting status after policy created.");
        String policyNumber = "Not Set";

        switch (getPolicyType().getShortName())
        {
            case "AutoCA": {
                // Not Implemented
                break;
            }
            case "AutoSS": {
                policyNumber = CreateAutoSSPolicy();
                break;
            }

            case "HomeSS_HO3": {
                // Not Implemented
                break;
            }

            case "HomeCA_HO3": {
                policyNumber = CreateHomeCAPolicy();
                break;
            }
        }

        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        SetErrorStatus(policyNumber);

        /*--Step 3--*/
        log.info("Step 3: Move VDM forward to NB + 15.");
        MoveToNB15(policyEffectiveDate);

        /*--Step 4--*/
        log.info("Step 4: Run STG1 jobs.");
        STG1STG2JobExecute();

        return policyNumber;
    }

    private String CreateAutoSSPolicy(){
        // keypathTabSection Result: "GeneralTab|AAAProductOwned"
        String keypathTabSection = TestData.makeKeyPath(aaa.main.modules.customer.defaulttabs.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

        // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
        String keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel());

        // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
        String keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());

        TestData testData = getPolicyTD()
                .adjust(keypathCurrentMember, "Yes")
                // Use different valid membership number so we can validate BML/Elastic response overrides this.
                .adjust(keypathMemberNum, InitialEnteredMemberNumber);

        mainApp().open();
        createCustomerIndividual();
        return createPolicy(testData);
    }

    private String CreateHomeCAPolicy(){
        // keypathTabSection Result: "ApplicantTab|AAAMembership"
        String keypathTabSection = TestData.makeKeyPath(ApplicantTab.class.getSimpleName(),
                HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());

        // keypathCurrentMember Result: "GeneralTab|AAAProductOwned|Current AAA Member"
        String keypathCurrentMember = TestData.makeKeyPath(keypathTabSection,
                HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel());

        // keypathMemberNum Result: "GeneralTab|AAAProductOwned|Membership Number"
        String keypathMemberNum = TestData.makeKeyPath(keypathTabSection,
                HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel());

        TestData testData = getPolicyTD()
                .adjust(keypathCurrentMember, "Yes")
                // Use different valid membership number so we can validate BML/Elastic response overrides this.
                .adjust(keypathMemberNum, InitialEnteredMemberNumber);

        mainApp().open();
        createCustomerIndividual();
        return createPolicy(testData);
    }

    private void SetErrorStatus(String policyNumber){
        AAAMembershipQueries.UpdateAAAMembershipStatusInSQL(policyNumber, AAAMembershipQueries.AAAMembershipStatus.Error);

        // Clear cache to avoid BML result getting overridden by the original number.
        adminApp().open();
        new CacheManager().goClearCacheManagerTable();
    }

    private void MoveToNB15(LocalDateTime policyEffectiveDate){
        log.info("Policy Effective Date: " + policyEffectiveDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(15));
    }

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
