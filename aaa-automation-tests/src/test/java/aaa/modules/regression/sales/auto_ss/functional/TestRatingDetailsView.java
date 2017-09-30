/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestRatingDetailsView extends AutoSSBaseTest {

    /**
     * PAS-1904
     *
     * @author Viktor Petrenko
     * @modified Lev Kazarnovskiy
     * @name View Rating details UI update.
     * @scenario
     * 0. Create customer
     * 1. Initiate Auto Select quote creation
     * 2. Go to the vehicle tab, fill info with valid VIN
     * 3. Add second vehicle with VIN that do not match any values in DB
     * 4. Rate quote
     * 5. Open rating detail view and verify if Comp And Coll Symbols are displayed for both vehicles
     * Verify that they are the same for Vehicle 2
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testQuoteRatingViewDetailsCompCollSymbolsArePresentAndNotEmpty(@Optional("") String state) {
        //Adjust default Data with modified VehicleTab Data
        TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, VehicleTab.class, true);

        NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.buttonCalculatePremium.click();
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

        CustomAssert.enableSoftMode();

        CustomAssert.assertTrue("Comp Symbol is not present", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").isPresent());
        CustomAssert.assertTrue("Coll Symbol is not present", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").isPresent());

        CustomAssert.assertFalse("First vehicle Comp Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue().isEmpty());
        CustomAssert.assertFalse("First vehicle Coll Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue().isEmpty());

        CustomAssert.assertFalse("Second vehicle Comp Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(3).getValue().isEmpty());
        CustomAssert.assertFalse("Second vehicle Coll Symbol is empty", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(3).getValue().isEmpty());

        //For the second vehicle with VIN that did not match we should validate if Comp and Coll symbols are equals (if VIN matches they could be different)
        CustomAssert.assertEquals("Comp and Coll symbols are not equals for vehicle 2",
                PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(3).getValue(),
                PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(3).getValue());

        CustomAssert.disableSoftMode();

        CustomAssert.assertAll();
    }
}
