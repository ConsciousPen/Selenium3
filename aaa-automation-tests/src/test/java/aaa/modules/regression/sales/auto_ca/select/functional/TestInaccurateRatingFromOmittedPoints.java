package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.ComboBox;


public class TestInaccurateRatingFromOmittedPoints extends AutoCaSelectBaseTest {

    PremiumAndCoveragesTab pncTab = new PremiumAndCoveragesTab();
    TestDataHelper _tdHelper = new TestDataHelper();

    /**
     *      Claim Data: <br>
     *     1. One accident where the insured is NOT the vehicle operator with occurrence date more than 31 months but less than 33 months of the policy effective date that would result in 2 points or more. <br>
     *     2. Another accident that falls more than 33 months but less than 36 months that would result in 1 point. <br>
     *     3. The 3rd accident which falls within 84 months of the policy effective date. <br>
     *     TODO: Add a mechanism to dynamically generate/return a CLUE response, for control over the dates of the claims. OR STUB must somehow reference NB-33mo, NB-36mo, etc.
     *     @author Tyrone Jemison
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17328: Inaccurate rating at NB caused by Include in Points and/or YAF not systematically included in rating")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-17328")
    public void pas17328_Scenario1_SelectToChoice(@Optional("") String state) {
        // Build Test Data
        TestData _td = getPolicyDefaultTD();
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.FIRST_NAME.getLabel(), "EUGENE");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.MIDDLE_NAME.getLabel(), "J");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.LAST_NAME.getLabel(), "LUNDIN");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.DATE_OF_BIRTH.getLabel(), "01/01/1980");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.ADDRESS_LINE_1.getLabel(), "840 MELROSE HILL ST");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.ZIP_CODE.getLabel(), "90029");
        _tdHelper.adjustTD(_td, PrefillTab.class, AutoCaMetaData.PrefillTab.CITY.getLabel(), "LOS ANGELES");
        _tdHelper.adjustTD(_td, PremiumAndCoveragesTab.class, AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel(), "$100,000/$300,000 (+$0.00)");

        // Open App, Create Customer, Initiate Quote, Fill Up To PNC Tab.
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(_td, PremiumAndCoveragesTab.class, true);

        // Capture Product Type. Verify it's 'Select'.
        String productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        CustomAssertions.assertThat(productDetermined).isEqualToIgnoringCase("CA Select");

        // Advance to order Driver Activity Reports and Order Reports.
        policy.getDefaultView().fillFromTo(_td, PremiumAndCoveragesTab.class, DriverActivityReportsTab.class, true);

        // Return to PNC Tab. Capture Product Type. Verify it's 'Choice' now.
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        productDetermined = pncTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), ComboBox.class).getValue();
        CustomAssertions.assertThat(productDetermined).isEqualToIgnoringCase("CA Choice");

        // Calculate Premium. Verify Product Hasn't Changed.
        pncTab.btnCalculatePremium();
        CustomAssertions.assertThat(productDetermined).isEqualToIgnoringCase("CA Choice");

        // Navigate to Driver Tab and Return to PNC Tab. For debugging later: Adding a BP after this line allows for simple verification of claim data gathered.
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());

        // Return to PNC Tab. Calculate Premium. Verify Product Hasn't Changed.
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        pncTab.btnCalculatePremium();
        CustomAssertions.assertThat(productDetermined).isEqualToIgnoringCase("CA Choice");
    }
}
