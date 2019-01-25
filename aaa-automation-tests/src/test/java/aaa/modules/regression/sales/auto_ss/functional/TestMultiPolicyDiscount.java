package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
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
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;


@StateList(states = Constants.States.AZ)
public class TestMultiPolicyDiscount extends AutoSSBaseTest {


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

        TestData testData = getPolicyTD();

        createQuoteAndFillUpTo(testData, GeneralTab.class, true);

        GeneralTab generalTab = new GeneralTab();

        AssetDescriptor<CheckBox> homeCheckbox = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME;

        generalTab.getOtherAAAProductOwnedAssetList().getAsset(homeCheckbox.getLabel(),
                homeCheckbox.getControlClass()).setValue(true);

        generalTab.submitTab();

        policy.getDefaultView().fillFromTo(testData, DriverTab.class, PremiumAndCoveragesTab.class, true);
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
