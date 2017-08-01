package aaa.modules.policy.templates;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.waiters.Waiters;

public class RatingDetailsCompCollSymbolsPresence extends PolicyBaseTest {
    public void verifyCompCollSymbolsPresence() {
        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD("DataGather", "TestData"), VehicleTab.class, true);

        new VehicleTab().addVehicle(getPolicyTD("DataGather", "TestData"));
        NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.buttonCalculatePremium.click(Waiters.AJAX);
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

        CustomAssert.enableSoftMode();

        CustomAssert.assertTrue("Comp Symbol is not present", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Comp Symbol").isPresent());
        CustomAssert.assertTrue("Coll Symbol is not present",PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Coll Symbol").isPresent());

        CustomAssert.assertFalse("First vehicle Comp Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Comp Symbol").getCell(2).getValue().isEmpty());
        CustomAssert.assertFalse("First vehicle Coll Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Coll Symbol").getCell(2).getValue().isEmpty());

        CustomAssert.assertFalse("Second vehicle Comp Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Comp Symbol").getCell(3).getValue().isEmpty());
        CustomAssert.assertFalse("Second vehicle Coll Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Coll Symbol").getCell(3).getValue().isEmpty());

        CustomAssert.disableSoftMode();

        CustomAssert.assertAll();
    }
}
