package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.helpers.TestDataHelper;
import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.modules.regression.sales.template.functional.TestMultiPolicyDiscountAbstract;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

@StateList(states = Constants.States.CA)
public class TestMultiPolicyDiscount extends TestMultiPolicyDiscountAbstract {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    private GeneralTab _generalTab = new GeneralTab();
    private DriverTab _driverTab = new DriverTab();
    private ErrorTab _errorTab = new ErrorTab();
    private PremiumAndCoveragesTab _pncTab = new PremiumAndCoveragesTab();
    private DocumentsAndBindTab _documentsAndBindTab = new DocumentsAndBindTab();
    private PurchaseTab _purchaseTab = new PurchaseTab();

    /**
     *  Creates a policy with MPD discount
     *  Runs NB +30 jobs for MPD discount validation
     *  Discount is removed due to non-active products found and removes discount
     *  Reason in transaction history set to "Discount validation failure, policy information updated."
     * @param state the test will run against.
     * @author Robert Boles - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Provide 'Reason' type for a MTC to show generic wording when MPD discount is added/removed/change")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-29273")
    public void pas29273_updateReasonMPDRemoval(@Optional("") String state) {
        pas29273_updateReasonMPDRemoval_Template();
    }

    /**
     * Make sure various combos of Unquoted Other AAA Products rate properly and are listed in the UI
     * on P&C Page as well as in View Rating Details. AC3
     * @param state the test will run against.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23983")
    public void pas23983_MPD_unquoted_rate_and_show_discounts(@Optional("") String state) {
        pas23983_MPD_unquoted_rate_and_show_discounts_Template();
    }

    /**
     * This tests that when verified policies are in the list, unquoted options are disabled, and if they are removed, the options are re-enabled.
     * @param state the test will run against.
     * @scenario
     * Prereqs: enterpriseSearchService.enterpriseCustomersSearchUri setup to return all 3 homeowner types on refresh.
     * 1. Using standard test data, create customer, start auto quote, fill up to general tab with default data.
     * 2. Add second NI that has correct response pattern to get 3 property policies back. Click the refresh button.
     * 3. Verify the unquoted options that come back from refresh are disabled. (Based on prereqs, should be all HO)
     * 4. Remove all the Disabled policies and make sure checkboxes re-enable.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-21481")
    public void pas_21481_MPD_Unquoted_Companion_Product_AC2_AC3(@Optional("") String state) {
        pas_21481_MPD_Unquoted_Companion_Product_AC2_AC3_Template();
    }

    /**
     * This tests that when unquoted HO policies are checked, that refresh button returned policies replace them in the table and
     * the unquoted options for those become disabled.
     * @param state the test will run against.
     * @scenario
     * Prereqs: enterpriseSearchService.enterpriseCustomersSearchUri setup to return all 3 homeowner types on refresh.
     * 1. Using standard test data, create customer, start auto quote, fill up to general tab with default data.
     * 2. Check unquoted Home, Condo, and Renters.
     * 3. Add second NI that has correct response pattern to get 3 property policies back. Click the refresh button.
     * 4. Verify the the MDM returned policies replace the unquoted options. (Based on prereqs, should be all HO)
     * 5. Verify the checkboxes are disabled for Home, Renters, and Condo.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-21481")
    public void pas_21481_MPD_Unquoted_Companion_Product_AC5(@Optional("") String state) {
        pas_21481_MPD_Unquoted_Companion_Product_AC5_Template();
    }

    /**
     * This test validates that New Business scenarios that have unquoted options checked result in error at bind time.
     * @param state the test will run against.
     * @scenario PAS-18315 Test 1
     * 1. Create new customer with default test data.
     * 2. Create new quote checking the current scenario boxes that are marked yes.
     * 3. Finish running through the quote and attempt to bind.
     * 4. Verify all scenarios but #7 block binding with hard stop error message: Policy cannot be bound with an unquoted companion policy.
     * @author Brian Bond - CIO
     */
    // BondTODO: Re-test this when bind error is implemented
    @Parameters({"state"})
    @Test( enabled = false, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Prevent Unquoted Bind at NB")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-18315")
    public void pas18315_CIO_Prevent_Unquoted_Bind_NB(@Optional("") String state) {
        pas18315_CIO_Prevent_Unquoted_Bind_NB_Template();
    }

