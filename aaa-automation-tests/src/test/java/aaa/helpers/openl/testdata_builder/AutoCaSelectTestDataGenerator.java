package aaa.helpers.openl.testdata_builder;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLPolicy;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public class AutoCaSelectTestDataGenerator extends AutoCaTestDataGenerator<AutoCaSelectOpenLPolicy> {

	public AutoCaSelectTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoCaSelectOpenLPolicy openLPolicy) {
		TestData ratingDataPattern = getRatingDataPattern().resolveLinks();
		if (Boolean.FALSE.equals(openLPolicy.isAaaMember())) {
			ratingDataPattern
					.mask(new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel())
					.mask(new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel())
					.mask(new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBER_LAST_NAME.getLabel());
		}

		TestData td = DataProviderFactory.dataOf(
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new AssignmentTab().getMetaKey(), getAssignmentTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
		return TestDataHelper.merge(ratingDataPattern, td);
	}

	@Override
	boolean isMotorHomeType(String statCode) {
		return "M".equals(statCode);
	}

	@Override
	boolean isTrailerType(String statCode) {
		return "T".equals(statCode);
	}

	@Override
	protected String getVehicleTabType(OpenLVehicle vehicle) {
		String statCode = vehicle.getStatCode() != null ? vehicle.getStatCode() : vehicle.getBiLiabilitySymbol();
		if (isRegularType(statCode)) {
			return "Regular";
		}
		if (isMotorHomeType(statCode)) {
			return "Motor Home";
		}
		if (isAntiqueClassicType(statCode)) {
			return "Antique / Classic";
		}
		if (isTrailerType(statCode)) {
			return "Trailer";
		}
		if (isCamperType(statCode)) {
			return "Camper";
		}
		throw new IstfException("Unknown vehicle type for statCode: " + statCode);
	}

	@Override
	protected String getVehicleTabStatCode(String statCode, int modelYear) {
		Map<String, String> statCodesMap = new HashMap<>();
		statCodesMap.put("A", "Antique vehicle");
		statCodesMap.put("C", "SUV Small"); // for 1990+ model years
		statCodesMap.put("D", "High Exposure car Midsize"); // for 1990+ model years
		statCodesMap.put("E", "SUV Large"); // for 1990+ model years
		statCodesMap.put("H", "High Exposure car"); // for 1989 or prior model years
		statCodesMap.put("I", "Passenger Car Midsize"); // for 1990+ model years
		statCodesMap.put("J", "High Exposure car Large"); // for 1990+ model years
		statCodesMap.put("K", "SUV Midsize"); // for 1990+ model years
		statCodesMap.put("L", "Low Speed Vehicle");
		statCodesMap.put("M", "Motorhome");
		statCodesMap.put("N", "Passenger Car Small"); // for 1990+ model years
		statCodesMap.put("O", "Pickup/ Utility Truck Standard"); // for 1990+ model years
		statCodesMap.put("P", "Passenger car, SUV, Station wagon, Passenger Van"); // for 1989 or prior model years
		statCodesMap.put("Q", "Passenger Car Large"); // for 1990+ model years
		statCodesMap.put("R", "Pickup/ Utility Truck Small"); // for 1990+ model years
		statCodesMap.put("S", "Limited Production vehicle");
		statCodesMap.put("T", "Trailer/ Shell");
		statCodesMap.put("U", "Pickup/ Utility Truck"); // for 1989 or prior model years
		statCodesMap.put("V", "Custom Van");
		statCodesMap.put("W", "Cargo Van");
		statCodesMap.put("X", "Passenger Van"); // for 1990+ model years
		statCodesMap.put("Y", "High Exposure car Small"); // for 1990+ model years
		statCodesMap.put("Z", "Station wagon");

		assertThat(statCodesMap).as("Unknown UI \"Stat Code\" combo box value for openl statCode %s", statCode).containsKey(statCode);
		return statCodesMap.get(statCode);
	}

	boolean isRegularType(String statCode) {
		List<String> codes = Arrays.asList("B", "C", "D", "E", "H", "I", "J", "K", "L", "N", "O", "P", "Q", "R", "S", "U", "V", "W", "X", "Y", "Z");
		return codes.contains(statCode);
	}

	boolean isAntiqueClassicType(String statCode) {
		return "A".equals(statCode);
	}

	boolean isCamperType(String statCode) {
		List<String> codes = Arrays.asList("RQ", "RT", "FW", "UT", "PC", "HT", "PT");
		return codes.contains(statCode);
	}

	private TestData getGeneralTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getAssignmentTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
