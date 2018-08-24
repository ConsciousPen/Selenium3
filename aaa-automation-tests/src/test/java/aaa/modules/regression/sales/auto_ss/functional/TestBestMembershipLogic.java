package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
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
// Use statesExcept for SS
@StateList(statesExcept = Constants.States.CA)
public class TestBestMembershipLogic extends AutoSSBaseTest {

    // Global test fields //

    // Must be mocked in the Elastic Search response but point to a valid Stub Retrieve Member Summary member number.
    private final String ExpectedElasticResponseMemberNumber = "9436258506738011";
    // Must be a valid Stub Retrieve Member Summary member number but not the same as what comes back from Elastic Mock.
    private final String InitialEnteredMemberNumber = "9999994444444440";

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Returns Elastic Search Active Policy at NB 15 if PolicyEffectiveDate is greater than Elastic termExpirationDate- PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the Elastic Service up for mocking for an expiration date after
     *     policy effective date and ACTIVE policy status and role status.
     * 2. Create policy with valid but error status membership by overwriting status after policy created.
     * 3. Move VDM forward to NB + 15.
     * 4. Run STG1 jobs.
     * 5. Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = ExpectedElasticResponseMemberNumber.
     * @details
     */
    @Parameters({"state"})
    @Test(/*enabled = false,*/ groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: NB15 BML valid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17193")
    public void pas15944_BML_NB15_Valid_Expiration_Date_And_Active_Status(@Optional("") String state) {
        /*--Step 1--*/
        log.info("Step 1: *Manual Intervention Required* Set the Elastic Service up for mocking for an expiration date after" +
                " policy effective date and ACTIVE policy status and role status.");
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.

        /*--Step 2--*/ /*--Step 3--*/
        String policyNumber = CreatePolicyAndMoveToNB15();

        /*--Step 4--*/
        log.info("Step 4: Run STG1 jobs.");
        STG1STG2JobExecute();

        /*--Step 5--*/
        log.info("Step 5: Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = ExpectedElasticResponseMemberNumber.");

        assertThat(AAAMembershipQueries.GetAAABestMembershipStatusFromSQL(policyNumber))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG1);

        assertThat(AAAMembershipQueries.GetAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(ExpectedElasticResponseMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Returns Elastic Search Active Policy at NB 15 ignoring if PolicyEffectiveDate is less than Elastic termExpirationDate- PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the Elastic Service up for mocking for an expiration date before
     *     policy effective date and Active policy status and role status.
     * 2. Create policy with valid but error status membership by overwriting status after policy created.
     * 3. Move VDM forward to NB + 15.
     * 4. Run STG1 jobs.
     * 5. Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = ExpectedElasticResponseMemberNumber.
     * @details Ignores termExpirationDate and only cares about status on Active.
     */
    @Parameters({"state"})
    @Test(/*enabled = false,*/ groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: NB15 BML invalid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17193")
    public void pas15944_BML_NB15_Ignores_termExpirationDate_for_Active_Status(@Optional("") String state) {
        /*--Step 1--*/
        log.info("Step 1: *Manual Intervention Required* Set the Elastic Service up for mocking for an expiration date before" +
                " policy effective date and Active policy status and role status.");
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.

        /*--Step 2--*/ /*--Step 3--*/
        String policyNumber = CreatePolicyAndMoveToNB15();

        /*--Step 4--*/
        log.info("Step 4: Run STG1 jobs.");
        STG1STG2JobExecute();

        /*--Step 5--*/
        log.info("Step 5: Verify in DB AAAOrderMembershipNumber = InitialEnteredMemberNumber due to fall back.");

        assertThat(AAAMembershipQueries.GetAAABestMembershipStatusFromSQL(policyNumber))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG1);

        assertThat(AAAMembershipQueries.GetAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(ExpectedElasticResponseMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Returns Elastic Search TRANSFER-IN Policy at NB 15 if PolicyEffectiveDate is greater than Elastic termExpirationDate- PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the Elastic Service up for mocking for an expiration date after
     *     policy effective date and TRANSFER-IN policy status and role status.
     * 2. Create policy with valid but error status membership by overwriting status after policy created.
     * 3. Move VDM forward to NB + 15.
     * 4. Run STG1 jobs.
     * 5. Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = ExpectedElasticResponseMemberNumber.
     * @details
     */
    @Parameters({"state"})
    @Test(/*enabled = false,*/ groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: NB15 BML valid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17193")
    public void pas15944_BML_NB15_Valid_Expiration_Date_And_TransferIn_Status(@Optional("") String state) {
        /*--Step 1--*/
        log.info("Step 1: *Manual Intervention Required* Set the Elastic Service up for mocking for an expiration date after" +
                " policy effective date and TRANSFER-IN policy status and role status.");
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.

        /*--Step 2--*/ /*--Step 3--*/
        String policyNumber = CreatePolicyAndMoveToNB15();

        /*--Step 4--*/
        log.info("Step 4: Run STG1 jobs.");
        STG1STG2JobExecute();

        /*--Step 5--*/
        log.info("Step 5: Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = ExpectedElasticResponseMemberNumber.");

        assertThat(AAAMembershipQueries.GetAAABestMembershipStatusFromSQL(policyNumber))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG1);

        assertThat(AAAMembershipQueries.GetAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(ExpectedElasticResponseMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Does not return Elastic Search TRANSFER-IN Policy at NB 15 if PolicyEffectiveDate is less than Elastic termExpirationDate- PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the Elastic Service up for mocking for an expiration date before
     *     policy effective date and TRANSFER-IN policy status and role status.
     * 2. Create policy with valid but error status membership by overwriting status after policy created.
     * 3. Move VDM forward to NB + 15.
     * 4. Run STG1 jobs.
     * 5. Verify in DB AAAOrderMembershipNumber = InitialEnteredMemberNumber due to fallback.
     * @details
     */
    @Parameters({"state"})
    @Test(/*enabled = false,*/ groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: NB15 BML invalid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-17193")
    public void pas15944_BML_NB15_Invalid_Expiration_Date_And_TransferIn_Status(@Optional("") String state) {
        /*--Step 1--*/
        log.info("Step 1: *Manual Intervention Required* Set the Elastic Service up for mocking for an expiration date before" +
                " policy effective date and TRANSFER-IN policy status and role status.");
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.

        /*--Step 2--*/ /*--Step 3--*/
        String policyNumber = CreatePolicyAndMoveToNB15();

        /*--Step 4--*/
        log.info("Step 4: Run STG1 jobs.");
        STG1STG2JobExecute();

        /*--Step 5--*/
        log.info("Step 5: Verify in DB AAAOrderMembershipNumber = InitialEnteredMemberNumber due to fall back.");

        assertThat(AAAMembershipQueries.GetAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(InitialEnteredMemberNumber);
    }

    private String CreatePolicyAndMoveToNB15(){

        /*--Step 2--*/
        log.info("Step 2: Create policy with valid but error status membership by overwriting status after policy created.");
        String policyNumber = CreateAutoSSPolicy();
        SetErrorStatus(policyNumber);

        /*--Step 3--*/
        log.info("Step 3: Move VDM forward to NB + 15.");
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
                // Use different valid membership number so we can validate BML/Elastic response overrides this.
                .adjust(keypathMemberNum, InitialEnteredMemberNumber);

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
