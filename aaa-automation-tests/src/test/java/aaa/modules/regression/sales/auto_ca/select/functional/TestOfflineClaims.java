package aaa.modules.regression.sales.auto_ca.select.functional;

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
        return PolicyType.AUTO_CA_SELECT;
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-14679")
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-18317")
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-14679")
    public void pas22172_ReconcilePUEndorsementAFRD(@Optional("CA") @SuppressWarnings("unused") String state) {
        reconcilePUEndorsementAFRBody();
    }
}
