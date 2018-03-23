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
import aaa.main.modules.policy.auto_ca.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
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
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				//new MembershipTab().getMetaKey(), getMembershipTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new AssignmentTab().getMetaKey(), getAssignmentTabData(openLPolicy),
				//new FormsTab().getMetaKey(), getFormsTabTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
		return TestDataHelper.merge(getRatingDataPattern(), td);
	}

	private TestData getPrefillTabData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getGeneralTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private List<TestData> getDriverTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		List<TestData> driversTestDataList = new ArrayList<>(openLPolicy.getDrivers().size());
		boolean isFirstDriver = true;
		for (AutoCaChoiceOpenLDriver driver : openLPolicy.getDrivers()) {
			int driverAge = Boolean.TRUE.equals(driver.isMatureDriver()) ? RandomUtils.nextInt(50, 81) : RandomUtils.nextInt(16, 50);
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
					AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel(), "C1234567",
					AutoCaMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), getDriverTabDateOfBirth(driverAge, openLPolicy.getEffectiveDate()),
					AutoCaMetaData.DriverTab.MATURE_DRIVER_COURSE_COMPLETED_WITHIN_36_MONTHS.getLabel(), driverAge >= 50 ? getYesOrNo(driver.isMatureDriver()) : null,
					AutoCaMetaData.DriverTab.MATURE_DRIVER_COURSE_COMPLETION_DATE.getLabel(), Boolean.TRUE.equals(driver.isMatureDriver())
							? openLPolicy.getEffectiveDate().minusMonths(new Random().nextInt(36)).format(DateTimeUtils.MM_DD_YYYY) : null,
					AutoCaMetaData.DriverTab.MOST_RECENT_GPA.getLabel(), driverAge <= 25 ? "regex=.*\\S.*" : null,
					AutoCaMetaData.DriverTab.SMOKER_CIGARETTES_OR_PIPES.getLabel(), Boolean.TRUE.equals(driver.isNonSmoker()) ? "No" : "Yes");

			List<TestData> activityInformationList = new ArrayList<>();
			if (driver.getDsr() != null && driver.getDsr() > 0) {
				switch (driver.getDsr()) {
					case 4:
						activityInformationList.add(get4ViolationPointsActivityInformationData(openLPolicy.getEffectiveDate()));
						break;
					case 10:
						activityInformationList.add(get3ViolationPointsActivityInformationData(openLPolicy.getEffectiveDate()));
						activityInformationList.add(get3ViolationPointsActivityInformationData(openLPolicy.getEffectiveDate()));
						activityInformationList.add(get4ViolationPointsActivityInformationData(openLPolicy.getEffectiveDate()));
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

	private TestData getFormsTabTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getPremiumAndCoveragesTabData(AutoCaChoiceOpenLPolicy openLPolicy) {
		/*Map<String, Object> unverifiableDrivingRecordSurchargeData = new HashMap<>();
		//TODO-dchubkov: There are no drivers list in "Unacceptable Risk Surcharge" section for NY state, to be investigated
		if (!getState().equals(Constants.States.NY)) {
			boolean isFirstDriver = true;
			for (AutoCaChoiceOpenLDriver driver : openLPolicy.getDrivers()) {
				if (isFirstDriver) {
					unverifiableDrivingRecordSurchargeData.put(UnverifiableDrivingRecordSurcharge.DRIVER_SELECTION_BY_CONTAINS_KEY + "Smith", driver.isUnverifiableDrivingRecord());
					isFirstDriver = false;
				} else {
					unverifiableDrivingRecordSurchargeData.put(driver.getName() + " " + driver.getName(), driver.isUnverifiableDrivingRecord());
				}
			}
		}*/

		List<TestData> detailedVehicleCoveragesList = new ArrayList<>(openLPolicy.getVehicles().size());
		Map<String, Object> policyCoveragesData = new HashMap<>();
		Map<String, Object> detailedCoveragesData = new HashMap<>();

		for (AutoCaChoiceOpenLVehicle vehicle : openLPolicy.getVehicles()) {
			//boolean isTrailerOrMotorHomeVehicle = isTrailerOrMotorHomeType(vehicle.getUsage());
			for (AutoOpenLCoverage coverage : vehicle.getCoverages()) {
				String coverageName = getPremiumAndCoveragesTabCoverageName(coverage.getCoverageCd());
				if (isPolicyLevelCoverage(coverage.getCoverageCd())) {
					policyCoveragesData.put(coverageName, getPremiumAndCoveragesTabLimitOrDeductible(coverage));
				} else {
					detailedCoveragesData.put(coverageName, getPremiumAndCoveragesTabLimitOrDeductible(coverage));
				}

				/*if (isTrailerOrMotorHomeVehicle) {
					//TODO-dchubkov: tests for "Trailer" and "Motor Home" vehicle types sometimes have "SP EQUIP" coverage which is impossible to set via UI
					detailedCoveragesData.remove(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.SPECIAL_EQUIPMENT_COVERAGE.getLabel());
				} else {
					detailedCoveragesData
							.put(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.FULL_SAFETY_GLASS.getLabel(), getPremiumAndCoveragesFullSafetyGlass(coverage.getGlassDeductible()));
				}*/

			}
			detailedVehicleCoveragesList.add(new SimpleDataProvider(detailedCoveragesData));
			detailedCoveragesData.clear();
		}

		return DataProviderFactory.dataOf(
				AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel(), getTerm(openLPolicy.getTerm()),
				//AutoCaMetaData.PremiumAndCoveragesTab.UNACCEPTABLE_RISK_SURCHARGE.getLabel(), openLPolicy.isUnacceptableRisk(),
				AutoCaMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS.getLabel(), "Yes", //TODO-dchubkov: enable only if need to fill expanded section
				AutoCaMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), openLPolicy.isMultiCar(),
				//AutoCaMetaData.PremiumAndCoveragesTab.UNVERIFIABLE_DRIVING_RECORD_SURCHARGE.getLabel(), new SimpleDataProvider(unverifiableDrivingRecordSurchargeData),
				AutoCaMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), detailedVehicleCoveragesList)
				.adjust(new SimpleDataProvider(policyCoveragesData));
	}

	private TestData getVehicleTabInformationData(AutoCaChoiceOpenLVehicle vehicle) {
		assertThat(vehicle.getAddress()).as("Vehicle's address list should have only one address").hasSize(1);
		Map<String, Object> vehicleInformation = new HashMap<>();
		String statCode = vehicle.getStatCode() != null ? vehicle.getStatCode() : vehicle.getBiLiabilitySymbol();
		String vehicleType = getVehicleTabType(statCode, vehicle.getModelYear());

		vehicleInformation.put(AutoCaMetaData.VehicleTab.TYPE.getLabel(), vehicleType);
		vehicleInformation.put(AutoCaMetaData.VehicleTab.YEAR.getLabel(), vehicle.getModelYear());
		vehicleInformation.put(AutoCaMetaData.VehicleTab.VALUE.getLabel(), 1000);

		if (vehicle.getModelYear() > 1980) {
			vehicleInformation.put(AutoCaMetaData.VehicleTab.MAKE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.MODEL.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.SERIES.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
		} else {
			vehicleInformation.put(AutoCaMetaData.VehicleTab.MAKE.getLabel(), "OTHER");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.OTHER_MAKE.getLabel(), "some other make $<rx:\\d{3}>");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.OTHER_MODEL.getLabel(), "some other model $<rx:\\d{3}>");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.OTHER_SERIES.getLabel(), "some other series $<rx:\\d{3}>");
			vehicleInformation.put(AutoCaMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel(), "some other body style $<rx:\\d{3}>");
			if ("Antique / Classic".equals(vehicleType)) {
				vehicleInformation.put(AutoCaMetaData.VehicleTab.RESTORED_TO_ORGINAL_STOCK_CONDITION_WITH_NO_ALTERATIONS.getLabel(), "Yes");
				vehicleInformation.put(AutoCaMetaData.VehicleTab.USED_SOLEY_IN_EXHIBITIONS_CLUB_ACTIVITY_PARADES_AND_OTHER_FUN_FUNCTIONS_OF_PUBLIC_INTEREST.getLabel(), "Yes");
			}
		}

		//TODO-dchubkov: just guessing, to be verified
		vehicleInformation.put(AutoCaMetaData.VehicleTab.ANTI_THEFT.getLabel(), vehicle.getAntiTheft() ? "STD" : "OPT");
		if (!"T".equals(statCode) && !"C".equals(statCode)) { // if not Trailer and Camper type
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
			case "P":
				vehicleInformation.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Pleasure (recreational driving only)");
				break;
			case "BU":
				vehicleInformation.put(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Farm business (farm to market delivery)");
				break;
			default:
				throw new IstfException("Unknown mapping for vehicleUsageCd: " + vehicle.getVehicleUsageCd());
		}
		return new SimpleDataProvider(vehicleInformation);
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

	private TestData get3ViolationPointsActivityInformationData(LocalDateTime effectiveDate) {
		return getActivityInformationData(effectiveDate, "Minor Violation", getRandom("Child seat belt violation", "Disregard police", "Driving left of center", "Driving on sidewalk"));
	}

	private TestData get4ViolationPointsActivityInformationData(LocalDateTime effectiveDate) {
		return getActivityInformationData(effectiveDate, "Major Violation", "regex=.*\\S.*");
	}

	private TestData getActivityInformationData(LocalDateTime effectiveDate, String type, String description) {
		String occurrenceAndConvictionDate = effectiveDate.minusMonths(new Random().nextInt(36)).format(DateTimeUtils.MM_DD_YYYY);
		return DataProviderFactory.dataOf(
				AutoCaMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), type,
				AutoCaMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), description,
				AutoCaMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), occurrenceAndConvictionDate,
				AutoCaMetaData.DriverTab.ActivityInformation.CONVICTION_DATE.getLabel(), occurrenceAndConvictionDate,
				AutoCaMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_YAF.getLabel(), "Yes");
	}

}
