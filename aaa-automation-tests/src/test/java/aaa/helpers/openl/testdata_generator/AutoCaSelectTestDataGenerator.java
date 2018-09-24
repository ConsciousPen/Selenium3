package aaa.helpers.openl.testdata_generator;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLCoverage;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLDriver;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLPolicy;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLVehicle;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import aaa.toolkit.webdriver.customcontrols.DetailedVehicleCoveragesRepeatAssetList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoCaSelectTestDataGenerator extends AutoCaTestDataGenerator<AutoCaSelectOpenLDriver, AutoCaSelectOpenLVehicle, AutoCaSelectOpenLPolicy> {

	public AutoCaSelectTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoCaSelectOpenLPolicy openLPolicy) {
		TestData ratingDataPattern = getRatingDataPattern().resolveLinks();
		if (Boolean.FALSE.equals(openLPolicy.isAaaMember())) {
			ratingDataPattern
					.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel()))
					.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel()));
		}

		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new AssignmentTab().getMetaKey(), getAssignmentTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
		return TestDataHelper.merge(ratingDataPattern, td);
	}

	@Override
	protected TestData getDriverTabInformationData(AutoCaSelectOpenLDriver openLDriver, boolean isFirstDriver, LocalDate policyEffectiveDate) {
		TestData driverData = super.getDriverTabInformationData(openLDriver, isFirstDriver, policyEffectiveDate);
		assertThat(openLDriver.getYaf()).as("\"yaf\" openl field should have maximum 7 Years activity (accidents) free").isLessThanOrEqualTo(7);

		if (Boolean.TRUE.equals(openLDriver.isNewDriver())) {
			LocalDate driversDateOfBirth = TimeSetterUtil.getInstance().parse(driverData.getValue(AutoCaMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), DateTimeUtils.MM_DD_YYYY).toLocalDate();
			LocalDate newDriverCourseCompletionMinDate = driversDateOfBirth.plusYears(16);
			LocalDate newDriverCourseCompletionMaxDate = driversDateOfBirth.plusYears(19).isBefore(policyEffectiveDate) ? driversDateOfBirth.plusYears(19) : policyEffectiveDate;
			assertThat(newDriverCourseCompletionMinDate).as("Calculated minimum allowable New Driver Course Completion Date should be less than maximum one").isBefore(newDriverCourseCompletionMaxDate);
			int duration = Math.abs(Math.toIntExact(ChronoUnit.DAYS.between(newDriverCourseCompletionMinDate, newDriverCourseCompletionMaxDate)));
			LocalDate newDriverCourseCompletionDate = duration == 0 ? newDriverCourseCompletionMinDate : newDriverCourseCompletionMinDate.plusDays(new Random().nextInt(duration));

			driverData
					.adjust(AutoCaMetaData.DriverTab.DRIVER_TYPE.getLabel(), "Available for Rating")
					.adjust(AutoCaMetaData.DriverTab.NEW_DRIVER_COURSE_COMPLETED.getLabel(), "Yes")
					.adjust(AutoCaMetaData.DriverTab.NEW_DRIVER_COURSE_COMPLETION_DATE.getLabel(), newDriverCourseCompletionDate.format(DateTimeUtils.MM_DD_YYYY));
		}

		return driverData;
	}

	@Override
	protected List<TestData> getDriverTabActivityInformationData(AutoCaSelectOpenLDriver openLDriver, LocalDate policyEffectiveDate) {
		List<TestData> activityInformationList = new ArrayList<>();
		switch (openLDriver.getDsr()) {
			//TODO-dchubkov: implement logic to add incidents with any violation points number
			case 3:
				activityInformationList.add(get3ViolationPointsActivityInformationData(policyEffectiveDate, openLDriver.getYaf()));
				break;
			case 4:
				activityInformationList.add(get4ViolationPointsActivityInformationData(policyEffectiveDate, false, openLDriver.getYaf()));
				break;
			case 13:
				activityInformationList.add(get3ViolationPointsActivityInformationData(policyEffectiveDate, openLDriver.getYaf()));
				activityInformationList.add(get3ViolationPointsActivityInformationData(policyEffectiveDate, openLDriver.getYaf()));
				activityInformationList.add(get3ViolationPointsActivityInformationData(policyEffectiveDate, openLDriver.getYaf()));
				activityInformationList.add(get4ViolationPointsActivityInformationData(policyEffectiveDate, false, openLDriver.getYaf()));
				break;
			default:
				throw new IstfException(String.format("Unknown mapping for dsr=%s value", openLDriver.getDsr()));
		}
		return activityInformationList;
	}

	@Override
	protected int getDriverAge(AutoCaSelectOpenLDriver openLDriver) {
		int driverAge;
		if (Boolean.TRUE.equals(openLDriver.isMatureDriver())) {
			assertThat(openLDriver.isNewDriver()).as("Driver's \"newDriver\" field should not be TRUE if field \"matureDriver\" is also TRUE").isFalse();
			driverAge = getRandomAge(50, 80, openLDriver.getTyde());
		} else {
			if (Boolean.TRUE.equals(openLDriver.isNewDriver())) {
				assertThat(openLDriver.getMaritalStatus()).as("Unable to generate driver's test data for newDriver=true if marital status is not single").isEqualTo("S");
				driverAge = getRandomAge(16, 25, openLDriver.getTyde());
			} else {
				driverAge = getRandomAge(26, 49, openLDriver.getTyde());
			}
		}
		return driverAge;
	}

	@Override
	protected TestData getVehicleTabInformationData(AutoCaSelectOpenLVehicle openLVehicle) {
		TestData vehicleInformation = super.getVehicleTabInformationData(openLVehicle);
		//TODO-dchubkov: implement test data generation for optionalCoverages field (postponed since all tests have empty values)
		assertThat(openLVehicle.getOptionalCoverages()).as("Test data generation for non-empty optionalCoverages field is not implemented").isNullOrEmpty();

		String statCode = openLVehicle.getBiLiabilitySymbol();
		String usage;
		String milesToWorkOrSchool = null;

		switch (openLVehicle.getCommuteBand()) {
			case "3":
				usage = "Business (small business non-commercial)";
				break;
			case "6":
				usage = "Business (small business non-commercial)";
				List<String> allowedStatCodes = Arrays.asList("R", "O", "U", "W");
				assertThat(statCode).as("Invalid stat code \"%$1s\" for commuteBand=%$2s; for this commuteBand only %$3s stat codes are allowed", statCode, openLVehicle.getCommuteBand(), allowedStatCodes)
						.isIn(allowedStatCodes);
				break;
			case "8":
				usage = "Farm business (farm to market delivery)";
				break;
			case "A":
				if ("A".equals(statCode) || "M".equals(statCode) || "T".equals(statCode)) {
					usage = "Business (small business non-commercial)";
				} else {
					usage = "Pleasure (recreational driving only)";
				}
				break;
			case "B":
				usage = "Commute (to/from work and school)";
				milesToWorkOrSchool = String.valueOf(RandomUtils.nextInt(1, 20));
				break;
			case "C":
				usage = "Commute (to/from work and school)";
				milesToWorkOrSchool = String.valueOf(RandomUtils.nextInt(20, 100));
				break;
			case "Z":
				usage = "Farm non-business(on premises)";
				break;
			default:
				throw new IstfException("Unknown mapping for openl field commuteBand=" + openLVehicle.getCommuteBand());

		}

		vehicleInformation.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), usage);
		if ("Business (small business non-commercial)".equals(usage)) {
			vehicleInformation
					.adjust(AutoCaMetaData.VehicleTab.IS_THE_VEHICLE_USED_IN_ANY_COMMERCIAL_BUSINESS_OPERATIONS.getLabel(), "Yes")
					.adjust(AutoCaMetaData.VehicleTab.BUSINESS_USE_DESCRIPTION.getLabel(), "some business use description $<rx:\\d{3}>");
		}
		if (milesToWorkOrSchool != null) {
			vehicleInformation.adjust(AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel(), milesToWorkOrSchool);
		}

		Map<String, String> ownershipData = new HashMap<>();
		String type;
		if (Boolean.TRUE.equals(openLVehicle.isGapCoverage() && Boolean.TRUE.equals(openLVehicle.isNewCarProtection()))) {
			type = "Financed";
		} else if (Boolean.TRUE.equals(openLVehicle.isNewCarProtection())) {
			type = getRandom("Owned", "Financed");
		} else if (Boolean.TRUE.equals(openLVehicle.isGapCoverage())) {
			type = getRandom("Leased", "Financed");
		} else {
			type = "Owned";
		}

		ownershipData.put(AutoCaMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE.getLabel(), type);
		if (!"Owned".equals(type)) {
			ownershipData.put(AutoCaMetaData.VehicleTab.Ownership.FIRST_NAME.getLabel(), "BANK OF AMERICA");
		}

		return vehicleInformation.adjust(AutoCaMetaData.VehicleTab.OWNERSHIP.getLabel(), new SimpleDataProvider(ownershipData));
	}

	@Override
	protected TestData getPremiumAndCoveragesTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		TestData td = super.getPremiumAndCoveragesTabData(openLPolicy);
		td.adjust(AutoCaMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), String.valueOf(openLPolicy.isMultiCar()));

		boolean isApplyFixedExpenseSet = false;
		//TODO-dchubkov: Vehicles coverages test data should be ordered same as appropriate vehicles. Think about how to generate test data without sort order dependency
		for (int i = 0; i < openLPolicy.getVehicles().size(); i++) {
			AutoCaSelectOpenLVehicle vehicle = openLPolicy.getVehicles().get(i);
			TestData vehicleCoverage = td.getTestDataList(AutoCaMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel()).get(i);

			if (Boolean.TRUE.equals(vehicle.isApplyFixedExpense())) {
				assertThat(isApplyFixedExpenseSet).as("applyFixedExpense=TRUE is allowed only for one vehicle").isFalse();
				assertThat(vehicle.getCoverages().stream().anyMatch(c -> "BI".equals(c.getCoverageCd()) && StringUtils.isNotBlank(c.getLimit())))
						.as("If vehicle's openl field applyFixedExpense=TRUE then it should have at least one non-empty BI coverage").isTrue();

				vehicleCoverage.adjust(DetailedVehicleCoveragesRepeatAssetList.WAIVE_LIABILITY, "No");
				isApplyFixedExpenseSet = true;
			}

			// Should be automatically set after covering ETEC coverage
			/*if (Boolean.TRUE.equals(vehicle.isEte())) {
				vehicleCoverage.adjust(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.ENHANCED_TRASPORTATION_EXPENCE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_CONTAINS_MARK + "=No Coverage");
			}*/

			if (Boolean.TRUE.equals(vehicle.isFullGlassCoverage())) {
				vehicleCoverage.adjust(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.FULL_SAFETY_GLASS.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_CONTAINS_MARK + "=No Coverage");
			}

			if (Boolean.TRUE.equals(vehicle.isOemCoverage())) {
				vehicleCoverage.adjust(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.ORIGINAL_EQUIPMENT_MANUFACTURER_PARTS.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_CONTAINS_MARK + "=No Coverage");
			}

			if (Boolean.TRUE.equals(vehicle.isRideShareCov())) {
				vehicleCoverage.adjust(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.RIDESHARING_COVERAGE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_CONTAINS_MARK + "=No Coverage");
			}

			if (Boolean.TRUE.equals(vehicle.isGapCoverage())) {
				vehicleCoverage.adjust(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.VEHICLE_LOAN_OR_LEASE_PROTECTION.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_CONTAINS_MARK + "=No Coverage");
			}

			if (Boolean.TRUE.equals(vehicle.isNewCarProtection())) {
				vehicleCoverage.adjust(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.NEW_CAR_ADDED_PROTECTION.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_CONTAINS_MARK + "=No Coverage");
				vehicleCoverage.adjust(AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.PURCHASE_DATE.getLabel(), TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
			}

		}
		return td;
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
	protected String getVehicleTabType(AutoCaSelectOpenLVehicle openLVehicle) {
		String statCode = openLVehicle.getBiLiabilitySymbol();
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

	@Override
	protected String[] getLimitOrDeductibleRange(AutoOpenLCoverage coverage) {
		if ("ETEC".equals(coverage.getCoverageCd())) {
			String limitCode = ((AutoCaSelectOpenLCoverage) coverage).getLimitCode();
			String[] limitRange = limitCode.split("/");
			assertThat(limitRange.length).as("Unknown mapping for limitCode: %s", limitCode).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(2);
			return limitRange;
		}
		return super.getLimitOrDeductibleRange(coverage);
	}

	private TestData getAssignmentTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		List<TestData> driverVehicleRelationshipTable = new ArrayList<>(openLPolicy.getVehicles().size());
		for (AutoCaSelectOpenLVehicle vehicle : openLPolicy.getVehicles()) {
			assertThat(vehicle.getPrimaryDriver().getType()).as("Vehicle's primary driver should have type=P").isEqualTo("P");

			String driverId = vehicle.getPrimaryDriver().getId();
			String primaryDriver = driverId.startsWith("Dr1") ? "contains=Smith" : driverId + DRIVER_FN_POSTFIX + " " + driverId + DRIVER_LN_POSTFIX;
			String manuallyRatedDriver = "";

			if (vehicle.getManuallyAssignedDriver() != null) {
				assertThat(vehicle.getManuallyAssignedDriver().getType()).as("Vehicle's manually assigned driver should have type=U or type=O").isIn("U", "O");
				assertThat(vehicle.istManuallyAssignedUndesignatedDriverInd()).as("Vehicle's \"manuallyAssignedUndesignatedDriverInd\" should be TRUE if \"manuallyAssignedDriver\" is not empty").isTrue();

				if ("U".equals(vehicle.getManuallyAssignedDriver().getType())) {
					manuallyRatedDriver = "Undesignated";
				} else { // O is other (just driver which is not mentioned in Primary Driver combobox)
					manuallyRatedDriver = AdvancedComboBox.RANDOM_EXCEPT_CONTAINS_MARK + "=|Undesignated|" + primaryDriver.replace("contains=", "");
				}
			}

			TestData assignmentData = DataProviderFactory.dataOf(
					AutoCaMetaData.AssignmentTab.DriverVehicleRelationshipTableRow.PRIMARY_DRIVER.getLabel(), primaryDriver,
					AutoCaMetaData.AssignmentTab.DriverVehicleRelationshipTableRow.MANUALLY_RATED_DRIVER.getLabel(), manuallyRatedDriver);
			driverVehicleRelationshipTable.add(assignmentData);
		}

		return DataProviderFactory.dataOf(AutoCaMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP.getLabel(), driverVehicleRelationshipTable);
	}

	private TestData getGeneralTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		LocalDate baseDate = LocalDate.of(openLPolicy.getBaseYear(), openLPolicy.getEffectiveDate().getMonth(), openLPolicy.getEffectiveDate().getDayOfMonth());
		TestData namedInsuredInformationData = DataProviderFactory.dataOf(AutoCaMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel(), baseDate.format(DateTimeUtils.MM_DD_YYYY));

		TestData policyInformationData = DataProviderFactory.dataOf(
				AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY));

		TestData aaaProductOwnedData = DataProviderFactory.emptyData();
		if (openLPolicy.getHome3or4() != null) {
			switch (openLPolicy.getHome3or4()) {
				case "HO-3":
					aaaProductOwnedData.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.HOME.getLabel(), "Yes");
					break;
				case "HO-4":
					aaaProductOwnedData.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.RENTERS.getLabel(), "Yes");
					break;
				case "HO-6":
					aaaProductOwnedData.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.CONDO.getLabel(), "Yes");
					break;
				case "None":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.HOME.getLabel(), "No")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.RENTERS.getLabel(), "No")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.CONDO.getLabel(), "No");
					break;
				default:
					throw new IstfException("Unknown mapping for openl field home3or4=" + openLPolicy.getHome3or4());
			}
		}

		if (openLPolicy.getLifemoto() != null) {
			switch (openLPolicy.getLifemoto()) {
				case "B":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), "Yes")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), "Yes");
					break;
				case "L":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), "No")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), "Yes");
					break;
				case "M":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), "Yes")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), "No");
					break;
				case "N":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), "No")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), "No");
					break;
				default:
					throw new IstfException("Unknown mapping for openl field lifemoto=" + openLPolicy.getLifemoto());
			}
		}

		return DataProviderFactory.dataOf(
				AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), namedInsuredInformationData,
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), aaaProductOwnedData,
				AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), policyInformationData);
	}

	private boolean isRegularType(String statCode) {
		List<String> codes = Arrays.asList("B", "C", "D", "E", "H", "I", "J", "K", "L", "N", "O", "P", "Q", "R", "S", "U", "V", "W", "X", "Y", "Z");
		return codes.contains(statCode);
	}

	private boolean isAntiqueClassicType(String statCode) {
		return "A".equals(statCode);
	}

	private boolean isCamperType(String statCode) {
		List<String> codes = Arrays.asList("RQ", "RT", "FW", "UT", "PC", "HT", "PT");
		return codes.contains(statCode);
	}
}
