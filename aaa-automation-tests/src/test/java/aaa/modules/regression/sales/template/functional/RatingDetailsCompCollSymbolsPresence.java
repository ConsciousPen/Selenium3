package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.waiters.Waiters;

public class RatingDetailsCompCollSymbolsPresence extends PolicyBaseTest {
    public void verifyCompCollSymbolsOnRatingDetails() {
        //Adjust default Data with modified VehicleTab Data
        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);

        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

        CustomAssert.enableSoftMode();

        CustomAssert.assertTrue("Comp Symbol is not present", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Comp Symbol").isPresent());
        CustomAssert.assertTrue("Coll Symbol is not present",PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Coll Symbol").isPresent());

        CustomAssert.assertFalse("First vehicle Comp Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Comp Symbol").getCell(2).getValue().isEmpty());
        CustomAssert.assertFalse("First vehicle Coll Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Coll Symbol").getCell(2).getValue().isEmpty());

        CustomAssert.assertFalse("Second vehicle Comp Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Comp Symbol").getCell(3).getValue().isEmpty());
        CustomAssert.assertFalse("Second vehicle Coll Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Coll Symbol").getCell(3).getValue().isEmpty());

        //For the second vehicle with VIN that did not match we should validate if Comp and Coll symbols are equals (if VIN matches they could be different)
        CustomAssert.assertEquals("Comp and Coll symbols are not equals for vehicle 2",
                PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Comp Symbol").getCell(3).getValue(),
                PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Coll Symbol").getCell(3).getValue());

        CustomAssert.disableSoftMode();

        CustomAssert.assertAll();
    }
}
