package aaa.helpers.openl.testdata_builder;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLDriver;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

abstract class AutoCaTestDataGenerator<P extends AutoCaOpenLPolicy> extends AutoTestDataGenerator<P> {

	AutoCaTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	protected List<TestData> getDriverTabData(AutoCaOpenLPolicy openLPolicy) {
		List<TestData> driversTestDataList = new ArrayList<>(openLPolicy.getDrivers().size());
		boolean isFirstDriver = true;
		for (AutoCaOpenLDriver driver : openLPolicy.getDrivers()) {
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
							? openLPolicy.getEffectiveDate().minusDays(new Random().nextInt(33 * 28)).format(DateTimeUtils.MM_DD_YYYY) : null,
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

	protected List<TestData> getVehicleTabData(AutoCaOpenLPolicy openLPolicy) {
		List<TestData> vehiclesTestDataList = new ArrayList<>(openLPolicy.getVehicles().size());
		for (OpenLVehicle vehicle : openLPolicy.getVehicles()) {
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

	protected TestData getPremiumAndCoveragesTabData(AutoCaOpenLPolicy openLPolicy) {
		List<TestData> detailedVehicleCoveragesList = new ArrayList<>(openLPolicy.getVehicles().size());
		Map<String, Object> policyCoveragesData = new HashMap<>();
		Map<String, Object> detailedCoveragesData = new HashMap<>();

		for (OpenLVehicle vehicle : openLPolicy.getVehicles()) {
			for (AutoOpenLCoverage coverage : vehicle.getCoverages()) {
				String coverageName = getPremiumAndCoveragesTabCoverageName(coverage.getCoverageCd());
				if (isPolicyLevelCoverageCd(coverage.getCoverageCd())) {
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

	protected TestData getVehicleTabInformationData(OpenLVehicle vehicle) {
		assertThat(vehicle.getAddress()).as("Vehicle's address list should have only one address").hasSize(1);
		Map<String, Object> vehicleInformation = new HashMap<>();
		String statCode = vehicle.getStatCode() != null ? vehicle.getStatCode() : vehicle.getBiLiabilitySymbol();
		String vehicleType = getVehicleTabType(vehicle);

		vehicleInformation.put(AutoCaMetaData.VehicleTab.TYPE.getLabel(), vehicleType);
		vehicleInformation.put(AutoCaMetaData.VehicleTab.YEAR.getLabel(), vehicle.getModelYear());
		/*vehicleInformation.put(AutoCaMetaData.VehicleTab.VALUE.getLabel(),
				getVehicleTabValueFromDb(vehicle.getCollSymbol(), vehicle.getCompSymbol(), vehicle.getModelYear(), vehicle.getVehType(), statCode));*/
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

		/*if (Boolean.TRUE.equals(vehicle.getAntiTheft())) {
			vehicleInformation.put(AutoCaMetaData.VehicleTab.ANTI_THEFT.getLabel(), "STD");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.ANTI_THEFT_RECOVERY_DEVICE.getLabel(), "Vehicle Recovery Device");
		}*/

		/*if (!"Trailer".equals(vehicleType) && !"Camper".equals(vehicleType)) {
			vehicleInformation.put(AutoCaMetaData.VehicleTab.ANTI_LOCK_BRAKES.getLabel(), vehicle.getAntiLock() ? "Rear only Standard" : "Not available");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel(), 3000);
		}*/

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

		return new SimpleDataProvider(vehicleInformation);
	}

	protected abstract String getVehicleTabType(OpenLVehicle vehicle);

	protected abstract String getVehicleTabStatCode(String statCode, int modelYear);

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

	private TestData get3ViolationPointsActivityInformationData(LocalDateTime effectiveDate) {
		return getActivityInformationData(effectiveDate, "Minor Violation", getRandom("Child seat belt violation", "Disregard police", "Driving left of center", "Driving on sidewalk"));
	}

	private TestData get4ViolationPointsActivityInformationData(LocalDateTime effectiveDate, Boolean hasDriverTrainingDiscount) {
		String incidentType = hasDriverTrainingDiscount ? "10-yr Major Violation" : "Major Violation";
		return getActivityInformationData(effectiveDate, incidentType, "regex=.*\\S.*");
	}

	private TestData getActivityInformationData(LocalDateTime effectiveDate, String type, String description) {
		// Incident should be not older than 33 month from effective date to affect premium;
		String occurrenceAndConvictionDate = effectiveDate.minusDays(new Random().nextInt(33 * 28)).format(DateTimeUtils.MM_DD_YYYY);
		return DataProviderFactory.dataOf(
				AutoCaMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), type,
				AutoCaMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), description,
				AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), occurrenceAndConvictionDate,
				AutoCaMetaData.DriverTab.ActivityInformation.CONVICTION_DATE.getLabel(), occurrenceAndConvictionDate,
				AutoCaMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_YAF.getLabel(), "Yes");
	}
}
