package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.*;
import org.testng.annotations.Optional;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiters;
import java.time.LocalDateTime;
import java.util.*;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

@StateList(statesExcept = Constants.States.CA)
public class TestMultiPolicyDiscount extends AutoSSBaseTest {

    public enum mpdPolicyType{
        home, renters, condo, life, motorcycle
    }

    // Add more states here if they get MC policy support.
    private ArrayList<String> motorcycleSupportedStates = new ArrayList<>(Arrays.asList(Constants.States.AZ));

    private GeneralTab _generalTab = new GeneralTab();
    private ErrorTab _errorTab = new ErrorTab();
    private PremiumAndCoveragesTab _pncTab = new PremiumAndCoveragesTab();
    private DocumentsAndBindTab _documentsAndBindTab = new DocumentsAndBindTab();
    private PurchaseTab _purchaseTab = new PurchaseTab();

    /**
     * Make sure various combos of Unquoted Other AAA Products rate properly and are listed in the UI
     * on P&C Page as well as in View Rating Details. AC3
     * @param state the test will run against.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23983")
    public void pas23983_MPD_unquoted_rate_and_show_discounts(@Optional("") String state) {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        ArrayList<HashMap<mpdPolicyType, Boolean>> scenarioList = getUnquotedManualScenarios();

        // Perform the following for each scenario. Done this way to avoid recreating users every scenario.
        for (int i = 0; i < scenarioList.size(); i++ ) {

            HashMap <mpdPolicyType, Boolean> currentScenario = scenarioList.get(i);

            // Set unquoted policies //
            setUnquotedCheckboxes(currentScenario);

            // On first iteration fill in data. Else jump to PnC page
            if (i == 0) {
                // Continue to next tab then move to P&C tab //
                _generalTab.submitTab();

                policy.getDefaultView().fillFromTo(testData, DriverTab.class, PremiumAndCoveragesTab.class, true);
            }
            else {
                NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
            }

            assertSoftly(softly -> {

                // Check in View Rating details for Multi-Policy Discount //
                String mpdDiscountApplied =
                        _pncTab.getRatingDetailsQuoteInfoData().getValue("AAA Multi-Policy Discount");

                // Close the VRD Popup
                PremiumAndCoveragesTab.RatingDetailsView.buttonRatingDetailsOk.click();

                // If any value is true then the VRD MPD Discount should be Yes. Else None.
                String mpdVRDExpectedValue = currentScenario.containsValue(true) ? "Yes" : "None";

                softly.assertThat(mpdDiscountApplied).isEqualTo(mpdVRDExpectedValue);


                // Validate Discount and Surcharges //
                String discountsAndSurcharges = PremiumAndCoveragesTab.discountsAndSurcharges.getValue();

                // Check against any property string. Done this way because only one property type listed in Discounts.
                Boolean propertyValuesPresent =
                        currentScenario.get(mpdPolicyType.home) ||
                                currentScenario.get(mpdPolicyType.renters) ||
                                currentScenario.get(mpdPolicyType.condo);

                Boolean propertyValuePresentInString =
                        discountsAndSurcharges.contains("Home") ||
                                discountsAndSurcharges.contains("Condo") ||
                                discountsAndSurcharges.contains("Renters");

                softly.assertThat(propertyValuePresentInString).isEqualTo(propertyValuesPresent);

                // MC and Life always show if added.
                softly.assertThat(currentScenario.get(mpdPolicyType.motorcycle)).
                        isEqualTo(discountsAndSurcharges.contains("Motorcycle"));

                softly.assertThat(currentScenario.get(mpdPolicyType.life)).
                        isEqualTo(discountsAndSurcharges.contains("Life"));
            });

            // Return to General tab.
            NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        }
    }

    /**
     * This tests that when verified policies are in the list, unquoted options are disabled, and if they are removed, the options are re-enabled.
     * @param state the test will run against.
     * @scenario
     * Prereqs: enterpriseSearchService.enterpriseCustomersSearchUri setup to return all 3 homeowner types on refresh.
     * 1. Using standard test data, create customer, start auto quote, fill up to general tab with default data.
     * 2. Click the refresh button.
     * 3. Verify the unquoted options that come back from refresh are disabled. (Based on prereqs, should be all HO)
     * 4. Remove all the Disabled policies and make sure checkboxes re-enable.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-21481")
    public void pas_21481_MPD_Unquoted_Companion_Product_AC2_AC3(@Optional("") String state) {

        // Step 1
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        // Step 2
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                .click(Waiters.AJAX);

        // Step 3:
        // Note: If following fails on first assert, validate hitting refresh comes back with
        // Home, Renters, and Condo policies. If not, check test pre-reqs have been met.
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE).isEnabled()).isTrue();

        // Only add motorcycle in supported states
        if (motorcycleSupportedStates.contains(getState())){
            assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE).isEnabled()).isTrue();
        }
        else{
            assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE).isPresent()).isFalse();
        }

        // Step 4
        _generalTab.removeAllOtherAAAProductsOwnedTablePolicies();

        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).isEnabled()).isTrue();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).isEnabled()).isTrue();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).isEnabled()).isTrue();
    }



    /**
     * This tests that when unquoted HO policies are checked, that refresh button returned policies replace them in the table and
     * the unquoted options for those become disabled.
     * @param state the test will run against.
     * @scenario
     * Prereqs: enterpriseSearchService.enterpriseCustomersSearchUri setup to return all 3 homeowner types on refresh.
     * 1. Using standard test data, create customer, start auto quote, fill up to general tab with default data.
     * 2. Check unquoted Home, Condo, and Renters.
     * 3. Click the refresh button.
     * 4. Verify the the MDM returned policies replace the unquoted options. (Based on prereqs, should be all HO)
     * 5. Verify the checkboxes are disabled for Home, Renters, and Condo.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-21481")
    public void pas_21481_MPD_Unquoted_Companion_Product_AC5(@Optional("") String state) {

        // Step 1
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        // Step 2
        setUnquotedCheckbox(mpdPolicyType.home, true);
        setUnquotedCheckbox(mpdPolicyType.renters, true);
        setUnquotedCheckbox(mpdPolicyType.condo, true);

        // Step 3
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                .click(Waiters.AJAX);

        // Step 4
        String policyTypeMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE.getLabel();
        String policyStatusMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.STATUS.getLabel();

        // Find row matching policyType, then pull the status cell out of it to assert on.
        String homeStatusColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.home.toString())
                .getCell(policyStatusMetaDataLabel)
                .getValue();

        String rentersStatusColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.renters.toString())
                .getCell(policyStatusMetaDataLabel)
                .getValue();

        String condoStatusColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.condo.toString())
                .getCell(policyStatusMetaDataLabel)
                .getValue();

        // Expected to be replaced with an Active or Quoted status. If not, make sure prereq is met.
        String unexpected = "UNQUOTED";
        assertThat(homeStatusColumnValue).isNotEqualTo(unexpected);
        assertThat(rentersStatusColumnValue).isNotEqualTo(unexpected);
        assertThat(condoStatusColumnValue).isNotEqualTo(unexpected);

        // Step 5
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).isEnabled()).isFalse();
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
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Prevent Unquoted Bind at NB")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-18315")
    public void pas18315_CIO_Prevent_Unquoted_Bind_NB(@Optional("") String state) {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        ArrayList<HashMap<mpdPolicyType, Boolean>> scenarioList = getUnquotedManualScenarios();

        // Perform the following for each scenario. Done this way to avoid recreating users every scenario.
        for (int i = 0; i < scenarioList.size(); i++ ) {

            HashMap <mpdPolicyType, Boolean> currentScenario = scenarioList.get(i);

            // Set unquoted policies //
            setUnquotedCheckboxes(currentScenario);

            // On first iteration fill in data. Else jump to Documents & Bind page
            if (i == 0) {
                // Continue to next tab then move to Documents & Bind Tab tab //
                _generalTab.submitTab();

                policy.getDefaultView().fillFromTo(testData, DriverTab.class, DocumentsAndBindTab.class, true);
            }
            else {
                NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
                _pncTab.btnCalculatePremium().click(Waiters.AJAX);
                NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

                // Ensure Physically sign Auto Insurance Application set
                _documentsAndBindTab.getRequiredToBindAssetList().getAsset(
                        AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)
                        .setValue("Physically Signed");
            }

            // Attempt to bind
            DocumentsAndBindTab.btnPurchase.click(Waiters.AJAX);

            // If any unquoted was checked ("true") then error message appears.
            if (currentScenario.containsValue(true)) {
                // Hard stop error page should be present
                String errorMsg = _errorTab.tableErrors.
                        getRow("Code", "MPD_COMPANION_UNQUOTED_VALIDATION").
                        getCell("Message").getValue();

                assertThat(errorMsg).startsWith("Policy cannot be bound with an unquoted companion policy.");

                _errorTab.cancel(true);
            }else{
                // Purchase confirmation should be present
                Button buttonNo = DocumentsAndBindTab.confirmPurchase.buttonNo;
                assertThat(buttonNo.isPresent() && buttonNo.isVisible()).isTrue();
                buttonNo.click(Waiters.AJAX);
            }

            // Return to General tab.
            NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        }
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
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Prevent Unquoted Bind during Endorsment")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-18315")
    public void pas18315_CIO_Prevent_Unquoted_Bind_Endorsment(@Optional("") String state) {
        // Step 1
        openAppCreatePolicy();

        // Step 2
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        // Step 3 (Using the first scenario which is check all)
        setUnquotedCheckboxes(getUnquotedManualScenarios().get(0));

        // Step 4
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        _pncTab.btnCalculatePremium().click(Waiters.AJAX);

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        _documentsAndBindTab.submitTab();

        // Step 5
        String errorMsg = _errorTab.tableErrors.
                getRow("Code", "MPD_COMPANION_UNQUOTED_VALIDATION").
                getCell("Message").getValue();

        assertThat(errorMsg).startsWith("Policy cannot be bound with an unquoted companion policy.");
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
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Prevent Unquoted Bind during Amended Renewal")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-18315")
    public void pas18315_CIO_Prevent_Unquoted_Bind_Amended_Renewal(@Optional("") String state) {
        // Step 1
        openAppCreatePolicy();

        // Step 2
        policy.createRenewal(getPolicyTD("InitiateRenewalEntry", "TestData"));

        // Step 3 (Using the first scenario which is check all)
        setUnquotedCheckboxes(getUnquotedManualScenarios().get(0));

        // Step 4
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        _pncTab.btnCalculatePremium().click(Waiters.AJAX);

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        _documentsAndBindTab.submitTab();

        // Step 5
        String errorMsg = _errorTab.tableErrors.
                getRow("Code", "MPD_COMPANION_UNQUOTED_VALIDATION").
                getCell("Message").getValue();

        assertThat(errorMsg).startsWith("Policy cannot be bound with an unquoted companion policy.");
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC1_1(@Optional("") String state) {
        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        // Add second NI
        _generalTab.addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        // Move to documents and bind tab.
        _generalTab.submitTab();

        policy.getDefaultView().fillFromTo(testData, DriverTab.class, DocumentsAndBindTab.class, true);

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());

        // Remove Second NI
        _generalTab.removeInsured(2);

        // Attempt to Bind
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        _documentsAndBindTab.submitTab();

        // Check for error.
        String errorMsg = _errorTab.tableErrors.
                getRow("Code", "Unprepared data").
                getCell("Message").getValue();

        assertThat(errorMsg).startsWith("Cannot issue policy which was not rated!");
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC1_2(@Optional("") String state) {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        // Add second NI
        _generalTab.addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        // Trigger refresh
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                .click(Waiters.AJAX);

        // Move to documents and bind tab.
        _generalTab.submitTab();

        policy.getDefaultView().fillFromTo(testData, DriverTab.class, DocumentsAndBindTab.class, true);

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());

        // Remove Second NI
        _generalTab.removeInsured(2);

        // Attempt to Bind
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        _documentsAndBindTab.submitTab();

        // Check for error.
        String errorMsg = _errorTab.tableErrors.
                getRow("Code", "Unprepared data").
                getCell("Message").getValue();

        assertThat(errorMsg).startsWith("Cannot issue policy which was not rated!");
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC2_1(@Optional("") String state) {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab.
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        // Add second NI
        _generalTab.addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        // Trigger refresh
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                .click(Waiters.AJAX);

        // Complete purchase
        _generalTab.submitTab();

        policy.getDefaultView().fillFromTo(testData, DriverTab.class, PurchaseTab.class, true);

        _purchaseTab.submitTab();

        // Start endorsement
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        _generalTab.removeInsured(2);

        // Pull customer names out of table
        String policyTypeMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE.getLabel();
        String customerNameDOBMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.CUSTOMER_NAME_DOB.getLabel();

        // Find row matching policyType, then pull the status cell out of it to assert on.
        String homeStatusColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.home.toString())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        String rentersStatusColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.renters.toString())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        String condoStatusColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.condo.toString())
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC2_2(@Optional("") String state) {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab.
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        // Add second NI
        _generalTab.addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");

        // Trigger refresh
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH)
                .click(Waiters.AJAX);

        // Complete purchase
        _generalTab.submitTab();

        policy.getDefaultView().fillFromTo(testData, DriverTab.class, PurchaseTab.class, true);

        _purchaseTab.submitTab();

        // Start renewal
        policy.createRenewal(getPolicyTD("InitiateRenewalEntry", "TestData"));

        // Remove driver 2
        _generalTab.removeInsured(2);

        // Pull customer names out of table
        String policyTypeMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE.getLabel();
        String customerNameDOBMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.CUSTOMER_NAME_DOB.getLabel();

        // Find row matching policyType, then pull the status cell out of it to assert on.
        String homeStatusColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.home.toString())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        String rentersStatusColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.renters.toString())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        String condoStatusColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.condo.toString())
                .getCell(customerNameDOBMetaDataLabel)
                .getValue();

        // Verify no refresh on table by checking Peter Parker has not reverted to default response
        String expectedName = "PETER PARKER";
        assertThat(homeStatusColumnValue).startsWith(expectedName);
        assertThat(rentersStatusColumnValue).startsWith(expectedName);
        assertThat(condoStatusColumnValue).startsWith(expectedName);
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

            setUnquotedCheckbox(fillInCheckbox, checkboxMap.get(fillInCheckbox));
        }
    }

    /**
     * Sets an individual checkbox to whatever is passed in.
     * @param policyType is which policy type unquoted box to fill in.
     * @param fillInCheckbox true = check, false = uncheck.
     */
    private void setUnquotedCheckbox(mpdPolicyType policyType, Boolean fillInCheckbox){

        GeneralTab generalTab = new GeneralTab();

        switch (policyType){
            case condo:
                generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).setValue(fillInCheckbox);
                break;

            case home:
                generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).setValue(fillInCheckbox);
                break;

