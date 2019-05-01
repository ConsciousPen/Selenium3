package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.helpers.claim.BatchClaimHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.utils.StateList;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@StateList(states = {Constants.States.AZ})
public class TestOffLineClaims extends TestOfflineClaimsTemplate {

    // NOTE: Claims Matching Logic: e2e tests should use HTTP instead of HTTPS in DB (value of Microservice propertyname ='aaaClaimsMicroService.microServiceUrl')
    // Example: http://claims-assignment.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1

    private static final String NAME_DOB_CLAIMS_DATA_MODEL = "name_dob_claims_data_model.yaml";

    /**
     * @author Andrii Syniagin
     * @name Test generation cas claim reponse.
     * @details This is to verify that the framework can correctly build a CAS Response XML. Run this after making any Response or YAML changes
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @Parameters({"state"})
    public void testCreateCasResponse(@Optional("AZ") @SuppressWarnings("unused") String state) {
        BatchClaimHelper batchClaimHelper = new BatchClaimHelper(NAME_DOB_CLAIMS_DATA_MODEL, getCasResponseFileName());
        String policyNumber = "AZSS999999999";
        File claimResponse = batchClaimHelper.processClaimTemplate((response) ->
                setPolicyNumber(policyNumber, response));
        assertThat(claimResponse).exists().isFile();
        assertThat(Assertions.contentOf(claimResponse)).contains(policyNumber);
    }

    /**
     * @author Chris Johns
     * @author Andrii Syniagin
     * @author Kiruthika Rajendran
     * PAS-14679 - DL # matching logic
     * PAS-14058 - COMP Claims match to FNI
     * PAS-18341 - Added PermissiveUse tag to Claims Service Contract
     * PAS-18300 - PERMISSIVE_USE match to FNI when dateOfLoss param = claim dateOfLoss
     * @name Test Offline STUB/Mock Data Claims
     * @scenario Test Steps:
     * 1. Create a Policy with 3 drivers; 1 with no STUB data match, 2, and 3 with STUB data match
     * 2. Move time to R-63
     * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 4. Run Claims Offline Batch Job
     * 5. Move Time to R-46
     * 6. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 7. Retrieve policy and enter renewal image
     * 8. Verify Claim Data is applied to the correct driver.
     * @details Clean Path. Expected Result is that claims data is applied to the correct driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
    public void pas14679_CompDLPUMatchMore(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas14679_CompDLPUMatchMore();
    }

    /**
     * @author Kiruthika Rajendran
     * @author Chris Johns
     * PAS-14679 - DL # matching logic
     * PAS-14058 - COMP Claims match to FNI
     * PAS-18341 - Added PermissiveUse tag to Claims Service Contract
     * PAS-18300 - PERMISSIVE_USE match to FNI when dateOfLoss param = claim dateOfLoss
     * @name Test NAME and DOB Match logic Via Manual Renewal To Support Security Token Validation
     * @scenario Test Steps:
     * 1. Create a Policy with 4 drivers
     * 2. Initiate Manual Renewal
     * 3. Place the CAS Claim Response for PAS consumption
     * 4. Run Claims "claimsRenewBatchReceiveJob" Batch Job
     * 5. Retrieve policy and enter renewal image
     * 6. Verify Claim Data is applied to the correct driver.
     * @details Clean Path. Expected Result is that claims data is applied to the correct driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
    public void pas14679_CompDLPUMatchMoreManual(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas14679_CompDLPUMatchMoreManual();
    }

    /**
     * @author Kiruthika Rajendran
     * @author Chris Johns
     * PAS-8310 - LASTNAME_FIRSTNAME_DOB & LASTNAME_FIRSTNAME_YOB matches
     * PAS-17894 - LASTNAME_FIRSTNAME & LASTNAME_FIRSTINITAL_DOB matches
     * PAS-18341 - Added PermissiveUse tag to Claims Service Contract
     * @name Test Match more claims to satisfy the Name and DOB match logic LASTNAME_FIRSTNAME_DOB,  LASTNAME_FIRSTNAME_YOB
     * @scenario Test Steps:
     * 1. Create a Policy with 4 drivers
     * 2. Move time to R-63
     * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 4. Run Claims Offline Batch Job
     * 5. Move Time to R-46
     * 6. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 7. Retrieve policy and enter renewal image
     * 8. Verify Claim Data is applied to the driver3 and driver4
     * @details Clean Path. Expected Result is that claims data is applied to the correct driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
    public void pas8310_nameDOBYOBMatchMore(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas8310_nameDOBYOBMatchMore();
    }

    /**
     * @author Kiruthika Rajendran
     * @author Chris Johns
     * PAS-21821 - PAS/Microservice Security Token
     * PAS-8310 - LASTNAME_FIRSTNAME_DOB & LASTNAME_FIRSTNAME_YOB matches
     * PAS-17894 - LASTNAME_FIRSTNAME & LASTNAME_FIRSTINITAL_DOB matches
     * PAS-18341 - Added PermissiveUse tag to Claims Service Contract
     * @name Test NAME and DOB Match logic Via Manual Renewal To Support Security Token Validation
     * @scenario Test Steps:
     * 1. Create a Policy with 4 drivers
     * 2. Initiate Manual Renewal
     * 3. Place the CAS Claim Response for PAS consumption
     * 4. Run Claims "claimsRenewBatchReceiveJob" Batch Job
     * 5. Retrieve policy and enter renewal image
     * 6. Verify Claim Data is applied to the driver3 and driver4
     * @details Clean Path. Expected Result is that claims data is applied to the correct driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
    public void pas8310_nameDobYobMatchMoreManual(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas8310_nameDobYobMatchMoreManual();
    }

    /**
     * @author Mantas Garsvinskas
     * @author Chris Johns
     * PAS-14552 - INC IN RATING: Determine if Previously Unmatched but Now matched should be Included in Rating
     * PAS-18341 - Added PermissiveUse tag to Claims Service Contract
     * PAS-22026 - INC IN RATING: answer to "within prior two terms" needs to carry forward to subsequent renewals
     * @name Test Claims 'Include In Rating' determination according to Occurrence date
     * @scenario Test Steps:
     * 1. Create a Policy with 1 driver ANNUAL TERM;
     * 3. Repeat Steps below and get to the Third Renewal Image
     * 3.1. Move time to R-63
     * 3.2. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 3.3. Move Time to R-46
     * 3.4. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 4 Retrieve policy and enter 3rd renewal image
     * 5. Verify Claim Data: PAS-14552
     * 5.1 Claim1: claimNumber1 - NOT INCLUDED IN RATING dateOfLoss = Two Policy Terms - 1 day (R1-1)
     * 5.1 Claim2: claimNumber2 - INCLUDED IN RATING dateOfLoss = Two Policy Terms (R1)
     * 5.1 Claim3: claimNumber3 - INCLUDED IN RATING dateOfLoss = Current Date (R3-46)
     * 5.1 Claim4: claimNumber4 - INCLUDED IN RATING dateOfLoss = Current Date (R3-46) PERMISSIVE_USE Match Assigned to FNI
     * 6. Use above steps to move to the 5th renewal
     * 7. Retrieve policy and enter renewal image
     * 8. Verify claim data: PAS-22026
     * 8.1 Claim1: claimNumber1 - Include in Rating = NO (Did not pass last age check)
     * 8.1 Claim2: claimNumber2 - Include in Rating = NO (Maintain Agent Override)
     * 8.1 Claim3: claimNumber3 - Include in Rating = YES (Passed last Age check, still within 60 month charge window)
     * 8.1 Claim4: claimNumber4 - Include in Rating = NO (Maintain Same Day Waiver from Claim 3)
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14552")
    public void pas14552_includeClaimsInRatingDeterminationShort(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas14552_includeClaimsInRatingDetermination("Short");
    }

    /**
     * @author Chris Johns
     * PAS-14552 - INC IN RATING: Determine if Previously Unmatched but Now matched should be Included in Rating
     * PAS-18341 - Added PermissiveUse tag to Claims Service Contract
     * PAS-22026 - INC IN RATING: answer to "within prior two terms" needs to carry forward to subsequent renewals
     * @name Test Claims 'Include In Rating' determination according to Occurrence date
     * @scenario Test Steps:
     * 9. Use above test/steps to move to the 8th renewal - **3 additional renewals**
     * 10. Retrieve policy and enter renewal image
     * 11. Verify claim data: PAS-22026 - Long Scenario
     * 11.1 Claim1: claimNumber1 - Include in Rating = NO (Did not pass last age check)
     * 11.2 Claim2: claimNumber2 - Include in Rating = YES (Passed last Age check, still within 60 month charge window)
     * 11.3 Claim3: claimNumber3 - Include in Rating = YES (Passed last Age check, still within 60 month charge window)
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14552")
    public void pas14552_includeClaimsInRatingDeterminationLong(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas14552_includeClaimsInRatingDetermination("Long");
    }



    /**
     * @author Chris Johns
     * PAS-22172 - END - CAS: reconcile permissive use claims when driver/named insured is added (avail for rating)
     * @name Test Offline STUB/Mock Data Claims
     * @scenario Test Steps:
     * 1. Create a Policy with 3 drivers; FNI will get X PU Claims
     * 2. Move time to R-63 and run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 3. Create CAS Response File Thru Automation Framework - This is the 'Offline Batch Job' step
     * 4. Move Time to R-46 and run Renewal Part2 + "claimsRenewBatchReceiveJob" - X PU claims are assigned
     * 5. Retrieve policy and enter renewal image
     * 6. Verify all PU claims are assigned to the FNI
     * 7. Accept a payment and renew the policy
     * 8. Initiate an endorsement
     * 9. Add an AFR driver who's CLUE report will return a claim that matches one of the PU claims on the FNI
     * 10. Calculate premium and order CLUE report
     * 11. Navigate to the drive tab, and verify the PU claim was moved from the FNI to the newly added driver
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
    public void pas22172_ReconcilePUEndorsementAFRD(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas22172_ReconcilePUEndorsementAFRD();
    }

    /**
     * @author Kiruthika Rajendran
     * @author Chris Johns
     * @author Saranya Hariharan
     * PAS-22608 - UI-SS-CAS: Permissive Use Indicator (Driver Tab, Transaction Compare, Roll on Changes and Renewal Merge)
     * @name Test Clue claims STUB/Mock Data Claims
     * @scenario Test Steps:
     * 1. Create a Quote with 2 drivers
     * 2. Add Company Input claim for driver1 and validate the PU indicator is NOT present
     * 3. Order reports in DriverActivityReports tab
     * 4. Navigate to Driver tab
     * 5. Verify the driver1 as MVR claims with no PU indicator and driver2 as CLUE claims with no PU indicator
     * 6. Bind the policy
     * 7. Initiate an endorsement
     * 8. Validate the company Input and MVR claims doesn't show up PU indicator for driver1
     * 9. Validate the CLUE claims doesn't show up PU indicator for driver2
     * 10. Calculate the Premium and click on Validate Driving History
     * 11. Bind the endorsement
     * @details Clean Path. Expected Result Permissive Use indicator NOT in Driver Activity
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22608")
    public void pas22608_verifyPermissiveUseIndicator(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas22608_verifyPermissiveUseIndicator();
    }

    /**
     * @author Chris Johns
     * PAS-22172 - END - CAS: reconcile permissive use claims when driver/named insured is added (avail for rating)
     * PAS-24652 - CHANGE FNI - General Tab (CA): move PU Yes claims when FNI changed via "dropdown" (endorsement and quote) (changed to FNI already exists as driver)
     * @name Test Offline STUB/Mock: validate permissive use claims 'move' to new FNI when FNI is changed to existing FNI on general tab
     * @scenario Renewal: See Template For Details and steps
     * @details Clean Path. Expected Result is that PU claim will be move from the FNI to the newly added driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24652")
    public void pas24652_ChangeFNIGeneralTabRenewal(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas24652_ChangeFNIGeneralTabRenewal();
    }

    /**
     * @author Mantas Garsvinskas
     * @author Saranya Hariharan
     * PAS-25162 - UI-CA-CAS: make sure “MATCHED” FNI claims do not show PU YES unless set by user
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-25162")
    public void pas25162_permissiveUseIndicatorDefaulting(@Optional("AZ") @SuppressWarnings("unused") String state) {
        pas25162_permissiveUseIndicatorDefaulting();
    }
}
