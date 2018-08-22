package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import java.time.LocalDateTime;

@StateList(states = Constants.States.AZ)
public class TestBestMembershipLogic extends AutoSSBaseTest {


    @Parameters({"state"})
    @Test()
    public void GetAdminPage(@Optional("") String state){
        AAAMembershipQueries.UpdateAAAMembershipStatusInSQL("AZSS952918539", AAAMembershipQueries.AAAMembershipStatus.Error);
        adminApp().open();
    }

    /**
     * This test requires manual intervention so is disabled until BML wiremock piece put in place.
     * @author Brian Bond
     * @name BML considers Expiration Date on Transfer In Status - PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the BML up for mocking.
     * 2. *Manual Intervention Required* Mock the BML service for Inactive as well as edit SQL Member Status to be Error.
     * 3. Create policy with valid but inactive membership by overwriting status after policy created.
     * 4. Move VDM forward to NB + 15.
     * 5. *Manual Intervention Required* Mock BML service for an expiration date prior to effective date and transfer-in policy status and role status.
     * 6. Verify the correct job handling occurs given response.
     * @details
     */
    @Parameters({"state"})
    @Test(/*enabled = false,*/ groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17193: MemberSinceDate in database matches stub response")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17193")
    public void pas15944_BML_NB15_Valid_Expiration_Date_And_Active_Status(@Optional("") String state) {
        // BondTODO: Update this to verify AAABestMemberShipStatus and confirm with Rajesh MembershipPolicyEntered or OrderMembershipNumber
        // BondTODO: Also check on UpdateAAAMembershipStatusInSQL() works with PS.Quote as well.
        String policyNumber = CreatePolicyAndMoveToNB15();

        /*--Step 5--*/
        log.info("Step 5: *Manual Intervention Required* Mock BML service for an expiration date prior to effective date and transfer-in status.");

        /*--Step 6--*/
        log.info("Step 6: Verify the correct job handling occurs given response.");
        STG1STG2JobExecute();
    }

    /**
     * This test requires manual intervention so is disabled until BML wiremock piece put in place.
     * @author Brian Bond
     * @name BML considers Expiration Date on Transfer In Status - PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the BML up for mocking.
     * 2. *Manual Intervention Required* Mock the BML service for Inactive as well as edit SQL Member Status to be Error.
     * 3. Create policy with valid but inactive membership by overwriting status after policy created.
     * 4. Move VDM forward to NB + 15.
     * 5. *Manual Intervention Required* Mock BML service for an expiration date prior to effective date and transfer-in policy status and role status.
     * 6. Verify the correct job handling occurs given response.
     * @details
     */
    @Parameters({"state"})
    @Test(/*enabled = false,*/ groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17193: MemberSinceDate in database matches stub response")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17193")
    public void pas15944_BML_considers_Expiration_Date_On_Transfer_In_Status(@Optional("") String state) {

        String policyNumber = CreatePolicyAndMoveToNB15();

        /*--Step 5--*/
        log.info("Step 5: *Manual Intervention Required* Mock BML service for an expiration date prior to effective date and transfer-in status.");


        /*--Step 6--*/
        log.info("Step 6: Verify the correct job handling occurs given response.");
        STG1STG2JobExecute();
    }

    private String CreatePolicyAndMoveToNB15(){
        /*--Step 1--*/
        log.info("Step 1: *Manual Intervention Required* Set the BML up for mocking.");
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.


        /*--Step 2--*/
        log.info("Step 2: *Manual Intervention Required* Mock the BML service for Inactive as well as edit SQL Member Status to be Error.");


        /*--Step 3--*/
        log.info("Step 3: Create policy with valid but inactive membership by overwriting status after policy created.");
        String policyNumber = CreateAutoSSPolicy();
        SetErrorStatus(policyNumber);

        /*--Step 4--*/
        log.info("Move VDM forward to NB + 15.");
        MoveToNB15();

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
                .adjust(keypathMemberNum, "9436258506738011");

        mainApp().open();
        createCustomerIndividual();
        return createPolicy(testData);
    }

    private void SetErrorStatus(String policyNumber){
        AAAMembershipQueries.UpdateAAAMembershipStatusInSQL(policyNumber, AAAMembershipQueries.AAAMembershipStatus.Error);
    }

    private void MoveToNB15(){
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(15));
    }

    private void MoveToNB30(){
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(30));
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