            case renters:
                generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).setValue(fillInCheckbox);
                break;

            case life:
                generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE).setValue(fillInCheckbox);
                break;

            case motorcycle:
                generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE).setValue(fillInCheckbox);
                break;
        }
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
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, true);
                put(mpdPolicyType.condo, true);
                put(mpdPolicyType.life, true);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, true);
                }
            }
        });

        // Scenario 2
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, false);
                }
            }
        });

        // Scenario 3
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, false);
                put(mpdPolicyType.renters, true);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, true);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, false);
                }
            }
        });

        // Scenario 4
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, false);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, true);
                put(mpdPolicyType.life, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, true);
                }
            }
        });

        // Scenario 5
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, true);
                put(mpdPolicyType.condo, true);
                put(mpdPolicyType.life, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, false);
                }
            }
        });

        // Scenario 6
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, true);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, true);
                }
            }
        });

        // Scenario 7
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, false);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, false);
                }
            }
        });

        return scenarioList;
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
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Trigger Re-rate event when companion policies are edited or removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24021")
    public void pas24021_MPD_ValidateRerateRuleFires(@Optional("") String state) {

        // Using default test data.
        TestData testData = getPolicyTD();

        // Set pre-conditions by creating a quote, rating and filling up to purchase.
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);
        _generalTab.mpd_SearchAndAddManually("Home", "NOT_FOUND");

        // Added MPD element, filling up to purchase point. Includes hacky methods to get around system error.
        policy.getDefaultView().fillFromTo(testData, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();
        ErrorTab.buttonCancel.click();

        editMPDAndRerate(0, "Renters", "ABC1"); // Editing only the policyType in this scenario.
        editMPDAndRerate(0, "Renters", "XYZ2"); // Editing only the policyNumber in this scenario.

        addMPDAndRerate("Home", "NOT_FOUND");

        removeMPDAndRerate(0);
    }

    /**
     * Handles looping through editing an mpd element, throwing the rerating error, validating its presence, re-calculating premium, then ensuring the rerate error is gone. <br>
     *     Returns the state of the test to a loopable position so the method can be called again directly, ending on the Documents and Bind Tab.
     * @param in_newPolicyType
     * @param in_newPolicyNumber
     */
    private void editMPDAndRerate(int index, String in_newPolicyType, String in_newPolicyNumber){
        // Change MPD Policy and Attempt to Purchase
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        _generalTab.mpd_EditPolicyInMPDTable(index, in_newPolicyType, in_newPolicyNumber);
        doRerate();
    }

    /**
     * Removes MPD policy and calls doRerate().
     * @param index
     */
    private void removeMPDAndRerate(int index){
        // Change MPD Policy and Attempt to Purchase
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        _generalTab.mpdTable_getRemoveLinkByIndex(0).click();
        doRerate();
    }

    /**
     * Adds MPD policy and calls doRerate().
     * @param in_newPolicyType
     * @param in_newPolicyNumber
     */
    private void addMPDAndRerate(String in_newPolicyType, String in_newPolicyNumber){
        // Change MPD Policy and Attempt to Purchase
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
        _generalTab.mpd_SearchAndAddManually(in_newPolicyType, in_newPolicyNumber);
        doRerate();
    }

    /**
     * Handles validating the error message requiring rerate. Wrapped for short, clean method calls. <br>
     * Has try/catch to handle event where we anticipate no element is found. <br>
     * Will fail attempting to get the object, but assert that the failure was expected and that we're at an expected point.
     * @param bExpected
     */
    private void ValidateRerateErrorMessage(boolean bExpected){
        try{
            if(_errorTab.tableErrors.getColumn(AutoSSMetaData.ErrorTab.ErrorsOverride.CODE.getLabel()).getValue().toString().contains("Unprepared data")){
                CustomAssertions.assertThat(_errorTab.tableErrors.getColumn(AutoSSMetaData.ErrorTab.ErrorsOverride.CODE.getLabel()).getValue().toString().contains("Unprepared data")).isEqualTo(bExpected);
            }
            else{
                _errorTab.overrideAllErrors();
                _errorTab.buttonOverride.click();
                _documentsAndBindTab.btnPurchase.click();
                CustomAssertions.assertThat(_errorTab.tableErrors.getColumn(AutoSSMetaData.ErrorTab.ErrorsOverride.CODE.getLabel()).getValue().toString().contains("Unprepared data")).isEqualTo(bExpected);
            }
        }catch(IstfException ex){
            CustomAssertions.assertThat(Page.dialogConfirmation.buttonNo.isPresent()).isTrue();
            CustomAssertions.assertThat(bExpected).isFalse(); // Making sure we were expecting it to be false here.
        }
    }

    /**
     * After making an edit to the policy, this method drives through validating the UW rule being fired and then validates removing the rule.
     */
    private void doRerate(){
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        _documentsAndBindTab.submitTab();

        // Validate Error
        ValidateRerateErrorMessage(true);

        // Return to P&C Tab and Re-Rate
        _errorTab.buttonCancel.click();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        _pncTab.btnCalculatePremium().click();

        // Return to Documents and Bind
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        _documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getLabel(),
                AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION.getControlClass()).setValue("Physically Signed");
        _documentsAndBindTab.btnPurchase.click();

        // Validate No UW Error
        ValidateRerateErrorMessage(false);
        Page.dialogConfirmation.buttonNo.click();
    }

    /**
     * Validates that the MPD Companion Validation Error occurs when manually adding a 'Home/Renters/Condo' MPD policy to a quote.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 2min
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
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
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
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
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas3649_MPD_ValidateEligibility_Renewal_Home(@Optional("") String state) {
        doMPDEligibilityTest_Renewal("Condo");
    }

    private void doMPDEligibilityTest(String in_policyType){
        // Using default test data.
        TestData testData = getPolicyTD();

        // Add MPD Element manually (after no results found)
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);
        _generalTab.mpd_SearchAndAddManually(in_policyType, "NOT_FOUND");

        // Continue towards purchase of quote.
        policy.getDefaultView().fillFromTo(testData, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    private void doMPDEligibilityTest_MidTerm(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppCreatePolicy();

        handleEndorsementType(bFlatEndorsement);

        _generalTab.mpd_SearchAndAddManually(in_policyType, "NOT_FOUND");
        fillFromGeneralTabToErrorMsg();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    private void doMPDEligibilityTest_Renewal(String in_policyType){
        // Create Policy
        createPolicyAdvanceToRenewalImage();

        // In Renewal Image, Add MPD Element and Bind
        _generalTab.mpd_SearchAndAddManually(in_policyType, "NOT_FOUND");
        fillFromGeneralTabToErrorMsg();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Need ability to prevent MTE bind with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23456")
    public void pas23456_MPD_Prevent_MTEBind(@Optional("") String state) {
        doMTEPreventBindTest(false, "Home");
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Need ability to prevent MTE bind with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23456")
    public void pas23456_MPD_Allow_MTEBind(@Optional("") String state) {
        doMTEAllowBindTest(false, "Home");
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas23456_MPD_Prevent_Renewal(@Optional("") String state) {
        doMTEPreventBindTest_Renewals("Renters", false);
    }

    /**
     * Validates that a NB policy can be bound when adding a System-Validated Home policy.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas23456_MPD_Allow_NBBindWithSystemValidatedPolicy(@Optional("") String state) {
        TestData testData = getPolicyTD();
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);
        _generalTab.mpd_SearchCustomerDetails("ELASTIC_QUOTED");
        _generalTab.mpdSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.
        policy.getDefaultView().fillFromTo(testData, GeneralTab.class, PurchaseTab.class, true);
        PurchaseTab.btnApplyPayment.click();
        Page.dialogConfirmation.buttonYes.click();
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus.getValue().contains("Active")).isTrue();
    }

    private void doMTEPreventBindTest(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppCreatePolicy();

        handleEndorsementType(bFlatEndorsement);

        // Add MPD Element via Customer Search
        _generalTab.mpd_SearchCustomerDetails("ELASTIC_QUOTED");
        _generalTab.mpdSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.

        fillFromGeneralTabToErrorMsg();

        // Validate error message appears.
        validateMTEBindError();
    }

    private void doMTEAllowBindTest(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppCreatePolicy();

        handleEndorsementType(bFlatEndorsement);

        // Add MPD Element via Customer Search
        _generalTab.mpd_SearchCustomerDetails("CUSTOMER_E");
        _generalTab.mpdSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.
        _generalTab.mpd_SearchCustomerDetails("CUSTOMER_NE");
        _generalTab.mpdSearchTable_addSelected(1);

        fillFromGeneralTabToErrorMsg();

        // Validate error message appears.
        validateMTEBindErrorDoesNotOccur();
    }

    private void doMTEPreventBindTest_Renewals(String in_policyType, boolean bAmendedRenew){
        // Get into Renewal Image
        createPolicyAdvanceToRenewalImage();

        if(bAmendedRenew) {
            // Provide blank data for renewal image. Complete and save it.
            fillFromGeneralTabToErrorMsg();
            // From policy summary page, begin endorsement on the renewal image.
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }

        // Add MPD Home element.
        _generalTab.mpd_SearchCustomerDetails("ELASTIC_QUOTED");
        _generalTab.mpdSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.

        // Complete Endorsement.
        fillFromGeneralTabToErrorMsg();

        // Validate error message appears.
        validateMTEBindError();
    }

    private void createPolicyAdvanceToRenewalImage(){
        String policyNumber = openAppCreatePolicy();
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime _renewalImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        mainApp().close();

        // Advance JVM to Image Creation Date
        TimeSetterUtil.getInstance().nextPhase(_renewalImageGenDate);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        // Go to Policy and Open Renewal Image
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        Tab.buttonGo.click();
        Tab.buttonOk.click();
        Page.dialogConfirmation.buttonOk.click();
    }

    private String openAppCreatePolicy(){
        TestData td = getPolicyDefaultTD();
        mainApp().open();
        createCustomerIndividual();
        return createPolicy(td);
    }

    private void validateMPDCompanionError(String thePolicyType){
        if (!thePolicyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !thePolicyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
            new ErrorTab().verify.errorsPresent(true, ErrorEnum.Errors.MPD_COMPANION_VALIDATION);
        }else {
            CustomAssertions.assertThat(PolicySummaryPage.labelPolicyNumber.isPresent());
        }
    }

    private void validateMTEBindError(){
        new ErrorTab().verify.errorsPresent(true, ErrorEnum.Errors.AAA_SS02012019);
    }

    private void validateMTEBindErrorDoesNotOccur(){
        try{
            new ErrorTab().verify.errorsPresent(false, ErrorEnum.Errors.AAA_SS02012019);
        }catch(IstfException ex){
            CustomAssertions.assertThat(ex.getMessage()).isEqualToIgnoringCase("Column Code was not found in the table");
        }
    }

    private void fillFromGeneralTabToErrorMsg(){
        policy.getDefaultView().fillFromTo(getPolicyTD("Endorsement", "TestData_Empty_Endorsement"), GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();
        Page.dialogConfirmation.buttonYes.click();
    }

    private void handleEndorsementType(boolean bFlatEndorsement){
        if (bFlatEndorsement){
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }else{
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        }
    }

    /**
     * This test validates that adding an unquoted HOME companion product appears on a generated AH11AZ document.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC1(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);
        _generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).setValue(true);
        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Home)");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "HOME-UNQUOTED");
    }

    /**
     * This test validates that adding an unquoted RENTERS companion product appears on a generated AH11AZ document.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC1dot1(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);
        _generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).setValue(true);
        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Renters)");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "RENTERS-UNQUOTED");
    }

    /**
     * This test validates that adding an unquoted CONDO companion product appears on a generated AH11AZ document.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC1dot2(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);
        _generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).setValue(true);
        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Condo)");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "CONDO-UNQUOTED");
    }

    /**
     * This test validates that adding multiple unquoted companion product appears on a generated AH11AZ document.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC1dot3(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);
        _generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).setValue(true);
        _generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).setValue(true);
        _generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).setValue(true);
        _generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE).setValue(true);
        _generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE).setValue(true);
        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Life, Motorcycle, Home)");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "HOME-UNQUOTED");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "LIFE-UNQUOTED");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "MOTORCYCLE-UNQUOTED");
    }

    /**
     * This test validates that two quoted companion products of different types appear on a generated AH11AZ document.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC2(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Quoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        _generalTab.mpd_SearchCustomerDetails("ELASTIC_QUOTED");
        _generalTab.mpdSearchTable_addSelected(0);
        _generalTab.mpd_SearchCustomerDetails("ELASTIC_QUOTED");
        _generalTab.mpdSearchTable_addSelected(1);

        String policyTypeMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE.getLabel();
        String policyNumberMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_NUMBER.getLabel();

        // Find row in MPD table matching policyType, then pull the policy num / address cell out of it to assert on. Remove address with split.
        String homeCompanionPolicyNumberColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.home.toString())
                .getCell(policyNumberMetaDataLabel)
                .getValue().split("\\n")[0];

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Home)");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, homeCompanionPolicyNumberColumnValue); //"QAZH3206557376"); //Asserts that the Mockwire Home policy shows up instead of the Renters policy.
    }

    /**
     * This test validates that two agent entered companion products of same types appear on a generated AH11AZ document.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC3(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        _generalTab.mpd_SearchByPolicyNumber("Home", "NOT_FOUND");
        _generalTab.mpd_ManuallyAddPolicyAfterNoResultsFound("Home", "TestHome_FirstAdded");
        _generalTab.mpd_SearchByPolicyNumber("Home", "NOT_FOUND");
        _generalTab.mpd_ManuallyAddPolicyAfterNoResultsFound("Home", "TestHome_SecondAdded");

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Home)");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "TestHome_FirstAdded"); //Asserts that the Mockwire Home policy shows up instead of the Renters policy.
    }

    /**
     * This test validates that ineligible companion products do not appear on a generated AH11AZ document.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC4(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        _generalTab.mpd_SearchCustomerDetails("CUSTOMER_NE");
        _generalTab.mpdSearchTable_addSelected(0);
        _generalTab.mpd_SearchCustomerDetails("CUSTOMER_NE");
        _generalTab.mpdSearchTable_addSelected(1);
        _generalTab.mpd_SearchCustomerDetails("CUSTOMER_NE");
        _generalTab.mpdSearchTable_addSelected(2);

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, document.getId(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
        try {
            CustomAssertions.assertThat(DocGenHelper.getDocument(document, query).toString().contains("Multi-Policy Discount")).isFalse();
        }catch(NoSuchElementException ex){
            CustomAssertions.assertThat(ex).hasMessage("No value present");
        }
    }

    /**
     * This test validates that an eligible home, life and motorcycle companion products appear on a generated AH11AZ document.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC5(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        _generalTab.mpd_SearchCustomerDetails("ELASTIC_QUOTED");
        _generalTab.mpdSearchTable_addSelected(0);
        _generalTab.mpd_SearchAndAddManually("Motorcycle", "NOT_FOUND");
        _generalTab.mpd_SearchByPolicyNumber("Life", "NOT_FOUND");
        _generalTab.mpd_ManuallyAddPolicyAfterNoResultsFound( "Life", "TestLife");

        String policyTypeMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE.getLabel();
        String policyNumberMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_NUMBER.getLabel();

        // Find row in MPD table matching policyType, then pull the policy num / address cell out of it to assert on. Remove address with split.
        String homeCompanionPolicyNumberColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.home.toString())
                .getCell(policyNumberMetaDataLabel)
                .getValue().split("\\n")[0];

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Motorcycle, Life, Home)");

        // Checking Affinity Group Section for Listed Policies.
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, homeCompanionPolicyNumberColumnValue);
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "NOT_FOUND");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "TestLife");
    }

    /**
     * This test validates that an ineligible companion product does not appear on a generated AH11AZ document, but the life policy does.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC6(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        _generalTab.mpd_SearchCustomerDetails("CUSTOMER_NE");
        _generalTab.mpdSearchTable_addSelected(0);
        _generalTab.mpd_SearchAndAddManually("Life", "TestLifePolicy");

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Life)");
        DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "TestLifePolicy");

    }
}