    /**
     * This test validates the endorsement scenario with unquoted options checked result in error at bind time.
     * @param state the test will run against.
     * @scenario PAS-18315 Test 2
     * 1. Bind policy with no MPD.
     * 2. Create an endorsement
     * 3. Check all unquoted checkboxes
     * 4. Attempt to complete the endorsement
     * 5. Verify error message stops you from completing endorsement
     * @author Brian Bond - CIO
     */
    // BondTODO: Re-test this when bind error is implemented
    @Parameters({"state"})
    @Test( enabled = false, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Prevent Unquoted Bind during Endorsment")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-18315")
    public void pas18315_CIO_Prevent_Unquoted_Bind_Endorsment(@Optional("") String state) {
        pas18315_CIO_Prevent_Unquoted_Bind_Endorsement_Template();
    }

    /**
     * This test validates the Amended Renewal scenario with unquoted options checked result in error at bind time.
     * @param state the test will run against.
     * @scenario PAS-18315 Test 3
     * 1. Bind policy with no MPD.
     * 2. Create and rate renewal image. Create an endorsement on renewal image (testing UI lockout so no need to run the timechange job execution process).
     * 3. Check all unquoted checkboxes
     * 4. Attempt to complete the endorsement
     * 5. Verify error message stops you from completing endorsement
     * @author Brian Bond - CIO
     */
    // BondTODO: Re-test this when bind error is implemented
    @Parameters({"state"})
    @Test(enabled = false, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Prevent Unquoted Bind during Amended Renewal")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-18315")
    public void pas18315_CIO_Prevent_Unquoted_Bind_Amended_Renewal(@Optional("") String state) {
        pas18315_CIO_Prevent_Unquoted_Bind_Amended_Renewal_Template();
    }

