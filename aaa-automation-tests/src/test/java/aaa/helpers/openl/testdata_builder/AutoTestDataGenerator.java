package aaa.helpers.openl.testdata_builder;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.openl.model.OpenLCoverage;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
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

	boolean isTrailer(String statCode) {
		List<String> trailerStatCodes = Arrays.asList("RQ", "RT", "FW", "UT", "PC", "HT", "PT");
		return trailerStatCodes.contains(statCode);
	}

	boolean isTrailerOrMotorHome(String usage) {
		return Arrays.asList("P1", "P2", "P3", "PT", "PR").contains(usage);
	}

	String getVehicleTabTrailerType(String statCode) {
		switch (statCode) {
			case "FW":
			case "RT":
			case "RQ":
				return "Travel Trailer";
			case "UT":
				return "Utility Trailer";
			case "PC":
				return "Pickup Camper";
			case "HT":
				return "Horse Trailer";
			case "PT":
				return "Pop-up Tent";
			default:
				throw new IstfException("Unknown trailer type for statCode: " + statCode);
		}
	}

	String getVehicleTabMotorHomeType(String statCode) {
		switch (statCode) {
			case "MA":
				return "Conventional Motor Home (Class A)";
			case "MB":
				return "Mini Motor Home (Class C)";
			case "MC":
				return "Camper Van (Class B)";
			default:
				throw new IstfException("Unknown motor home type for statCode: " + statCode);
		}
	}

	String getVehicleTabAntiTheft(String antiTheft) {
		return "N".equals(antiTheft) ? "None" : "Vehicle Recovery Device";
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

	String getPremiumAndCoveragesTabCoverageName(String coverageCD) {
		switch (coverageCD) {
			case "BI":
				return AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel();
			case "PD":
				return AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY.getLabel();
			case "UMBI":
				return AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel();
			case "SP EQUIP":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.SPECIAL_EQUIPMENT_COVERAGE.getLabel();
			case "COMP":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COMPREGENSIVE_DEDUCTIBLE.getLabel();
			case "COLL":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COLLISION_DEDUCTIBLE.getLabel();
			case "UMPD":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.UNINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel();
			case "UIMBI":
				//TODO-dchubkov: replace with correct coverage name key
				return "UNKNOWN COVERAGE (AZ)";
			case "UIMPD":
				//TODO-dchubkov: replace with correct coverage name key
				return "UNKNOWN COVERAGE (DC)";
			case "UM/SUM":
				//TODO-dchubkov: replace with correct coverage name key
				return "UNKNOWN COVERAGE (NY)";
			case "MP":
				return AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_PAYMENTS.getLabel();
			case "PIP":
				return AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION.getLabel();
			default:
				throw new IstfException("Unknown mapping for coverageCD: " + coverageCD);
		}
	}

	boolean isPolicyLevelCoverage(String coverageCD) {
		return Arrays.asList("BI", "PD", "UMBI", "MP", "PIP").contains(coverageCD);
	}

	TestData getVehicleTabVehicleDetailsData(String safetyScore) {
		return DataProviderFactory.dataOf(
				AutoSSMetaData.VehicleTab.ENROLL_IN_USAGE_BASED_INSURANCE.getLabel(), "Yes",
				AutoSSMetaData.VehicleTab.GET_VEHICLE_DETAILS.getLabel(), "click",
				AutoSSMetaData.VehicleTab.SAFETY_SCORE.getLabel(), safetyScore);
	}

	String getPremiumAndCoveragesPaymentPlan(String paymentPlanType, int term) {
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
				return getGeneralTabTerm(term);
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
		assertThat(limitRange.length).as("Unknown mapping for limit/deductible: %s", limitOrDeductible).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(2);

		String returnLimit = "contains=" + getFormattedCoverageLimit(limitRange[0], coverage.getCoverageCD());
		if (limitRange.length == 2) {
			returnLimit += "/" + getFormattedCoverageLimit(limitRange[1], coverage.getCoverageCD());
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
		assertThat(autoInsurancePersistency).as("\"autoInsurancePersistency\" openL field should be equal or greater than \"aaaInsurancePersistency\"")
				.isGreaterThanOrEqualTo(aaaInsurancePersistency);

		LocalDateTime inceptionDate = autoInsurancePersistency.equals(aaaInsurancePersistency)
				? policyEffectiveDate : policyEffectiveDate.minusYears(autoInsurancePersistency - aaaInsurancePersistency);

		int duration = Math.abs(Math.toIntExact(Duration.between(policyEffectiveDate, TimeSetterUtil.getInstance().getCurrentTime()).toDays()));
		LocalDateTime expirationDate = policyEffectiveDate.plusDays(new Random().nextInt(duration));

		return DataProviderFactory.dataOf(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel(), inceptionDate.format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel(), expirationDate.format(DateTimeUtils.MM_DD_YYYY));
	}

	String getGeneralTabTerm(int term) {
		switch (term) {
			case 12:
				return "Annual";
			case 6:
				return "regex=Semi-[aA]nnual";
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

	String getFormattedCoverageLimit(String coverageLimit, String coverageCD) {
		Dollar cLimit = new Dollar(coverageLimit);
		if (isPolicyLevelCoverage(coverageCD)) {
			cLimit = cLimit.multiply(1000);
		}
		return cLimit.toString().replaceAll("\\.00", "");
	}

	String getDbRestraintsCode(String openlAirbagCode) {
		switch (openlAirbagCode) {
			case "N":
				return null;
			case "0":
				return "'0002' OR RESTRAINTSCODE = 'AUTOSB'";
			case "1":
				return "'000B' OR RESTRAINTSCODE = '000D' OR RESTRAINTSCODE = '000M' OR RESTRAINTSCODE = '0001'";
			case "2":
				return "'0002' OR RESTRAINTSCODE = '000C' OR RESTRAINTSCODE = '000E' OR RESTRAINTSCODE = '000F' OR RESTRAINTSCODE = '000L' OR RESTRAINTSCODE = '000U'";
			case "3":
				return "'0004' OR RESTRAINTSCODE = '000G' OR RESTRAINTSCODE = '000J' OR RESTRAINTSCODE = '000K' OR RESTRAINTSCODE = '000X' OR RESTRAINTSCODE = '0003' "
						+ "OR RESTRAINTSCODE = '000R' OR RESTRAINTSCODE = '000S'";
			case "4":
				return "'0004' OR RESTRAINTSCODE = '000H' OR RESTRAINTSCODE = '000I' OR RESTRAINTSCODE = '000Y' OR RESTRAINTSCODE = '000V' OR RESTRAINTSCODE = '000W' "
						+ "OR RESTRAINTSCODE = '0007' OR RESTRAINTSCODE = '0006' OR RESTRAINTSCODE = '000T'";
			default:
				throw new IstfException("Unknown mapping for airbagCode: " + openlAirbagCode);
		}
	}

	String getDbAntitheftCode(String openlAntiTheftString) {
		return "N".equals(openlAntiTheftString) ? "'NONE'" : "'STD'";
	}

	String covertToValidVin(String vin) {
		if (StringUtils.isBlank(vin)) {
			return null;
		}
		return vin.replaceAll("&", RandomStringUtils.randomNumeric(1)) + RandomStringUtils.randomNumeric(7);
	}
}
