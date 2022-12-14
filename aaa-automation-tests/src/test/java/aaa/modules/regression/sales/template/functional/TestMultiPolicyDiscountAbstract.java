package aaa.modules.regression.sales.template.functional;


import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;


public abstract class TestMultiPolicyDiscountAbstract extends PolicyBaseTest {

    private static final String _XPATH_TO_ALL_SEARCH_RESULT_CHECKBOXES = "*//input[contains(@id, 'customerSelected')]";

    // Add more states here if they get MC policy support.
    private ArrayList<String> motorcycleSupportedStates = new ArrayList<>(Arrays.asList(Constants.States.AZ, Constants.States.CA));

    protected enum mpdPolicyType{
        HOME("home"), RENTERS("renters"), CONDO("condo"), LIFE("life"), MOTORCYCLE("motorcycle");

        private final String type;
        mpdPolicyType(String type) {
            this.type = type;
        }

        public String getValue() {
            return this.type;
        }
    }

    protected enum EndorsementType {FLAT, MIDTERM, FUTURE_DATED}

    /**
     * @return Test Data for an AZ SS policy with no other active policies
     */
    protected abstract TestData getTdAuto_mask_CurrentCarrier_RequiredToIssue();

    ////////////////////////
    // Navigation Helpers //
    ////////////////////////
    protected abstract void navigateToGeneralTab();
    protected abstract void navigateToPremiumAndCoveragesTab();
    protected abstract void navigateToDocumentsAndBindTab();

    /////////////////////////
    // General Tab Helpers //
    /////////////////////////

    // General Tab
    protected abstract Tab getGeneralTab();
    protected abstract ComboBox getGeneralTab_CurrentAAAMemberAsset();
    protected abstract TextBox getGeneralTab_MembershipNumberAsset();
    protected abstract void generalTab_RemoveInsured(int index);

    // General Tab (These have not been sorted yet)
    protected abstract String getGeneralTab_PolicyTypeMetaDataLabel();
    protected abstract String getGeneralTab_PolicyStatusMetaDataLabel();
    protected abstract String getGeneralTab_CustomerNameDOBMetaDataLabel();

    // General Tab -> Contact Information
    protected abstract TextBox getGeneralTab_ContactInformation_EmailAsset();

    // General Tab -> OtherAAAProductsOwned (MPD Section)
    protected abstract Table getGeneralTab_OtherAAAProductTable();
    protected abstract Button getGeneralTab_OtherAAAProductsOwned_RefreshAsset();
    protected abstract Button getGeneralTab_OtherAAAProductsOwned_ManualPolicyAddButton();
    protected abstract void setGeneralTab_OtherAAAProductsOwned_UnquotedCheckbox(mpdPolicyType policyType, Boolean fillInCheckbox);
    protected abstract String getGeneralTab_OtherAAAProducts_LifePolicyCheckboxLabel();
    protected abstract String getGeneralTab_OtherAAAProducts_MotorcyclePolicyCheckboxLabel();

    // General Tab -> OtherAAAProductsOwned (MPD Section) -> ListOfProductsRows
    protected abstract ComboBox getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_PolicyTypeEditAsset();
    protected abstract TextBox getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_QuotePolicyNumberEditAsset();
    protected abstract Button getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_SaveBtnAsset();

    // General Tab -> OtherAAAProductsOwned (MPD Section_ -> SearchOtherAAAProducts
    protected abstract Button getGeneralTab_OtherAAAProductsOwned_SearchOtherAAAProducts_AddSelectedBtnAsset();
    protected abstract StaticElement getGeneralTab_OtherAAAProductsOwned_SearchOtherAAAProducts_ExceededLimitMessageAsset();

    /**
     * Adds another named insured and fills out required data.
     * @param firstName is named insured's first name.
     * @param lastName is named insured's last name.
     * @param dateOfBirth is named insured's date of birth in mm/dd/yyyy format
     * @param livedHereLessThan3Years is "Yes" or "No" if named insured has lived at location for less than 3 years.
     * @param residence can be any option in the Residence drop down.
     */
    protected abstract void generalTab_addNamedInsured(String firstName, String lastName, String dateOfBirth, String livedHereLessThan3Years, String residence);

    ////////////////////////
    // Driver Tab Helpers //
    ////////////////////////
    protected abstract Tab getDriverTab();

    /////////////////////////////////////
    // Premium & Coverages Tab Helpers //
    /////////////////////////////////////
    protected abstract Tab getPremiumsAndCoveragesTab();
    protected abstract TestData getPnCTab_RatingDetailsQuoteInfoData();
    protected abstract String pncTab_ViewRatingDetails_MPDAppliedKVPLabel();
    protected abstract void closePnCTab_ViewRatingDetails();
    protected abstract Button getPnCTab_BtnCalculatePremium();
    protected abstract String getPnCTab_DiscountsAndSurcharges();
    protected abstract Dollar getPnCTab_getPolicyCoveragePremium();

    //////////////////////////////////
    // Documents & Bind Tab Helpers //
    //////////////////////////////////
    protected abstract Tab getDocumentsAndBindTab();
    protected abstract TestData getDocumentsAndBindTab_getRequiredToIssueAssetList();
    protected abstract void getDocumentsAndBindTab_setRequiredToIssueAssetList(TestData testData);
    protected abstract Button getDocumentsAndBindTab_BtnPurchase();
    protected abstract Button getDocumentsAndBindTab_ConfirmPurchase_ButtonYes();
    protected abstract Button getDocumentsAndBindTab_ConfirmPurchase_ButtonNo();

    //////////////////////////
    // Purchase Tab Helpers //
    //////////////////////////
    protected abstract Tab getPurchaseTab();
    protected abstract Button getPurchaseTab_btnApplyPayment();

    ///////////////////////
    // Error Tab Helpers //
    ///////////////////////
    protected abstract Tab getErrorTab();
    protected abstract Table getErrorTab_TableErrors();
    protected abstract Button getErrorTab_ButtonCancel();
    protected abstract void errorTabOverrideErrors(ErrorEnum.Errors... errors);
    protected abstract void errorTabOverride();
    protected abstract void errorTabOverrideAllErrors();
    protected abstract String getErrorTab_ErrorOverride_ErrorCodeValue();
    protected abstract Button getErrorTab_ButtonOverrideAsset();
    protected abstract void validateMTEBindError();
    protected abstract void validateMTEBindErrorDoesNotOccur();
    protected abstract void errorTab_Verify_ErrorsPresent(boolean expectedValue, ErrorEnum.Errors... errors);

    /**
     * Conducts a basic search using the input String as a policy number.
     * @param inputPolicyNumber
     */
    protected abstract void otherAAAProducts_SearchByPolicyNumber(String policyType, String inputPolicyNumber);

    /**
     * This method is used when viewing the Search Other AAA Products popup after searching via Policy Number. <br>
     * Clicks 'Add' button, unless provided instruction to change data.
     */
    protected abstract void otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound(String policyType);

    /**
     * Used to add a companion policy from the General Tab. Will perform a policy number search and then add the item at the desired index.
     * @param policyType Examples: Home, Renters, Condo, Life, Motorcycle
     * @param inputPolicyNumber Policy number or wiremock 'hook' to use during search. Example hook: CUSTOMER_E
     * @param indexToSelect Beginning at index 0, the row of search results to be selected and added.
     */
    protected abstract void addCompanionPolicy_PolicySearch_AddByIndex(String policyType, String inputPolicyNumber, Integer indexToSelect);


