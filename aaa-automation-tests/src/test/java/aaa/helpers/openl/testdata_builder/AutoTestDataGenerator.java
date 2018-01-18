package aaa.helpers.openl.testdata_builder;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.openl.model.OpenLCoverage;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

abstract class AutoTestDataGenerator<P extends OpenLPolicy> extends TestDataGenerator<P> {

	AutoTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	String getDriverTabGender(String gender) {
		if ("F".equals(gender)) {
			return "Female";
		}
		if ("M".equals(gender)) {
			return "Male";
		}
		throw new IstfException("Unknown mapping for gender: " + gender);
	}

	String getDriverTabMartialStatus(String martialStatus) {
		// Rating engine accepts S, M and W
		switch (martialStatus) {
			case "M":
				return getRandom("Married", "Registered Domestic Partner");//, "Common Law", "Civil Union");
			case "S":
				return getRandom("Single", "Divorced", "Separated");
			case "W":
				return "Widowed";
			default:
				throw new IstfException("Unknown mapping for martialStatus or not acceptable by rating engine: " + martialStatus);
		}
	}

	String getDriverTabDateOfBirth(Integer driverAge, LocalDateTime policyEffectiveDate) {
		LocalDateTime dateOfBirth = policyEffectiveDate.minusYears(driverAge);
		// If driver's age is 24 and his birthday is within 30 days of the policy effective date, then driver's age is mapped as 25
		if (driverAge == 24 && dateOfBirth.isAfter(policyEffectiveDate) && dateOfBirth.isBefore(policyEffectiveDate.plusDays(30))) {
			dateOfBirth = dateOfBirth.plusYears(1);
		}
		return dateOfBirth.format(DateTimeUtils.MM_DD_YYYY);
	}

	String getDriverTabDateOfBirth(int ageFirstLicensed, int totalYearsDrivingExperience) {
		LocalDateTime dateOfBirth = TimeSetterUtil.getInstance().getCurrentTime().minusYears(ageFirstLicensed + totalYearsDrivingExperience);
		return dateOfBirth.format(DateTimeUtils.MM_DD_YYYY);
	}

	String getDriverTabLicenseType(boolean isForeignLicense) {
		if (isForeignLicense) {
			return "Foreign";
		}
		return AdvancedComboBox.RANDOM_EXCEPT_MARK + "=Foreign|Not Licensed|Learner's Permit|";
	}

