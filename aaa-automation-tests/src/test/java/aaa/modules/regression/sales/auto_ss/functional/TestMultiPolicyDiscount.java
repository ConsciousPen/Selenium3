package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

import static aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab.RatingDetailsView.buttonRatingDetailsOk;

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
     *
     * @param state
     * @author Tyrone Jemison - CIO
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Trigger Re-rate event when companion policies are edited or removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24021")
    public void pas24021_MPD_TriggerRerateEventOnEditOrRemoval(@Optional("") String state) {

        TestData testData = getPolicyTD();

        createQuoteAndFillUpTo(testData, GeneralTab.class, true);
        _generalTab.mpd_SearchAndAddManually("Home", "NOT_FOUND");
        _generalTab.mpd_EditPolicyInMPDTable(0, "Condo", "ABC1");

        // Added and edited, now attempt to Bind.
        HACK_NavigateAroundDriverTab();
        policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, VehicleTab.class, false);
        HACK_NavigateAroundVehicleTab();
        policy.getDefaultView().fillFromTo(testData, FormsTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();
        Page.dialogConfirmation.buttonNo.click();

        editMPDAndRerate(0, "Renters", "ABC1"); // Editing only the policyType in this scenario.
        editMPDAndRerate(0, "Renters", "XYZ2"); // Editing only the policyNumber in this scenario.
    }

    private void HACK_NavigateAroundDriverTab(){
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        DriverTab dt = new DriverTab();
        dt.getAssetList().getAsset(AutoSSMetaData.DriverTab.OCCUPATION.getLabel(), AutoSSMetaData.DriverTab.OCCUPATION.getControlClass()).setValue("Clergy");
        dt.getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getControlClass()).setValue("B12254293");
        dt.getAssetList().getAsset(AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel(), AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED.getControlClass()).setValue("16");
        dt.submitTab();
    }

    private void HACK_NavigateAroundVehicleTab(){

        VehicleTab vt = new VehicleTab();
        vt.getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE.getLabel(), AutoSSMetaData.VehicleTab.USAGE.getControlClass()).setValue("Pleasure");
        vt.getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN.getLabel(), AutoSSMetaData.VehicleTab.VIN.getControlClass()).setValue("4T1VK13E8PU075864");
        vt.submitTab();
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
     * Handles validating the error message requiring rerate. Wrapped for short, clean method calls.
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
}