    /**
     * Returns Unquoted Checkbox control based on passed in data.
     * @param policyType Which checkbox to return checkbox.
     * @return Checkbox representing requested control.
     */
    protected abstract CheckBox getUnquotedCheckBox(mpdPolicyType policyType);

    /**
     * Used to search an MPD policy, via Customer Details. <br>
     * @param firstName This parameter has been chosen to drive the search results/response. Edit this field with mapped MPD search string to manipulate which response comes back. <br>
     * @param lastName Customer Last Name. <br>
     * @param dateOfBirth Customer Date of Birth in 'mm/dd/yyyy' format. <br>
     * @param address Customer Street Address. <br>
     * @param city Customer City. <br>
     * @param zipCode Customer Zip Code. <br>
     */
    protected abstract void otherAAAProducts_SearchCustomerDetails(String firstName, String lastName, String dateOfBirth, String address, String city, String state, String zipCode);

    /**
     *  Creates a policy with MPD discount
     *  Runs NB +30 jobs for MPD discount validation
     *  Discount is removed due to non-active products found and removes discount
     * @author Robert Boles - CIO
     */
    protected void pas29273_updateReasonMPDRemoval_Template() {
        TestData testData = getTdAuto_mask_CurrentCarrier_RequiredToIssue();
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, getGeneralTab().getClass(),true);

