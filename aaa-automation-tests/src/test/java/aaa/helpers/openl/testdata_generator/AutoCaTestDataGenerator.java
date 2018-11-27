package aaa.helpers.openl.testdata_generator;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDate;
import java.util.*;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLDriver;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLVehicle;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;

abstract class AutoCaTestDataGenerator<D extends AutoCaOpenLDriver, V extends OpenLVehicle, P extends AutoCaOpenLPolicy<D, V>> extends AutoTestDataGenerator<P> {
	protected static final String DRIVER_FN_POSTFIX = "_FN";
	protected static final String DRIVER_LN_POSTFIX = "_LN";

	AutoCaTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	protected List<TestData> getDriverTabData(P openLPolicy) {
		List<TestData> driversTestDataList = new ArrayList<>(openLPolicy.getDrivers().size());
		boolean isFirstDriver = true;
		for (D openLDriver : openLPolicy.getDrivers()) {
			TestData driverData = getDriverTabInformationData(openLDriver, isFirstDriver, openLPolicy.getEffectiveDate());
			driversTestDataList.add(driverData);
			isFirstDriver = false;
		}
		return driversTestDataList;
	}

	protected TestData getDriverTabInformationData(D openLDriver, boolean isFirstDriver, LocalDate policyEffectiveDate) {
		int driverAge = getDriverAge(openLDriver);

		TestData driverData = DataProviderFactory.dataOf(
				AutoCaMetaData.DriverTab.DRIVER_SEARCH_DIALOG.getLabel(), isFirstDriver ? null : DataProviderFactory.emptyData(),
				AutoCaMetaData.DriverTab.DRIVER_TYPE.getLabel(), isFirstDriver ? null : "Available for Rating",
				AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), isFirstDriver ? null : AdvancedComboBox.RANDOM_EXCEPT_MARK + "=First Named Insured|",
				AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), isFirstDriver ? null : openLDriver.getId() + DRIVER_FN_POSTFIX,
				AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), isFirstDriver ? null : openLDriver.getId() + DRIVER_LN_POSTFIX,
				AutoCaMetaData.DriverTab.GENDER.getLabel(), getDriverTabGender(openLDriver.getGender()),
				AutoCaMetaData.DriverTab.MARITAL_STATUS.getLabel(), getDriverTabMartialStatus(openLDriver.getMaritalStatus()),
				AutoCaMetaData.DriverTab.OCCUPATION.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_EMPTY,
				AutoCaMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel(), driverAge - openLDriver.getTyde(),
				AutoCaMetaData.DriverTab.PERMIT_BEFORE_LICENSE.getLabel(), "No",
				AutoCaMetaData.DriverTab.LICENSE_STATE.getLabel(), getState(),
				AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel(), "C$<rx:\\d{7}>",
				AutoCaMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), getDriverTabDateOfBirth(driverAge, policyEffectiveDate),
				AutoCaMetaData.DriverTab.MATURE_DRIVER_COURSE_COMPLETED_WITHIN_36_MONTHS.getLabel(), driverAge >= 50 ? getYesOrNo(openLDriver.isMatureDriver()) : null,
				AutoCaMetaData.DriverTab.MATURE_DRIVER_COURSE_COMPLETION_DATE.getLabel(), Boolean.TRUE.equals(openLDriver.isMatureDriver())
						? policyEffectiveDate.minusDays(new Random().nextInt(maxIncidentFreeInMonthsToAffectRating * 28)).format(DateTimeUtils.MM_DD_YYYY) : null,
				AutoCaMetaData.DriverTab.MOST_RECENT_GPA.getLabel(), driverAge <= 25 ? AdvancedComboBox.RANDOM_EXCEPT_EMPTY : null);

		List<TestData> activityInformationList = new ArrayList<>();
		if (openLDriver.getDsr() != null && openLDriver.getDsr() > 0) {
			activityInformationList = getDriverTabActivityInformationData(openLDriver, policyEffectiveDate);
		}

		if (!activityInformationList.isEmpty()) {
			driverData.adjust(AutoCaMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), activityInformationList);
		}

		return driverData;
	}

	protected abstract List<TestData> getDriverTabActivityInformationData(D openLDriver, LocalDate policyEffectiveDate);

	protected abstract int getDriverAge(D openLDriver);

	protected List<TestData> getVehicleTabData(P openLPolicy) {
		List<TestData> vehiclesTestDataList = new ArrayList<>(openLPolicy.getVehicles().size());
		for (V vehicle : openLPolicy.getVehicles()) {
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

	protected TestData getVehicleTabInformationData(V openLVehicle) {
		Map<String, Object> vehicleInformation = new HashMap<>();
		String vehicleType = getVehicleTabType(openLVehicle);

		vehicleInformation.put(AutoCaMetaData.VehicleTab.TYPE.getLabel(), vehicleType);
		vehicleInformation.put(AutoCaMetaData.VehicleTab.YEAR.getLabel(), openLVehicle.getModelYear());
		vehicleInformation.put(AutoCaMetaData.VehicleTab.VALUE.getLabel(),
				getVehicleTabValueFromDb(openLVehicle.getCollSymbol(), openLVehicle.getCompSymbol(), openLVehicle.getModelYear(), openLVehicle instanceof AutoCaSelectOpenLVehicle, openLVehicle.getBiLiabilitySymbol()));
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
			String uiStatCode = getVehicleTabStatCode(openLVehicle.getBiLiabilitySymbol(), openLVehicle.getModelYear());
			vehicleInformation.put(AutoCaMetaData.VehicleTab.STAT_CODE.getLabel(), uiStatCode);
			if ("Custom Van".equals(uiStatCode)) {
				vehicleInformation.put(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT.getLabel(), "Yes");
				vehicleInformation.put(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT_VALUE.getLabel(), "$<rx:\\d{3}>");
				vehicleInformation.put(AutoCaMetaData.VehicleTab.SPECIAL_EQUIPMENT_DESCRIPTION.getLabel(), "some special equipment description $<rx:\\d{3}>");
			}
		}

		vehicleInformation.put(AutoCaMetaData.VehicleTab.EXISTING_DAMEGE.getLabel(), "None");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.SALVAGED.getLabel(), "No");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel(), 10);
		vehicleInformation.put(AutoCaMetaData.VehicleTab.CUSTOMER_DECLARED_ANNUAL_MILES.getLabel(), openLVehicle.getAnnualMileage());

		int streetNumber = RandomUtils.nextInt(100, 1000);
		String streetName = RandomStringUtils.randomAlphabetic(10).toUpperCase() + " St";
		vehicleInformation.put(AutoCaMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel(), "Yes");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.ZIP_CODE.getLabel(), openLVehicle.getAddress().getZip());
		vehicleInformation.put(AutoCaMetaData.VehicleTab.ADDRESS_LINE_1.getLabel(), streetNumber + " " + streetName);
		vehicleInformation.put(AutoCaMetaData.VehicleTab.STATE.getLabel(), openLVehicle.getAddress().getState());
		vehicleInformation.put(AutoCaMetaData.VehicleTab.VALIDATE_ADDRESS_BTN.getLabel(), "click");
		vehicleInformation.put(AutoCaMetaData.VehicleTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData());

		return new SimpleDataProvider(vehicleInformation);
	}

	protected TestData getPremiumAndCoveragesTabData(P openLPolicy) {
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
			if (vehicle.getCoverages().stream().noneMatch(c -> "COMP".equals(c.getCoverageCd()))) {
				detailedCoveragesData.put(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COMPREGENSIVE_DEDUCTIBLE.getLabel(), getFormattedCoverageLimit("N", "COMP"));
			}
			detailedVehicleCoveragesList.add(new SimpleDataProvider(detailedCoveragesData));
			detailedCoveragesData.clear();
		}

		return DataProviderFactory.dataOf(
				AutoCaMetaData.PremiumAndCoveragesTab.PRODUCT.getLabel(), openLPolicy.getTestPolicyType().equals(PolicyType.AUTO_CA_SELECT) ? "CA Select" : "CA Choice",
				AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel(), getPremiumAndCoveragesPaymentPlan(openLPolicy.getTerm()),
				AutoCaMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS.getLabel(), "Yes", //TODO-dchubkov: enable only if need to fill expanded section
				AutoCaMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), openLPolicy.isMultiCar(),
				AutoCaMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), detailedVehicleCoveragesList)
				.adjust(new SimpleDataProvider(policyCoveragesData));
	}

	protected abstract String getVehicleTabType(V openLVehicle);

	protected abstract String getVehicleTabStatCode(String statCode, int modelYear);

	protected int getRandomAge(int minAgeInclusive, int maxAgeInclusive, int tyde) {
		assertThat(minAgeInclusive)
				.as("Can't get random driver's age for minAgeInclusive=%1$s and maxAgeInclusive=%2$s; minAgeInclusive should be positive and less than maxAgeInclusive argument", minAgeInclusive, maxAgeInclusive)
				.isPositive().isLessThan(maxAgeInclusive);

		int minimalAgeFirstLicensed = 16;
		int ageFirstLicensed = Math.abs(minAgeInclusive - tyde);
		if (ageFirstLicensed < minimalAgeFirstLicensed) {
			minAgeInclusive += Math.abs(minAgeInclusive - ageFirstLicensed);
			assertThat(minAgeInclusive)
					.as("Can't get random driver's age for minAgeInclusive=%1$s, maxAgeInclusive=%2$s and tyde=%3$s; new minAgeInclusive with tyde consideration should be less than maxAgeInclusive argument",
							minAgeInclusive, maxAgeInclusive, tyde).isLessThan(maxAgeInclusive);
		}
		return RandomUtils.nextInt(minAgeInclusive, maxAgeInclusive + 1);
	}

	protected TestData get3ViolationPointsActivityInformationData(LocalDate effectiveDate, Integer totalYearsAaccidentsFree) {
		String description = getRandom("Child seat belt violation", "Disregard police", "Driving left of center", "Driving on sidewalk");
		return getActivityInformationData(effectiveDate, "Minor Violation", description, totalYearsAaccidentsFree);
	}

	protected TestData get4ViolationPointsActivityInformationData(LocalDate effectiveDate, Boolean hasDriverTrainingDiscount, Integer totalYearsAaccidentsFree) {
		String incidentType = Boolean.TRUE.equals(hasDriverTrainingDiscount) ? "10-yr Major Violation" : "Major Violation";
		return getActivityInformationData(effectiveDate, incidentType, AdvancedComboBox.RANDOM_EXCEPT_EMPTY, totalYearsAaccidentsFree);
	}

	protected TestData getActivityInformationData(LocalDate effectiveDate, String type, String description, Integer totalYearsAccidentsFree) {
		// Incident should be not older than 33 month from effective date to affect premium;
		int maxIncidentFreeInMonth = maxIncidentFreeInMonthsToAffectRating;
		LocalDate latestIncidentDate = effectiveDate;

		if (totalYearsAccidentsFree != null) {
			assertThat(totalYearsAccidentsFree * 12).as("totalYearsAccidentsFree argument (or \"yaf\" field) in months should not be more than %s to affect rating", maxIncidentFreeInMonth)
					.isLessThanOrEqualTo(maxIncidentFreeInMonth);
			latestIncidentDate = latestIncidentDate.minusYears(totalYearsAccidentsFree);
			maxIncidentFreeInMonth = maxIncidentFreeInMonth - totalYearsAccidentsFree * 12;
		}

		String occurrenceAndConvictionDate = latestIncidentDate.minusDays(new Random().nextInt(maxIncidentFreeInMonth * 28)).format(DateTimeUtils.MM_DD_YYYY);
		return DataProviderFactory.dataOf(
				AutoCaMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), type,
				AutoCaMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), description,
				AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), occurrenceAndConvictionDate,
				AutoCaMetaData.DriverTab.ActivityInformation.CONVICTION_DATE.getLabel(), occurrenceAndConvictionDate,
				AutoCaMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_YAF.getLabel(), "Yes");
	}

	private Integer getVehicleTabValueFromDb(Integer collSymbol, Integer compSymbol, Integer modelYear, boolean isAutoSelect, String statCode) {
		String msrpVersion = isAutoSelect ? "MSRP_2000_SELECT" : "MSRP_2000_CHOICE";
		String getVinQuery = "select m.MSRPMIN, m.MSRPMAX from MSRPCOMPCOLLLOOKUP m where m.COLLSYMBOL=? and m.COMPSYMBOL=? and m.KEY in"
				+ " (select c.KEY from MSRPCOMPCOLLCONTROL c where c.MSRPVERSION=?"
				+ " and (? between c.VEHICLEYEARMIN and c.VEHICLEYEARMAX or (c.VEHICLEYEARMIN is null and c.VEHICLEYEARMAX is null))"
				+ " and (c.LIABILITYSYMBOL=? or c.LIABILITYSYMBOL is null))";
		Map<String, String> resultRow = DBService.get().getRow(getVinQuery, collSymbol, compSymbol, msrpVersion, modelYear, statCode);

		assertThat(resultRow)
				.as("Can't get MSRPMIN and MSRPMAX values from DB for collSymbol=%1$s, compSymbol=%2$s, modelYear=%3$s and biLiabilitySymbol=%4$s", collSymbol, compSymbol, modelYear, statCode)
				.isNotEmpty();
		return RandomUtils.nextInt(Integer.parseInt(resultRow.get("MSRPMIN")), Integer.parseInt(resultRow.get("MSRPMAX")));
	}
}
