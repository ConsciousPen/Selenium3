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
     * PAS-29098 - Order and receipt dates are not updated for CA auto policies after internal claims are ordered during renewal.
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
    /**
     * PAS-23977 - END: Reconcile Claim # Formats (CLUE and CAS)
     *
     * @name Test Offline STUB/Mock: reconcile permissive use claims when driver/named insured is added and compare of CLUE claim from newly added driver to existing PU Yes claim on FNI .
     * @scenario Test Steps: See Template For Details
     * @details Clean Path. Expected Result is that PU claim will be move from the FNI to the newly added driver and only claim numbers will be compared ignoring the format differences.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-22172")
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
    /**
     * PAS-23977 - END: Reconcile Claim # Formats (CLUE and CAS)
     *
     * @name Test Offline STUB/Mock: reconcile permissive use claims when driver/named insured is added and compare of CLUE claim from newly added driver to existing PU Yes claim on FNI .
     * @scenario Test Steps: See Template For Details
     * @details Clean Path. Expected Result is that PU claim will be move from the FNI to the newly added driver and only claim numbers will be compared ignoring the format differences.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24587")
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24587")
    public void pas24587_ClueReconcilePUAFRUserFlagged(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas24587_ClueReconcilePUAFRUserFlagged();
    }

    /**
     * @author Kiruthika Rajendran
     * PAS-25463 - UI-CA: do NOT Show Permissive Use Indicator on Driver Tab (non-"claim" activity) (by source and type)
     * @name Test Offline STUB/Mock: validate permissive use indicator when driver/named insure is added
     * @scenario Test Steps:
     * 1. Create a quote with 2 drivers and named insured driver1  has the following activies
     * - Company and Customer input (Type other than Accident) - PU indicator do not show up
     * - Company and Customer input (Type as Accident) - - PU indicator shows up
     * - MVR claims - PU indicator do not show up
     * 2. Bind the policy
     * 3. Initiate the first endorsement
     * 4. Validate the driver 1 named insure has following activties
     * - Company and Customer input (Type other than Accident) - PU indicator do not show up
     * - Company and Customer input (Type as Accident) - - PU indicator shows up
     * - MVR claims - PU indicator do not show up
     * 5. Bind the endorsement
     * @details Clean Path. Expected Result is that Permissive Use Indicator on Driver Tab will not show up for non "claim" activity
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-25463")
    public void pas25463_ViolationsMVRPUIndicatorCheck(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas25463_ViolationsMVRPUIndicatorCheck();
    }

    /**
     * @author Mantas Garsvinskas
     * PAS-25162 - UI-CA-CAS: make sure ???MATCHED??? FNI claims do not show PU YES unless set by user
     * @name Test Offline Claims Permissive Use Indicator defaulting Rules
     * @scenario Test Steps:
     * 1. Create a Policy with 2 drivers: FNI and 1 additional
     * 2. Move time to R-63 and run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 3. Create CAS Response File with required Claims (all claims permissiveUse = Y)
     * 3.1. --DL matched Claim
     * 3.2. --COMP matched Claim
     * 3.3. --LASTNAME_FIRSTNAME_DOB matched Claim
     * 3.4. --LASTNAME_FIRSTNAME_YOB matched Claim
     * 3.5. --LASTNAME_FIRSTNAME matched Claim
     * 3.6. --LASTNAME_FIRSTINITIAL_DOB matched Claim
     * 3.7. --NO_MATCH not matched, but permissiveUse = Y, so PERMISSIVE_USE matched Claim;
     * 4. Move Time to R-46 and run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 5. Retrieve policy and enter renewal image
     * 6. Verify all Claims: 'Permissive Use Loss?' flag is set according to defaulting rules
     * 7. Accept a payment and renew the policy
     * --Next steps will be added after PAS-26322
     * 8. Move time to R2-63 and run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 9. Create CAS Response File with required Claims
     * 9.1. --EXISTING_MATCH matched Claims: Previously was PU = Y, Now PU = N, and viceversa
     * 10. Move Time to R-46 and run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 11. Retrieve policy and enter renewal image
     * 12. Verify all Claims: 'Permissive Use Loss?' flag is set according to defaulting rules (EXISTING_MATCH retaining same value as before)
     * @details Clean Path. Expected Result is that 'Permissive Use Loss' is defaulted to 'Yes' only for PU Claims (Existing Matches as well)
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-25162")
    public void pas25162_permissiveUseIndicatorDefaulting(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas25162_permissiveUseIndicatorDefaulting();
    }

    /**
     * @author Kiruthika Rajendran
     * PAS-20828 - CA: Product Determination Cannot be Influenced by Permissive Use Claims
     * @name Test Offline Claims Product Determination for the Permissive Use Claims
     * @scenario Test Steps:
     * 1. Create a quote with 1 driver with following condition
     * - Change the driving experience as 5 (greater than 3)
     * - no activity is added
     * 2. Navigate to P&C tab and assert the product as 'Select'
     * 3. Navigate back to driver tab and change to the below data
     * - keep the driving experience as 5 (greater than 3)
     * - Add one company activity (At fault Injury)
     * - Mark PU as 'No' in the above activity
     * 4. Navigate to P&C tab and assert that product as 'Choice'
     * 5. Navigate back to driver tab and change to the below data
     * - Keep the driving experience as 5 (greater than 3)
     * - Update the activity with PU as 'Yes'
     * 6. Navigate to P&C tab and assert that product as 'Select'
     * 7. Navigate back to driver tab and change to the below data
     * - Change the driving experience as 1 (lesser than 3)
     * - Keep the activity with PU as Yes
     * 8. Navigate to P&C tab and assert the product as 'Choice'
     * 9. Navigate back to driver tab and change to the below data
     * - Change the driving experience as 5 back (greater than 3)
     * - Keep the activity with PU as Yes
     * 10. Navigate to P&C tab and assert the product as 'Select'
     * 11. Navigate back to driver tab and change to the below data
     * *         - Keep the driving experience as 5 back (greater than 3)
     * *         - Change the activity with PU as No
     * 12. Navigate to P&C tab and assert the product as 'Choice'
     * 13. Save and Exit
     * @details Clean Path. Expected Result is that Product Determination is not influenced when 'Permissive Use Loss' is defaulted to 'Yes'
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-20828")
    public void pas20828_productDetermineWithPUClaims(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas20828_productDetermineWithPUClaims();
    }

    /**
     * @author Chris Johns
     * @author Kiruthika Rajendran
     * PAS-24652 - CHANGE FNI - General Tab (CA): move PU Yes claims when FNI changed via "dropdown" (endorsement and quote) (changed to FNI already exists as driver)
     * PAS-25271 - DRIVER TAB: make "rel. to first named insured" NOT editable for existing driver
     * @name Test Offline STUB/Mock: validate permissive use claims 'move' to new FNI when FNI is changed to existing FNI on general tab
     * @scenario New Business and Endorsement: See Template For Details and steps
     * @details Clean Path. Expected Result is that PU claim will be move from the FNI to the newly added driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24652")
    public void pas24652_ChangeFNIGeneralTabNBEndorsement(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas24652_ChangeFNIGeneralTabNBEndorsement();
    }

    /**
     * @author Chris Johns
     * @author Kiruthika Rajendran
     * PAS-22172 - END - CAS: reconcile permissive use claims when driver/named insured is added (avail for rating)
     * PAS-24652 - CHANGE FNI - General Tab (CA): move PU Yes claims when FNI changed via "dropdown" (endorsement and quote) (changed to FNI already exists as driver)
     * PAS-25271 - DRIVER TAB: make "rel. to first named insured" NOT editable for existing driver
     * @name Test Offline STUB/Mock: validate permissive use claims 'move' to new FNI when FNI is changed to existing FNI on general tab
     * @scenario Renewal: See Template For Details and steps
     * @details Clean Path. Expected Result is that PU claim will be move from the FNI to the newly added driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24652")
    public void pas24652_ChangeFNIGeneralTabRenewal(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas24652_ChangeFNIGeneralTabRenewal();
    }

    /**
     * @author Chris Johns
     * PAS-28399 -CHANGE FNI - General Tab: don't allow if "changed to FNI" not correctly set up as driver
     * @name Restring FNI change on general tab when NI is not a Driver
     * @scenario NB and Enrosement
     * 1. Initiate a quote with 3 NI and 2 Drivers
     * 2. Change the FNI to the NI that is NOT a driver
     * 3. Pop-up Error message stops the action: "The select named insured has not been established as a ???named insured driver??? on the driver tab.???
     * 4. Bind the policy and initiate an endorsement OR Renewal
     * 5. Change the FNI to the NI that is NOT a driver
     * 6. Pop-up Error message stops the action: "The select named insured has not been established as a ???named insured driver??? on the driver tab.???
     * 7. Add third NI as a driver
     * 8. Change the FNI to newly added driver
     * 9. Pop-up Error does NOT appear and does not stop the action
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24652")
    public void pas28399_RestrictChangeFNIGeneralTabEndorsement(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas28399_RestrictChangeFNIGeneralTab("ENDORSEMENT");
    }

    /**
     * @author Chris Johns
     * PAS-28399 - CHANGE FNI - General Tab: don't allow if "changed to FNI" not correctly set up as driver
     * @name Restring FNI change on general tab when NI is not a Driver
     * @scenario NB and Renewal: Steps Mentioned above: Renewal flow
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24652")
    public void pas28399_RestrictChangeFNIGeneralTabRenewal(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas28399_RestrictChangeFNIGeneralTab("RENEWAL");
    }

    /**
     * @author Kiruthika Rajendran
     * PAS-27908 - PROD ELIGIBILITY: update uw rule so PU YES claims not counted (10015015 - select) (common code, fix all 4)
     * @name Test Offline Claims: validate UW rules is not counted when PU is Yes
     * @scenario Test Steps:
     * Scenario1 - CA Select Quote
     * 1) In a CA Select quote, Add a rated driver
     * 2) Override the product Choice to Select
     * 3) Order the Clue and MVR report in DAR page
     * 4) Navigate back to driver tab and add the below company activities (include in points)
     * - two at fault accident with injury with include in points
     * - one at fault accident, no injury >$1000
     * - one minor violations
     * 5) Calculate the Premium and Order report and proceed to bind the quote
     * 6) The below overrides will get triggered and cancel it
     * Rule1 - 10015021 -- Add claim as the combination of more than 2 Select dsr points with the combination of minor violations and at fault accidents
     * Rule2 - 10015021 -- This rule fires when there is more than one at-fault injury accidents
     * Rule3 - 10051023 -- This rule fires when there is more than 2 at-fault accidents in past 3 years
     * 7) Navigate back to driver tab and mark PU flag as Yes for Claims
     * 8) Calculate the Premium and Order report and proceed to bind the quote
     * 9) Verify the above overrides should not show up and it should bind the quote
     * <p>
     * Scenario2 - CA Select Endorsement
     * 1) In Endorsement, make sure there are two named insured in the General tab
     * 2) Switch the FNI to second named insured
     * 3) Navigate to Driver tab and add the second named insured as the driver (FNI) (include in points)
     * 4) Add the below company activities to the new driver which is a FNI
     * - two at fault accident with injury with include in points
     * - one at fault accident, no injury >$1000
     * - one minor violations
     * 5) Calculate the Premium and Order report and proceed to bind the endorsement
     * 6) The below overrides will get triggered and cancel it
     * Rule1 - 10015021 -- Add claim as the combination of more than 2 Select dsr points with the combination of minor violations and at fault accidents
     * Rule2 - 10015021 -- This rule fires when there is more than one at-fault injury accidents
     * Rule3 - 10051023 -- This rule fires when there is more than 2 at-fault accidents in past 3 years
     * 7) Navigate back to driver tab and mark PU flag as Yes for Claims
     * 8) Calculate the Premium and Order report and proceed to bind the endorsement
     * 9) Verify the above overrides should not show up and it should bind the endorsement
     * @details Clean Path. Expected Result is that UW override rules does not show when PU flag is set as Yes
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-27908")
    public void pas27908_UpdateUWRulesWithPUFlag(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas27908_UpdateUWRulesWithPUFlag();
    }

    /**
     * @author Saranya Hariharan
     * PAS-27226: CA Mature Driver Discount doesn't work according to rules
     * @name Validate MDD does not gets applied to the Driver who has violated the Underlying rules.
     * @scenario NB and Endorsement: See Template For Details and steps
     * @details Clean Path. Expected Result is that Mature Driver Discount does not get applied if there are not according to discount eligibility rules.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-27226")
    public void pas27226_MatureDriverDiscount(@Optional("CA") @SuppressWarnings("unused") String state) {
        pas27226_MatureDriverDiscount();
    }
}
