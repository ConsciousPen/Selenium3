package aaa.helpers.openl.testdata_builder;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLDriver;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLPolicy;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLVehicle;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoCaChoiceTestDataGenerator extends AutoTestDataGenerator<AutoCaChoiceOpenLPolicy> {

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
	boolean isPolicyLevelCoverage(String coverageCD) {
		return Arrays.asList("BI", "PD", "UMBI", "MP").contains(coverageCD);
	}

	String getVehicleTabStatCode(String statCode, int modelYear) {
		switch (statCode) {
			case "A":
				return modelYear > 1989
						? getRandom("Passenger Car Small", "Trailer/ Shell")
						: getRandom("Trailer/ Shell", "Antique vehicle");
			case "B":
				return modelYear > 1989
						? getRandom("Passenger Car Midsize", "Station wagon", "SUV Small", "Pickup/ Utility Truck Small", "Passenger Van")
						: "Pickup/ Utility Truck Small";
			case "C":
				return modelYear > 1989
						? getRandom("Pickup/ Utility Truck Standard", "Passenger Car Large", "Custom Van")
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

	private List<TestData> getDriverTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		List<TestData> driversTestDataList = new ArrayList<>(openLPolicy.getDrivers().size());
		boolean isFirstDriver = true;
		for (AutoCaChoiceOpenLDriver driver : openLPolicy.getDrivers()) {
			int driverAge;
			if (Boolean.TRUE.equals(driver.isMatureDriver())) {
				driverAge = getRandomAge(50, 80, driver.getTyde());
			} else {
				if (Boolean.TRUE.equals(driver.isOccasionalUse())) {
					assertThat(driver.getMaritalStatus()).as("Unable to generate driver's test data for occasionalUse=true if marital status is not single").isEqualTo("S");
					assertThat(driver.getTyde()).as("Unable to generate driver's test data for occasionalUse=true if total years of driving experience is less than 8").isLessThan(8);
					driverAge = getRandomAge(16, 24, driver.getTyde());
				} else {
					driverAge = getRandomAge(25, 49, driver.getTyde());
				}
			}

			TestData driverData = DataProviderFactory.dataOf(
					AutoCaMetaData.DriverTab.DRIVER_SEARCH_DIALOG.getLabel(), isFirstDriver ? null : DataProviderFactory.emptyData(),
					AutoCaMetaData.DriverTab.DRIVER_TYPE.getLabel(), isFirstDriver ? null : "Available for Rating",
					AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), isFirstDriver ? null : AdvancedComboBox.RANDOM_EXCEPT_MARK + "=First Named Insured|",
					AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), isFirstDriver ? null : "FN_" + driver.getId(),
					AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), isFirstDriver ? null : "LN_" + driver.getId(),
					AutoCaMetaData.DriverTab.GENDER.getLabel(), getDriverTabGender(driver.getGender()),
					AutoCaMetaData.DriverTab.MARITAL_STATUS.getLabel(), getDriverTabMartialStatus(driver.getMaritalStatus()),
					AutoCaMetaData.DriverTab.OCCUPATION.getLabel(), "regex=.*\\S.*",
					AutoCaMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel(), driverAge - driver.getTyde(),
					AutoCaMetaData.DriverTab.PERMIT_BEFORE_LICENSE.getLabel(), "No",
					AutoCaMetaData.DriverTab.LICENSE_STATE.getLabel(), getState(),
					AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel(), "C$<rx:\\d{7}>",
					AutoCaMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), getDriverTabDateOfBirth(driverAge, openLPolicy.getEffectiveDate()),
					AutoCaMetaData.DriverTab.MATURE_DRIVER_COURSE_COMPLETED_WITHIN_36_MONTHS.getLabel(), driverAge >= 50 ? getYesOrNo(driver.isMatureDriver()) : null,
					AutoCaMetaData.DriverTab.MATURE_DRIVER_COURSE_COMPLETION_DATE.getLabel(), Boolean.TRUE.equals(driver.isMatureDriver())
							? openLPolicy.getEffectiveDate().minusMonths(new Random().nextInt(33)).format(DateTimeUtils.MM_DD_YYYY) : null,
					AutoCaMetaData.DriverTab.MOST_RECENT_GPA.getLabel(), driverAge <= 25 ? "regex=.*\\S.*" : null,
					AutoCaMetaData.DriverTab.SMOKER_CIGARETTES_OR_PIPES.getLabel(), Boolean.TRUE.equals(driver.isNonSmoker()) ? "No" : "Yes");

			List<TestData> activityInformationList = new ArrayList<>();
			if (driver.getDsr() != null && driver.getDsr() > 0) {
				if (Boolean.TRUE.equals(driver.hasDriverTrainingDiscount())) {
					assertThat(driver.getDsr()).as("Unable to set \"driverTrainingDiscount\"=%1$s with \"dsr\"=%2$s (less than 4)", driver.hasDriverTrainingDiscount(), driver.getDsr())
							.isGreaterThanOrEqualTo(4);
				}
				switch (driver.getDsr()) {
					//TODO-dchubkov: implement logic to add incidents with any violation points number
					case 4:
						activityInformationList.add(get4ViolationPointsActivityInformationData(openLPolicy.getEffectiveDate(), driver.hasDriverTrainingDiscount()));
						break;
					case 13:
						//For Choice product we should always add "3" points if driver has at least 3 activities with non-zero violation points included in rating.
						// Therefore if we need driver with 13 points we can add 3 incidents with 10 points in total
						activityInformationList.add(get3ViolationPointsActivityInformationData(openLPolicy.getEffectiveDate()));
						activityInformationList.add(get3ViolationPointsActivityInformationData(openLPolicy.getEffectiveDate()));
						activityInformationList.add(get4ViolationPointsActivityInformationData(openLPolicy.getEffectiveDate(), driver.hasDriverTrainingDiscount()));
						break;
					default:
						throw new IstfException(String.format("Unknown mapping for dsr=%s value", driver.getDsr()));
				}
			}

			if (!activityInformationList.isEmpty()) {
				driverData.adjust(AutoCaMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), activityInformationList);
			}

			driversTestDataList.add(driverData);
			isFirstDriver = false;
		}
		return driversTestDataList;
	}

	private int getRandomAge(int minAgeInclusive, int maxAgeInclusive, int tyde) {
		assertThat(minAgeInclusive)
				.as("Can't get random driver's age for minAgeInclusive=%1$s and maxAgeInclusive=%2$s; minAgeInclusive should be positive and less than maxAgeInclusive argument", minAgeInclusive, maxAgeInclusive)
				.isPositive().isLessThan(maxAgeInclusive);

		int minimalAgeFirstLicensed = 16;
		int ageFirstLicensed = minAgeInclusive - tyde;
		if (ageFirstLicensed < minimalAgeFirstLicensed) {
			minAgeInclusive += Math.abs(minAgeInclusive - ageFirstLicensed);
			assertThat(minAgeInclusive)
					.as("Can't get random driver's age for minAgeInclusive=%1$s, maxAgeInclusive=%2$s and tyde=%3$s; new minAgeInclusive with tyde consideration should be less than maxAgeInclusive argument",
							minAgeInclusive, maxAgeInclusive, tyde).isLessThan(maxAgeInclusive);
		}
		return RandomUtils.nextInt(minAgeInclusive, maxAgeInclusive + 1);
	}

	private TestData getMembershipTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private List<TestData> getVehicleTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		List<TestData> vehiclesTestDataList = new ArrayList<>(openLPolicy.getVehicles().size());
		for (AutoCaChoiceOpenLVehicle vehicle : openLPolicy.getVehicles()) {
			if (vehicle.getBiLiabilitySymbol() != null) {
				HashSet<String> liabilitySymbols = new HashSet<>();
				liabilitySymbols.add(vehicle.getBiLiabilitySymbol());
				liabilitySymbols.add(vehicle.getPdLiabilitySymbol());
				liabilitySymbols.add(vehicle.getMpLiabilitySymbol());
				liabilitySymbols.add(vehicle.getUmLiabilitySymbol());
				if (liabilitySymbols.size() > 1) {
					throw new NotImplementedException(String.format("Not all *LiabilitySymbol field values are the same: %s, test data generation for this case is not implemented", liabilitySymbols));
				}
			}

			TestData vehicleData = getVehicleTabInformationData(vehicle);
			vehiclesTestDataList.add(vehicleData);
		}
		return vehiclesTestDataList;
	}

	private TestData getAssignmentTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		List<TestData> driverVehicleRelationshipTable = new ArrayList<>(openLPolicy.getVehicles().size());
		for (int i = 0; i < openLPolicy.getVehicles().size(); i++) {
			TestData assignmentData = DataProviderFactory.dataOf(AutoCaMetaData.AssignmentTab.DriverVehicleRelationshipTableRow.PRIMARY_DRIVER.getLabel(), "index=1");
			driverVehicleRelationshipTable.add(assignmentData);
		}
		return DataProviderFactory.dataOf(AutoCaMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP.getLabel(), driverVehicleRelationshipTable);
	}

	private TestData getPremiumAndCoveragesTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		List<TestData> detailedVehicleCoveragesList = new ArrayList<>(openLPolicy.getVehicles().size());
		Map<String, Object> policyCoveragesData = new HashMap<>();
		Map<String, Object> detailedCoveragesData = new HashMap<>();

		for (AutoCaChoiceOpenLVehicle vehicle : openLPolicy.getVehicles()) {
			for (AutoOpenLCoverage coverage : vehicle.getCoverages()) {
				String coverageName = getPremiumAndCoveragesTabCoverageName(coverage.getCoverageCd());
				if (isPolicyLevelCoverage(coverage.getCoverageCd())) {
					policyCoveragesData.put(coverageName, getPremiumAndCoveragesTabLimitOrDeductible(coverage));
				} else {
					detailedCoveragesData.put(coverageName, getPremiumAndCoveragesTabLimitOrDeductible(coverage));
					if ("SP EQUIP".equals(coverage.getCoverageCd())) {
						detailedCoveragesData.put(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.SPECIAL_EQUIPMENT_DESCRIPTION.getLabel(), "some SP EQUIP description $<rx:\\d{3}>");
					}
				}
			}
			detailedVehicleCoveragesList.add(new SimpleDataProvider(detailedCoveragesData));
			detailedCoveragesData.clear();
		}

		return DataProviderFactory.dataOf(
				AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel(), getTerm(openLPolicy.getTerm()),
				AutoCaMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS.getLabel(), "Yes", //TODO-dchubkov: enable only if need to fill expanded section
				AutoCaMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), openLPolicy.isMultiCar(),
				AutoCaMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), detailedVehicleCoveragesList)
				.adjust(new SimpleDataProvider(policyCoveragesData));
	}

	private TestData getVehicleTabInformationData(AutoCaChoiceOpenLVehicle vehicle) {
		assertThat(vehicle.getAddress()).as("Vehicle's address list should have only one address").hasSize(1);
		Map<String, Object> vehicleInformation = new HashMap<>();
		String statCode = vehicle.getStatCode() != null ? vehicle.getStatCode() : vehicle.getBiLiabilitySymbol();
		String vehicleType = getVehicleType(vehicle.getVehType());

		vehicleInformation.put(AutoCaMetaData.VehicleTab.TYPE.getLabel(), vehicleType);
		vehicleInformation.put(AutoCaMetaData.VehicleTab.YEAR.getLabel(), vehicle.getModelYear());
		vehicleInformation.put(AutoCaMetaData.VehicleTab.VALUE.getLabel(),
				getVehicleTabValueFromDb(vehicle.getCollSymbol(), vehicle.getCompSymbol(), vehicle.getModelYear(), vehicle.getVehType(), statCode));
		vehicleInformation.put(AutoCaMetaData.VehicleTab.MAKE.getLabel(), "OTHER");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.OTHER_MAKE.getLabel(), "some other make $<rx:\\d{3}>");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel(), "some other model $<rx:\\d{3}>");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.OTHER_SERIES.getLabel(), "some other series $<rx:\\d{3}>");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel(), "some other body style $<rx:\\d{3}>");
		if ("Antique / Classic".equals(vehicleType)) {
			vehicleInformation.put(AutoCaMetaData.VehicleTab.RESTORED_TO_ORGINAL_STOCK_CONDITION_WITH_NO_ALTERATIONS.getLabel(), "Yes");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.USED_SOLEY_IN_EXHIBITIONS_CLUB_ACTIVITY_PARADES_AND_OTHER_FUN_FUNCTIONS_OF_PUBLIC_INTEREST.getLabel(), "Yes");
		}

		if ("Regular".equals(vehicleType)) {
			//for other vehicle types Stat Code is disabled
			String uiStatCode = getVehicleTabStatCode(statCode, vehicle.getModelYear());
			vehicleInformation.put(AutoCaMetaData.VehicleTab.STAT_CODE.getLabel(), uiStatCode);
			if ("Custom Van".equals(uiStatCode)) {
				vehicleInformation.put(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT.getLabel(), "Yes");
				vehicleInformation.put(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT_VALUE.getLabel(), "$<rx:\\d{3}>");
				vehicleInformation.put(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT_DESCRIPTION.getLabel(), "some special equipment description $<rx:\\d{3}>");
			}
		}

		if (Boolean.TRUE.equals(vehicle.getAntiTheft())) {
			vehicleInformation.put(AutoCaMetaData.VehicleTab.ANTI_THEFT.getLabel(), "STD");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.ANTI_THEFT_RECOVERY_DEVICE.getLabel(), "Vehicle Recovery Device");
		}

		if (!"Trailer".equals(vehicleType) && !"Camper".equals(vehicleType)) {
			vehicleInformation.put(AutoCaMetaData.VehicleTab.ANTI_LOCK_BRAKES.getLabel(), vehicle.getAntiLock() ? "Rear only Standard" : "Not available");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel(), 3000);
		}

		vehicleInformation.put(AutoCaMetaData.VehicleTab.EXISTING_DAMEGE.getLabel(), "None");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.SALVAGED.getLabel(), "No");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel(), 10);
		vehicleInformation.put(AutoCaMetaData.VehicleTab.CUSTOMER_DECLARED_ANNUAL_MILES.getLabel(), vehicle.getAnnualMileage());

		int streetNumber = RandomUtils.nextInt(100, 1000);
		String streetName = RandomStringUtils.randomAlphabetic(10).toUpperCase() + " St";
		vehicleInformation.put(AutoCaMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel(), "Yes");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.ZIP_CODE.getLabel(), vehicle.getAddress().get(0).getZip());
		vehicleInformation.put(AutoCaMetaData.VehicleTab.ADDRESS_LINE_1.getLabel(), streetNumber + " " + streetName);
		vehicleInformation.put(AutoCaMetaData.VehicleTab.STATE.getLabel(), vehicle.getAddress().get(0).getState());
		vehicleInformation.put(AutoCaMetaData.VehicleTab.VALIDATE_ADDRESS_BTN.getLabel(), "click");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData());

		switch (vehicle.getVehicleUsageCd()) {
			case "WC":
				vehicleInformation.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Commute (to/from work and school)");
				break;
			case "FM":
				vehicleInformation.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Farm non-business(on premises)");
				break;
			case "FMB":
				vehicleInformation.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Farm business (farm to market delivery)");
				break;
			case "BU":
				vehicleInformation.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Business (small business non-commercial)");
				vehicleInformation.put(AutoCaMetaData.VehicleTab.IS_THE_VEHICLE_USED_IN_ANY_COMMERCIAL_BUSINESS_OPERATIONS.getLabel(), "Yes");
				vehicleInformation.put(AutoCaMetaData.VehicleTab.BUSINESS_USE_DESCRIPTION.getLabel(), "some business use description $<rx:\\d{3}>");
				break;
			case "P": // TODO-dchubkov: to be checked
			case "PL":
				vehicleInformation.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Pleasure (recreational driving only)");
				break;
			default:
				throw new IstfException("Unknown mapping for vehicleUsageCd: " + vehicle.getVehicleUsageCd());
		}
		return new SimpleDataProvider(vehicleInformation);
	}

	private Integer getVehicleTabValueFromDb(Integer collSymbol, Integer compSymbol, Integer modelYear, String vehType, String statCode) {
		String getVinQuery = "select m.MSRPMIN, m.MSRPMAX from MSRPCOMPCOLLLOOKUP m where m.COLLSYMBOL=? and m.COMPSYMBOL=? and m.KEY in (select c.KEY from MSRPCOMPCOLLCONTROL c\n"
				+ "   where c.MSRPVERSION = 'MSRP_2000_CHOICE'\n"
				+ "   and (? between c.VEHICLEYEARMIN and c.VEHICLEYEARMAX or (c.VEHICLEYEARMIN is null and c.VEHICLEYEARMAX is null))\n"
				+ "   and (c.VEHICLETYPE = ? or c.VEHICLETYPE is null)\n"
				+ "   and (c.LIABILITYSYMBOL = ? or c.LIABILITYSYMBOL is null))";
		Map<String, String> resultRow = DBService.get().getRow(getVinQuery, collSymbol, compSymbol, modelYear, vehType, statCode);
		return RandomUtils.nextInt(Integer.parseInt(resultRow.get("MSRPMIN")), Integer.parseInt(resultRow.get("MSRPMAX")));
	}

	private String getVehicleTabType(String statCode, int modelYear) {
		switch (statCode) {
			case "A":
			case "D":
				return modelYear > 1990 ? "Regular" : "Antique / Classic";
			case "C":
				return "Camper";
			case "M":
				return "Motor Home";
			case "T":
				return "Trailer";
			default:
				throw new IstfException("Unknown vehicle type for statCode: " + statCode);
		}
	}

	private String getVehicleType(String vehType) {
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

	private TestData get3ViolationPointsActivityInformationData(LocalDateTime effectiveDate) {
		return getActivityInformationData(effectiveDate, "Minor Violation", getRandom("Child seat belt violation", "Disregard police", "Driving left of center", "Driving on sidewalk"));
	}

	private TestData get4ViolationPointsActivityInformationData(LocalDateTime effectiveDate, Boolean hasDriverTrainingDiscount) {
		String incidentType = hasDriverTrainingDiscount ? "10-yr Major Violation" : "Major Violation";
		return getActivityInformationData(effectiveDate, incidentType, "regex=.*\\S.*");
	}

	private TestData getActivityInformationData(LocalDateTime effectiveDate, String type, String description) {
		String occurrenceAndConvictionDate = effectiveDate.minusMonths(new Random().nextInt(33)).format(DateTimeUtils.MM_DD_YYYY);
		return DataProviderFactory.dataOf(
				AutoCaMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), type,
				AutoCaMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), description,
				AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), occurrenceAndConvictionDate,
				AutoCaMetaData.DriverTab.ActivityInformation.CONVICTION_DATE.getLabel(), occurrenceAndConvictionDate,
				AutoCaMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_YAF.getLabel(), "Yes");
	}
}
