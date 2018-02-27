package aaa.modules.regression.sales.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestLiabilitySymbolsUsageTemplate;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


import java.util.Arrays;
import java.util.List;

public class TestLiabilitySymbolsUsage extends TestLiabilitySymbolsUsageTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @author Lev Kazarnovskiy
     *
     * PAS-6582 Update Product Rules to use Liability Symbols instead of STAT
     * @name Quick Test of VIN and Control Table Upload
     *
     * @scenario
     * * See detailed steps in template file
     * {@link TestLiabilitySymbolsUsageTemplate#pas6582_StatCodeRules()}
     *
     * VIN Used for test: 2HGFA3F21B
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6582")
    public void pas6582_commonStatCodeRules (){
        pas6582_StatCodeRules();
    }


    /**
     * @author Lev Kazarnovskiy
     *
     * PAS-6582 Update Product Rules to use Liability Symbols instead of STAT
     * @name Verify specific rules for Cargo and Custom VAN vehicles for Select products
     * @scenario
     * 1. Create Auto Select quote and on VehicleTab enter Cargo Van vehicle VIN
     * 2. Verify that default values for Liability Symbols are used for it
     * 3. Set 'Special Equipment' to yes.
     * 4. Save quote. Retrieve in DB and verify that the Symbol is set to V, for custom van
     * 5. Set 'Special Equipment' to NO
     * 6. Save quote. Retrieve in DB and verify that the Symbol is set to W, for cargo van
     *
     * @details
     * VIN Used for test: 1FTCE2EL5A
     * Default 'Stat Code'/'Liability Symbols' Values): W
     *
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.LOW})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-6582")
    public void pas6582_CargoVanRules (){
        VehicleTab vehicleTab = new VehicleTab();

        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "1FTCE2EL5A");

        createQuoteAndFillUpTo(testData, VehicleTab.class);
        vehicleTab.saveAndExit();

        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        List <String> liabilitySymbols = Arrays.asList("biSymbol", "pdsymbol", "umsymbol", "mpsymbol");
        liabilitySymbols.forEach( s -> assertThat(getLiabSymbolsValuesForQuote(quoteNumber, s)).isEqualTo("W")
                .as("W symbol was not set as default for liability symbols for Cargo Van vehicle, please check used VIN and/or product rules"));

        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT).setValue("Yes");
        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT_VALUE).setValue("40000");
        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT_DESCRIPTION).setValue("Test");
        VehicleTab.buttonTopSave.click();
        verifyLiabilitySymbolsInDB(quoteNumber, "V");

        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT).setValue("No");
        VehicleTab.buttonTopSave.click();
        verifyLiabilitySymbolsInDB(quoteNumber, "W");
    }
}
