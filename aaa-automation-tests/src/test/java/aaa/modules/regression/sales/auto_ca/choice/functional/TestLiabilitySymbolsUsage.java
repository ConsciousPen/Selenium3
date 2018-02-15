package aaa.modules.regression.sales.auto_ca.choice.functional;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestLiabilitySymbolsUsageTemplate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


public class TestLiabilitySymbolsUsage extends TestLiabilitySymbolsUsageTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    String VinNumberForLiabilitySymbolsAbsenceTest = "4S2CK58W8X4307498";

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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6582")
    public void pas6582_commonStatCodeRules (){
        pas6582_StatCodeRules();
    }

    /**
     * @author Lev Kazarnovskiy
     *
     * PAS-6582 Update Product Rules to use Liability Symbols instead of STAT
     * @name Verify that Error will occur if Liab Symbols are null in DB and Stat Code was not selected on UI on VehicleTab
     * @scenario
     * 1. Create AutoSS quote and:
     * enter VIN that doesn't have Liability symbols values in DB;
     * 2. Do not select Stat Code value for Vehicle;
     * 3. Proceed to the PremiumAndCoverages Tab;
     * 4. Verify that error occurs;
     * 5. Return to the VehicleTab and select Stat Code and Value;
     * 6. Try to rate quote one more time -  no errors should occur;
     *
     * @details
     * VIN Used for test: 4S2CK58W8X4307498, see {@link #VinNumberForLiabilitySymbolsAbsenceTest}
     *
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM, "fixDB"})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6582")
    public void pas6582_LiabilitySymbolsAbsence (){
        VehicleTab vehicleTab = new VehicleTab();
        ErrorTab errorTab = new ErrorTab();

        //Delete liability and comprehensive symbols for particular VIN to trigger the rule
        deleteLiabilitySymbolsForVIN(VinNumberForLiabilitySymbolsAbsenceTest);

        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), VinNumberForLiabilitySymbolsAbsenceTest);

        createQuoteAndFillUpTo(testData, FormsTab.class);
        new FormsTab().submitTab();

        assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_MES_PC_0017_CA_CHOICE.getMessage())).exists();
        errorTab.cancel();

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE).setValue("SUV Midsize");
        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VALUE).setValue("20000");

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());

        //verify that error doesn't show anymore
        assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE,
                ErrorEnum.Errors.ERROR_AAA_MES_PC_0017_CA_CHOICE.getMessage()).isPresent()).isFalse();

        PremiumAndCoveragesTab.calculatePremium();

        assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE,
                ErrorEnum.Errors.ERROR_AAA_MES_PC_0017_CA_CHOICE.getMessage()).isPresent()).isFalse();
    }

    /**
     * @author Lev Kazarnovskiy
     *
     * PAS-6582 Update Product Rules to use Liability Symbols instead of STAT
     * @name Verify that Stat Code will be cleared on UI if Vehicle Type was selected as Regular and liability symbols in DB were
     * updated to null values
     * @scenario
     * 1. Create AutoSS quote and:
     * on VehicleTab select VehicleType OTHER than Regular,
     * 2. Save the quote and verify that liability symbols values are filled for it in DB
     * 3. Open quote and go to the VehicleTab
     * 4. Set VehicleType = Regular, verify that Stat code field is cleared on UI;
     * 5. Save quote, verify that values for liab symbols in DB are updated to null value;
     *
     * @details
     *
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.LOW})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-6582")
    public void pas6582_statCodeClearing () {

        VehicleTab vehicleTab = new VehicleTab();

        //'VIN Doesn't Match' Data is used
        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.TYPE.getLabel()), "Motor Home"));

        createQuoteAndFillUpTo(testData, VehicleTab.class);
        //Save quote to get values for liability symbols in DB
        vehicleTab.saveAndExit();
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        //If some info will be changed in the system - expected value could be changed to the actual one.
        // The main goal is 'not null' value
        verifyLiabilitySymbolsInDB(quoteNumber, "M");

        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.TYPE).setValue("Regular");

        assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STAT_CODE)).hasValue("");

        //Save quote to update values for it in DB
        VehicleTab.buttonTopSave.click();
        verifyLiabilitySymbolsInDB(quoteNumber, null);
    }

    @AfterClass(alwaysRun = true, groups = {"fixDB"})
    private void restoreValuesInDB(){
        restoreCompCollAndLiabilitySymbolsForVIN(VinNumberForLiabilitySymbolsAbsenceTest);
    }
}
