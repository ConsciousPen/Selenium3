/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
* CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Arrays;
import java.util.List;
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

public class TestRatingDetailsView extends AutoSSBaseTest {
	/**
	*PAS-535 Modifies View Rating Details to display separate comp and coll symbols.
	*PAS-2712 Update UI (View Rating Details) to show 4 symbols instead of STAT CODE
	*@author Viktor Petrenko
	*@name View Rating details UI update.
	*@scenario 0. Create customer and auto SS policy with 2 Vehicles
	*1. Initiate quote creation
	*2. Go to the vehicle tab
	*3. Add second vehicle
	*4. Rate quote
	*5. Open rating detail view
	*@details
	*/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-535")
	public void pas535_verifyNewSymbolsPresence(@Optional("") String state) {
		TestData testDataAdjusted = getPolicyTD().adjust("VehicleTab",getTestSpecificTD("TestData").getTestDataList("VehicleTab")).resolveLinks();

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testDataAdjusted, VehicleTab.class, true);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().btnCalculatePremium().click();
		PremiumAndCoveragesTab.RatingDetailsView.open();

		// Start of PAS-535 Modifies View Rating Details to display separate comp and coll symbols.
		List<String> pas535Fields = Arrays.asList("Coll Symbol","Comp Symbol");
		pas535Fields.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).isPresent()).isEqualTo(true));

		pas535Fields.forEach(f -> assertThat(
				PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue().isEmpty() &&
				PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue().isEmpty()).isEqualTo(false));
		// End of PAS-535 Modifies View Rating Details to display separate comp and coll symbols.
		// Start of PAS-2712 Update UI (View Rating Details)
		List<String> pas2712Fields = Arrays.asList("BI Symbol","PD Symbol","UM Symbol", "MP Symbol");
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).isPresent()).isEqualTo(true));
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Stat Code").isPresent()).isEqualTo(false);
		// End of PAS-2712 Update UI (View Rating Details)
	}
}
