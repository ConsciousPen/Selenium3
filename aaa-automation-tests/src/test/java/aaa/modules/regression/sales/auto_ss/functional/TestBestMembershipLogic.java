package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
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
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

@StateList(statesExcept = Constants.States.CA)
public class TestBestMembershipLogic extends TestBestMembershipLogicTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-15944")
    public void pas15944_BML_Ignores_termExpirationDate_for_Active_Status(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate before policy effective date.

        /*--Step 2--*/
        String policyNumber = createDefaultYesAAAMembershipPolicy();

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-15944")
    public void pas15944_BML_Valid_Expiration_Date_And_TransferIn_Status(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate after policy effective date.

        /*--Step 2--*/
        String policyNumber = createDefaultYesAAAMembershipPolicy();

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-15944")
    public void pas15944_BML_Invalid_Expiration_Date_And_TransferIn_Status(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for TRANSFER-IN policy/role status and termExpirationDate before policy effective date.

        /*--Step 2--*/
        String policyNumber = createDefaultYesAAAMembershipPolicy();

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-15944")
    public void pas15944_BML_Valid_Expiration_Date_And_TransferIn_Status_STG2(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultYesAAAMembershipPolicy();

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-15944")
    public void pas15944_BML_Invalid_Expiration_Date_And_TransferIn_Status_STG2(@Optional("") String state) {

        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultYesAAAMembershipPolicy();

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-15944")
    public void pas15944_BML_Valid_Expiration_Date_And_TransferIn_Status_STG3(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultYesAAAMembershipPolicy();

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-15944")
    public void pas15944_BML_Invalid_Expiration_Date_And_TransferIn_Status_STG3(@Optional("") String state) {

        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultYesAAAMembershipPolicy();

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-15944")
    public void pas15944_BML_Valid_Expiration_Date_And_TransferIn_Status_STG4(@Optional("") String state) {
        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultYesAAAMembershipPolicy();

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-15944")
    public void pas15944_BML_Invalid_Expiration_Date_And_TransferIn_Status_STG4(@Optional("") String state) {

        /*--Step 1--*/
        // Setup SOAPUI Mocking
        // Modify the enterpriseSearchService.enterpriseCustomerDetailsSearchUri in admin to point at the mock.
        // Set Mock for INACTIVE status.

        /*--Step 2--*/
        String policyNumber = createDefaultYesAAAMembershipPolicy();

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

    /**
     * Membership = Yes; BML = Found; RMS = Active; Discount = Yes;
     * 12min Test Run.
     * @param state Initials for optional state to pull Test Data from.
     * @author Tyrone Jemison
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-14048: STG3/STG4 match STG1/STG2")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void pas14048_TestScenario1(@Optional("") String state) {
        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1980");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Yes");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), ACTIVE_MEMBERSHIP_NUMBER);
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
    }

    /**
     * Membership = Yes; BML = Not Found; RMS = Inactive; Discount3 = Yes; Discount4 = No;
     * 12min Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario2(@Optional("AZ") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Yes");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), INACTIVE_BML_MEMBERSHIP_NUMBER);
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Inactive, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.Inactive, getPolicyType().getShortName());
    }

    /**
     * Membership = Yes; BML = Found; RMS = Inactive; Discount3 = Yes; Discount4 = No;
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario3(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Yes");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), INACTIVE_BML_MEMBERSHIP_NUMBER);
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Inactive, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.Inactive, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = Yes; BML = Not Found; RMS = Active; Discount = Yes;
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario4(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Yes");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), ACTIVE_MEMBERSHIP_NUMBER);
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = No; BML = Found; RMS = Active; Discount = Yes;
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario5(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "No");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = No; BML = Not Found; Discount = No;
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario6(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "No");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.Inactive, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.Inactive, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = No; BML = Found; RMS = Inactive; Discount = No;
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario7(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "No");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.Inactive, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.Inactive, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = Pending; BML = Found; RMS = Active; Discount = Yes;
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario8(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Membership Pending");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = Pending; BML = Not Found; Discount3 = Pending; Discount4 = No
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario9(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Membership Pending");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.PENDING, RMSStatus.NA, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.NA, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = Pending; BML = Found; RMS = Inactive; Discount3 = Pending; Discount4 = No
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario10(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Membership Pending");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.PENDING, RMSStatus.Inactive, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.Inactive, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = Override Term; BML = Found; RMS = Active; Discount = Yes
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario11(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Membership Override");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.OVERRIDE_TYPE.getLabel(), "Term");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBER_SINCE_DATE.getLabel(), "01/01/2000");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.YES, RMSStatus.Active, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = Override Term; BML = Not Found; Discount = No
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario12(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Membership Override");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.OVERRIDE_TYPE.getLabel(), "Term");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBER_SINCE_DATE.getLabel(), "01/01/2000");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.OVERRIDE_TERM, RMSStatus.NA, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.NA, getPolicyType().getShortName());
    }

    /**
     * @Scenairo Membership = Override Term; BML = Found; RMS = Inactive; Discount = No
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario13(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Membership Override");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.OVERRIDE_TYPE.getLabel(), "Term");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBER_SINCE_DATE.getLabel(), "01/01/2000");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.OVERRIDE_TERM, RMSStatus.Inactive, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.NO, RMSStatus.Inactive, getPolicyType().getShortName());
    }

    /**
     * Membership = Override Life; Discount = Yes
     * 15 Test Run.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14048")
    public void PAS14048_TestScenario14(@Optional("") String state) {

        TestData defaultTestData = getPolicyTD().resolveLinks();
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.FIRST_NAME.getLabel(), "JOHN");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.LAST_NAME.getLabel(), "SHEPARD");
        defaultTestData = adjustTD(defaultTestData, PrefillTab.class, AutoSSMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1988");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Membership Override");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.OVERRIDE_TYPE.getLabel(), "Life");
        defaultTestData = adjustTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBER_SINCE_DATE.getLabel(), "01/01/2000");
        defaultTestData = maskTD(defaultTestData, GeneralTab.class, AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.GENDER.getLabel(), "Male");
        defaultTestData = adjustTD(defaultTestData, DriverTab.class, AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), "Divorced");
        defaultTestData = defaultTestData.resolveLinks();

        LocalDateTime _policyExpirationDate = doTestReturnPolicyNumber(defaultTestData);
        String _policyNumber = scrapePolicyNumberandExit();
        movePolicyToSTG3Renewal(_policyNumber);
        doValidations(_policyNumber, MembershipStatus.OVERRIDE_LIFE, RMSStatus.NA, getPolicyType().getShortName());
        movePolicyToSTG4Renewal(_policyNumber, _policyExpirationDate);
        doValidations(_policyNumber, MembershipStatus.OVERRIDE_LIFE, RMSStatus.NA, getPolicyType().getShortName());
    }
}
