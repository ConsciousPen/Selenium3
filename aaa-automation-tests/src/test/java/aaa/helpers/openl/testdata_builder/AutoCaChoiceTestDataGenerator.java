package aaa.helpers.openl.testdata_builder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLPolicy;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLVehicle;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoCaChoiceTestDataGenerator extends AutoCaTestDataGenerator<AutoCaChoiceOpenLPolicy> {

	public AutoCaChoiceTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoCaChoiceOpenLPolicy openLPolicy) {
		String defaultEffectiveDate = getRatingDataPattern().getValue(
				new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel());
		openLPolicy.setEffectiveDate(TimeSetterUtil.getInstance().parse(defaultEffectiveDate, DateTimeUtils.MM_DD_YYYY));

		TestData td = DataProviderFactory.dataOf(
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new AssignmentTab().getMetaKey(), getAssignmentTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
		return TestDataHelper.merge(getRatingDataPattern(), td);
	}

	@Override
	protected Map<String, String> getVehicleTabUsageData(OpenLVehicle vehicle) {
		Map<String, String> usageData = new HashMap<>();
		switch (((AutoCaChoiceOpenLVehicle) vehicle).getVehicleUsageCd()) {
			case "WC":
				usageData.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Commute (to/from work and school)");
				break;
			case "FM":
				usageData.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Farm non-business(on premises)");
				break;
			case "FMB":
				usageData.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Farm business (farm to market delivery)");
				break;
			case "BU":
				usageData.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Business (small business non-commercial)");
				usageData.put(AutoCaMetaData.VehicleTab.IS_THE_VEHICLE_USED_IN_ANY_COMMERCIAL_BUSINESS_OPERATIONS.getLabel(), "Yes");
				usageData.put(AutoCaMetaData.VehicleTab.BUSINESS_USE_DESCRIPTION.getLabel(), "some business use description $<rx:\\d{3}>");
				break;
			case "P": // TODO-dchubkov: to be checked
			case "PL":
				usageData.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Pleasure (recreational driving only)");
				break;
			default:
				throw new IstfException("Unknown mapping for vehicleUsageCd: " + ((AutoCaChoiceOpenLVehicle) vehicle).getVehicleUsageCd());
		}
		return usageData;
	}

	@Override
	protected String getVehicleTabType(OpenLVehicle vehicle) {
		String vehType = ((AutoCaChoiceOpenLVehicle) vehicle).getVehType();
		switch (vehType) {
			case "M":
				return "Motor Home";
			case "P":
				return "Regular";
			case "O":
				return getRandom("Antique / Classic", "Trailer", "Camper");
			default:
				throw new IstfException("Unknown vehicle type for vehType: " + vehType);
		}
	}

	@Override
	boolean isPolicyLevelCoverageCd(String coverageCd) {
		return Arrays.asList("BI", "PD", "UMBI", "MP").contains(coverageCd);
	}

	@Override
	protected String getVehicleTabStatCode(String statCode, int modelYear) {
		switch (statCode) {
			case "A":
				return modelYear > 1989
						//TODO-dchubkov: remove workaround after fixing PAS-11783 - Positive delta limits appears in policy level coverages in add more than 1 vehicle for Auto CA Select quote
						//? getRandom("Passenger Car Small", "Trailer/ Shell")
						? getRandom("Passenger Car Small")
						: getRandom("Trailer/ Shell", "Antique vehicle");
			case "B":
				return modelYear > 1989
						? getRandom("Passenger Car Midsize", "Station wagon", "SUV Small", "Pickup/ Utility Truck Small", "Passenger Van")
						: "Pickup/ Utility Truck Small";
			case "C":
				return modelYear > 1989
						//for "Custom Van" we need to fill Special Equipment.* controls in Vehicle tab which affect total premium, therefore this stat code is excluded
						//? getRandom("Pickup/ Utility Truck Standard", "Passenger Car Large", "Custom Van")
						? getRandom("Pickup/ Utility Truck Standard", "Passenger Car Large")
						: "Pickup/Utility Truck";
			case "D":
				return modelYear > 1989
						? getRandom("Motorhome", "SUV Large", "Cargo Van", "SUV Midsize")
						: getRandom("Motorhome", "Cargo Van");
			case "E":
				return modelYear > 1989
						? getRandom("Limited Production vehicle", "High Exposure car Small", "High Exposure car Midsize", "High Exposure car Large")
						: getRandom("High Exposure car", "Limited Production vehicle");
			default:
				throw new IstfException(String.format("Unknown UI \"Stat Code\" combo box value for openl statCode %s", statCode));
		}
	}
}
