package aaa.modules.regression.sales.template.functional;

import java.util.Arrays;
import java.util.List;
import org.testng.AssertJUnit;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class TestSymbolsPresenceTemplate extends PolicyBaseTest {
	public void verifySymbolsPresence() {
		//Adjust default Data with modified VehicleTab Data
		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();

		// Start of PAS-535 Modifies View Rating Details to display separate comp and coll symbols.
		List<String> pas535Fields = Arrays.asList("Coll Symbol", "Comp Symbol");
		pas535Fields.forEach(f -> AssertJUnit.assertTrue(f + " should present",
				PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).isPresent()));

		pas535Fields.forEach(f -> AssertJUnit.assertFalse(f + "  is empty",
				PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue().isEmpty() &&
						PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue().isEmpty()));
		// End of PAS-535 Modifies View Rating Details to display separate comp and coll symbols.
		CustomAssert.enableSoftMode();
		//For the second vehicle with VIN that did not match we should validate if Comp and Coll symbols are equals (if VIN matches they could be different)
		CustomAssert.assertEquals("Comp and Coll symbols are not equals for vehicle 2",
				PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(3).getValue(),
				PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(3).getValue());

		CustomAssert.disableSoftMode();
		// Start of PAS-2712 Update UI (View Rating Details)
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "PD Symbol");
		pas2712Fields.forEach(f -> AssertJUnit.assertTrue(f + " should present", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).isPresent()));
		AssertJUnit.assertFalse(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Stat Code").isPresent());
		AssertJUnit.assertFalse(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Vehicle Type").isPresent());
		// End of PAS-2712 Update UI (View Rating Details)
		CustomAssert.assertAll();
	}
}
