package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

import aaa.helpers.db.queries.VehicleQueries;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import toolkit.datax.TestData;
import toolkit.db.DBService;

import java.util.Arrays;
import java.util.List;

public class TestLiabilitySymbolsUsageTemplate extends CommonTemplateMethods {

    VehicleTab vehicleTab = new VehicleTab();

    /**
     * @author Lev Kazarnovskiy
     * <p>
     * PAS-6582 Update Product Rules to use Liability Symbols instead of STAT
     * @name Test common rules for Select and Choice products
     * @scenario
     * 1. Create AutoSS quote and on VehicleTab select values to get partial match
     * 2. Verify that If VIN match, STAT Code field is removed from page
     * 3. Verify that Stat code value for Motorhom/Trailer/Camper Types will be locked as 'Motorhome' or 'Trailer/Shell'
     * 3. Verify that antitheft, airbags and alternative fuel information is pre-populated if such information can be found in Vin table in DB
     * @details
     *
     * VIN Used for test: 2HGFA3F21B
     */
    public void pas6582_StatCodeRules(){

        TestData testData = getPartialMatchData(getPolicyTD());

        createQuoteAndFillUpTo(testData, VehicleTab.class);

        assertSoftly(softly -> {
            softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).isPresent(false);
            softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.AIR_BAGS)).isNotEqualTo("");
            softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.ANTI_THEFT)).isNotEqualTo("");
            softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.ALTERNATIVE_FUEL_VEHICLE)).hasValue("Yes");
        });

        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE).setValue("Motor Home");
        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).isPresent().hasValue("Motorhome");

        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE).setValue("Trailer");
        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).isPresent().hasValue("Trailer/ Shell");

        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE).setValue("Camper");
        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).isPresent().hasValue("Trailer/ Shell");
    }

    private TestData getPartialMatchData(TestData testData) {
        testData
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2011")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), "HONDA")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MODEL.getLabel()), "CIVIC")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.SERIES.getLabel()), "CIVIC HYBRID")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()), "SEDAN 4 DOOR");
        return testData;
    }

    protected void verifyLiabilitySymbolsInDB (String quoteNumber, String expectedResult){
        List<String> liabilitySymbols = Arrays.asList("biSymbol", "pdsymbol", "umsymbol", "mpsymbol");
        liabilitySymbols.forEach( s -> assertThat(getLiabSymbolsValues(quoteNumber, s)).isEqualTo(expectedResult));
    }

    protected String getLiabSymbolsValues(String quoteNumber, String liabSymbolName){
        return DBService.get().getValue
                (String.format(VehicleQueries.SELECT_FROM_VEHICLERATINGINFO_BY_QUOTE_NUMBER, liabSymbolName, quoteNumber)).orElse(null);
    }

    protected void deleteLiabilitySymbolsForVIN(String vinNumber) {
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_LIABILITY_SYMBOLS_FOR_VIN, getShortedVin(vinNumber)));
    }

    protected void restoreLiabilitySymbolsForVIN(String vinNumber) {
       DBService.get().executeUpdate(String.format(VehicleQueries.RESTORE_LIABILITY_SYMBOLS_FOR_VIN, getShortedVin(vinNumber)));
    }

    private String getShortedVin (String vinNumber) {
        StringBuilder finalVin;
        if (vinNumber.length() > 10) {
            finalVin = new StringBuilder(vinNumber.substring(0, 10));
        } else {
            throw new AssertionError("Entered VIN might not be valid for processing of this test");
        }
        finalVin.setCharAt(8, '%');
        return finalVin.toString();
    }
}
