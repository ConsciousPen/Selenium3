package aaa.helpers.openl.testdata_builder;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.openl.model.OpenLCoverage;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

abstract class AutoTestDataGenerator<P extends OpenLPolicy> extends TestDataGenerator<P> {
	AutoTestDataGenerator(String state) {
		super(state);
	}

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
		switch (martialStatus) {
			case "M":
				return "Married";
			case "S":
				return "Single";
			case "D":
				return "Divorced";
			case "W":
				return "Widowed";
			case "P": //TODO-dchubkov: double check openl value
				return "Separated";
			case "R":
				return "Registered Domestic Partner";
			default:
				throw new IstfException("Unknown mapping for martialStatus: " + martialStatus);
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

	String getVehicleTabUsage(String usage) {
		switch (usage) {
			case "A":
				return "Artisan";
			case "B":
				return "Business";
			case "F":
				return "Farm";
			case "W1": // <15 miles to school or work
			case "W2": // 15+ miles to school or work
				return "Commute";
			//For Tailer, Golf Cart and Motor Home
			case "P":
			case "P1": // Occupied Less than 30 Days a Year
			case "P2": // Occupied 30-150 Days a Year
			case "P3": // Occupied More than 150 Days a Year
				return "Pleasure"; // or Nano in case of Nano policy
			case "PT":
				return "Traveling Primary Residence";
			case "PR":
				return "Non-Traveling Primary Residence";
			default:
				throw new IstfException("Unknown mapping for usage: " + usage);
		}
	}

	String getVehicleTabAntiTheft(String antiTheft) {
		if ("N".equals(antiTheft)) {
			return "None";
		}
		return "Vehicle Recovery Device";
	}

	String getVehicleTabAirBags(String airBagCode) {
		switch (airBagCode) {
			case "N":
				return "None";
			case "0004":
				return getRandom("Both Front and Side", "Both Front and Side with Rear Side");
			case "0002":
				return getRandom("None", "Both Front");
			case "000B":
			case "000D":
			case "000M":
			case "0001":
				return "Driver";
			case "000C":
			case "000E":
			case "000F":
			case "000L":
			case "000U":
			case "000G":
			case "000J":
			case "000K":
			case "000X":
			case "0003":
			case "000R":
			case "000S":
				return "Both Front";
			case "000H":
			case "000I":
			case "000Y":
			case "000V":
			case "000W":
			case "0007":
			case "0006":
			case "000T":
				return "Both Front and Side";
			case "AUTOSB": //TODO-dchubkov: need to double check
				return "";
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

	TestData getGeneralTabBaseData(Integer aaaInsurancePersistency, Integer aaaAsdInsurancePersistency, LocalDateTime policyEffectiveDate) {
		assertThat(aaaInsurancePersistency).isEqualTo(aaaAsdInsurancePersistency)
				.as("\"aaaInsurancePersistency\" openL field should be equal to \"aaaAsdInsurancePersistency\" since both are equally calculated");
		return DataProviderFactory.dataOf(AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel(), policyEffectiveDate.minusYears(aaaInsurancePersistency).format(DateTimeUtils.MM_DD_YYYY));
	}

	TestData getGeneralTabAgentInceptionAndExpirationData(Integer autoInsurancePersistency, Integer aaaInsurancePersistency, LocalDateTime policyEffectiveDate) {
		assertThat(autoInsurancePersistency).isGreaterThanOrEqualTo(aaaInsurancePersistency)
				.as("\"autoInsurancePersistency\" openL field should be equal or greater than \"aaaInsurancePersistency\"");

		LocalDateTime inceptionDate =
				autoInsurancePersistency.equals(aaaInsurancePersistency) ? policyEffectiveDate : policyEffectiveDate.minusYears(autoInsurancePersistency - aaaInsurancePersistency);
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
