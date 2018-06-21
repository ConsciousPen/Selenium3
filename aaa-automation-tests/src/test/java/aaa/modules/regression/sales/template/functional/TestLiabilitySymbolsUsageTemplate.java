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
     * 1. Create Auto Select/Choice quote and on VehicleTab select values to get partial match
     * 2. Verify that If VIN match, STAT Code field is removed from page
     * 3. Verify that Stat code value for Motorhom/Trailer/Camper Types will be locked as 'Motorhome' or 'Trailer/Shell'
     * 3. Verify that antitheft, airbags and alternative fuel information is pre-populated if such information can be found in Vin table in DB
     * @details
     * Useful query in case of further test failures:
     *
    Select v.id, v.stat, V.ALTFUEL, M.Id AS REFid, v.vin, V.VERSION, m.year, m.make, v.make_text, v.model_text, V.SERIES_TEXT, v.value, v.body, v.BODYTYPE_TEXT, V.SEGMENTATION_CD, V.BODY_STYLE_CD, V.BODYSHELL, V.ENGINE_NAME, v.NUMOFCYLINDERS, V.WD, v.WHEELDRIVE, v.RESTRAINTSCODE,
    v.RESTRAINTSCODE_TEXT, v.ANTILOCKCODE, v.ANTILOCKCODE_TEXT, v.ANTITHEFTCODE, v.ANTITHEFTCODE_TEXT, V.PHYSICALDAMAGECOLLISION as CollisionSymbol, V.PHYSICALDAMAGECOMPREHENSIVE as ComprehensiveSymbol, V.ALTFUEL , V.BI_SYMBOL, V.PD_SYMBOL,
    V.UM_SYMBOL, V.MP_SYMBOL, V.ENTRYDATE, V.VALID, V.ANTITHEFT_DISCOUNT, V.RESTRAINTS_DISCOUNT
    From Vehiclerefdatavin V, Vehiclerefdatamodel M Where V.Vehiclerefdatamodelid = M.Id and
    V.vin like '2HGFA3F2%B';
     *
     * VIN Used for test: 2HGFA3F21B
     */
    protected void pas6582_StatCodeRules(){

        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData_PartialMatch").resolveLinks());

        createQuoteAndFillUpTo(testData, VehicleTab.class);

        //Validation that Stat code field is hidden after partial matched for VIN getting, validation that fields are populated with Info
        assertSoftly(softly -> {
            softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).isPresent(false);
            softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.AIR_BAGS)).doesNotHaveValue("");
            softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.ANTI_THEFT)).doesNotHaveValue("");
            softly.assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.ALTERNATIVE_FUEL_VEHICLE)).hasValue("Yes");
        });

        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE).setValue("Motor Home");
        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).isPresent().hasValue("Motorhome");

        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE).setValue("Trailer");
        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).isPresent().hasValue("Trailer/ Shell");

        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE).setValue("Camper");
        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).isPresent().hasValue("Trailer/ Shell");
    }

    //Method to verify liabilitySymbols values for specific quote/policy in the DB
    protected void verifyLiabilitySymbolsInDB (String quoteNumber, String expectedResult){
        List<String> liabilitySymbols = Arrays.asList("biSymbol", "pdsymbol", "umsymbol", "mpsymbol");
        liabilitySymbols.forEach( s -> assertThat(getLiabSymbolsValuesForQuote(quoteNumber, s)).isEqualTo(expectedResult));
    }

    protected void deleteLiabilitySymbolsForVIN(String vinNumber) {
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_LIABILITY_SYMBOLS_FOR_VIN, getShortedVin(vinNumber)));
    }

    protected void restoreCompCollAndLiabilitySymbolsForVIN(String vinNumber) {
       DBService.get().executeUpdate(String.format(VehicleQueries.RESTORE_LIABILITY_SYMBOLS_FOR_VIN, getShortedVin(vinNumber)));
    }

    private String getLiabSymbolsValuesForQuote(String quoteNumber, String symbolName){
        return DBService.get().getValue
                (String.format(VehicleQueries.SELECT_FROM_VEHICLERATINGINFO_BY_QUOTE_NUMBER, symbolName, quoteNumber)).orElse(null);
    }

    /**
     *
     * @param vinNumber for the test
     * @return VIN number without last 7 symbols and with '%' instead of 9th symbol to find corresponding VIN in DB
     */
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
