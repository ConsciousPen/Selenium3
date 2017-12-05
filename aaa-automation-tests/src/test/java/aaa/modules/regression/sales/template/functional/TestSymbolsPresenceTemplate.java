package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

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
		pas535Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).isPresent()).isEqualTo(true));

		pas535Fields.forEach(f ->assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(2).getValue().isEmpty() &&
						PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).getCell(3).getValue().isEmpty()).isEqualTo(true));
		// End of PAS-535 Modifies View Rating Details to display separate comp and coll symbols.
		//For the second vehicle with VIN that did not match we should validate if Comp and Coll symbols are equals (if VIN matches they could be different)
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(3).getValue())
				.isEqualTo(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(3).getValue());
		// Start of PAS-2712 Update UI (View Rating Details)
		List<String> pas2712Fields = Arrays.asList("BI Symbol", "PD Symbol", "UM Symbol", "PD Symbol");
		pas2712Fields.forEach(f -> assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, f).isPresent()).isEqualTo(true));
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Stat Code").isPresent()).isEqualTo(true);
		assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Vehicle Type").isPresent()).isEqualTo(true);
		// End of PAS-2712 Update UI (View Rating Details)
	}
}