    /**
     * This test validates that removing named insureds without rating results in error message at bind time.
     * @param state the test will run against.
     * @scenario
     * 1. Create quote with 2 NIs
     * 2. Remove one of the NI (NO mpd data returned)
     * 3. Navigate to Doc and Bind tab and bind
     * 4. Verify a hard stop error occurs directing user to Re-Rate the policy.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Removing a NI and associated companion products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC1_1(@Optional("") String state) {
        pas_3622_CIO_Remove_NI_Companion_AC1_1_Template();
    }

    /**
     * This test validates that removing named insureds without rating results in error message at bind time.
     * @param state the test will run against.
     * @scenario
     * 1. Create quote with 2 NI (one of the NI is ELASTIC_QUOTED)
     * 2. Populate MPD table via refresh
     * 3. Remove ELASTIC_QUOTED
     * 4. Re trigger refresh (Table will now be empty)
     * 5. Bind
     * 6. Verify a hard stop error occurs directing user to Re-Rate the policy.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Removing a NI and associated companion products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC1_2(@Optional("") String state) {
        pas_3622_CIO_Remove_NI_Companion_AC1_2_Template();
    }

    /**
     * This test validates that removing named insureds on endorsements does not refresh MPD table.
     * @param state the test will run against.
     * @scenario
     * 1. Bind a policy with 2 NI (one of the NI is REFRESH_P) with MPD table populated
     * 2. Create Endorsement
     * 3. Remove REFRESH_P
     * 4. Verify Table does not refresh when removing REFRESH_P (Data will stay Peter Parker instead of reverting to default).
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Removing a NI and associated companion products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC2_1(@Optional("") String state) {
        pas_3622_CIO_Remove_NI_Companion_AC2_1_Template();
    }

    /**
     * This test validates that removing named insureds on amended renewals does not refresh MPD table.
     * @param state the test will run against.
     * @scenario
     * 1. Bind a policy with 2 NI (one of the NI is REFRESH_P) with MPD table populated
     * 2. Create Renewal Image
     * 3. Remove REFRESH_P
     * 4. Verify Table does not refresh when removing REFRESH_P (Data will stay Peter Parker instead of reverting to default).
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Removing a NI and associated companion products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC2_2(@Optional("") String state) {
        pas_3622_CIO_Remove_NI_Companion_AC2_2_Template();
    }

    /**
     * This test validates that removing MPD policies removes discounts during a Flat (Same day) Endorsement
     * @scenario
     * 1. Create/Bind an Auto SS policy with Home, Condo, Renters, Motorcycle(if AZ), and Life insurance added.
     * 2. Start a Flat (same as effective date) endorsement
     * 3. Navigate to the Premiums & Coverages (P&C) tab and note the premium, Discounts & Surcharges sections, and the View Rating Details (VRD).
     * 4. Navigate back to the General Tab and remove all the discounts from MPD table.
     * 5. Navigate back to the P&C tab and Calculate Premium.
     * Note: The automation actually opens up another endorsement to grab the final values rather than adding new code to get the proposed values.
     * @author Brian Bond - CIO
     * @param state to override.
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Remove MPD flat endorsement when companion product is removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-28659")
    public void pas28659_Discount_Removed_Flat_Endorsement(@Optional("") String state) {
        run_pas28659_DiscountRemovalTest(EndorsementType.FLAT);
    }

    /**
     * This test validates that removing MPD policies removes discounts during a Future Dated (more than 24 hours after effective date) Endorsement
     * @scenario
     * 1. Create/Bind an Auto SS policy with Home, Condo, Renters, Motorcycle(if AZ), and Life insurance added.
     * 2. Start a Future Dated endorsement (after effective date) endorsement.
     * 3. Navigate to the Premiums & Coverages (P&C) tab and note the premium, Discounts & Surcharges sections, and the View Rating Details (VRD).
     * 4. Navigate back to the General Tab and remove all the discounts from MPD table.
     * 5. Navigate back to the P&C tab and Calculate Premium.
     * Note: The automation actually opens up another endorsement to grab the final values rather than adding new code to get the proposed values.
     * @author Brian Bond - CIO
     * @param state to override.
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Remove MPD Future-Dated endorsement when companion product is removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-28659")
    public void pas28659_Discount_Removed_FutureDated_Endorsement(@Optional("") String state) {
        run_pas28659_DiscountRemovalTest(EndorsementType.FUTURE_DATED);
    }

    /**
     * This test validates that removing MPD policies removes discounts during a Future Dated (more than 24 hours after effective date) Endorsement
     * @scenario
     * 1. Create/Bind an Auto SS policy with Home, Condo, Renters, Motorcycle(if AZ), and Life insurance added.
     * 2. Move the date to New Business plus 2 days (NB+2) and start an endorsement.
     * 3. Navigate to the Premiums & Coverages (P&C) tab and note the premium, Discounts & Surcharges sections, and the View Rating Details (VRD).
     * 4. Navigate back to the General Tab and remove all the discounts from MPD table.
     * 5. Navigate back to the P&C tab and Calculate Premium.
     * Note: The automation actually opens up another endorsement to grab the final values rather than adding new code to get the proposed values.
     * @author Brian Bond - CIO
     * @param state to override.
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Remove MPD Mid-Term endorsement when companion product is removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-28659")
    public void pas28659_Discount_Removed_MidTerm_Endorsement(@Optional("") String state) {
        run_pas28659_DiscountRemovalTest(EndorsementType.MIDTERM);
    }

    /**
     * This test validates that removing named insureds without rating results in error message at bind time.
     * @param endorsementType What scenario to run.
     */
    private void run_pas28659_DiscountRemovalTest(EndorsementType endorsementType){

        TestData testData = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()))
                .mask(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), AutoCaMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel()));

        run_pas28659_DiscountRemovalTest_Template(testData, endorsementType);
    }

    /**
     * This test is provides coverage for validating that the Under Writer rerate rule is thrown as an error whenever the following conditions are met: <br>
     *     1. A rated quote has an MPD element edited (policy type or policy number). <br>
     *     2. A rated quote has an MPD element added. <br>
     *     3. A rated quote has an MPD element removed.
     * @param state
     * @author Tyrone Jemison - CIO
     * @runtime 4 minutes
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Trigger Re-rate event when companion policies are edited or removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24021")
    public void pas24021_MPD_ValidateRerateRuleFires(@Optional("") String state) {
        pas24021_MPD_ValidateRerateRuleFiresTemplate();
    }



    /**
     * Validates that the MPD Companion Validation Error occurs when manually adding a 'Home/Renters/Condo' MPD policy to a quote.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 2min
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24729")
    public void pas3649_MPD_ValidateEligibilityRuleFires_Home(@Optional("") String state) {
        doMPDEligibilityTest("Home");
    }

    /**
     * Validates that the MPD Companion Validation Error DOES NOT occur when manually adding a 'Life/Motorcycle' MPD policy to a quote.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 2min
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24729")
    public void pas3649_MPD_ValidateEligibilityRuleFires_Life(@Optional("") String state) {
        doMPDEligibilityTest("Life");
    }

    /**
     * Validates that the MPD Companion Validation Error occurs when manually adding a 'Home/Renters/Condo' MPD policy to a MidTerm Endorsement.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 3min
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24729")
    public void pas3649_MPD_ValidateEligibility_MidTerm_Renters(@Optional("") String state) {
        doMPDEligibilityTest_MidTerm(false, "Renters");
    }

    /**
     * Validates that the MPD Companion Validation Error occurs when manually adding a 'Home/Renters/Condo' MPD policy to a Renewal Image.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 8min
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24729")
    public void pas3649_MPD_ValidateEligibility_Renewal_Home(@Optional("") String state) {
        doMPDEligibilityTest_Renewal("Condo");
    }

    /**
     * Validates that while a policy is YOUNGER than 30 days effective, an agent can bind an endorsement containing a quoted companion policy.
     * @param state
     * @author Tyrone Jemison
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL, Groups.CIO }, description = "Modify rule to prevent MTE bind after NB+30 with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-31861")
    public void pas31861_MPD_QuotedCompanionProduct_AllowMTEBindBasedOnEffectiveDate(@Optional("CA") String state) {
        doQuotedMPDBindTest(true, false);
    }

    /**
     * Validates that while a policy is OLDER than 30 days effective, an agent can bind an endorsement containing a quoted companion policy.
     * @param state
     * @author Tyrone Jemison
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL, Groups.CIO }, description = "Modify rule to prevent MTE bind after NB+30 with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-31861")
    public void pas31861_MPD_QuotedCompanionProduct_RestrictMTEBindBasedOnEffectiveDate(@Optional("CA") String state) {
        doQuotedMPDBindTest(true, true);
    }

    @Parameters({"state"})
    @Test(enabled = false, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Need ability to prevent MTE bind with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23456")
    public void pas23456_MPD_Prevent_MTEBind(@Optional("") String state) {
        doMTEPreventBindTest(false, "Home");
    }

    @Parameters({"state"})
    @Test(enabled = false, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Need ability to prevent MTE bind with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-23456")
    public void pas23456_MPD_Allow_MTEBind(@Optional("") String state) {
        doMTEAllowBindTest(false, "Home");
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24729")
    public void pas23456_MPD_Prevent_Renewal(@Optional("") String state) {
        doMTEPreventBindTest_Renewals("Renters", false);
    }
    /**
     * Validates that a NB policy can be bound when adding a System-Validated Home policy.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-24729")
    public void pas23456_MPD_Allow_NBBindWithSystemValidatedPolicy(@Optional("") String state) {
        pas23456_MPD_Allow_NBBindWithSystemValidatedPolicyTemplate();
    }

    // Removed docgen deprecated tests for PAS-22193

    @Parameters({"state"})
    @Test(enabled = true, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-27241")
    public void pas27241_MPDPagination(@Optional("") String state){
        pas27241_MPDPaginationTemplate();
    }

    /**
     *  This test ensures that at NB+30, when MPD validation occurs and the discount is removed, an AHDRXX document is generated. <br>
     *  This test currently has no document validation, but will simply generate the document for manual validation. <br>
     *  Marked as disabled so that it will not be included in test suite (no validations are automated)
     * @author Tyrone Jemison (CIO)
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CIO})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-31709")
    public void pas31709_MPD_Discount_Removal_Generates_AHDRXX(@Optional("CA") String state){
        TestData testData = getPolicyDefaultTD();
        testData = TestDataHelper.adjustTD(testData, getGeneralTab().getClass(), AutoCaMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AutoCaMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "3111111111111121");

        pas31709_MPDDiscountRemovalGeneratesAHDRXX(testData);
    }

    // CLASS METHODS
    /**
     * Conducts a basic search using the input String as a policy number.
     * @param inputPolicyNumber
     */
    @Override
    protected void otherAAAProducts_SearchByPolicyNumber(String policyType, String inputPolicyNumber){
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Policy Number");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_TYPE.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_TYPE.getControlClass()).setValue(policyType);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getControlClass()).setValue(inputPolicyNumber);

        if (!policyType.equalsIgnoreCase(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !policyType.equalsIgnoreCase(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
            _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
        }
    }

    /**
     * Used to add a companion policy from the General Tab. Will perform a policy number search and then add the item at the desired index.
     * @param policyType Examples: Home, Renters, Condo, Life, Motorcycle
     * @param inputPolicyNumber Policy number or wiremock 'hook' to use during search. Example hook: CUSTOMER_E
     * @param indexToSelect Beginning at index 0, the row of search results to be selected and added.
     */
    @Override
    protected void addCompanionPolicy_PolicySearch_AddByIndex(String policyType, String inputPolicyNumber, Integer indexToSelect){
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Policy Number");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_TYPE.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_TYPE.getControlClass()).setValue(policyType);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getControlClass()).setValue(inputPolicyNumber);

        if (!policyType.equalsIgnoreCase(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !policyType.equalsIgnoreCase(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
            _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
        }

        new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + indexToSelect + ":customerSelected")).setValue(true);
        getGeneralTab_OtherAAAProductsOwned_SearchOtherAAAProducts_AddSelectedBtnAsset().click();
    }

    /**
     * Masks Current Carrier Information and Required to Issue fields.
     * @return Test Data for an AZ SS policy with no other active policies
     */
    @Override
    protected TestData getTdAuto_mask_CurrentCarrier_RequiredToIssue() {
        return getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()));
    }

    ////////////////////////
    // Navigation Helpers //
    ////////////////////////
    @Override
    protected void navigateToGeneralTab(){
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
    }

    @Override
    protected void navigateToPremiumAndCoveragesTab(){ NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get()); }

    @Override
    protected void navigateToDocumentsAndBindTab(){ NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get()); }

    /////////////////////////
    // General Tab Helpers //
    /////////////////////////

    // General Tab
    @Override
    protected Tab getGeneralTab(){
        return _generalTab;
    }

    @Override
    protected ComboBox getGeneralTab_CurrentAAAMemberAsset() {
        return _generalTab.getAAAMembershipAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER);
    }

    @Override
    protected TextBox getGeneralTab_MembershipNumberAsset(){
        return _generalTab.getAAAMembershipAssetList().getAsset(AutoCaMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER);
    }

    @Override
    protected void generalTab_RemoveInsured(int index){
        _generalTab.removeInsured(index);
    }

    // General Tab (These have not been sorted yet)
    @Override
    protected String getGeneralTab_PolicyTypeMetaDataLabel(){
        return AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE.getLabel();
    }

    @Override
    protected String getGeneralTab_PolicyStatusMetaDataLabel(){
        return AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.STATUS.getLabel();
    }

    @Override
    protected String getGeneralTab_CustomerNameDOBMetaDataLabel(){
        return AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.CUSTOMER_NAME_DOB.getLabel();
    }

    // General Tab -> Contact Information
    @Override
    protected TextBox getGeneralTab_ContactInformation_EmailAsset(){
        return _generalTab.getContactInfoAssetList().getAsset(AutoCaMetaData.GeneralTab.ContactInformation.EMAIL);
    }

    // General Tab -> OtherAAAProductsOwned (MPD Section)
    @Override
    protected Table getGeneralTab_OtherAAAProductTable(){
        return _generalTab.getOtherAAAProductTable();
    }

    @Override
    protected Button getGeneralTab_OtherAAAProductsOwned_RefreshAsset(){
        return _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH);
    }

    @Override
    protected Button getGeneralTab_OtherAAAProductsOwned_ManualPolicyAddButton(){
        return _generalTab.getSearchOtherAAAProducts().getAsset(
                AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_SELECTED_BTN);
    }

    /**
     * Sets an individual checkbox to whatever is passed in.
     * @param policyType is which policy type unquoted box to fill in.
     * @param fillInCheckbox true = check, false = uncheck.
     */
    @Override
    protected void setGeneralTab_OtherAAAProductsOwned_UnquotedCheckbox(mpdPolicyType policyType, Boolean fillInCheckbox){
        getUnquotedCheckBox(policyType).setValue(fillInCheckbox);
    }

    @Override
    protected String getGeneralTab_OtherAAAProducts_LifePolicyCheckboxLabel(){
        return AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel();
    }

    @Override
    protected String getGeneralTab_OtherAAAProducts_MotorcyclePolicyCheckboxLabel(){
        return AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel();
    }

    // General Tab -> OtherAAAProductsOwned (MPD Section) -> ListOfProductsRows
    @Override
    protected ComboBox getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_PolicyTypeEditAsset(){
        return _generalTab.getListOfProductsRowsAssetList()
                .getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE_EDIT);
    }

    @Override
    protected TextBox getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_QuotePolicyNumberEditAsset(){
        return _generalTab.getListOfProductsRowsAssetList()
                .getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.QUOTE_POLICY_NUMBER_EDIT);
    }

    @Override
    protected Button getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_SaveBtnAsset(){
        return _generalTab.getListOfProductsRowsAssetList()
                .getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.SAVE_BTN);
    }

    // General Tab -> OtherAAAProductsOwned (MPD Section_ -> SearchOtherAAAProducts
    @Override
    protected Button getGeneralTab_OtherAAAProductsOwned_SearchOtherAAAProducts_AddSelectedBtnAsset(){
        return  _generalTab.getSearchOtherAAAProducts()
                .getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_SELECTED_BTN);
    }

    @Override
    protected StaticElement getGeneralTab_OtherAAAProductsOwned_SearchOtherAAAProducts_ExceededLimitMessageAsset(){
        return _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.EXCEEDED_LIMIT_MESSAGE);
    }

    /**
     * Adds another named insured and fills out required data.
     * @param firstName is named insured's first name.
     * @param lastName is named insured's last name.
     * @param dateOfBirth is named insured's date of birth in mm/dd/yyyy format
     * @param livedHereLessThan3Years is "Yes" or "No" if named insured has lived at location for less than 3 years.
     * @param residence can be any option in the Residence drop down.
     */
    @Override
    public void generalTab_addNamedInsured(String firstName, String lastName, String dateOfBirth, String livedHereLessThan3Years, String residence){
        // Click Add Insured Button
        _generalTab.getNamedInsuredInfoAssetList()
                .getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED.getLabel(),
                        AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED.getControlClass()).click(Waiters.AJAX);

        // Click cancel on the Named Insured Popup
        _generalTab.getNamedInsuredInfoAssetList()
                .getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.INSURED_SEARCH_DIALOG.getLabel(),
                        AutoCaMetaData.GeneralTab.NamedInsuredInformation.INSURED_SEARCH_DIALOG.getControlClass()).cancel();

        // First Name
        _generalTab.getNamedInsuredInfoAssetList().
                getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(),
                        AutoCaMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getControlClass()).setValue(firstName);

        // Last Name
        _generalTab.getNamedInsuredInfoAssetList().
                getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(),
                        AutoCaMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getControlClass()).setValue(lastName);

        // Date of Birth
        _generalTab.getNamedInsuredInfoAssetList().
                getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH.getLabel(),
                        AutoCaMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH.getControlClass()).setValue(dateOfBirth);

        // Lived here less than 3 years
        _generalTab.getNamedInsuredInfoAssetList().
                getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS.getLabel(),
                        AutoCaMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS.getControlClass()).setValue(livedHereLessThan3Years);

        // CA does not have Residence Dropdown like SS. Do not use parameter for CA.
    }

    ////////////////////////
    // Driver Tab Helpers //
    ////////////////////////
    @Override
    protected Tab getDriverTab(){
        return _driverTab;
    }

    /////////////////////////////////////
    // Premium & Coverages Tab Helpers //
    /////////////////////////////////////
    @Override
    protected Tab getPremiumsAndCoveragesTab(){
        return _pncTab;
    }

    @Override
    protected TestData getPnCTab_RatingDetailsQuoteInfoData(){
        return  _pncTab.getRatingDetailsQuoteInfoData();
    }

    @Override
    protected String pncTab_ViewRatingDetails_MPDAppliedKVPLabel(){return "Multi-policy Discount";}

    @Override
    protected void closePnCTab_ViewRatingDetails(){
        PremiumAndCoveragesTab.RatingDetailsView.buttonRatingDetailsOk.click();
    }

    @Override
    protected Button getPnCTab_BtnCalculatePremium() {
        return _pncTab.btnCalculatePremium();
    }

    @Override
    protected String getPnCTab_DiscountsAndSurcharges(){
        return PremiumAndCoveragesTab.discountsAndSurcharges.getValue();
    }

    @Override
    protected Dollar getPnCTab_getPolicyCoveragePremium(){
        return _pncTab.getPolicyCoveragePremium();
    }

    //////////////////////////////////
    // Documents & Bind Tab Helpers //
    //////////////////////////////////
    @Override
    protected Tab getDocumentsAndBindTab() { return _documentsAndBindTab; }

    @Override
    protected TestData getDocumentsAndBindTab_getRequiredToIssueAssetList(){
        return _documentsAndBindTab.getRequiredToIssueAssetList().getValue();
    }

    @Override
    protected void getDocumentsAndBindTab_setRequiredToIssueAssetList(TestData testData){
        _documentsAndBindTab.getRequiredToIssueAssetList().setValue(testData);
    }

    @Override
    protected Button getDocumentsAndBindTab_BtnPurchase(){
        return DocumentsAndBindTab.btnPurchase;
    }

    @Override
    protected Button getDocumentsAndBindTab_ConfirmPurchase_ButtonYes(){
        return DocumentsAndBindTab.confirmPurchase.buttonYes;
    }

    @Override
    protected Button getDocumentsAndBindTab_ConfirmPurchase_ButtonNo(){
        return DocumentsAndBindTab.confirmPurchase.buttonNo;
    }

    //////////////////////////
    // Purchase Tab Helpers //
    //////////////////////////
    @Override
    protected Tab getPurchaseTab(){ return _purchaseTab; }

    @Override
    protected Button getPurchaseTab_btnApplyPayment(){
        return PurchaseTab.btnApplyPayment;
    }

    ///////////////////////
    // Error Tab Helpers //
    ///////////////////////
    @Override
    protected Tab getErrorTab() { return _errorTab; }

    @Override
    protected Table getErrorTab_TableErrors(){
        return _errorTab.tableErrors;
    }

    @Override
    protected Button getErrorTab_ButtonCancel(){
        return ErrorTab.buttonCancel;
    }

    @Override
    protected void errorTabOverrideErrors(ErrorEnum.Errors... errors) {
        _errorTab.overrideErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER, errors);
    }

    @Override
    protected void errorTabOverride() {
        _errorTab.override();
    }

    @Override
    protected void errorTabOverrideAllErrors() {
        _errorTab.overrideAllErrors();
    }

    @Override
    protected String getErrorTab_ErrorOverride_ErrorCodeValue(){
        return getErrorTab_TableErrors().getColumn(AutoCaMetaData.ErrorTab.ErrorsOverride.CODE.getLabel()).getValue().toString();
    }

    @Override
    protected Button getErrorTab_ButtonOverrideAsset(){
        return _errorTab.buttonOverride;
    }

    @Override
    protected void validateMTEBindError(){
        errorTab_Verify_ErrorsPresent(true, ErrorEnum.Errors.AAA_CSA02012019);
    }

    @Override
    protected void validateMTEBindErrorDoesNotOccur(){
        try{
            errorTab_Verify_ErrorsPresent(false, ErrorEnum.Errors.AAA_CSA02012019);
        }catch(IstfException ex){
            CustomAssertions.assertThat(ex.getMessage()).isEqualToIgnoringCase("Column Code was not found in the table");
        }
    }

    @Override
    protected void errorTab_Verify_ErrorsPresent(boolean expectedValue, ErrorEnum.Errors... errors){
        new ErrorTab().verify.errorsPresent(expectedValue, errors);
    }



    /**
     * This method is used when viewing the Search Other AAA Products popup after searching via Policy Number. <br>
     * Clicks 'Add' button, unless provided instruction to change data.
     */
    @Override
    protected void otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound(String policyType){
        if(policyType.equalsIgnoreCase(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.HOME.getLabel()) || policyType.equalsIgnoreCase(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS.getLabel()) ||
                policyType.equalsIgnoreCase(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.CONDO.getLabel())){

            _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_HOME_RENTERS_CONDO_BTN.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_HOME_RENTERS_CONDO_BTN.getControlClass()).click();

        }else{
            _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_MOTOR_OR_LIFE_BTN.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_MOTOR_OR_LIFE_BTN.getControlClass()).click();
        }
    }

    /**
     * Returns Unquoted Checkbox control based on passed in data.
     * @param policyType Which checkbox to return checkbox.
     * @return Checkbox representing requested control.
     */
    @Override
    protected CheckBox getUnquotedCheckBox(mpdPolicyType policyType){

        CheckBox unquotedCheckBox = null;
        switch(policyType){
            case HOME:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.HOME);
                break;

            case RENTERS:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS);
                break;

            case CONDO:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.CONDO);
                break;

            case LIFE:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.LIFE);
                break;

            case MOTORCYCLE:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE);
                break;
        }

        if (unquotedCheckBox == null){
            CustomAssertions.fail("getUnquotedCheckBox(mpdPolicyType policyType) Unsupported policy type " + policyType);
        }
        return unquotedCheckBox;
    }

    /**
     * Used to search an MPD policy, via Customer Details. <br>
     * @param firstName This parameter has been chosen to drive the search results/response. Edit this field with mapped MPD search string to manipulate which response comes back. <br>
     * @param lastName Customer Last Name. <br>
     * @param dateOfBirth Customer Date of Birth in 'mm/dd/yyyy' format. <br>
     * @param address Customer Street Address. <br>
     * @param city Customer City. <br>
     * @param zipCode Customer Zip Code. <br>
     */
    @Override
    protected void otherAAAProducts_SearchCustomerDetails(String firstName, String lastName, String dateOfBirth, String address, String city, String state, String zipCode){
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Customer Details");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ZIP_CODE.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ZIP_CODE.getControlClass()).setValue(zipCode);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.FIRST_NAME.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.FIRST_NAME.getControlClass()).setValue(firstName);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.LAST_NAME.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.LAST_NAME.getControlClass()).setValue(lastName);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.DATE_OF_BIRTH.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.DATE_OF_BIRTH.getControlClass()).setValue(dateOfBirth);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADDRESS_LINE_1.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADDRESS_LINE_1.getControlClass()).setValue(address);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.CITY.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.CITY.getControlClass()).setValue(city);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.STATE.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.STATE.getControlClass()).setValue(state);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoCaMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
    }
}