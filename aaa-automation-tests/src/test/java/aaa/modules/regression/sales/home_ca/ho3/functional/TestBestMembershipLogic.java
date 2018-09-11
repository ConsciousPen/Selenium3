package aaa.modules.regression.sales.home_ca.ho3.functional;

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

import java.time.LocalDateTime;

@StateList(states = Constants.States.CA)
public class TestBestMembershipLogic extends TestBestMembershipLogicTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Returns Elastic Search Active Policy at STG1 ignoring if PolicyEffectiveDate is less than Elastic termExpirationDate- PAS-15944
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
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: STG1 BML ignores expiration date on active status")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-15944")
    public void pas15944_BML_Ignores_termExpirationDate_for_Active_Status(@Optional("") String state) {
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
     * @name BML Returns Elastic Search TRANSFER-IN Policy at STG1 if PolicyEffectiveDate is greater than Elastic termExpirationDate- PAS-15944
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
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: STG1 BML valid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-15944")
    public void pas15944_BML_Valid_Expiration_Date_And_TransferIn_Status(@Optional("") String state) {
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
     * @name BML Does not return Elastic Search TRANSFER-IN Policy at STG1 if PolicyEffectiveDate is less than Elastic termExpirationDate- PAS-15944
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
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: STG1 BML invalid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-15944")
    public void pas15944_BML_Invalid_Expiration_Date_And_TransferIn_Status(@Optional("") String state) {
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

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Returns Elastic Search TRANSFER-IN Policy at STG2 if PolicyEffectiveDate is greater than Elastic termExpirationDate- PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the Elastic Service up for mocking an invalid response.
     * 2. Create policy with valid but error status membership by overwriting status after policy created.
     * 3. Move VDM forward to NB + 15.
     * 4. Run STG1 jobs.
     * 5. Set Elastic to provide a TRANSFER-IN response with an termExpirationDate after
     *    policy effective date and TRANSFER-IN policy status and role status.
     * 6. Move VDM forward to NB + 30.
     * 7. Run STG2 jobs.
     * 8. Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = DefaultBMLResponseMemberNumber.
     * @details
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: STG2 BML valid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-15944")
    public void pas15944_BML_Valid_Expiration_Date_And_TransferIn_Status_STG2(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultFallbackPolicy();

        /*--Step 3--*/ /*--Step 4--*/
        LocalDateTime policyEffectiveDate = movePolicyToSTG1NB15(policyNumber);

        /*--Step 5--*/
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate after policy effective date.

        /*--Step 6--*/ /*--Step 7--*/
        movePolicyToSTG2NB30(policyNumber, policyEffectiveDate);

        /*--Step 8--*/
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG2);

        assertThat(AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(DefaultBMLResponseMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Does not return Elastic Search TRANSFER-IN Policy at STG2 if PolicyEffectiveDate is less than Elastic termExpirationDate- PAS-15944
     * @scenario
     * 1. *Manual Intervention Required* Set the Elastic Service up for mocking an invalid response.
     * 2. Create policy with valid but error status membership by overwriting status before policy created.
     * 3. Move VDM forward to NB + 15.
     * 4. Run STG1 jobs.
     * 5. Set Elastic to provide a TRANSFER-IN response with an termExpirationDate before
     *    policy effective date and TRANSFER-IN policy status and role status.
     * 6. Move VDM forward to NB + 30.
     * 7. Run STG2 jobs.
     * 8. Verify in DB AAAOrderMembershipNumber = DefaultFallbackMemberNumber due to fallback.
     * @details
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: STG2 BML invalid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-15944")
    public void pas15944_BML_Invalid_Expiration_Date_And_TransferIn_Status_STG2(@Optional("") String state) {

        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultFallbackPolicy();

        /*--Step 3--*/ /*--Step 4--*/
        LocalDateTime policyEffectiveDate = movePolicyToSTG1NB15(policyNumber);

        /*--Step 5--*/
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate before policy effective date.

        /*--Step 6--*/ /*--Step 7--*/
        movePolicyToSTG2NB30(policyNumber, policyEffectiveDate);

        /*--Step 8--*/
        assertThat(AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(DefaultFallbackMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Returns Elastic Search TRANSFER-IN Policy at STG3 if PolicyEffectiveDate is greater than Elastic termExpirationDate- PAS-15944
     * @scenario
     *  1. *Manual Intervention Required* Set the Elastic Service up for mocking an invalid response.
     *  2. Create policy with valid but error status membership by overwriting status after policy created.
     *  3. Move VDM forward to NB + 15.
     *  4. Run STG1 jobs.
     *  5. Move VDM forward to NB + 30.
     *  6. Run STG2 jobs.
     *  7. Set Elastic to provide a TRANSFER-IN response with an termExpirationDate after
     *     policy effective date and TRANSFER-IN policy status and role status.
     *  8. Move VDM forward to STG3 Renewal.
     *  9. Run STG3 jobs.
     * 10. Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = DefaultBMLResponseMemberNumber.
     * @details
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: STG3 BML valid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-15944")
    public void pas15944_BML_Valid_Expiration_Date_And_TransferIn_Status_STG3(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultFallbackPolicy();

        /*--Step 3--*/ /*--Step 4--*/
        LocalDateTime policyEffectiveDate = movePolicyToSTG1NB15(policyNumber);

        /*--Step 5--*/ /*--Step 6--*/
        movePolicyToSTG2NB30(policyNumber, policyEffectiveDate);

        /*--Step 7--*/
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate after policy effective date.

        /*--Step 8--*/ /*--Step 9--*/
        movePolicyToSTG3Renewal(policyNumber);

        /*--Step 10--*/
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG3);

        assertThat(AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(DefaultBMLResponseMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Does not return Elastic Search TRANSFER-IN Policy at STG3 if PolicyEffectiveDate is less than Elastic termExpirationDate- PAS-15944
     * @scenario
     *  1. *Manual Intervention Required* Set the Elastic Service up for mocking an invalid response.
     *  2. Create policy with valid but error status membership by overwriting status after policy created.
     *  3. Move VDM forward to NB + 15.
     *  4. Run STG1 jobs.
     *  5. Move VDM forward to NB + 30.
     *  6. Run STG2 jobs.
     *  7. Set Elastic to provide a TRANSFER-IN response with an termExpirationDate before
     *     policy effective date and TRANSFER-IN policy status and role status.
     *  8. Move VDM forward to STG3 Renewal.
     *  9. Run STG3 jobs.
     * 10. Verify in DB AAAOrderMembershipNumber = DefaultFallbackMemberNumber due to fallback.
     * @details
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: STG3 BML invalid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-15944")
    public void pas15944_BML_Invalid_Expiration_Date_And_TransferIn_Status_STG3(@Optional("") String state) {

        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultFallbackPolicy();

        /*--Step 3--*/ /*--Step 4--*/
        LocalDateTime policyEffectiveDate = movePolicyToSTG1NB15(policyNumber);

        /*--Step 5--*/ /*--Step 6--*/
        movePolicyToSTG2NB30(policyNumber, policyEffectiveDate);

        /*--Step 7--*/
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate before policy effective date.

        /*--Step 8--*/ /*--Step 9--*/
        movePolicyToSTG3Renewal(policyNumber);

        /*--Step 10--*/
        assertThat(AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(DefaultFallbackMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Returns Elastic Search TRANSFER-IN Policy at STG4 if PolicyEffectiveDate is greater than Elastic termExpirationDate- PAS-15944
     * @scenario
     *  1. *Manual Intervention Required* Set the Elastic Service up for mocking an invalid response.
     *  2. Create policy with valid but error status membership by overwriting status after policy created.
     *  3. Move VDM forward to NB + 15.
     *  4. Run STG1 jobs.
     *  5. Move VDM forward to NB + 30.
     *  6. Run STG2 jobs.
     *  7. Move VDM forward to STG3 Renewal.
     *  8. Run STG3 jobs.
     *  9. Set Elastic to provide a TRANSFER-IN response with an termExpirationDate after
     *     policy effective date and TRANSFER-IN policy status and role status.
     * 10. Move VDM forward to STG4 Renewal.
     * 11. Run STG4 jobs.
     * 12. Verify in DB AAABestMembershipStatus = FOUND_STG1 and AAAOrderMembershipNumber = DefaultBMLResponseMemberNumber.
     * @details
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: STG4 BML valid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-15944")
    public void pas15944_BML_Valid_Expiration_Date_And_TransferIn_Status_STG4(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultFallbackPolicy();

        /*--Step 3--*/ /*--Step 4--*/
        LocalDateTime policyEffectiveDate = movePolicyToSTG1NB15(policyNumber);

        /*--Step 5--*/ /*--Step 6--*/
        movePolicyToSTG2NB30(policyNumber, policyEffectiveDate);

        /*--Step 7--*/ /*--Step 8--*/
        LocalDateTime policyExpirationDate = movePolicyToSTG3Renewal(policyNumber);

        /*--Step 9--*/
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate after policy effective date.

        /*--Step 10--*/ /*--Step 11--*/
        movePolicyToSTG4Renewal(policyNumber, policyExpirationDate);

        /*--Step 12--*/
        assertThat(AAAMembershipQueries.getAAABestMembershipStatusFromSQL(policyNumber))
                .isNotNull().hasValue(AAAMembershipQueries.AAABestMembershipStatus.FOUND_STG4);

        assertThat(AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(DefaultBMLResponseMemberNumber);
    }

    /**
     * This test requires manual intervention so is disabled until Elastic Search mock piece put in place.
     * @author Brian Bond
     * @name BML Does not return Elastic Search TRANSFER-IN Policy at STG4 if PolicyEffectiveDate is less than Elastic termExpirationDate- PAS-15944
     * @scenario
     *  1. *Manual Intervention Required* Set the Elastic Service up for mocking an invalid response.
     *  2. Create policy with valid but error status membership by overwriting status after policy created.
     *  3. Move VDM forward to NB + 15.
     *  4. Run STG1 jobs.
     *  5. Move VDM forward to NB + 30.
     *  6. Run STG2 jobs.
     *  7. Move VDM forward to STG3 Renewal.
     *  8. Run STG3 jobs.
     *  9. Set Elastic to provide a TRANSFER-IN response with an termExpirationDate before
     *     policy effective date and TRANSFER-IN policy status and role status.
     * 10. Move VDM forward to STG4 Renewal.
     * 11. Run STG4 jobs.
     * 12. Verify in DB AAAOrderMembershipNumber = DefaultFallbackMemberNumber due to fallback.
     * @details
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "15944: STG4 BML invalid expiration date and active status")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-15944")
    public void pas15944_BML_Invalid_Expiration_Date_And_TransferIn_Status_STG4(@Optional("") String state) {

        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultFallbackPolicy();

        /*--Step 3--*/ /*--Step 4--*/
        LocalDateTime policyEffectiveDate = movePolicyToSTG1NB15(policyNumber);

        /*--Step 5--*/ /*--Step 6--*/
        movePolicyToSTG2NB30(policyNumber, policyEffectiveDate);

        /*--Step 7--*/ /*--Step 8--*/
        LocalDateTime policyExpirationDate = movePolicyToSTG3Renewal(policyNumber);

        /*--Step 9--*/
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate before policy effective date.

        /*--Step 10--*/ /*--Step 11--*/
        movePolicyToSTG4Renewal(policyNumber, policyExpirationDate);

        /*--Step 12--*/
        assertThat(AAAMembershipQueries.getAAAOrderMembershipNumberFromSQL(policyNumber))
                .isNotNull().hasValue(DefaultFallbackMemberNumber);
    }
}