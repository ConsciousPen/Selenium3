package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

import java.util.ArrayList;
import java.util.HashMap;

import static aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab.buttonRatingDetailsOk;


@StateList(states = Constants.States.AZ)
public class TestMultiPolicyDiscount extends AutoSSBaseTest {

    public enum mpdPolicyType{
        home, renters, condo, life, motorcycle
    }

    AssetDescriptor<CheckBox> homeCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME;
    AssetDescriptor<CheckBox> rentersCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS;
    AssetDescriptor<CheckBox> condoCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO;
    AssetDescriptor<CheckBox> lifeCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE;
    AssetDescriptor<CheckBox> motorCycleCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE;


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

        // Data and tools setup
        TestData testData = getPolicyTD();
        GeneralTab generalTab = new GeneralTab();
        PremiumAndCoveragesTab pncTab = new PremiumAndCoveragesTab();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        ArrayList<HashMap <mpdPolicyType, Boolean>> scenarioList = getUnquotedManualScenarios();

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

            // Validate appropriate discounts //
            String discountsAndSurcharges = PremiumAndCoveragesTab.discountsAndSurcharges.getValue();

            // Check in View Rating details for Multi-Policy Discount
            TestData td = pncTab.getRatingDetailsQuoteInfoData();
            buttonRatingDetailsOk.click();
            String mpdDiscountApplied = td.getValue("AAA Multi-Policy Discount");

            // Return to General tab.
            NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());

            //BondTODO: Assert on data.
        }
    }

    private void setUnquotedCheckboxes(HashMap <mpdPolicyType, Boolean> checkboxMap){
        GeneralTab generalTab = new GeneralTab();

        for (mpdPolicyType fillInCheckbox : checkboxMap.keySet()) {
            switch (fillInCheckbox){
                    case condo:
                        generalTab.getOtherAAAProductOwnedAssetList().getAsset(condoCheckbox.getLabel(),
                                condoCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                        break;

                    case home:
                        generalTab.getOtherAAAProductOwnedAssetList().getAsset(homeCheckbox.getLabel(),
                                homeCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                        break;

                    case renters:
                        generalTab.getOtherAAAProductOwnedAssetList().getAsset(rentersCheckbox.getLabel(),
                                rentersCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                        break;

                    case life:
                        generalTab.getOtherAAAProductOwnedAssetList().getAsset(lifeCheckbox.getLabel(),
                                lifeCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                        break;

                    case motorcycle:
                        generalTab.getOtherAAAProductOwnedAssetList().getAsset(motorCycleCheckbox.getLabel(),
                                motorCycleCheckbox.getControlClass()).setValue(checkboxMap.get(fillInCheckbox));
                        break;
            }
        }
    }

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
    public void pas24021_MPD_TriggerReateEventOnEditOrRemoval(@Optional("") String state) {

        TestData testData = getPolicyTD();

        createQuoteAndFillUpTo(testData, GeneralTab.class, true);
    }

}
