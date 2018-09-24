package aaa.modules.regression.sales.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.regression.sales.template.functional.TestChoiceStatCodeConversionTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestChoiceStatCodeConversion extends TestChoiceStatCodeConversionTemplate {

    private static final String CACHOICE_VINMATCH = "1FMCU937591212312";

    private VehicleTab vehicleTab = new VehicleTab();
    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }


    /**
     * @author Mantas Garsvinskas
     *
     * PAS-18994 CA Choice policies not recieving vehicle type surcharge
     * @name Test to check that STAT Conversion is not happening for VIN Match Case
     *
     * @scenario
     * 1. Create Auto Choice quote and:
     * enter VIN that do have Liability symbols values in DB: C (All fields automatically filled according to VIN Match);
     * 2. Proceed to the PremiumAndCoverages Tab;
     * 3. Verify liablity symbols and Special Hazard Surcharge Value
     */
    @StateList(states = Constants.States.CA)
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-18994")
    public void pas18994_notConvertedliablitySymbols (@Optional("CA") String state){

        TestData testData = getPolicyTD()
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), CACHOICE_VINMATCH);

        createQuoteAndFillUpTo(testData, PremiumAndCoveragesTab.class);

        premiumAndCoveragesTab.getAssetList().getAsset(AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT).setValue("CA Choice");
        premiumAndCoveragesTab.calculatePremium();








        //TODO Transfer everything to template if needed

    }


}
