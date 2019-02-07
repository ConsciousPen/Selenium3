package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import toolkit.utils.screenshots.ScreenshotManager;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiters;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

@StateList(states = Constants.States.AZ)
public class TestMultiPolicyDiscount extends AutoSSBaseTest {

    public enum mpdPolicyType{
        home, renters, condo, life, motorcycle
    }
    private GeneralTab _generalTab = new GeneralTab();
    private ErrorTab _errorTab = new ErrorTab();
    private PremiumAndCoveragesTab _pncTab = new PremiumAndCoveragesTab();
    private DocumentsAndBindTab _documentsAndBindTab = new DocumentsAndBindTab();

    /**
     * Make sure various combos of Unquoted Other AAA Products rate properly and are listed in the UI
     * on P&C Page as well as in View Rating Details. AC3
     * @param state the test will run against.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23983")
    public void pas23983_MPD_unquoted_rate_and_show_discounts(@Optional("") String state) {

        // Data and tools setup
        TestData testData = getPolicyTD();
        GeneralTab generalTab = new GeneralTab();
        PremiumAndCoveragesTab pncTab = new PremiumAndCoveragesTab();

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
                generalTab.submitTab();

                policy.getDefaultView().fillFromTo(testData, DriverTab.class, PremiumAndCoveragesTab.class, true);
            }
            else {
                NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
            }

            assertSoftly(softly -> {

                // Check in View Rating details for Multi-Policy Discount //
                String mpdDiscountApplied =
                        pncTab.getRatingDetailsQuoteInfoData().getValue("AAA Multi-Policy Discount");

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
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-21481")
    public void pas_21481_MPD_Unquoted_Companion_Product_AC2_AC3(@Optional("") String state) {

        // Step 1
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        // Step 2
        Button refreshButton = _generalTab.getOtherAAAProductOwnedAssetList().getAsset(
                AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH.getLabel(),
                AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH.getControlClass());

        refreshButton.click(Waiters.AJAX);

        // Step 3:
        // Note: If following fails on first assert, validate hitting refresh comes back with
        // Home, Renters, and Condo policies. If not, check test pre-reqs have been met.
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE).isEnabled()).isTrue();

        // Motorcycle in AZ only
        if (getState().equals("AZ")){
            assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE).isEnabled()).isTrue();
        }
        else{
            assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE).isPresent()).isFalse();
        }

        // Step 4
        // This for loop might need to be refactored as method could get rejected.
        for(int i = 0; i < 3; i++) {
            _generalTab.mpdTable_getRemoveLinkByIndex(0).click(Waiters.AJAX);
        }

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
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
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
        Button refreshButton = _generalTab.getOtherAAAProductOwnedAssetList().getAsset(
                AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH.getLabel(),
                AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH.getControlClass());

        refreshButton.click(Waiters.AJAX);

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

        // Note: If following fails on first assert with Actual:"UNQUOTED", validate hitting refresh comes back with
        // Home, Renters, and Condo policies. If not, check test pre-reqs have been met.

        // Unquoted show up as UNQUOTED status. All should be ACTIVE if came from Refresh button.
        assertThat(homeStatusColumnValue).isEqualTo("ACTIVE");
        assertThat(rentersStatusColumnValue).isEqualTo("ACTIVE");
        assertThat(condoStatusColumnValue).isEqualTo("ACTIVE");

        // Step 5
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).isEnabled()).isFalse();
        assertThat(_generalTab.getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).isEnabled()).isFalse();
    }

    /**
     * Sets the unquoted policy checkboxes based of passed in checkboxMap.
     * @param checkboxMap is what to set each checkbox to. Expects all 5 product keys with bool value where true checks and false unchecks.
     */
    private void setUnquotedCheckboxes(HashMap <mpdPolicyType, Boolean> checkboxMap)throws IllegalArgumentException{
        // Check values
        if (checkboxMap.size() != mpdPolicyType.values().length){
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

        // Scenario 1
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, true);
                put(mpdPolicyType.condo, true);
                put(mpdPolicyType.life, true);
                put(mpdPolicyType.motorcycle, true);
            }
        });

        // Scenario 2
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, false);
                put(mpdPolicyType.motorcycle, false);}
        });

        // Scenario 3
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, false);
                put(mpdPolicyType.renters, true);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, true);
                put(mpdPolicyType.motorcycle, false);}
        });

        // Scenario 4
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, false);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, true);
                put(mpdPolicyType.life, false);
                put(mpdPolicyType.motorcycle, true);}
        });

        // Scenario 5
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, true);
                put(mpdPolicyType.condo, true);
                put(mpdPolicyType.life, false);
                put(mpdPolicyType.motorcycle, false);}
        });

        // Scenario 6
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, true);
                put(mpdPolicyType.motorcycle, true);}
        });

        // Scenario 7
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, false);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, false);
                put(mpdPolicyType.motorcycle, false);}
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
        Page.dialogConfirmation.buttonNo.click();

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
    private void ValidateErrorMessage(boolean bExpected){
        try{
            CustomAssertions.assertThat(_errorTab.tableErrors.getColumn(AutoSSMetaData.ErrorTab.ErrorsOverride.CODE.getLabel()).getValue().toString().contains("Unprepared data")).isEqualTo(bExpected);
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
        ValidateErrorMessage(true);

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
        ValidateErrorMessage(false);
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
    public void pas24729_MPD_ValidateEligibilityRuleFires_Home(@Optional("") String state) {
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
    public void pas24729_MPD_ValidateEligibilityRuleFires_Life(@Optional("") String state) {
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
    public void pas24729_MPD_ValidateEligibility_MidTerm_Renters(@Optional("") String state) {
        doMPDEligibilityTest_MidTerm(false, "Renters");
    }

    /**
     * Validates that the MPD Companion Validation Error occurs when manually adding a 'Home/Renters/Condo' MPD policy to a Renewal Image.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 4-7min
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas24729_MPD_ValidateEligibility_Renewal_Home(@Optional("") String state) {
        doMPDEligibilityTest_Renewal("Condo");
    }

    private void doMPDEligibilityTest(String in_policyType){
        // Using default test data.
        TestData testData = getPolicyTD();

        // Add MPD Element manually (after no results found)
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);
        _generalTab.mpd_SearchAndAddManually(in_policyType, "NOT_FOUND");
        //doScreenshot("DoMPDEligibilityTest", in_policyType, "AddedMPD");

        // Continue towards purchase of quote.
        policy.getDefaultView().fillFromTo(testData, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        //doScreenshot("DoMPDEligibilityTest", in_policyType, "ErrorValidation");
        validateMPDCompanionError(in_policyType);
    }

    private void doMPDEligibilityTest_MidTerm(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppCreatePolicy();

        if (bFlatEndorsement){
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }else{
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        }

        _generalTab.mpd_SearchAndAddManually(in_policyType, "NOT_FOUND");
        //doScreenshot("DoMPDEligibilityTest_MidTerm", in_policyType, "AddedMPD");
        policy.getDefaultView().fillFromTo(getPolicyTD("Endorsement", "TestData_Empty_Endorsement"), GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();
        Page.dialogConfirmation.buttonYes.click();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        //doScreenshot("DoMPDEligibilityTest_MidTerm", in_policyType, "ErrorValidation");
        validateMPDCompanionError(in_policyType);
    }

    private void doMPDEligibilityTest_Renewal(String in_policyType){
        // Create Policy
        createPolicyAdvanceToRenewalImage();

        // In Renewal Image, Add MPD Element and Bind
        _generalTab.mpd_SearchAndAddManually(in_policyType, "NOT_FOUND");
        policy.getDefaultView().fillFromTo(getPolicyTD("Endorsement", "TestData_Empty_Endorsement"), GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();
        Page.dialogConfirmation.buttonYes.click();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    /**
     * Plug this method in whenever you want to use automation to facilitate grabbing screenshots. <br>
     *     Captures the entire web browser rather than only what's visible at the moment. (E.G. Scroll Bars) <br>
     *         !!DO NOT PROVIDE SPACES IN STRINGS!!
     * @param testName Name of test class being executed.
     * @param fileName Name of specific test method being executed.
     * @param extraNotes Details of specific test operation being captured by screenshot.
     */
    private void doScreenshot(String testName, String fileName, String extraNotes){
        ScreenshotManager.getInstance().makeScreenshot(String.format("%s_%s_%s", testName, fileName, extraNotes));
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Need ability to prevent MTE bind with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23456")
    public void pas23456_MPD_Prevent_MTEBind(@Optional("") String state) {
        doMTEPreventBindTest(false, "Home");
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas23456_MPD_Prevent_Renewal(@Optional("") String state) {
        doMTEPreventBindTest_Renewals("Renters", false);
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas23456_MPD_Prevent_AmendedRenewal(@Optional("") String state) {
        doMTEPreventBindTest_Renewals("Home", true);
    }

    private void doMTEPreventBindTest(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppCreatePolicy();

        if (bFlatEndorsement){
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }else{
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        }

        // Add MPD Element via Customer Search
        _generalTab.mpd_SearchCustomerDetails("CUSTOMER_E");
        _generalTab.mpdSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.

        policy.getDefaultView().fillFromTo(getPolicyTD("Endorsement", "TestData_Empty_Endorsement"), GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();
        Page.dialogConfirmation.buttonYes.click();

        // Validate error message appears.
        validateMTEBindError(in_policyType);
    }

    private void doMTEPreventBindTest_Renewals(String in_policyType, boolean bAmendedRenew){
        // Get into Renewal Image
        createPolicyAdvanceToRenewalImage();

        if(bAmendedRenew) {
            // Provide blank data for renewal image. Complete and save it.
            policy.getDefaultView().fillFromTo(getPolicyTD("Endorsement", "TestData_Empty_Endorsement"), GeneralTab.class, DocumentsAndBindTab.class, true);
            _documentsAndBindTab.btnPurchase.click();
            Page.dialogConfirmation.buttonYes.click();
            // From policy summary page, begin endorsement on the renewal image.
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }

        // Add MPD Home element.
        _generalTab.mpd_SearchCustomerDetails("CUSTOMER_E");
        _generalTab.mpdSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.

        // Complete Endorsement.
        policy.getDefaultView().fillFromTo(getPolicyTD("Endorsement", "TestData_Empty_Endorsement"), GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();
        Page.dialogConfirmation.buttonYes.click();

        // Validate error message appears.
        validateMTEBindError(in_policyType);
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

    private void validateMTEBindError(String thePolicyType){
        new ErrorTab().verify.errorsPresent(true, ErrorEnum.Errors.AAA_SS02012019);
    }
}
