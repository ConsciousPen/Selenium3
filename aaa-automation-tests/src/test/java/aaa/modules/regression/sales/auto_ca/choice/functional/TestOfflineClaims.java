package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsCATemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.CA})
public class TestOfflineClaims extends TestOfflineClaimsCATemplate {

    // NOTE: Claims Matching Logic: e2e tests should use HTTP instead of HTTPS in DB (value of Microservice propertyname ='aaaClaimsMicroService.microServiceUrl')
    // Example: http://claims-assignment.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    /**
     * @author Chris Johns
     * @author Andrii Syniagin
     * @author Kiruthika Rajendran
     * PAS-14679 - DL # matching logic
     * PAS-14058 - COMP Claims match to FNI
     * PAS-18341 - Added PermissiveUse tag to Claims Service Contract
     * PAS-18300 - PERMISSIVE_USE match to FNI when dateOfLoss param = claim dateOfLoss
     * PAS-23269 - UI-CA: Show Permissive Use Indicator on Driver Tab
     * @name Test Offline STUB/Mock Data Claims
     * @scenario Test Steps:
     * 1. Create a Policy with 3 drivers; 1 with no STUB data match, 2, and 3 with STUB data match
     * 2. Move time to R-63
     * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 4. Run Claims Offline Batch Job
     * 5. Move Time to R-46
     * 6. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 7. Retrieve policy and enter renewal image
     * 8. Verify Claim Data is applied to the correct driver
     * 9. Check for the Activity for Internal claims with PU indicator
     * 10. Close the application
     * 11. Login with privileged user L41/E34. Retrieve policy and enter renewal image
     * 12. Check for the CAS claims with PU indicator
     * 13. Move time to R-35
     * 14. Run the renewal jobs
     * 15. Pay the bill
     * 16. Move time to R
     * 17. Run the StatusJob
     * 18. Login with qa_roles/qa with F35 roles,States --> CA and UW-->01 and Billing -->01
     * 19. Initiate an endorsement
     * 20. Navigate to driver tab
     * 21. Check the PU indicator is not editable for this user group
     * @details Clean Path. Expected Result is that claims data is applied to the correct driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-14679")
    public void pas14679_CompDLPUMatchMore(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas14679_CompDLPUMatchMore();
    }

    /**
     * @author Kiruthika Rajendran
     * @author Chris Johns
     * PAS-18317 - UI-CA: do NOT Show Permissive Use Indicator on Driver Tab (non-FNI) - Overrides "PAS-23269"
     * @name Test Clue claims STUB/Mock Data Claims
     * @scenario Test Steps:
     * 1. Create a Quote with 4 drivers
     * 2. Add Company Input claim for driver4 and validate the PU indicator is NOT present
     * 3. Bind the policy
     * 4. Initiate an endorsement
     * 5. Add driver5
     * 6. Calculate the Premium and click on Validate Driving History
     * 7. Go to Driver tab and
     * 8. Check for the Activity for Clue claims with PU indicator is NOT present
     * @details Clean Path. Expected Result Permissive Use indicator NOT in Driver Activity
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-18317")
    public void pas18317_verifyPermissiveUseIndicator(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas18317_verifyPermissiveUseIndicator();
    }

    /**
     * @author Chris Johns
     * PAS-22172 - END - CAS: reconcile permissive use claims when driver/named insured is added (avail for rating)
     * @name Test Offline STUB/Mock: reconcile permissive use claims when driver/named insured is added
     * @scenario Test Steps: See Template For Details
     * @details Clean Path. Expected Result is that PU claim will be move from the FNI to the newly added driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-22172")
    public void pas22172_ReconcilePUEndorsementAFRD(@Optional("CA") @SuppressWarnings("unused") String state) {
        reconcilePUEndorsementAFRBody();
    }

    /**
     * @author Kiruthika Rajendran
     * PAS-24587 - END - User Flagged: reconcile permissive use claims when driver/named insured is added (avail for rating)
     * @name Test Offline STUB/Mock: reconcile permissive use claims when driver/named insured is added
     * @scenario Test Steps:
     * 1. Create a policy with one driver
     * 2. Move time to R-63
     * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 4. Run Claims Offline Batch Job
     * 5. Move Time to R-46
     * 6. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 7. Retrieve policy and enter renewal image
     * 8. Verify Claim Data is applied to driver1
     * 9. Check for the Activity for Internal claims with PU indicator as No
     * 10. Accept a payment and renew the policy
     * 11. Initiate an endorsement
     * 12. Change PU flag to Yes for driver1
     * 13. Add an AFR driver who's CLUE report will return a claim that matches one of the PU claims on the FNI
     * 14. Calculate Premium and Order CLUE report
     * 15. Validate the Internal claims is dropped from driver1 and assigned to driver2 as CLUE claims
     * @details Clean Path. Expected Result is that internal claims will be move from the FNI to the newly added driver when Agent marks the PU as 'Yes'
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-24587")
    public void pas24587_CASClueReconcilePUAFRUserFlagged(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas24587_CASClueReconcilePUAFRUserFlagged();
    }

    /**
     * @author Kiruthika Rajendran
     * PAS-24587 - END - User Flagged: reconcile permissive use claims when driver/named insured is added (avail for rating)
     * @name Test Offline STUB/Mock: reconcile permissive use claims when driver/named insured is added
     * @scenario Test Steps:
     * 1. Create a policy with 2 drivers and driver2 has CLUE claims
     * 2. Initiate first endorsement
     * 3. Reassign the CLUE claims to driver1 and remove driver2
     * 4. Mark the PU flag as yes in driver1 clue claim
     * 5. Bind the endorsement
     * 6. Initiate second endorsement
     * 7. Add driver2
     * 8. Calculate Premium and order CLUE report
     * 9. Validate the driver2 has assigned the CLUE claim back and dropped from driver1
     * @details Clean Path. Expected Result is that internal claims will be move from the FNI to the newly added driver when Agent marks the PU as 'Yes'
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-24587")
    public void pas24587_ClueReconcilePUAFRUserFlagged(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas24587_ClueReconcilePUAFRUserFlagged();
    }

    /**
     * @author Kiruthika Rajendran
     * PAS-25463 - UI-CA: do NOT Show Permissive Use Indicator on Driver Tab (non-"claim" activity) (by source and type)
     * @name Test Offline STUB/Mock: validate permissive use indicator when driver/named insured is added
     * @scenario Test Steps:
     * 1. Create a quote with 2 drivers and named insured driver1  has the following activies
     *        - Company and Customer input (Type other than Accident) - PU indicator do not show up
     *        - Company and Customer input (Type as Accident) - - PU indicator shows up
     *        - MVR claims - PU indicator do not show up
     * 2. Bind the policy
     * 3. Initiate the first endorsement
     * 4. Validate the driver 1 named insurance has following activties
     *        - Company and Customer input (Type other than Accident) - PU indicator do not show up
     *        - Company and Customer input (Type as Accident) - - PU indicator shows up
     *        - MVR claims - PU indicator do not show up
     * 5. Bind the endorsement
     * @details Clean Path. Expected Result is that Permissive Use Indicator on Driver Tab will not show up for non "claim" activity
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-25463")
    public void pas25463_ViolationsMVRPUIndicatorCheck(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas25463_ViolationsMVRPUIndicatorCheck();
    }
}
