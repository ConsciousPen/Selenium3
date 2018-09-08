package aaa.modules.regression.sales.auto_ca.choice.functional;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.modules.regression.sales.template.functional.TestBestMembershipLogicTemplate;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMembershipQueries;
import aaa.main.modules.policy.PolicyType;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestBestMembershipLogic extends TestBestMembershipLogicTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Returns Elastic Search Active Policy at NB 15 ignoring if PolicyEffectiveDate is less than Elastic termExpirationDate- PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the Elastic Service up for mocking for a termExpirationDate before
     *     policy effective date and Active policy status and role status.
     * 2. Create policy with valid but error status membership by overwriting status after policy created.
     * 3. Move VDM forward to NB + 15.
     * 4. Run STG1 jobs.
     * 5. Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = DefaultBMLResponseMemberNumber.
     * @details Ignores termExpirationDate and only cares about status on Active.
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: NB15 BML invalid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-17193")
    public void pas15944_BML_NB15_Ignores_termExpirationDate_for_Active_Status(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate before policy effective date.

        /*--Step 2--*/
        String policyNumber = createDefaultFallbackPolicy();

        /*--Step 3--*/ /*--Step 4--*/
        movePolicyToSTG1NB15(policyNumber);

        /*--Step 5--*/
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG1);

        assertThat(AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(DefaultBMLResponseMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Returns Elastic Search TRANSFER-IN Policy at NB 15 if PolicyEffectiveDate is greater than Elastic termExpirationDate- PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the Elastic Service up for mocking for a termExpirationDate after
     *     policy effective date and TRANSFER-IN policy status and role status.
     * 2. Create policy with valid but error status membership by overwriting status after policy created.
     * 3. Move VDM forward to NB + 15.
     * 4. Run STG1 jobs.
     * 5. Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = DefaultBMLResponseMemberNumber.
     * @details
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: NB15 BML valid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-17193")
    public void pas15944_BML_NB15_Valid_Expiration_Date_And_TransferIn_Status(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate after policy effective date.

        /*--Step 2--*/
        String policyNumber = createDefaultFallbackPolicy();

        /*--Step 3--*/ /*--Step 4--*/
        movePolicyToSTG1NB15(policyNumber);

        /*--Step 5--*/
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG1);

        assertThat(AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(DefaultBMLResponseMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Does not return Elastic Search TRANSFER-IN Policy at NB 15 if PolicyEffectiveDate is less than Elastic termExpirationDate- PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the Elastic Service up for mocking for an termExpirationDate before
     *     policy effective date and TRANSFER-IN policy status and role status.
     * 2. Create policy with valid but error status membership by overwriting status after policy created.
     * 3. Move VDM forward to NB + 15.
     * 4. Run STG1 jobs.
     * 5. Verify in DB AAAOrderMembershipNumber = DefaultFallbackMemberNumber due to fallback.
     * @details
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: NB15 BML invalid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-17193")
    public void pas15944_BML_NB15_Invalid_Expiration_Date_And_TransferIn_Status(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate before policy effective date.

        /*--Step 2--*/
        String policyNumber = createDefaultFallbackPolicy();

        /*--Step 3--*/ /*--Step 4--*/
        movePolicyToSTG1NB15(policyNumber);

        /*--Step 5--*/
        assertThat(AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(DefaultFallbackMemberNumber);
    }
}