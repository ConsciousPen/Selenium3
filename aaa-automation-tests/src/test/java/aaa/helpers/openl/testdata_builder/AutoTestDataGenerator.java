package aaa.helpers.openl.testdata_builder;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

abstract class AutoTestDataGenerator<P extends OpenLPolicy> extends TestDataGenerator<P> {

	public AutoTestDataGenerator(String state, TestData ratingDataPattern) {
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
			case "J":
				return "Domestic Partner"; // Auto CA Choice
			case "M":
				return getRandom("Married", "regex=.*Domestic Partner");//, "Common Law", "Civil Union");
			case "S":
				return getRandom("Single");
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

	boolean isPrivatePassengerAutoType(String statCode) {
		List<String> codes = Arrays.asList("AP", "AH", "AU", "AW", "AV", "AN", "AI", "AQ", "AY", "AD", "AJ", "AC", "AK", "AE", "AR", "AO", "AX", "AZ");
		return codes.contains(statCode);
	}

	boolean isConversionVanType(String statCode) {
		return "AW".equals(statCode) || "AV".equals(statCode);
	}

	boolean isMotorHomeType(String statCode) {
		return "MA".equals(statCode) || "MB".equals(statCode) || "MC".equals(statCode);
	}

	boolean isTrailerType(String statCode) {
		List<String> codes = Arrays.asList("RQ", "RT", "FW", "UT", "PC", "HT", "PT");
		return codes.contains(statCode);
	}

	boolean isTrailerOrMotorHomeType(String usage) {
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

	protected String getVehicleTabType(String statCode) {
		if (isPrivatePassengerAutoType(statCode)) {
			return "Private Passenger Auto";
		}
		if (isConversionVanType(statCode)) {
			return "Conversion Van";
		}
		if (isMotorHomeType(statCode)) {
			return "Motor Home";
		}
		if (isTrailerType(statCode)) {
			return "Trailer";
		}
		throw new IstfException("Unknown vehicle type for statCode: " + statCode);
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

	String getVehicleTabStatCode(String statCode) {
		Map<String, String> statCodesMap = new HashMap<>();

		// Private Passenger Auto stat codes
		statCodesMap.put("AN", "Small car");
		statCodesMap.put("AI", "Midsize car");
		statCodesMap.put("AQ", "Large car");
		statCodesMap.put("AC", "Small SUV");
		statCodesMap.put("AK", "Midsize SUV");
		statCodesMap.put("AE", "Large SUV");
		statCodesMap.put("AX", "Passenger Van");
		statCodesMap.put("AZ", "Crossover/Station Wagon");
		statCodesMap.put("AR", "Small pickup or Utility Truck");
		statCodesMap.put("AO", "Standard pickup or Utility Truck");
		statCodesMap.put("AY", "Small High Exposure Vehicle");
		statCodesMap.put("AD", "Midsize High Exposure Vehicle");
		statCodesMap.put("AJ", "Large High Exposure Vehicle");

		// Conversion Van stat codes
		statCodesMap.put("AW", "Cargo Van");
		statCodesMap.put("AV", "Custom Van");

		// Trailer stat codes
		statCodesMap.put("FW", "Fifth-Wheel Trailer");
		statCodesMap.put("RQ", "Recreational/Cargo Quarter");
		statCodesMap.put("RT", "Recreational Trailer");

		assertThat(statCodesMap).as("Unknown UI \"Stat Code\" combo box value for openl statCode %s", statCode).containsKey(statCode);
		return statCodesMap.get(statCode);
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
		Map<String, String> coveragesMap = new HashMap<>();

		coveragesMap.put("BI", AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel());
		coveragesMap.put("PD", AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY.getLabel());
		switch (getState()) {
			case Constants.States.OR:
			case Constants.States.CO:
			case Constants.States.CT:
			case Constants.States.IN:
			case Constants.States.OK:
			case Constants.States.WY:
				coveragesMap.put("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel());
				break;
			case Constants.States.MT:
				coveragesMap.put("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_BODILY_INJURY.getLabel());
				break;
			default:
				coveragesMap.put("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY.getLabel());
				break;
		}

		if (getState().equals(Constants.States.PA)) {
			coveragesMap.put("MP", AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_EXPENSES.getLabel());
		} else {
			coveragesMap.put("MP", AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_PAYMENTS.getLabel());
		}

		coveragesMap.put("SP EQUIP", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.SPECIAL_EQUIPMENT_COVERAGE.getLabel());
		coveragesMap.put("COMP", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COMPREGENSIVE_DEDUCTIBLE.getLabel());
		coveragesMap.put("COLL", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COLLISION_DEDUCTIBLE.getLabel());
		if (getState().equals(Constants.States.MT)) {
			coveragesMap.put("UIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_BODILY_INJURY.getLabel());
		} else {
			coveragesMap.put("UIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel());
		}
		coveragesMap.put("UMPD", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel());
		coveragesMap.put("UIMPD", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel());
		coveragesMap.put("PIP", AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION.getLabel());
		coveragesMap.put("ADBC", AutoSSMetaData.PremiumAndCoveragesTab.ACCIDENTAL_DEATH_BENEFITS.getLabel());
		coveragesMap.put("IL", AutoSSMetaData.PremiumAndCoveragesTab.INCOME_LOSS_BENEFIT.getLabel());
		coveragesMap.put("FUNERAL", AutoSSMetaData.PremiumAndCoveragesTab.FUNERAL_BENEFITS.getLabel());
		coveragesMap.put("EMB", AutoSSMetaData.PremiumAndCoveragesTab.EXTRAORDINARY_MEDICAL_EXPENSE_BENEFITS.getLabel());
		coveragesMap.put("UM/SUM", AutoSSMetaData.PremiumAndCoveragesTab.SUPPLEMENTARY_UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel());
		coveragesMap.put("OBEL",  AutoSSMetaData.PremiumAndCoveragesTab.OPTIONAL_BASIC_ECONOMIC_LOSS.getLabel());
		coveragesMap.put("APIP",  AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_PIP.getLabel());
		coveragesMap.put("TOWING",  AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel());
		coveragesMap.put("RENTAL",  AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT.getLabel());

		//AutoCa Choice
		coveragesMap.put("UM", AutoCaMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY.getLabel());

		assertThat(coveragesMap).as("Unknown mapping for coverageCD: " + coverageCD).containsKey(coverageCD);
		return coveragesMap.get(coverageCD);
	}

	boolean isPolicyLevelCoverage(String coverageCD) {
		List<String> policyLevelCoverage = Arrays.asList("BI", "PD", "UMBI", "UIMBI", "MP", "PIP", "ADBC", "IL", "FUNERAL", "EMB", "UIMPD", "UM/SUM", "APIP", "OBEL");
		if (!getState().equals(Constants.States.OR)) {
			policyLevelCoverage = new ArrayList<>(policyLevelCoverage);
			policyLevelCoverage.add("UMPD");
		}
		return policyLevelCoverage.contains(coverageCD);
	}

	boolean isFirstPartyBenefitsComboCoverage(String coverageCD) {
		return Arrays.asList("ADBC", "IL", "FUNERAL").contains(coverageCD);
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
				return getTerm(term);
			case "Z":
				return getRandom("Eleven Pay - Zero Down", "Monthly - Zero Down");
			default:
				throw new IstfException("Unknown mapping for paymentPlanType: " + paymentPlanType);
		}
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
		LocalDateTime expirationDate = duration == 0 ? policyEffectiveDate : policyEffectiveDate.plusDays(new Random().nextInt(duration));

		return DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel(), inceptionDate.format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel(), expirationDate.format(DateTimeUtils.MM_DD_YYYY));
	}

	String getTerm(int term) {
		switch (term) {
			case 12:
				return "Annual";
			case 6:
				return "regex=Semi-[aA]nnual.*";
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
				return getRandom(getRangedDollarValue(15_000, 30_000), getRangedDollarValue(20_000, 40_000), getRangedDollarValue(25_000, 50_000), getRangedDollarValue(50_000, 100_000));
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
		if ("Y".equals(coverageLimit)) {
			return AdvancedComboBox.RANDOM_EXCEPT_CONTAINS_MARK + "=No Coverage";
		}
		if ("N".equals(coverageLimit)) {
			return "starts=No Coverage";
		}
		Dollar cLimit = new Dollar(coverageLimit.replace("Y", ""));
		if (isPolicyLevelCoverage(coverageCD) && !isFirstPartyBenefitsComboCoverage(coverageCD)) {
			cLimit = cLimit.multiply(1000);
		}
		return cLimit.toString().replaceAll("\\.00", "");
	}

	String getPremiumAndCoveragesTabLimitOrDeductible(AutoOpenLCoverage coverage) {
		String coverageCd = coverage.getCoverageCd();
		if ("SP EQUIP".equals(coverageCd)) {
			return new Dollar(coverage.getLimit()).toString();
		}

		String limitOrDeductible;
		if ("COMP".equals(coverageCd) || "COLL".equals(coverageCd) || getState().equals(Constants.States.NY) && "PIP".equals(coverageCd)) {
			limitOrDeductible = coverage.getDeductible();
		} else {
			limitOrDeductible = coverage.getLimit();
		}

		String[] limitRange = limitOrDeductible.split("/");
		assertThat(limitRange.length).as("Unknown mapping for limit/deductible: %s", limitOrDeductible).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(2);

		if ("EMB".equals(coverageCd)) {
			return "1000000".equals(limitRange[0]) ? "starts=Yes" : "starts=No";
		}

		//for AutoCA Choice
		if ("RENTAL".equals(coverageCd) || "TOWING".equals(coverageCd)) {
			return "1".equals(limitRange[0]) ? "starts=Yes" : "starts=No Coverage";
		}

		StringBuilder returnLimit = new StringBuilder();
		String formattedLimit = getFormattedCoverageLimit(limitRange[0], coverageCd);
		if (!formattedLimit.startsWith(AdvancedComboBox.RANDOM_MARK) && !formattedLimit.startsWith("starts=")) {
			returnLimit.append("starts=");
		}
		returnLimit.append(formattedLimit);
		if (limitRange.length == 2) {
			returnLimit.append("/");
			if ("IL".equals(coverageCd)) {
				returnLimit.append("month (").append(getFormattedCoverageLimit(limitRange[1], coverageCd)).append(" max)");
			} else {
				returnLimit.append(getFormattedCoverageLimit(limitRange[1], coverageCd));
			}
		}
		return returnLimit.toString();
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
