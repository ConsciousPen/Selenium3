package aaa.modules.regression.conversions.home_ss;

import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.modules.BaseTest;
import org.testng.annotations.Optional;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class helper extends BaseTest {
    PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
    /* method asserts conditions
    */
    public void assertMasonryVaneerFirstRenewal() {
        assertSoftly(softly -> {
            softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.MASONRY_VENEER)).isEnabled(true);
            propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.MASONRY_VENEER).setValue("Yes");
        });}

    /*
   method asserts conditions
   */
    public void assertOilStorageTankSecondRenewalHo6(String state) {


        assertSoftly(softly -> {

            if(state.matches("NJ"))

                softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isEnabled(true);
            else {
                softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isEnabled(false);
            }
        });}



    public void assertOilStorageTankSecondRenewalHo3Dp3(String state) {
        assertSoftly(softly -> {

            if(state.matches("NJ"))

                softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isEnabled(false);
            else {
                softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isEnabled(true);
            }
        });}
    /*
   method asserts conditions
   */
    public void assertMasonryVaneerSecondRenewal() {
        assertSoftly(softly -> {
            softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.MASONRY_VENEER)).isEnabled(false);
        });}

    public void assertOilStorageTankFirstRenewalHo3Dp3(String state) {
        assertSoftly(softly -> {
            switch (state) {
                case "PA":
                case "VA":
                case "DE":
                case "MD":
                case "NY":
                case "CT":
                case "WY": {
                    softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                            .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isEnabled(true);
                    break;
                }
            /*Specific for HO3 and DP3*/
                case "NJ": {
                    propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                            .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK).setValue("Active underground propane tank");
                    softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                            .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.ADD_FUEL_SYSTEM_STORAGE_TANK_COVERAGE)).isEnabled(true);
                    softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                            .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.AGE_OF_OIL_OR_PROPANE_FUEL_STORAGE_TANK)).isEnabled(true);

                    propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                            .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK).setValue("Above ground oil or propane tank on slab");
                    softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                            .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.ADD_FUEL_SYSTEM_STORAGE_TANK_COVERAGE)).isEnabled(true);
                    softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                            .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.AGE_OF_OIL_OR_PROPANE_FUEL_STORAGE_TANK)).isEnabled(true);

                    break;
                }
                default: {
                    softly.assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                            .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isPresent(false);

                }
            }
        });}

    public void assertOilStorageTankFirstRenewalHo4(@Optional("") String state) {
        assertSoftly(softly -> {
            assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                    .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isPresent(false);

        });}

    public void assertOilStorageTankFirstRenewalHo6(@Optional("") String state) {
        assertSoftly(softly -> {
            assertThat(propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK)
                    .getAsset(HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK)).isPresent(true);

        });}






}
