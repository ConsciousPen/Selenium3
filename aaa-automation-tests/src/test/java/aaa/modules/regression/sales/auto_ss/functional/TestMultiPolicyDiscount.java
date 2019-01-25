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
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

import static aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab.buttonRatingDetailsOk;


@StateList(states = Constants.States.AZ)
public class TestMultiPolicyDiscount extends AutoSSBaseTest {

    public enum mpdPolicyType{
        home, renters, condo, life, motorcycle
    }
    GeneralTab _generalTab = new GeneralTab();

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
    public void pas24021_MPD_TriggerReateEventOnEditOrRemoval(@Optional("") String state) {

        TestData testData = getPolicyTD();

        createQuoteAndFillUpTo(testData, GeneralTab.class, true);
        _generalTab.mpd_SearchByPolicyNumber("Life", "NOT_FOUND");
    }

}
