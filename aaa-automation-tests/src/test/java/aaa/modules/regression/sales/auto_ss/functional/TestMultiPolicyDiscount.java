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

import static aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab.buttonRatingDetailsOk;


@StateList(states = Constants.States.AZ)
public class TestMultiPolicyDiscount extends AutoSSBaseTest {

    public enum mpdPolicyType{
        home, renters, condo, life, motorcycle
    }
    GeneralTab _generalTab = new GeneralTab();
    ErrorTab _errorTab = new ErrorTab();
    PremiumAndCoveragesTab _pncTab = new PremiumAndCoveragesTab();
    DocumentsAndBindTab _documentsAndBindTab = new DocumentsAndBindTab();

    /**
     * Make sure various combos of Unquoted Other AAA Products rate properly and are listed in the UI
     * on P&C Page as well as in View Rating Details.
     * @param state the test will run against.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23983")
    public void pas23983_MPD_unquoted_rate_and_show_discounts(@Optional("") String state) {

        // Create customer and move to general tab. //
        TestData testData = getPolicyTD();

        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        GeneralTab generalTab = new GeneralTab();

        // Set unquoted policies //

        AssetDescriptor<CheckBox> homeCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME;

        generalTab.getOtherAAAProductOwnedAssetList().getAsset(homeCheckbox.getLabel(),
                homeCheckbox.getControlClass()).setValue(true);

        // Continue to next tab then move to P&C tab //
        generalTab.submitTab();

        policy.getDefaultView().fillFromTo(testData, DriverTab.class, PremiumAndCoveragesTab.class, true);

        PremiumAndCoveragesTab pncTab = new PremiumAndCoveragesTab();

        // Validate appropriate discounts //

        String discountsAndSurcharges = PremiumAndCoveragesTab.discountsAndSurcharges.getValue();

        // Check in View Rating details for Multi-Policy Discount
        TestData td = pncTab.getRatingDetailsQuoteInfoData();
        buttonRatingDetailsOk.click();
        String mpdDiscountApplied = td.getValue("AAA Multi-Policy Discount");

        // Return to General tab, reset unquoted, then setup next scenario
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
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
