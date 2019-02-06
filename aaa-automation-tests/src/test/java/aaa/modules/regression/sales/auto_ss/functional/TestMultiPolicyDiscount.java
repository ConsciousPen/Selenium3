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
import toolkit.utils.screenshots.ScreenshotManager;
import aaa.main.enums.ErrorEnum;
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
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

@StateList(states = Constants.States.AZ)
public class TestMultiPolicyDiscount extends AutoSSBaseTest {

    public enum mpdPolicyType{
        home, renters, condo, life, motorcycle
    }
    GeneralTab _generalTab = new GeneralTab();
    ErrorTab _errorTab = new ErrorTab();
    PremiumAndCoveragesTab _pncTab = new PremiumAndCoveragesTab();
    DocumentsAndBindTab _documentsAndBindTab = new DocumentsAndBindTab();

    private AssetDescriptor<CheckBox> _homeCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME;
    private AssetDescriptor<CheckBox> _rentersCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS;
    private AssetDescriptor<CheckBox> _condoCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO;
    private AssetDescriptor<CheckBox> _lifeCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE;
    private AssetDescriptor<CheckBox> _motorCycleCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE;


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
     * Sets the unquoted policy checkboxes based of passed in checkboxMap.
     * @param checkboxMap is what to set each checkbox to. Expects all 5 product keys with bool value where true checks and false unchecks.
     */
    private void setUnquotedCheckboxes(HashMap <mpdPolicyType, Boolean> checkboxMap)throws IllegalArgumentException{
        // Check values
        if (checkboxMap.size() != mpdPolicyType.values().length){
            throw new IllegalArgumentException("setUnquotedCheckboxes requires that every policy type has a boolean included. " +
                    "Make sure that all values in mpdPolicyType enum are present with associated booleans for checkboxMap");
        }

        GeneralTab generalTab = new GeneralTab();

        for (mpdPolicyType fillInCheckbox : checkboxMap.keySet()) {
            switch (fillInCheckbox){
                case condo:
                    generalTab.getOtherAAAProductOwnedAssetList().getAsset(_condoCheckbox.getLabel(),
                            _condoCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                    break;

                case home:
                    generalTab.getOtherAAAProductOwnedAssetList().getAsset(_homeCheckbox.getLabel(),
                            _homeCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                    break;

                case renters:
                    generalTab.getOtherAAAProductOwnedAssetList().getAsset(_rentersCheckbox.getLabel(),
                            _rentersCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                    break;

                case life:
                    generalTab.getOtherAAAProductOwnedAssetList().getAsset(_lifeCheckbox.getLabel(),
                            _lifeCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                    break;

                case motorcycle:
                    generalTab.getOtherAAAProductOwnedAssetList().getAsset(_motorCycleCheckbox.getLabel(),
                            _motorCycleCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                    break;
            }
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
     * @Runtime 2min
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
     * @Runtime 4min
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
        if (!in_policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !in_policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
            new ErrorTab().verify.errorsPresent(true, ErrorEnum.Errors.MPD_COMPANION_VALIDATION);
        }else {
            CustomAssertions.assertThat(Page.dialogConfirmation.isPresent());
        }
    }

    private void doMPDEligibilityTest_MidTerm(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        TestData td = getPolicyDefaultTD();
        mainApp().open();
        createCustomerIndividual();
        createPolicy(td);

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
        if (!in_policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !in_policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
            new ErrorTab().verify.errorsPresent(true, ErrorEnum.Errors.MPD_COMPANION_VALIDATION);
        }else {
            CustomAssertions.assertThat(PolicySummaryPage.labelPolicyNumber.isPresent());
        }
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
        if (!in_policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !in_policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
            new ErrorTab().verify.errorsPresent(true, ErrorEnum.Errors.MPD_COMPANION_VALIDATION);
        }else {
            CustomAssertions.assertThat(PolicySummaryPage.labelPolicyNumber.isPresent());
        }
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
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Need ability to prevent MTE bind with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23456")
    public void pas23456_MPD_Allow_MTEBind(@Optional("") String state) {
        doMTEPreventBindTest(false, "Life");
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas23456_MPD_Prevent_Renewal(@Optional("") String state) {
        doMTEPreventBindTest_Renewals("Renters");
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas23456_MPD_Prevent_AmendedRenewal(@Optional("") String state) {

    }

    private void doMTEPreventBindTest(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        TestData td = getPolicyDefaultTD();
        mainApp().open();
        createCustomerIndividual();
        createPolicy(td);

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

        if (!in_policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !in_policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
            new ErrorTab().verify.errorsPresent(true, ErrorEnum.Errors.MPD_COMPANION_VALIDATION);
        }else {
            CustomAssertions.assertThat(PolicySummaryPage.labelPolicyNumber.isPresent());
        }
    }

    private void doMTEPreventBindTest_Renewals(String in_policyType){
        // Get into Renewal Image
        createPolicyAdvanceToRenewalImage();
    }

    private void createPolicyAdvanceToRenewalImage(){
        // Create Policy
        TestData td = getPolicyDefaultTD();
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy(td);
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
}