        getGeneralTab_CurrentAAAMemberAsset().setValue("Yes");
        //Set the membership number to active
        this.getGeneralTab_MembershipNumberAsset().setValue("4290074030137505");
        //puts quoted products into the MPD table with REFRESH_PQ@yeah.com
        getGeneralTab_ContactInformation_EmailAsset().setValue("REFRESH_PQ@yeah.com");
        getGeneralTab_OtherAAAProductsOwned_RefreshAsset().click(Waiters.AJAX);
        getGeneralTab().submitTab();

        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getPurchaseTab().getClass(), true);
        if (getErrorTab_TableErrors().isPresent()) {
            errorTabOverrideErrors(ErrorEnum.Errors.ERROR_AAA_MVR_order_validation_SS);
            errorTabOverride();
        }
        getPurchaseTab().submitTab();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Policy Number " + PolicySummaryPage.getPolicyNumber());
        LocalDateTime policyEffectiveDatePlus30 = PolicySummaryPage.getEffectiveDate().plusDays(30);
        if(policyEffectiveDatePlus30.getDayOfWeek() == DayOfWeek.SATURDAY) {
            policyEffectiveDatePlus30 = policyEffectiveDatePlus30.plusDays(2);
        } else if (policyEffectiveDatePlus30.getDayOfWeek() == DayOfWeek.SUNDAY) {
            policyEffectiveDatePlus30 = policyEffectiveDatePlus30.plusDays(1);
        }
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDatePlus30);
        log.info("Time Setter Move " + policyEffectiveDatePlus30);

        jobsNBplus30runNoChecks();
        mainApp().reopen();
        SearchPage.openPolicy(policyNumber);
        transactionHistoryRecordCountCheck(policyNumber, 2, "Discount validation failure, policy information updated.", new ETCSCoreSoftAssertions());
    }

    /**
     * Make sure various combos of Unquoted Other AAA Products rate properly and are listed in the UI
     * on P&C Page as well as in View Rating Details. AC3
     * @author Brian Bond - CIO
     */
    protected void pas23983_MPD_unquoted_rate_and_show_discounts_Template() {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        ArrayList<HashMap<mpdPolicyType, Boolean>> scenarioList = getUnquotedManualScenarios();

        // Perform the following for each scenario. Done this way to avoid recreating users every scenario.
        for (int i = 0; i < scenarioList.size(); i++ ) {

            HashMap <mpdPolicyType, Boolean> currentScenario = scenarioList.get(i);

            // Set unquoted policies //
            setUnquotedCheckboxes(currentScenario);

            // On first iteration fill in data. Else jump to PnC page
            if (i == 0) {
                // Continue to next tab then move to P&C tab //
                getGeneralTab().submitTab();

                policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(),
                        getPremiumsAndCoveragesTab().getClass(), true);
            }
            else {
                navigateToPremiumAndCoveragesTab();
            }

            assertSoftly(softly -> {

                // Check in View Rating details for Multi-Policy Discount //
                String mpdDiscountApplied =
                        getPnCTab_RatingDetailsQuoteInfoData().getValue(pncTab_ViewRatingDetails_MPDAppliedKVPLabel());

                // Close the VRD Popup
                closePnCTab_ViewRatingDetails();

                // If any value is true then the VRD MPD Discount should be Yes. Else None.
                String mpdVRDExpectedValue = currentScenario.containsValue(true) ? "Yes" : "None";

                softly.assertThat(mpdDiscountApplied).isEqualTo(mpdVRDExpectedValue);

                // Validate Discount and Surcharges //
                String discountsAndSurcharges = getPnCTab_DiscountsAndSurcharges().toLowerCase(); // CA Lower SS Pascal

                // Check against any property string. Done this way because only one property type listed in Discounts.
                Boolean propertyValuesPresent =
                        currentScenario.get(mpdPolicyType.HOME) ||
                        currentScenario.get(mpdPolicyType.RENTERS) ||
                        currentScenario.get(mpdPolicyType.CONDO);

                Boolean propertyValuePresentInString =
                        discountsAndSurcharges.contains("home") ||
                                discountsAndSurcharges.contains("condo") ||
                                discountsAndSurcharges.contains("renters");

                softly.assertThat(propertyValuePresentInString).isEqualTo(propertyValuesPresent);

                // MC and Life always show if added.
                softly.assertThat(currentScenario.get(mpdPolicyType.MOTORCYCLE)).
                        isEqualTo(discountsAndSurcharges.contains("motorcycle"));

                softly.assertThat(currentScenario.get(mpdPolicyType.LIFE)).
                        isEqualTo(discountsAndSurcharges.contains("life"));
            });

            // Return to GeneralTab tab.
            navigateToGeneralTab();
        }
    }

    /**
     * This tests that when verified policies are in the list, unquoted options are disabled, and if they are removed, the options are re-enabled.
     * @scenario
     * Prereqs: enterpriseSearchService.enterpriseCustomersSearchUri setup to return all 3 homeowner types on refresh.
     * 1. Using standard test data, create customer, start auto quote, fill up to general tab with default data.
     * 2. Add second NI that has correct response pattern to get 3 property policies back. Click the refresh button.
     * 3. Verify the unquoted options that come back from refresh are disabled. (Based on prereqs, should be all HO)
     * 4. Remove all the Disabled policies and make sure checkboxes re-enable.
     * @author Brian Bond - CIO
     */
    protected void pas_21481_MPD_Unquoted_Companion_Product_AC2_AC3_Template() {

        // Step 1
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        // Step 2

        // REFRESH_P will come back with all 3 property types
        generalTab_addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        getGeneralTab_OtherAAAProductsOwned_RefreshAsset().click(Waiters.AJAX);

        // Step 3:
        // Note: If following fails on first assert, validate hitting refresh comes back with
        // Home, Renters, and Condo policies. If not, check test pre-reqs have been met.
        assertThat(getUnquotedCheckBox(mpdPolicyType.HOME).isEnabled()).isFalse();
        assertThat(getUnquotedCheckBox(mpdPolicyType.RENTERS).isEnabled()).isFalse();
        assertThat(getUnquotedCheckBox(mpdPolicyType.CONDO).isEnabled()).isFalse();
        assertThat(getUnquotedCheckBox(mpdPolicyType.LIFE).isEnabled()).isTrue();

        // Only add motorcycle in supported states
        if (motorcycleSupportedStates.contains(getState())){
            assertThat(getUnquotedCheckBox(mpdPolicyType.MOTORCYCLE).isEnabled()).isTrue();
        }
        else{
            assertThat(getUnquotedCheckBox(mpdPolicyType.MOTORCYCLE).isPresent()).isFalse();
        }

        // Step 4
        removeAllOtherAAAProductsOwnedTablePolicies();

        assertThat(getUnquotedCheckBox(mpdPolicyType.HOME).isEnabled()).isTrue();
        assertThat(getUnquotedCheckBox(mpdPolicyType.RENTERS).isEnabled()).isTrue();
        assertThat(getUnquotedCheckBox(mpdPolicyType.CONDO).isEnabled()).isTrue();
    }

    /**
     * This tests that when unquoted HO policies are checked, that refresh button returned policies replace them in the table and
     * the unquoted options for those become disabled.
     * @scenario
     * Prereqs: enterpriseSearchService.enterpriseCustomersSearchUri setup to return all 3 homeowner types on refresh.
     * 1. Using standard test data, create customer, start auto quote, fill up to general tab with default data.
     * 2. Check unquoted Home, Condo, and Renters.
     * 3. Add second NI that has correct response pattern to get 3 property policies back. Click the refresh button.
     * 4. Verify the the MDM returned policies replace the unquoted options. (Based on prereqs, should be all HO)
     * 5. Verify the checkboxes are disabled for Home, Renters, and Condo.
     * @author Brian Bond - CIO
     */
    protected void pas_21481_MPD_Unquoted_Companion_Product_AC5_Template() {

        // Step 1
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        // Step 2
        setGeneralTab_OtherAAAProductsOwned_UnquotedCheckbox(mpdPolicyType.HOME, true);
        setGeneralTab_OtherAAAProductsOwned_UnquotedCheckbox(mpdPolicyType.RENTERS, true);
        setGeneralTab_OtherAAAProductsOwned_UnquotedCheckbox(mpdPolicyType.CONDO, true);

        // Step 3

        // REFRESH_P will come back with all 3 property types
        generalTab_addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        getGeneralTab_OtherAAAProductsOwned_RefreshAsset().click(Waiters.AJAX);

        // Step 4
        String policyTypeMetaDataLabel = getGeneralTab_PolicyTypeMetaDataLabel();
        String policyStatusMetaDataLabel = getGeneralTab_PolicyStatusMetaDataLabel();

        // Find row matching policyType, then pull the status cell out of it to assert on.
        String homeStatusColumnValue = getGeneralTab_OtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.HOME.getValue())
                .getCell(policyStatusMetaDataLabel)
                .getValue();

        String rentersStatusColumnValue = getGeneralTab_OtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.RENTERS.getValue())
                .getCell(policyStatusMetaDataLabel)
                .getValue();

        String condoStatusColumnValue = getGeneralTab_OtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.CONDO.getValue())
                .getCell(policyStatusMetaDataLabel)
                .getValue();

        // Expected to be replaced with an Active or Quoted status. If not, make sure prereq is met.
        String unexpected = "UNQUOTED";
        assertThat(homeStatusColumnValue).isNotEqualTo(unexpected);
        assertThat(rentersStatusColumnValue).isNotEqualTo(unexpected);
        assertThat(condoStatusColumnValue).isNotEqualTo(unexpected);

        // Step 5
        assertThat(getUnquotedCheckBox(mpdPolicyType.HOME).isEnabled()).isFalse();
        assertThat(getUnquotedCheckBox(mpdPolicyType.RENTERS).isEnabled()).isFalse();
        assertThat(getUnquotedCheckBox(mpdPolicyType.CONDO).isEnabled()).isFalse();
    }

    /**
     * This test validates that New Business scenarios that have unquoted options checked result in error at bind time.
     * @scenario PAS-18315 Test 1
     * 1. Create new customer with default test data.
     * 2. Create new quote checking the current scenario boxes that are marked yes.
     * 3. Finish running through the quote and attempt to bind.
     * 4. Verify all scenarios but #7 block binding with hard stop error message: Policy cannot be bound with an unquoted companion policy.
     * @author Brian Bond - CIO
     */
    protected void pas18315_CIO_Prevent_Unquoted_Bind_NB_Template() {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        ArrayList<HashMap<mpdPolicyType, Boolean>> scenarioList = getUnquotedManualScenarios();

        // Perform the following for each scenario. Done this way to avoid recreating users every scenario.
        for (int i = 0; i < scenarioList.size(); i++ ) {

            HashMap <mpdPolicyType, Boolean> currentScenario = scenarioList.get(i);

            // Set unquoted policies //
            setUnquotedCheckboxes(currentScenario);

            // On first iteration fill in data. Else jump to Documents & Bind page
            if (i == 0) {
                // Continue to next tab then move to Documents & Bind Tab tab //
                getGeneralTab().submitTab();

                policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(),
                        getDocumentsAndBindTab().getClass(), true);
            }
            else {
                navigateToPremiumAndCoveragesTab();
                getPnCTab_BtnCalculatePremium().click(Waiters.AJAX);
                navigateToDocumentsAndBindTab();

                // Ensure Physically sign Auto Insurance Application set -- COMMENTED OUT DUE TO ELEMENT NO LONGER BEING IN APP.
                //_documentsAndBindTab.getRequiredToBindAssetList().getAsset(
                //       AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)
                //        .setValue("Physically Signed");
            }

            // Attempt to bind
            getDocumentsAndBindTab_BtnPurchase().click(Waiters.AJAX);

            // If any unquoted was checked ("true") then error message appears.
            if (currentScenario.containsValue(true)) {
                // Hard stop error page should be present
                String errorMsg = getErrorTab_TableErrors().
                        getRow("Code", "MPD_COMPANION_UNQUOTED_VALIDATION").
                        getCell("Message").getValue();

                assertThat(errorMsg).startsWith("Policy cannot be bound with an unquoted companion policy.");

                getErrorTab().cancel(true);
            }else{
                // Purchase confirmation should be present
                Button buttonNo = getDocumentsAndBindTab_ConfirmPurchase_ButtonNo();
                assertThat(buttonNo.isPresent() && buttonNo.isVisible()).isTrue();
                buttonNo.click(Waiters.AJAX);
            }

            // Return to General tab.
            navigateToGeneralTab();
        }
    }

    /**
     * This test validates the endorsement scenario with unquoted options checked result in error at bind time.
     * @scenario PAS-18315 Test 2
     * 1. Bind policy with no MPD.
     * 2. Create an endorsement
     * 3. Check all unquoted checkboxes
     * 4. Attempt to complete the endorsement
     * 5. Verify error message stops you from completing endorsement
     * @author Brian Bond - CIO
     */
    protected void pas18315_CIO_Prevent_Unquoted_Bind_Endorsement_Template() {
        // Step 1
        openAppAndCreatePolicy();

        // Step 2
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        // Step 3 (Using the first scenario which is check all)
        setUnquotedCheckboxes(getUnquotedManualScenarios().get(0));

        // Step 4
        navigateToPremiumAndCoveragesTab();
        getPnCTab_BtnCalculatePremium().click(Waiters.AJAX);

        navigateToDocumentsAndBindTab();
        getDocumentsAndBindTab().submitTab();

        // Step 5
        String errorMsg = getErrorTab_TableErrors().
                getRow("Code", "MPD_COMPANION_UNQUOTED_VALIDATION").
                getCell("Message").getValue();

        assertThat(errorMsg).startsWith("Policy cannot be bound with an unquoted companion policy.");
    }

    /**
     * This test validates the Amended Renewal scenario with unquoted options checked result in error at bind time.
     * @scenario PAS-18315 Test 3
     * 1. Bind policy with no MPD.
     * 2. Create and rate renewal image. Create an endorsement on renewal image (testing UI lockout so no need to run the timechange job execution process).
     * 3. Check all unquoted checkboxes
     * 4. Attempt to complete the endorsement
     * 5. Verify error message stops you from completing endorsement
     * @author Brian Bond - CIO
     */
    protected void pas18315_CIO_Prevent_Unquoted_Bind_Amended_Renewal_Template() {
        // Step 1
        openAppAndCreatePolicy();

        // Step 2
        policy.createRenewal(getPolicyTD("InitiateRenewalEntry", "TestData"));

        // Step 3 (Using the first scenario which is check all)
        setUnquotedCheckboxes(getUnquotedManualScenarios().get(0));

        // Step 4
        navigateToPremiumAndCoveragesTab();
        getPnCTab_BtnCalculatePremium().click(Waiters.AJAX);

        navigateToDocumentsAndBindTab();
        getDocumentsAndBindTab().submitTab();

        // Step 5
        String errorMsg = getErrorTab_TableErrors().
                getRow("Code", "MPD_COMPANION_UNQUOTED_VALIDATION").
                getCell("Message").getValue();

        assertThat(errorMsg).startsWith("Policy cannot be bound with an unquoted companion policy.");
    }

    /**
     * This test validates that removing named insureds without rating results in error message at bind time.
     * @scenario
     * 1. Create quote with 2 NIs
     * 2. Remove one of the NI (NO mpd data returned)
     * 3. Navigate to Doc and Bind tab and bind
     * 4. Verify a hard stop error occurs directing user to Re-Rate the policy.
     * @author Brian Bond - CIO
     */
    protected void pas_3622_CIO_Remove_NI_Companion_AC1_1_Template() {
        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        // Add second NI
        generalTab_addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        // Move to documents and bind tab.
        getGeneralTab().submitTab();

        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getDocumentsAndBindTab().getClass(), true);

        navigateToGeneralTab();

        // Remove Second NI
        generalTab_RemoveInsured(2);

        // Attempt to Bind
        navigateToDocumentsAndBindTab();
        getDocumentsAndBindTab().submitTab();

        // Check for error.
        String errorMsg = getErrorTab_TableErrors().
                getRow("Code", "Unprepared data").
                getCell("Message").getValue();

        assertThat(errorMsg).startsWith("Cannot issue policy which was not rated!");
    }

    /**
     * This test validates that removing named insureds without rating results in error message at bind time.
     * @scenario
     * 1. Create quote with 2 NI (one of the NI is ELASTIC_QUOTED)
     * 2. Populate MPD table via refresh
     * 3. Remove ELASTIC_QUOTED
     * 4. Re trigger refresh (Table will now be empty)
     * 5. Bind
     * 6. Verify a hard stop error occurs directing user to Re-Rate the policy.
     * @author Brian Bond - CIO
     */
    protected void pas_3622_CIO_Remove_NI_Companion_AC1_2_Template() {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        // Add second NI
        generalTab_addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        // Trigger refresh
        getGeneralTab_OtherAAAProductsOwned_RefreshAsset().click(Waiters.AJAX);

        // Move to documents and bind tab.
        getGeneralTab().submitTab();

        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getDocumentsAndBindTab().getClass(), true);

        navigateToGeneralTab();

        // Remove Second NI
        generalTab_RemoveInsured(2);

        // Attempt to Bind
        navigateToDocumentsAndBindTab();
        getDocumentsAndBindTab().submitTab();

        // Check for error.
        String errorMsg = getErrorTab_TableErrors().
                getRow("Code", "Unprepared data").
                getCell("Message").getValue();

        assertThat(errorMsg).startsWith("Cannot issue policy which was not rated!");
    }

    /**
     * This test validates that removing named insureds on endorsements does not refresh MPD table.
     * @scenario
     * 1. Bind a policy with 2 NI (one of the NI is REFRESH_P) with MPD table populated
     * 2. Create Endorsement
     * 3. Remove REFRESH_P
     * 4. Verify Table does not refresh when removing REFRESH_P (Data will stay Peter Parker instead of reverting to default).
     * @author Brian Bond - CIO
     */
    protected void pas_3622_CIO_Remove_NI_Companion_AC2_1_Template() {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab.
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        // Add second NI
        generalTab_addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        // Trigger refresh
        getGeneralTab_OtherAAAProductsOwned_RefreshAsset().click(Waiters.AJAX);

        // Complete purchase
        getGeneralTab().submitTab();

        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getPurchaseTab().getClass(), true);

        getPurchaseTab().submitTab();

        // Start endorsement
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        generalTab_RemoveInsured(2);

        // Pull customer names out of table
        String policyTypeMetaDataLabel = getGeneralTab_PolicyTypeMetaDataLabel();
        String customerNameDOBMetaDataLabel = getGeneralTab_CustomerNameDOBMetaDataLabel();

        // Find row matching policyType, then pull the status cell out of it to assert on.
        String homeStatusColumnValue = getGeneralTab_OtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.HOME.getValue())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        String rentersStatusColumnValue = getGeneralTab_OtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.RENTERS.getValue())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        String condoStatusColumnValue = getGeneralTab_OtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.CONDO.getValue())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        // Verify no refresh on table by checking Peter Parker has not reverted to default response
        String expectedName = "PETER PARKER";
        assertThat(homeStatusColumnValue).startsWith(expectedName);
        assertThat(rentersStatusColumnValue).startsWith(expectedName);
        assertThat(condoStatusColumnValue).startsWith(expectedName);
    }

    /**
     * This test validates that removing named insureds on amended renewals does not refresh MPD table.
     * @scenario
     * 1. Bind a policy with 2 NI (one of the NI is REFRESH_P) with MPD table populated
     * 2. Create Renewal Image
     * 3. Remove REFRESH_P
     * 4. Verify Table does not refresh when removing REFRESH_P (Data will stay Peter Parker instead of reverting to default).
     * @author Brian Bond - CIO
     */
    protected void pas_3622_CIO_Remove_NI_Companion_AC2_2_Template() {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab.
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        // Add second NI
        generalTab_addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        // Trigger refresh
        getGeneralTab_OtherAAAProductsOwned_RefreshAsset().click(Waiters.AJAX);

        // Complete purchase
        getGeneralTab().submitTab();

        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getPurchaseTab().getClass(), true);

        getPurchaseTab().submitTab();

        // Start renewal
        //policy.createRenewal(getPolicyTD("InitiateRenewalEntry", "TestData"));
        policy.renew().perform();

        // Remove driver 2
        generalTab_RemoveInsured(2);

        // Pull customer names out of table
        String policyTypeMetaDataLabel = getGeneralTab_PolicyTypeMetaDataLabel();
        String customerNameDOBMetaDataLabel = getGeneralTab_CustomerNameDOBMetaDataLabel();

        // Find row matching policyType, then pull the status cell out of it to assert on.
        String homeStatusColumnValue = getGeneralTab_OtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.HOME.getValue())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        String rentersStatusColumnValue = getGeneralTab_OtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.RENTERS.getValue())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        String condoStatusColumnValue = getGeneralTab_OtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.CONDO.getValue())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        // Verify no refresh on table by checking Peter Parker has not reverted to default response
        String expectedName = "PETER PARKER";
        assertThat(homeStatusColumnValue).startsWith(expectedName);
        assertThat(rentersStatusColumnValue).startsWith(expectedName);
        assertThat(condoStatusColumnValue).startsWith(expectedName);
    }

    /**
     * This test validates that removing named insureds without rating results in error message at bind time.
     * @param endorsementType What scenario to run.
     */
    protected void run_pas28659_DiscountRemovalTest_Template(TestData testData, EndorsementType endorsementType){
        String policyNumber = pas28659_SetupScenario_Template(testData);

        //String decPageBeforeRemovalXML = AAAMultiPolicyDiscountQueries.getDecPage(policyNumber).orElse("Null");

        //String decPageBeforeRemoval = getAHDRXXValueFromNodeName("PlcyDiscDesc", decPageBeforeRemovalXML);

        // Assert all available MPD are present before removal.
        //validateAllMPDiscounts(parseXMLDocMPDList(decPageBeforeRemoval), true);

        switch (endorsementType){
            case FLAT:
                policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
                break;

            case MIDTERM:
                JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
                JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
                mainApp().open();
                SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
                policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
                break;

            case FUTURE_DATED:
                policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
                break;
        }

        // Navigate to the Premiums & Coverages (P&C) tab and note the premium, Discounts & Surcharges sections, and the View Rating Details (VRD).
        navigateToPremiumAndCoveragesTab();

        Dollar discountedPremium =  getPnCTab_getPolicyCoveragePremium();

        // Remove discounts from General Tab
        navigateToGeneralTab();

        removeAllOtherAAAProductsOwnedTablePolicies();

        // Calculate premium.
        navigateToPremiumAndCoveragesTab();
        getPnCTab_BtnCalculatePremium().click(Waiters.AJAX);

        // Validate Discount and Surcharges //
        List<String> discountsAndSurcharges =
                parseDiscountAndSurchargesString(getPnCTab_DiscountsAndSurcharges());

        assertThat(discountsAndSurcharges).doesNotContain("Multi-Policy Discount");

        navigateToDocumentsAndBindTab();
        getDocumentsAndBindTab().submitTab();

        /*
        // Check document
        String decPageAfterRemovalXML = AAAMultiPolicyDiscountQueries.getDecPage(policyNumber).orElse("Null");

        String decPageAfterRemoval = getAHDRXXValueFromNodeName("PlcyDiscDesc", decPageAfterRemovalXML);

        // Assert all available MPD are NOT present after removal.
        validateAllMPDiscounts(parseXMLDocMPDList(decPageAfterRemoval), false);
         */

        // Check VRD (Done this way to not have to redo how _pncTab.getRatingDetailsQuoteInfoData() works
        if (endorsementType == EndorsementType.FUTURE_DATED){
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
        }else {
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }

        // Calculate premium.
        navigateToPremiumAndCoveragesTab();
        getPnCTab_BtnCalculatePremium().click(Waiters.AJAX);

        Dollar removedMPDPremium = getPnCTab_getPolicyCoveragePremium();

        assertThat(discountedPremium.lessThan(removedMPDPremium)).isTrue();

        // Check in View Rating details for Multi-Policy Discount
        // Needs to finish and open a second endorsement to show up.
        String mpdDiscountApplied =
                getPnCTab_RatingDetailsQuoteInfoData().getValue(pncTab_ViewRatingDetails_MPDAppliedKVPLabel());

        assertThat(mpdDiscountApplied).isEqualTo("None");

        // Close the VRD Popup
        closePnCTab_ViewRatingDetails();
    }

    /**
     * This test is provides coverage for validating that the Under Writer rerate rule is thrown as an error whenever the following conditions are met: <br>
     *     1. A rated quote has an MPD element edited (policy type or policy number). <br>
     *     2. A rated quote has an MPD element added. <br>
     *     3. A rated quote has an MPD element removed.
     * @author Tyrone Jemison - CIO
     * @runtime 4 minutes
     */
    protected void pas24021_MPD_ValidateRerateRuleFiresTemplate() {

        // Using default test data.
        TestData testData = getPolicyTD();

        // Set pre-conditions by creating a quote, rating and filling up to purchase.
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);
        otherAAAProducts_SearchAndManuallyAddCompanionPolicy("Home", "NOT_FOUND");
        getGeneralTab().submitTab();

        // Added MPD element, filling up to purchase point. Includes hacky methods to get around system error.
        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getDocumentsAndBindTab().getClass(), true);
        getDocumentsAndBindTab_BtnPurchase().click();

        getErrorTab_ButtonCancel().click();

        editMPDAndRerate(0, "Renters", "ABC1"); // Editing only the policyType in this scenario.
        editMPDAndRerate(0, "Renters", "XYZ2"); // Editing only the policyNumber in this scenario.

        addMPDAndRerate("Home", "NOT_FOUND");

        removeMPDAndRerate(0);
    }

    /**
     * Validates that a NB policy can be bound when adding a System-Validated Home policy.
     */
    protected void pas23456_MPD_Allow_NBBindWithSystemValidatedPolicyTemplate() {
        TestData testData = getPolicyTD();
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("ELASTIC_QUOTED");
        otherAAAProductsSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.
        getGeneralTab().submitTab();
        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getPurchaseTab().getClass(), true);

        getPurchaseTab().submitTab();

        //getPurchaseTab_btnApplyPayment().click();
        //Page.dialogConfirmation.buttonYes.click();
        assertThat(PolicySummaryPage.labelPolicyStatus.getValue().contains("Active")).isTrue();
    }

    protected void pas27241_MPDPaginationTemplate(){

        int numberOfResultsRequiredForSuccessfulValidation = 6;
        // Handles getting us a policy and moves us up to our testing point, on the General Tab.
        createQuoteAndFillUpTo(getPolicyDefaultTD(), getGeneralTab().getClass(), false);

        // Test Results > 50 display error on UI.
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMERS_51");

        // Validate Error appears and count the number of results on the page.
        assertThat(getGeneralTab_OtherAAAProductsOwned_SearchOtherAAAProducts_ExceededLimitMessageAsset()).isPresent();
        assertThat(getSearchResultsCount()).isEqualTo(numberOfResultsRequiredForSuccessfulValidation);
        otherAAAProductsSearchTable_addSelected(new int[]{0, 1, 2, 3, 4, 5});

        // Test Results <= 50 DO NOT display error on UI.
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_GBY");

        // Validate Error does NOT appear and count the number of results on the page.
        assertThat(getGeneralTab_OtherAAAProductsOwned_SearchOtherAAAProducts_ExceededLimitMessageAsset()).isAbsent();
        assertThat(getSearchResultsCount()).isEqualTo(numberOfResultsRequiredForSuccessfulValidation);
    }

    /**
     * This test ensures that at NB+30, when MPD validation occurs and the discount is removed, an AHDRXX document is generated.
     */
    protected void pas31709_MPDDiscountRemovalGeneratesAHDRXX(TestData testData){

        // Open App, Create Customer, Initiate Quote, Add Quoted Companion Policies, Bind.
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);
        addCompanionPolicy_PolicySearch_AddByIndex("Home", "REFRESH_PQ", 1);
        getGeneralTab().submitTab();
        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getPurchaseTab().getClass(), true);
        getPurchaseTab().submitTab();

        // Move to NB+30
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        LocalDateTime nb30Date = policyEffectiveDate.plusDays(30l);
        TimeSetterUtil.getInstance().nextPhase(nb30Date);
        printDebugMessage("Moved from effective date: " + policyEffectiveDate.toString() + " to nb30Date: " + nb30Date.toString());
        printDebugMessage("Batch run date = " + TimeSetterUtil.getInstance().getCurrentTime());

        // Run Batch Job
        JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
        JobUtils.executeJob(BatchJob.membershipValidationJob);

        // Validate AHDRXX document exists.
        printDebugMessage("Generated AHDRXX for Policy: " + policyNumber);
    }

    protected void doMPDEligibilityTest(String in_policyType){
        // Using default test data.
        TestData testData = getPolicyTD();

        // Add MPD Element manually (after no results found)
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);
        otherAAAProducts_SearchAndManuallyAddCompanionPolicy(in_policyType, "NOT_FOUND");
        getGeneralTab().submitTab();

        // Continue towards purchase of quote.
        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getDocumentsAndBindTab().getClass(), true);
        getDocumentsAndBindTab_BtnPurchase().click();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    protected void doMPDEligibilityTest_MidTerm(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppAndCreatePolicy();

        handleEndorsementType(bFlatEndorsement);

        otherAAAProducts_SearchAndManuallyAddCompanionPolicy(in_policyType, "NOT_FOUND");
        fillFromGeneralTabToErrorMsg();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    protected void doMPDEligibilityTest_Renewal(String in_policyType){
        // Create Policy
        createPolicyAdvanceToRenewalImage();

        // In Renewal Image, Add MPD Element and Bind
        otherAAAProducts_SearchAndManuallyAddCompanionPolicy(in_policyType, "NOT_FOUND");
        fillFromGeneralTabToErrorMsg();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    protected void doMTEAllowBindTest(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppAndCreatePolicy();

        handleEndorsementType(bFlatEndorsement);

        // Add MPD Element via Customer Search
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_E");
        otherAAAProductsSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_NE");
        otherAAAProductsSearchTable_addSelected(1);

        fillFromGeneralTabToErrorMsg();

        // Validate error message appears.
        validateMTEBindErrorDoesNotOccur();
    }

    /**
     * Test begins with a quoted companion policy being added during NB. <br>
     *     Next the test ensures that if base policy effective date + 30 days < System Date, quoted policies can be endorsed.
     */
    protected void doQuotedMPDBindTest(Boolean bAddQuotedCompanionPolicyAtNewBusiness, Boolean bMakeSystemDateOverNB30){
        // Initiate Quote
        createQuoteAndFillUpTo(getPolicyDefaultTD(), getGeneralTab().getClass(), true);

        // Add Quoted MPD if required for test
        if(bAddQuotedCompanionPolicyAtNewBusiness){
            addCompanionPolicy_PolicySearch_AddByIndex("Home", "ELASTIC_QUOTED", 0);
        }

        // Continue and bind the NB quote.
        getGeneralTab().submitTab();
        policy.getDefaultView().fillFromTo(getPolicyDefaultTD(), getDriverTab().getClass(), getPurchaseTab().getClass(), true);
        getPurchaseTab().submitTab();

        // Move to before or after NB+30, depending on value of 'bMakeSystemDateOverNB30'
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
        printDebugMessage("Created policy: " + policyNumber);

        // Determine time point to move to.
        LocalDateTime nb30Date;
        if(bMakeSystemDateOverNB30){
            nb30Date = policyEffectiveDate.plusDays(32l);
        }else{
            nb30Date = policyEffectiveDate.plusDays(16l);
        }

        // Go to new time point. Display # of days moved from Effective Date.
        TimeSetterUtil.getInstance().nextPhase(nb30Date);
        Long daysFromEffectiveDate = policyEffectiveDate.until(TimeSetterUtil.getInstance().getCurrentTime(), ChronoUnit.DAYS);
        printDebugMessage("Moved from effective date: " + policyEffectiveDate.toString() + " to NB(" + daysFromEffectiveDate.toString() + ") Date: " + nb30Date.toString());

        //Open App and retrieve policy
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        // Do Endorsement
        handleEndorsementType(true);
        fillFromGeneralTabToErrorMsg();

        // Validate If Error Present / UW Rule Thrown
        if(bMakeSystemDateOverNB30)
        {
            validateMTEBindError();
        }else{
            validateMTEBindErrorDoesNotOccur();
        }

    }

    protected void doMTEPreventBindTest(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppAndCreatePolicy();

        handleEndorsementType(bFlatEndorsement);

        // Add MPD Element via Customer Search
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("ELASTIC_QUOTED");
        otherAAAProductsSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.

        fillFromGeneralTabToErrorMsg();

        // Validate error message appears.
        //errorTab_Verify_ErrorsPresent(true, ErrorEnum.Errors.AAA_SS02012019);
        validateMTEBindError();
    }

    protected void doMTEPreventBindTest_Renewals(String in_policyType, boolean bAmendedRenew){
        // Get into Renewal Image
        createPolicyAdvanceToRenewalImage();

        if(bAmendedRenew) {
            // Provide blank data for renewal image. Complete and save it.
            fillFromGeneralTabToErrorMsg();
            // From policy summary page, begin endorsement on the renewal image.
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }

        // Add MPD Home element.
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("ELASTIC_QUOTED");
        otherAAAProductsSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.

        // Complete Endorsement.
        fillFromGeneralTabToErrorMsg();

        // Validate error message appears.
        validateMTEBindError();
    }

    /**
     * Handles looping through editing an mpd element, throwing the rerating error, validating its presence, re-calculating premium, then ensuring the rerate error is gone. <br>
     *     Returns the state of the test to a loopable position so the method can be called again directly, ending on the Documents and Bind Tab.
     * @param in_newPolicyType
     * @param in_newPolicyNumber
     */
    private void editMPDAndRerate(int index, String in_newPolicyType, String in_newPolicyNumber){
        // Change MPD Policy and Attempt to Purchase
        navigateToGeneralTab();
        otherAAAProducts_EditPolicyInMPDTable(index, in_newPolicyType, in_newPolicyNumber);
        doRerate();
    }

    private void otherAAAProducts_EditPolicyInMPDTable(int index, String newPolicyType, String newPolicyNumber){
        otherAAAProductsTable_getEditLinkByIndex(index).click();
        getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_PolicyTypeEditAsset().setValue(newPolicyType);
        getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_QuotePolicyNumberEditAsset().setValue(newPolicyNumber);
        getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_SaveBtnAsset().click();
    }

    /**
     * After making an edit to the policy, this method drives through validating the UW rule being fired and then validates removing the rule.
     */
    private void doRerate(){
        navigateToDocumentsAndBindTab();
        getDocumentsAndBindTab().submitTab();

        // Validate Error
        ValidateRerateErrorMessage(true);

        // Return to P&C Tab and Re-Rate
        getErrorTab_ButtonCancel().click();
        navigateToPremiumAndCoveragesTab();
        getPnCTab_BtnCalculatePremium().click();

        // Return to Documents and Bind
        navigateToDocumentsAndBindTab();
        //_documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(),
        //        AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getControlClass()).setValue("Physically Signed");
        getDocumentsAndBindTab_BtnPurchase().click(Waiters.AJAX);

        // Validate No UW Error
        ValidateRerateErrorMessage(false);
        Page.dialogConfirmation.buttonNo.click();
    }

    /**
     * Adds MPD policy and calls doRerate().
     * @param in_newPolicyType
     * @param in_newPolicyNumber
     */
    private void addMPDAndRerate(String in_newPolicyType, String in_newPolicyNumber){
        // Change MPD Policy and Attempt to Purchase
        navigateToGeneralTab();
        otherAAAProducts_SearchAndManuallyAddCompanionPolicy(in_newPolicyType, in_newPolicyNumber);
        doRerate();
    }

    /**
     * Removes MPD policy and calls doRerate().
     * @param index
     */
    private void removeMPDAndRerate(int index){
        // Change MPD Policy and Attempt to Purchase
        navigateToGeneralTab();
        otherAAAProductsTable_getRemoveLinkByIndex(0).click();
        doRerate();
    }

    /**
     * Given an index beginning from 0, this will select and add the chosen system returned policy.
     * @param index
     */
    private void otherAAAProductsSearchTable_addSelected(int index){
        new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + index + ":customerSelected")).setValue(true);
        getGeneralTab_OtherAAAProductsOwned_SearchOtherAAAProducts_AddSelectedBtnAsset().click();
    }

    /**
     * Used to search an MPD policy, via Customer Details. Applies provided string over 'First Name' <br>
     * All of the other fields are populated using 'Junk' data, allowing a tester to call the method using only the parameter that controls the wire-mock response.
     * @param searchFieldValue This variable is applied to the First Name field of the Customer Details Search and can manipulate response results. <br>
     */
    private void otherAAAProducts_SearchCustomerDetails_UsePrefilledData(String searchFieldValue){
        otherAAAProducts_SearchCustomerDetails(searchFieldValue,
                "JunkLastName", "01/01/1980", "JunkAddress", "JunkCity", "AZ", "JunkZip");
    }

    private void createPolicyAdvanceToRenewalImage(){
        String policyNumber = openAppAndCreatePolicy();
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime _renewalImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        mainApp().close();

        // Advance JVM to Image Creation Date
        TimeSetterUtil.getInstance().nextPhase(_renewalImageGenDate);
        JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
        JobUtils.executeJob(BatchJob.renewalImageRatingAsyncTaskJob);
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
        JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

        // Go to Policy and Open Renewal Image
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
    }

    private void fillFromGeneralTabToErrorMsg(){
        policy.getDefaultView().fillFromTo(getPolicyTD("Endorsement", "TestData_Empty_Endorsement"),
                getGeneralTab().getClass(), getDocumentsAndBindTab().getClass(), true);
        getDocumentsAndBindTab_BtnPurchase().click();
        Page.dialogConfirmation.buttonYes.click();
    }

    private void handleEndorsementType(boolean bFlatEndorsement){
        if (bFlatEndorsement){
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }else{
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        }
    }

    private void validateMPDCompanionError(String thePolicyType){
        if (!thePolicyType.equalsIgnoreCase(getGeneralTab_OtherAAAProducts_LifePolicyCheckboxLabel()) &&
                !thePolicyType.equalsIgnoreCase(getGeneralTab_PolicyStatusMetaDataLabel())){
            errorTab_Verify_ErrorsPresent( true, ErrorEnum.Errors.MPD_COMPANION_VALIDATION);
        }else {
            assertThat(PolicySummaryPage.labelPolicyNumber.isPresent());
        }
    }

    /**
     * Returns the 'Remove' link object, given an index. <br>
     * @param index Index represents desired Row, where the edit link is contained.
     * @return
     */
    private Link otherAAAProductsTable_getRemoveLinkByIndex(int index) {
        return new Link(By.id("policyDataGatherForm:otherAAAProductsTable:" + index + ":removeMPDPolicyLink"));
    }

    /**
     * Handles validating the error message requiring rerate. Wrapped for short, clean method calls. <br>
     * Has try/catch to handle event where we anticipate no element is found. <br>
     * Will fail attempting to get the object, but assert that the failure was expected and that we're at an expected point.
     * @param bExpected
     */
    private void ValidateRerateErrorMessage(boolean bExpected){
        try{
            String errorCodeValue = getErrorTab_ErrorOverride_ErrorCodeValue();

            if(errorCodeValue.contains("Unprepared data")){
                assertThat(errorCodeValue.contains("Unprepared data")).isEqualTo(bExpected);
            }
            else{
                errorTabOverrideAllErrors();
                getErrorTab_ButtonOverrideAsset().click();
                getDocumentsAndBindTab_BtnPurchase().click();
                assertThat(getErrorTab_ErrorOverride_ErrorCodeValue().contains("Unprepared data")).isEqualTo(bExpected);
            }
        }catch(IstfException ex){
            assertThat(Page.dialogConfirmation.buttonNo.isPresent()).isTrue();
            assertThat(bExpected).isFalse(); // Making sure we were expecting it to be false here.
        }
    }

    /***
     * This method will use an open-ended xpath to capture the total number of checkboxes visible in search results. <br>
     *     This value can be used to count the total number of results returned, due to the difficulty of navigating the MPD Table DOM.
     * @return
     */
    private int getSearchResultsCount(){
        List<WebElement> arrayOfCheckboxesFound = BrowserController.get().driver().findElements(By.xpath(_XPATH_TO_ALL_SEARCH_RESULT_CHECKBOXES));
        return arrayOfCheckboxesFound.size();
    }

    /**
     * Given a list of indexes, this will iterate through the list, select each index as true, then click the add selected button.
     * @param indexList
     */
    private void otherAAAProductsSearchTable_addSelected(int[] indexList){
        for(int index : indexList)
        {
            new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + index + ":customerSelected")).setValue(true);
        }

        getGeneralTab_OtherAAAProductsOwned_ManualPolicyAddButton().click();
    }

    /**
     * Parses the Discount And Surcharges string retrieved from PremiumAndCoveragesTab.discountsAndSurcharges.getValue();
     * @param rawDAndCValue comes from PremiumAndCoveragesTab.discountsAndSurcharges.getValue();
     * @return parsed list of discounts
     */
    private static List<String> parseDiscountAndSurchargesString(String rawDAndCValue){
        String[] parsedValues = rawDAndCValue.split(":")[1].split(",");

        for (String value : parsedValues){
            value = value.trim();
        }

        return Arrays.asList(parsedValues);
    }

    private void otherAAAProducts_SearchAndManuallyAddCompanionPolicy(String policyType, String policyNumber){
        otherAAAProducts_SearchByPolicyNumber(policyType, policyNumber);
        otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound(policyType);
    }

    /**
     * Returns the 'Edit' link object, given an index. <br>
     * @param index Index represents desired Row, where the edit link is contained.
     * @return
     */
    private Link otherAAAProductsTable_getEditLinkByIndex(int index){
        return new Link(By.id("policyDataGatherForm:otherAAAProductsTable:" + index + ":editMPDPolicyLink"));
    }

    /**
     * Creates a new policy with REFRESH_P search and added Life and MC (if AZ)
     */
    private String pas28659_SetupScenario_Template(TestData testData){

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, getGeneralTab().getClass(),true);

        otherAAAProducts_SearchAndManuallyAddCompanionPolicy("Life", "L123456789");

        if (motorcycleSupportedStates.contains(getState())) {
            otherAAAProducts_SearchAndManuallyAddCompanionPolicy("Motorcycle", "M123456789");
        }

        otherAAAProducts_SearchByPolicyNumber("Home", "REFRESH_P");

        // Add home, condo, renters by index.
        otherAAAProductsSearchTable_addSelected(new int[]{0, 1, 2});

        getGeneralTab().submitTab();

        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getDocumentsAndBindTab().getClass(), true);

        documentsAndSetting_setYesToAllRequiredToIssue();

        getDocumentsAndBindTab().submitTab();

        //policy.getDefaultView().fillFromTo(testData, getDocumentsAndBindTab().getClass(), getPurchaseTab().getClass(), true);

        getPurchaseTab().fillTab(testData).submitTab();

        String policyNumber = PolicySummaryPage.getPolicyNumber();

        log.info("Policy " + policyNumber + " was created.");

        return policyNumber;
    }

    /**
     * Jobset needed to process MPD discount validation at NB +30
     */
    private void jobsNBplus30runNoChecks() {
        JobUtils.executeJob(BatchJob.aaaBatchMarkerJob);
        JobUtils.executeJob(BatchJob.aaaAutomatedProcessingInitiationJob);
        JobUtils.executeJob(BatchJob.automatedProcessingRatingJob);
        JobUtils.executeJob(BatchJob.automatedProcessingRunReportsServicesJob);
        JobUtils.executeJob(BatchJob.automatedProcessingIssuingOrProposingJob);
        JobUtils.executeJob(BatchJob.automatedProcessingStrategyStatusUpdateJob);
        JobUtils.executeJob(BatchJob.automatedProcessingBypassingAndErrorsReportGenerationJob);
        log.info("Current application date: " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
    }

    /**
     *
     * @param policyNumber policy number in test
     * @param rowCount which row of the table (how many transactions in history)
     * @param value value to be present in Reason field
     * @param softly for assertions
     */
    private void transactionHistoryRecordCountCheck(String policyNumber, int rowCount, String value, ETCSCoreSoftAssertions softly) {
        PolicySummaryPage.buttonTransactionHistory.click();
        softly.assertThat(PolicySummaryPage.tableTransactionHistory).hasRows(rowCount);

        String valueShort = "";
        if (!StringUtils.isEmpty(value)) {
            valueShort = value.substring(0, 20);
            assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getHintValue()).contains(value);
        }
        softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getValue()).contains(valueShort);

        String transactionHistoryQuery = "select * from(\n"
                + "select pt.TXREASONTEXT\n"
                + "from PolicyTransaction pt\n"
                + "where POLICYID in \n"
                + "        (select id from POLICYSUMMARY \n"
                + "        where POLICYNUMBER = '%s')\n"
                + "    order by pt.TXDATE desc)\n"
                + "    where rownum=1";
        softly.assertThat(DBService.get().getValue(String.format(transactionHistoryQuery, policyNumber)).orElse(StringUtils.EMPTY)).isEqualTo(value);
    }

    /**
     * Preconfigured pairwise scenarios for testing Unquoted or Manual Add scenarios. Generated using AllPairs.exe
     * @return List of scenarios to be iterated through.
     */
    private ArrayList<HashMap <mpdPolicyType, Boolean>> getUnquotedManualScenarios(){
        ArrayList<HashMap <mpdPolicyType, Boolean>> scenarioList = new ArrayList<>();
        /*
        Pair Testing Scenario
        Scenario	Home	Renters	Condo	Life	Motorcycle
        1	        Yes	    Yes 	Yes 	Yes 	Yes
        2	        Yes 	No  	No  	No  	No
        3	        No  	Yes 	No  	Yes 	No
        4	        No  	No  	Yes 	No  	Yes
        5	        ~Yes	Yes 	Yes 	No  	No
        6	        ~Yes	No  	No  	Yes 	Yes
        7	        No  	No  	No  	No  	No
         */

        String currentState = getState();

        // Scenario 1
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.HOME, true);
                put(mpdPolicyType.RENTERS, true);
                put(mpdPolicyType.CONDO, true);
                put(mpdPolicyType.LIFE, true);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.MOTORCYCLE, true);
                }
            }
        });

        // Scenario 2
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.HOME, true);
                put(mpdPolicyType.RENTERS, false);
                put(mpdPolicyType.CONDO, false);
                put(mpdPolicyType.LIFE, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.MOTORCYCLE, false);
                }
            }
        });

        // Scenario 3
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.HOME, false);
                put(mpdPolicyType.RENTERS, true);
                put(mpdPolicyType.CONDO, false);
                put(mpdPolicyType.LIFE, true);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.MOTORCYCLE, false);
                }
            }
        });

        // Scenario 4
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.HOME, false);
                put(mpdPolicyType.RENTERS, false);
                put(mpdPolicyType.CONDO, true);
                put(mpdPolicyType.LIFE, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.MOTORCYCLE, true);
                }
            }
        });

        // Scenario 5
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.HOME, true);
                put(mpdPolicyType.RENTERS, true);
                put(mpdPolicyType.CONDO, true);
                put(mpdPolicyType.LIFE, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.MOTORCYCLE, false);
                }
            }
        });

        // Scenario 6
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.HOME, true);
                put(mpdPolicyType.RENTERS, false);
                put(mpdPolicyType.CONDO, false);
                put(mpdPolicyType.LIFE, true);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.MOTORCYCLE, true);
                }
            }
        });

        // Scenario 7
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.HOME, false);
                put(mpdPolicyType.RENTERS, false);
                put(mpdPolicyType.CONDO, false);
                put(mpdPolicyType.LIFE, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.MOTORCYCLE, false);
                }
            }
        });

        return scenarioList;
    }

    /**
     * Sets the unquoted policy checkboxes based of passed in checkboxMap.
     * @param checkboxMap is what to set each checkbox to. Expects all 5 product keys with bool value where true checks and false unchecks.
     */
    private void setUnquotedCheckboxes(HashMap <mpdPolicyType, Boolean> checkboxMap)throws IllegalArgumentException{

        // Only some states supports MC. If in the list, only check against mpdPolicyType length. Otherwise subtract 1 so no error thrown.
        int adjustForMotorcycle = motorcycleSupportedStates.contains(getState()) ? 0 : 1 ;

        // Check values
        if (checkboxMap.size() != mpdPolicyType.values().length - adjustForMotorcycle){
            throw new IllegalArgumentException("setUnquotedCheckboxes requires that every policy type has a boolean included. " +
                    "Make sure that all values in mpdPolicyType enum are present with associated booleans for checkboxMap");
        }

        for (mpdPolicyType fillInCheckbox : checkboxMap.keySet()) {

            setGeneralTab_OtherAAAProductsOwned_UnquotedCheckbox(fillInCheckbox, checkboxMap.get(fillInCheckbox));
        }
    }

    /**
     * Removes all policies from the Other AAA Products Owned table.
     */
    private void removeAllOtherAAAProductsOwnedTablePolicies(){
        List<Row> rows = getGeneralTab_OtherAAAProductTable().getRows();

        int zeroBasedRowIterator = rows.size() - 1;

        // Start at end of list since table gets smaller
        for (int i = zeroBasedRowIterator; i >= 0; i-- ){
            // Uses cell index due to column not labelled
            rows.get(i).getCell(7).controls.links.get("Remove").click(Waiters.AJAX);
        }
    }

    /**
     * Sets yes on all Require to Issue items.
     */
    private void documentsAndSetting_setYesToAllRequiredToIssue(){
        TestData requiredToIssueList = getDocumentsAndBindTab_getRequiredToIssueAssetList();

        for (String value : requiredToIssueList.getKeys()){
            requiredToIssueList.adjust(value, "Yes");
        }

        getDocumentsAndBindTab_setRequiredToIssueAssetList(requiredToIssueList);
    }

    private void printDebugMessage(String message){
        String formattedText = String.format("<QALOGS> %s <QALOGS>", message);
        log.debug(System.lineSeparator() + formattedText + System.lineSeparator());
    }

}
