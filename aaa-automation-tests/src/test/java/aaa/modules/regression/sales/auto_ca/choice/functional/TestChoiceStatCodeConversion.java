package aaa.modules.regression.sales.auto_ca.choice.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestChoiceStatCodeConversion extends AutoCaChoiceBaseTest {

    private static final String VINMATCH_CAC_C = "1FMCU937591212312";
    private static final String VINMATCH_CAC_I = "JM3TB2MV9A5123526";

    private VehicleTab vehicleTab = new VehicleTab();
    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

    /**
     * @author Mantas Garsvinskas
     *
     * PAS-18994 CA Choice policies not recieving vehicle type surcharge
     * @name Test to check that STAT Conversion is not happening for VIN Match Case
     *
     * @scenario
     * 1. Create Auto Choice quote and:
     * 1.1 Case1 - VIN Match: enter VIN which do have Liability symbol values in DB (Symbols: C);
     * 1.2 Case2 - VIN Match: enter VIN which do have Liability symbol values in DB (Symbols: I);
     * 1.3 Case3 - Partial Match: Liability symbol exist in DB for Best Match According to Y/M/M/S (Symbols: C);
     * 2. Proceed to the PremiumAndCoverages Tab;
     * 3. Verify VRD: liability symbols, Comp/Coll symbols and Special Hazard Surcharge Value
     * @details
     */
    @StateList(states = Constants.States.CA)
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-18994")
    public void pas18994_notConvertedLiabilitySymbols (@Optional("CA") String state){

        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), VINMATCH_CAC_C);
        TestData tdPartialMatch = getTestSpecificTD("TestData_PartialMatch").resolveLinks();

        // Test Case 1 with VIN Match AND Not Converted Symbols = C
        createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);
        checkVRD("C", "8", "Yes");

        // Test Case 2 with VIN Match AND Not Converted Symbols = I
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
        vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.VIN).setValue(VINMATCH_CAC_I);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        checkVRD("I", "15", "No");

        // Test Case 3 with VIN Partial Match AND Not Converted Symbols = C
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
        vehicleTab.getAssetList().fill(tdPartialMatch);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        checkVRD("C", "48", "Yes");

    }

    /**
     * @author Mantas Garsvinskas
     *
     * PAS-18994 CA Choice policies not recieving vehicle type surcharge
     * @name Test to check that STAT Conversion is happening for VIN No Match Case (MSRP Match)
     *
     * @scenario
     * 1. Create Auto Choice quote and:
     * 1.1 Case1 - VIN No Match: MSRP Match (Symbols: Y Converted to E)
     * 2. Proceed to the PremiumAndCoverages Tab;
     * 3. Verify VRD: liability symbols, Comp/Coll symbols and Special Hazard Surcharge Value
     * @details
     */
    @StateList(states = Constants.States.CA)
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-18994")
    public void pas18994_convertedLiabilitySymbols (@Optional("CA") String state){

        TestData tdMSRPMatch = getPolicyTD().adjust(getTestSpecificTD("TestData_MSRP").resolveLinks());

        createQuoteAndFillUpTo(tdMSRPMatch, PremiumAndCoveragesTab.class);
        checkVRD("E", "5", "Yes");
    }

     /**
     Method validates VRD: Liability Symbols, Comp/Coll Symbols and Special Hazard Surcharge
     */
    private void checkVRD(String liabilitySymbol, String compCollSymbol, String specialHazardSurchargeValue){

        premiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.RatingDetailsView.open();
        assertSoftly(softly -> {
            softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"BI Symbol").getCell(2).getValue()).isEqualTo(liabilitySymbol);
            softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"PD Symbol").getCell(2).getValue()).isEqualTo(liabilitySymbol);
            softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "UM Symbol").getCell(2).getValue()).isEqualTo(liabilitySymbol);
            softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "MP Symbol").getCell(2).getValue()).isEqualTo(liabilitySymbol);
            softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isEqualTo(compCollSymbol);
            softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isEqualTo(compCollSymbol);
            softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(1, "Special Hazard Surcharge").getCell(2).getValue()).isEqualTo(specialHazardSurchargeValue);
        });
         PremiumAndCoveragesTab.RatingDetailsView.close();

    }
}