	TestData getVehicleTabInformationData(OpenLVehicle vehicle, boolean isTrailer) {
		assertThat(vehicle.getAddress()).as("Vehicle's address list should have only one address").hasSize(1);

		Map<String, Object> vehicleInformation = new HashMap<>();
		int streetNumber = RandomUtils.nextInt(100, 1000);
		String streetName = RandomStringUtils.randomAlphabetic(10).toUpperCase() + " St";

		vehicleInformation.put(AutoSSMetaData.VehicleTab.YEAR.getLabel(), vehicle.getModelYear());
		vehicleInformation.put(AutoSSMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel(), "Yes");
		vehicleInformation.put(AutoSSMetaData.VehicleTab.ZIP_CODE.getLabel(), vehicle.getAddress().get(0).getZip());
		vehicleInformation.put(AutoSSMetaData.VehicleTab.ADDRESS_LINE_1.getLabel(), streetNumber + " " + streetName);
		vehicleInformation.put(AutoSSMetaData.VehicleTab.STATE.getLabel(), vehicle.getAddress().get(0).getState());
		vehicleInformation.put(AutoSSMetaData.VehicleTab.VALIDATE_ADDRESS_BTN.getLabel(), "click");
		vehicleInformation.put(AutoSSMetaData.VehicleTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.dataOf("Street number", streetNumber, "Street Name", streetName));
		//TODO-dchubkov: Replace with valid stat code as soon as I get answer how to make these values appear on UI
		//vehicleInformation.put(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "contains=" + vehicle.getStatCode());
		vehicleInformation.put(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "regex=.*\\S.*");
		vehicleInformation.put(AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel(), "$<rx:\\d{3}>00");

		if (isTrailerOrMotorHomeUsage(vehicle.getUsage())) {
			vehicleInformation.put(AutoSSMetaData.VehicleTab.TYPE.getLabel(), isTrailer ? "Trailer" : "Motor Home");
			vehicleInformation.put(isTrailer ? AutoSSMetaData.VehicleTab.TRAILER_TYPE.getLabel() : AutoSSMetaData.VehicleTab.MOTOR_HOME_TYPE.getLabel(), "regex=.*\\S.*");
			vehicleInformation.put(AutoSSMetaData.VehicleTab.PRIMARY_OPERATOR.getLabel(), "regex=.*\\S.*");
			vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel(), "some other make $<rx:\\d{3}>");
			vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel(), "some other model $<rx:\\d{3}>");
			if (!isTrailer) {
				vehicleInformation.remove(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel()); // not available for Motor Home
			}
		} else {
			vehicleInformation.put(AutoSSMetaData.VehicleTab.TYPE.getLabel(), getRandom("Private Passenger Auto", "Conversion Van"));
			vehicleInformation.put(AutoSSMetaData.VehicleTab.MAKE.getLabel(), "regex=.*\\S.*");
			vehicleInformation.put(AutoSSMetaData.VehicleTab.MODEL.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
			vehicleInformation.put(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel(), "regex=.*\\S.*");
			vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel(), "regex=.*\\S.*");
			vehicleInformation.put(AutoSSMetaData.VehicleTab.AIR_BAGS.getLabel(), getVehicleTabAirBags(vehicle.getAirbagCode()));
			vehicleInformation.put(AutoSSMetaData.VehicleTab.ANTI_THEFT.getLabel(), getVehicleTabAntiTheft(vehicle.getAntiTheftString()));
		}

		switch (vehicle.getUsage()) {
			case "A":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Artisan");
				break;
			case "B":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Business");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.IS_THE_VEHICLE_USED_IN_ANY_COMMERCIAL_BUSINESS_OPERATIONS.getLabel(), "No");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.BUSINESS_USE_DESCRIPTION.getLabel(), "some description $<rx:\\d{3}>");
				break;
			case "F":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Farm");
				break;
			case "W1":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Commute");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel(), RandomUtils.nextInt(1, 15));
				break;
			case "W2":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Commute");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel(), RandomUtils.nextInt(16, 100));
				break;
			case "P":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Pleasure"); // or Nano in case of Nano policy
				break;

			//For Tailer, Golf Cart and Motor Home
			case "P1":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Pleasure Use - Occupied Less than 30 Days a Year");
				break;
			case "P2":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Pleasure Use - Occupied 30-150 Days a Year");
				break;
			case "P3":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Pleasure Use - Occupied More than 150 Days a Year");
				break;
			case "PT":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Traveling Primary Residence");
				break;
			case "PR":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Non-Traveling Primary Residence");
				break;
			default:
				throw new IstfException("Unknown mapping for usage: " + vehicle.getUsage());
		}

		return new SimpleDataProvider(vehicleInformation);
	}

	boolean isTrailerOrMotorHomeUsage(String usage) {
		return "P1".equals(usage) || "P2".equals(usage) || "P3".equals(usage) || "PT".equals(usage) || "PR".equals(usage);
	}

	String getVehicleTabAntiTheft(String antiTheft) {
		if ("N".equals(antiTheft)) {
			return "None";
		}
		//TODO-dchubkov: get UI value for "A", "P", "Y" antiTheft
		return "Vehicle Recovery Device";
	}

	String getVehicleTabAirBags(String airBagCode) {
		switch (airBagCode) {
			case "N":
				return "None";
			case "1":
				return "Driver";
			case "2":
				return "Both Front";
			case "3":
				return "Both Front and Side";
			case "4":
				return "Both Front and Side with Rear Side";
			default:
				throw new IstfException("Unknown mapping for airbagCode: " + airBagCode);
		}
	}

	String getPremiumAndCoveragesTabCoverageKey(String coverageCD) {
		switch (coverageCD) {
			case "BI":
				return AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel();
			case "PD":
				return AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY.getLabel();
			case "UMBI":
				return AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel();
			case "SP EQUIP":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.SPECIAL_EQUIPMENT_COVERAGE.getLabel();
			case "COMP":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COMPREGENSIVE_DEDUCTIBLE.getLabel();
			case "COLL":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COLLISION_DEDUCTIBLE.getLabel();
			case "UMPD":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.UNINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel();
			case "MP":
				return AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_PAYMENTS.getLabel();
			case "PIP":
				return AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION.getLabel();
			default:
				throw new IstfException("Unknown mapping for coverageCD: " + coverageCD);
		}
	}

	TestData getVehicleTabVehicleDetailsData(String vin, String safetyScore) {
		return DataProviderFactory.dataOf(AutoSSMetaData.VehicleTab.VIN.getLabel(), vin,
				AutoSSMetaData.VehicleTab.ENROLL_IN_USAGE_BASED_INSURANCE.getLabel(), "Yes",
				AutoSSMetaData.VehicleTab.GET_VEHICLE_DETAILS.getLabel(), "click",
				AutoSSMetaData.VehicleTab.SAFETY_SCORE.getLabel(), safetyScore);
	}

	String getPremiumAndCoveragesPaymentPlan(String paymentPlanType) {
		switch (paymentPlanType) {
			case "A":
				return "Quarterly";
			case "B":
				return "Eleven Pay - Standard";
			case "C":
				return "Semi-Annual";
			case "L":
				return getRandom("Eleven Pay - Low Down", "Monthly - Low Down"); //TODO-dchubkov: to be verified
			case "P":
				return getRandom("Annual", "Semi-Annual");
			case "Z":
				return getRandom("Eleven Pay - Zero Down", "Monthly - Zero Down");
			default:
				throw new IstfException("Unknown mapping for paymentPlanType: " + paymentPlanType);
		}
	}

	String getPremiumAndCoveragesTabLimitOrDeductible(OpenLCoverage coverage) {
		String coverageCD = coverage.getCoverageCD();
		if ("SP EQUIP".equals(coverageCD)) {
			return new Dollar(coverage.getLimit()).toString();
		}

		String limitOrDeductible = "COMP".equals(coverageCD) || "COLL".equals(coverageCD) ? coverage.getDeductible() : coverage.getLimit();
		String[] limitRange = limitOrDeductible.split("/");
		String returnLimit;
		if (limitRange.length > 2) {
			throw new IstfException("Unknown mapping for limit/Deductible: " + limitOrDeductible);
		}
		returnLimit = "contains=" + new Dollar(limitRange[0] + "000").toString().replaceAll("\\.00", "");
		if (limitRange.length == 2) {
			returnLimit = returnLimit + "/" + new Dollar(limitRange[1] + "000").toString().replaceAll("\\.00", "");
		}
		return returnLimit;
	}

	String getPremiumAndCoveragesFullSafetyGlass(String glassDeductible) {
		return "N/A".equals(glassDeductible) ? "No Coverage" : "Yes";
	}

	String getGeneralTabResidence(boolean isHomeOwner) {
		if (isHomeOwner) {
			return getRandom("Own Home", "Own Condo", "Own Mobile Home");
		}
		return getRandom("Rents Multi-Family Dwelling", "Rents Single-Family Dwelling", "Lives with Parent", "Other");
	}

	TestData getGeneralTabAgentInceptionAndExpirationData(Integer autoInsurancePersistency, Integer aaaInsurancePersistency, LocalDateTime policyEffectiveDate) {
		assertThat(autoInsurancePersistency).isGreaterThanOrEqualTo(aaaInsurancePersistency)
				.as("\"autoInsurancePersistency\" openL field should be equal or greater than \"aaaInsurancePersistency\"");

		LocalDateTime inceptionDate = autoInsurancePersistency.equals(aaaInsurancePersistency)
				? policyEffectiveDate : policyEffectiveDate.minusYears(autoInsurancePersistency - aaaInsurancePersistency);

		LocalDateTime expirationDate = policyEffectiveDate.plusDays(new Random().nextInt((int) Duration.between(policyEffectiveDate, TimeSetterUtil.getInstance().getCurrentTime()).toDays()));

		return DataProviderFactory.dataOf(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel(), inceptionDate.format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel(), expirationDate.format(DateTimeUtils.MM_DD_YYYY));
	}

	String getGeneralTabTerm(int term) {
		switch (term) {
			case 12:
				return "Annual";
			case 6:
				return Constants.States.VA.equals(getState()) ? "Semi-annual" : "Semi-Annual";
			default:
				throw new IstfException("Unable to build test data. Unsupported openL policy term: " + term);
		}
	}

	String generalTabIsAdvanceShopping(boolean isAdvanceShopping) {
		if (isAdvanceShopping) {
			throw new IstfException("Unknown mapping for isAdvanceShopping = true");
		}
		return "No Discount";
	}

	String getGeneralTabPriorBILimit(String priorBILimit) {
		switch (priorBILimit) {
			case "N":
				return "None";
			case "FR":
				return getRandom(getRangedDollarValue(15_000, 30_000), getRangedDollarValue(20_000, 40_000), getRangedDollarValue(25_000, 50_000), getRangedDollarValue(30_000, 60_000));
			case "50/XX":
				return getRangedDollarValue(50_000, 100_000);
			case "100/XX":
				return getRangedDollarValue(100_000, 300_000);
			case "200/XX":
				return getRandom(getRangedDollarValue(250_000, 500_000), getRangedDollarValue(300_000, 500_000));
			case "500/XX":
				return getRandom(getRangedDollarValue(500_000, 500_000), getRangedDollarValue(500_000, 1_000_000), getRangedDollarValue(1_000_000, 1_000_000));
			default:
				throw new IstfException("Unknown mapping for priorBILimit = " + priorBILimit);
		}
	}

}
