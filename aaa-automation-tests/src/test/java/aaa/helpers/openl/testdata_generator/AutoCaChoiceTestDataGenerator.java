package aaa.helpers.openl.testdata_generator;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLDriver;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLPolicy;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLVehicle;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoCaChoiceTestDataGenerator extends AutoCaTestDataGenerator<AutoCaChoiceOpenLDriver, AutoCaChoiceOpenLVehicle, AutoCaChoiceOpenLPolicy> {

	public AutoCaChoiceTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoCaChoiceOpenLPolicy openLPolicy) {
		//		String defaultEffectiveDate = getRatingDataPattern().getValue(
		//				new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel());
		//		openLPolicy.setEffectiveDate(TimeSetterUtil.getInstance().parse(defaultEffectiveDate, DateTimeUtils.MM_DD_YYYY).toLocalDate());

		TestData td = DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new AssignmentTab().getMetaKey(), getAssignmentTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
		return TestDataHelper.merge(getRatingDataPattern(), td);
	}

	@Override
	protected TestData getDriverTabInformationData(AutoCaChoiceOpenLDriver openLDriver, boolean isFirstDriver, LocalDate policyEffectiveDate) {
		TestData driverData = super.getDriverTabInformationData(openLDriver, isFirstDriver, policyEffectiveDate);
		driverData.adjust(AutoCaMetaData.DriverTab.SMOKER_CIGARETTES_OR_PIPES.getLabel(), Boolean.TRUE.equals(openLDriver.isNonSmoker()) ? "No" : "Yes");
		return driverData;
	}

	@Override
	protected List<TestData> getDriverTabActivityInformationData(AutoCaChoiceOpenLDriver openLDriver, LocalDate policyEffectiveDate) {
		List<TestData> activityInformationList = new ArrayList<>();

		if (Boolean.TRUE.equals(openLDriver.hasDriverTrainingDiscount())) {
			assertThat(openLDriver.getDsr()).as("Unable to set \"driverTrainingDiscount\"=%1$s with \"dsr\"=%2$s (less than 4)", openLDriver.hasDriverTrainingDiscount(), openLDriver.getDsr())
					.isGreaterThanOrEqualTo(4);
		}
		switch (openLDriver.getDsr()) {
			//TODO-dchubkov: implement logic to add incidents with any violation points number
			case 3:
				activityInformationList.add(get3ViolationPointsActivityInformationData(policyEffectiveDate, null));
				break;
			case 4:
				activityInformationList.add(get4ViolationPointsActivityInformationData(policyEffectiveDate, openLDriver.hasDriverTrainingDiscount(), null));
				break;
			case 13:
				//For Choice product we should always add "3" points if driver has at least 3 activities with non-zero violation points included in rating.
				// Therefore if we need driver with 13 points we can add 3 incidents with 10 points in total
				activityInformationList.add(get3ViolationPointsActivityInformationData(policyEffectiveDate, null));
				activityInformationList.add(get3ViolationPointsActivityInformationData(policyEffectiveDate, null));
				activityInformationList.add(get4ViolationPointsActivityInformationData(policyEffectiveDate, openLDriver.hasDriverTrainingDiscount(), null));
				break;
			default:
				throw new IstfException(String.format("Unknown mapping for dsr=%s value", openLDriver.getDsr()));
		}
		return activityInformationList;
	}

	@Override
	public int getDriverAge(AutoCaChoiceOpenLDriver openLDriver) {
		int driverAge;
		if (Boolean.TRUE.equals(openLDriver.isMatureDriver())) {
			driverAge = calculateAge(50, 80, openLDriver.getTyde());
		} else {
			if (Boolean.TRUE.equals(openLDriver.isOccasionalUse())) {
				assertThat(openLDriver.getMaritalStatus()).as("Unable to generate driver's test data for occasionalUse=true if marital status is not single").isEqualTo("S");
				assertThat(openLDriver.getTyde()).as("Unable to generate driver's test data for occasionalUse=true if total years of driving experience is less than 8").isLessThan(8);
				driverAge = calculateAge(16, 24, openLDriver.getTyde());
			} else {
				driverAge = calculateAge(25, 49, openLDriver.getTyde());
			}
		}
		return driverAge;
	}

	@Override
	protected TestData getVehicleTabInformationData(AutoCaChoiceOpenLVehicle openLVehicle) {
		TestData vehicleInformation = super.getVehicleTabInformationData(openLVehicle);

		switch (openLVehicle.getVehicleUsageCd()) {
			case "WC":
				vehicleInformation.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Commute (to/from work and school)");
				break;
			case "FM":
				vehicleInformation.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Farm non-business(on premises)");
				break;
			case "FMB":
				vehicleInformation.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Farm business (farm to market delivery)");
				break;
			case "BU":
				vehicleInformation
						.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Business (small business non-commercial)")
						.adjust(AutoCaMetaData.VehicleTab.IS_THE_VEHICLE_USED_IN_ANY_COMMERCIAL_BUSINESS_OPERATIONS.getLabel(), "Yes")
						.adjust(AutoCaMetaData.VehicleTab.BUSINESS_USE_DESCRIPTION.getLabel(), "some business use description $<rx:\\d{3}>");
				break;
			case "P": // TODO-dchubkov: to be checked
			case "PL":
				vehicleInformation.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Pleasure (recreational driving only)");
				break;
			default:
				throw new IstfException("Unknown mapping for vehicleUsageCd: " + openLVehicle.getVehicleUsageCd());
		}

		if (Boolean.TRUE.equals(openLVehicle.getAntiTheft())) {
			vehicleInformation
					.adjust(AutoCaMetaData.VehicleTab.ANTI_THEFT.getLabel(), "STD")
					.adjust(AutoCaMetaData.VehicleTab.ANTI_THEFT_RECOVERY_DEVICE.getLabel(), "Vehicle Recovery Device");
		}

		if (!"Trailer".equals(getVehicleTabType(openLVehicle)) && !"Camper".equals(getVehicleTabType(openLVehicle))) {
			vehicleInformation
					.adjust(AutoCaMetaData.VehicleTab.ANTI_LOCK_BRAKES.getLabel(), openLVehicle.getAntiLock() ? "Rear only Standard" : "Not available")
					.adjust(AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel(), "3000");
		}
		return vehicleInformation;
	}

	@Override
	protected String getVehicleTabType(AutoCaChoiceOpenLVehicle openLVehicle) {
		String vehType = openLVehicle.getVehType();
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
		return Arrays.asList("BI", "PD", "UMBI", "MP", "UM").contains(coverageCd);
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

	@Override
	protected String getPremiumAndCoveragesTabLimitOrDeductible(AutoOpenLCoverage coverage) {
		String[] limitRange = getLimitOrDeductibleRange(coverage);
		if ("RENTAL".equals(coverage.getCoverageCd()) || "TOWING".equals(coverage.getCoverageCd())) {
			return "1".equals(limitRange[0]) ? "starts=Yes" : "starts=No Coverage";
		}
		return super.getPremiumAndCoveragesTabLimitOrDeductible(coverage);
	}

	private TestData getAssignmentTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		List<TestData> driverVehicleRelationshipTable = new ArrayList<>(openLPolicy.getVehicles().size());
		for (int i = 0; i < openLPolicy.getVehicles().size(); i++) {
			TestData assignmentData = DataProviderFactory.dataOf(AutoCaMetaData.AssignmentTab.DriverVehicleRelationshipTableRow.PRIMARY_DRIVER.getLabel(), "index=1");
			driverVehicleRelationshipTable.add(assignmentData);
		}
		return DataProviderFactory.dataOf(AutoCaMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP.getLabel(), driverVehicleRelationshipTable);
	}
}
