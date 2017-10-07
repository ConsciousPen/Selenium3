/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;


import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestRatingDetailsView extends AutoSSBaseTest {
	/**
	 * PAS-535
	 *
	 * @author Viktor Petrenko
	 * @name View Rating details UI update.
	 * @scenario 0. Create customer and auto SS policy with 2 Vehicles
	 * 1. Initiate quote creation
	 * 2. Go to the vehicle tab
	 * 3. Add second vehicle
	 * 4. Rate quote
	 * 5. Open rating detail view
	 * @details
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteRatingViewDetailsCompCollSymbolsArePresentAndNotEmpty(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD("DataGather", "TestData_TwoVehicles"), VehicleTab.class, true);

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

		CustomAssert.disableSoftMode();

		CustomAssert.assertAll();
	}
}
