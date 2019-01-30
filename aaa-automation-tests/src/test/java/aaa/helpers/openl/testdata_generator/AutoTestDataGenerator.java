package aaa.helpers.openl.testdata_generator;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
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
	protected static int maxIncidentFreeInMonthsToAffectRating = 33;

	AutoTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	List<String> getPolicyLevelCoverageCDs() {
		List<String> policyLevelCoverage = Arrays.asList("BI", "RBI", "PD", "UMBI", "UIMBI", "EUIMBI", "MP", "PIP", "ADBC", "IL", "FUNERAL", "EMB", "UIMPD", "EUIMPD", "UM/SUM", "APIP", "OBEL");
		if (!getState().equals(Constants.States.OR)) {
			policyLevelCoverage = new ArrayList<>(policyLevelCoverage);
			policyLevelCoverage.add("UMPD");
		}
		return policyLevelCoverage;
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
		if ("GC".equals(statCode)) {
			return "Golf Cart";
		}
		throw new IstfException("Unknown vehicle type for statCode: " + statCode);
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
				return "Married"; // also possible "regex=.*Domestic Partner", "Common Law", "Civil Union"
			case "S":
				return "Single";
			case "W":
				return "Widowed";
			default:
				throw new IstfException("Unknown mapping for martialStatus or not acceptable by rating engine: " + martialStatus);
		}
	}

	public static String getDriverTabDateOfBirth(Integer driverAge, LocalDate policyEffectiveDate) {
		LocalDate dateOfBirth = policyEffectiveDate.minusYears(driverAge);
		// If driver's age is 24 and his birthday is within 30 days of the policy effective date, then driver's age is mapped as 25
		if (driverAge == 24 && dateOfBirth.isAfter(policyEffectiveDate) && dateOfBirth.isBefore(policyEffectiveDate.plusDays(30))) {
			dateOfBirth = dateOfBirth.plusYears(1);
		}
		return dateOfBirth.format(DateTimeUtils.MM_DD_YYYY);
	}

	String getDriverTabLicenseType(boolean isForeignLicense) {
		if (isForeignLicense) {
			return "Foreign";
		}
		return AdvancedComboBox.RANDOM_EXCEPT_MARK + "=Foreign|Not Licensed|Learner's Permit|";
	}

	boolean isPrivatePassengerAutoType(String statCode) {
		if (statCode.matches("^BI\\d{3}$")) {
			return true;
		}
		List<String> codes = new ArrayList<>(Arrays.asList("AA", "AP", "AH", "AU", "AV", "AN", "AI", "AQ", "AY", "AD", "AJ", "AC", "AK", "AE", "AR", "AO", "AX", "AZ"));
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

	boolean isTrailerOrMotorHomeOrGolfCartType(String usage) {
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

	String getVehicleTabStatCode(String statCode) {
		Map<String, String> statCodesMap = new HashMap<>();

		// Private Passenger Auto stat codes
		statCodesMap.put("AA", "Antique"); //not used
		statCodesMap.put("AN", "Small car");
		statCodesMap.put("AI", "Midsize car");
		statCodesMap.put("AQ", "Large car");
		statCodesMap.put("AC", "Small SUV");
		statCodesMap.put("AK", "Midsize SUV");
		statCodesMap.put("AE", "Large SUV");
		statCodesMap.put("AX", "Passenger Van");
		statCodesMap.put("AZ", "Crossover/Station Wagon");
		statCodesMap.put("AR", "Small pickup or Utility Truck");
		statCodesMap.put("AP", "Passenger Vehicle");
		statCodesMap.put("AU", "Trucks");
		statCodesMap.put("AO", "Standard pickup or Utility Truck");
		statCodesMap.put("AH", "High exposure Vehicle");
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
		//		return "N".equals(antiTheft) ? "None" : "Vehicle Recovery Device";
		switch (antiTheft) {
			case "N":
				return "None";
			case "P":
				return "VIN Etching";
			case "Y":
				return "Homing Device (Recovery Device)";
			default:
				throw new IstfException("Unknown mapping for antiTheft: " + antiTheft);
		}
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
		coveragesMap.put("BI-Verbal", AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel());
		coveragesMap.put("PD", AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY.getLabel());
		switch (getState()) {
			case Constants.States.OR:
			case Constants.States.CO:
			case Constants.States.CT:
			case Constants.States.IN:
			case Constants.States.OK:
			case Constants.States.WY:
			case Constants.States.MD:
			case Constants.States.DE:
			case Constants.States.NJ:
			case Constants.States.OH:
			case Constants.States.VA:
			case Constants.States.KS:
				coveragesMap.put("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel());
				coveragesMap.put("UMBI-Verbal", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel());
				break;
			case Constants.States.NV:
				coveragesMap.put("UMBI", "Uninsured and Underinsured Motorist Bodily Injury");
				break;
			case Constants.States.MT:
			case Constants.States.WV:
				coveragesMap.put("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_BODILY_INJURY.getLabel());
				coveragesMap.put("UMBI-Verbal", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_BODILY_INJURY.getLabel());
				break;
			default:
				coveragesMap.put("UMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY.getLabel());
				coveragesMap.put("UMBI-Verbal", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY.getLabel());
				break;
		}

		switch (getState()) {
			case Constants.States.PA:
				coveragesMap.put("MP", AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_BENEFIT.getLabel());
				break;
			case Constants.States.VA:
				coveragesMap.put("MP", AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_EXPENSE.getLabel());
				break;
			default:
				coveragesMap.put("MP", AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_PAYMENTS.getLabel());
				break;
		}

		switch (getState()) {
			case Constants.States.MD:
				coveragesMap.put("SP EQUIP", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.SPECIAL_EQUIPMENT.getLabel());
				break;
			case Constants.States.VA:
				coveragesMap.put("SP EQUIP", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.EXCESS_ELECTRONIC_EQUIPMENT.getLabel());
				break;
			default:
				coveragesMap.put("SP EQUIP", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.SPECIAL_EQUIPMENT_COVERAGE.getLabel());
				break;
		}

		if (getState().equals(Constants.States.VA)) {
			coveragesMap.put("COMP", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.OTHER_THAN_COLLISION.getLabel());
		} else {
			coveragesMap.put("COMP", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COMPREGENSIVE_DEDUCTIBLE.getLabel());
		}
		coveragesMap.put("COLL", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COLLISION_DEDUCTIBLE.getLabel());

		switch (getState()) {
			case Constants.States.MT:
			case Constants.States.WV:
				coveragesMap.put("UIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_BODILY_INJURY.getLabel());
				coveragesMap.put("EUIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_BODILY_INJURY.getLabel());
				break;
			case Constants.States.MD:
				coveragesMap.put("EUIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel());
				break;
			default:
				coveragesMap.put("UIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel());
				coveragesMap.put("EUIMBI", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel());
				break;
		}

		if (getState().equals(Constants.States.MD)) {
			coveragesMap.put("EUIMPD", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel());
		} else {
			coveragesMap.put("EUIMPD", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel());
		}

		switch (getState()) {
			case Constants.States.NJ:
				coveragesMap.put("PIP", AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.MEDICAL_EXPENSE.getLabel());
				break;
			case Constants.States.KY:
				coveragesMap.put("PIP", AutoSSMetaData.PremiumAndCoveragesTab.BASIC_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel());
				break;
			default:
				coveragesMap.put("PIP", AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION.getLabel());
				break;
		}

		if (Constants.States.KY.equals(getState())) {
			coveragesMap.put("APIP", AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel());
		} else {
			coveragesMap.put("APIP", AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_PIP.getLabel());
		}

		coveragesMap.put("UMPD", AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel());
		coveragesMap.put("UIMPD", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel());

		if (Constants.States.PA.equals(getState())) {
			coveragesMap.put("ADBC", AutoSSMetaData.PremiumAndCoveragesTab.ACCIDENTAL_DEATH_BENEFIT.getLabel());
		} else {
			coveragesMap.put("ADBC", AutoSSMetaData.PremiumAndCoveragesTab.ACCIDENTAL_DEATH_BENEFITS.getLabel());
		}
		if (Constants.States.PA.equals(getState())) {
			coveragesMap.put("IL", AutoSSMetaData.PremiumAndCoveragesTab.WORK_LOSS_BENEFIT.getLabel());
		} else {
			coveragesMap.put("IL", AutoSSMetaData.PremiumAndCoveragesTab.INCOME_LOSS_BENEFIT.getLabel());
		}
		if (Constants.States.PA.equals(getState())) {
			coveragesMap.put("FUNERAL", AutoSSMetaData.PremiumAndCoveragesTab.FUNERAL_EXPENSE_BENEFIT.getLabel());
		} else {
			coveragesMap.put("FUNERAL", AutoSSMetaData.PremiumAndCoveragesTab.FUNERAL_BENEFITS.getLabel());
		}

		coveragesMap.put("EMB", AutoSSMetaData.PremiumAndCoveragesTab.EXTRAORDINARY_MEDICAL_EXPENSE_BENEFITS.getLabel());
		coveragesMap.put("UM/SUM", AutoSSMetaData.PremiumAndCoveragesTab.SUPPLEMENTARY_UNINSURED_UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel());
		coveragesMap.put("OBEL", AutoSSMetaData.PremiumAndCoveragesTab.OPTIONAL_BASIC_ECONOMIC_LOSS.getLabel());
		coveragesMap.put("TOWING", AutoSSMetaData.PremiumAndCoveragesTab.TOWING_AND_LABOR_COVERAGE.getLabel());
		coveragesMap.put("RENTAL", AutoSSMetaData.PremiumAndCoveragesTab.RENTAL_REIMBURSEMENT.getLabel());

		if (getState().equals(Constants.States.VA)) {
			coveragesMap.put("LOAN", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.AUTO_LOAN_LEASE_COVERAGE.getLabel());
		} else {
			coveragesMap.put("LOAN", AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.VEHICLE_LOAN_OR_LEASE_PROTECTION.getLabel());
		}

		coveragesMap.put("RBI", AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel());
		coveragesMap.put("PIP WORK LOSS", AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION.getLabel());  // TODO-dchubkov: change to correct coverage!

		//AutoCa Choice
		coveragesMap.put("UM", AutoCaMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORISTS_BODILY_INJURY.getLabel());
		coveragesMap.put("CDW", AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COLLISION_DEDUCTIBLE_WAIVER.getLabel());

		//AutoCa Select
		coveragesMap.put("ETEC", AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.ENHANCED_TRASPORTATION_EXPENCE.getLabel());
		coveragesMap.put("MAINT", AutoCaMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.ALL_RISK.getLabel()); // rating name is ALLRISK
		coveragesMap.put("UIM", AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel()); // rating name is UIMBI

		assertThat(coveragesMap).as("Unknown mapping for coverageCD: " + coverageCD).containsKey(coverageCD);
		return coveragesMap.get(coverageCD);
	}

	boolean isPolicyLevelCoverageCd(String coverageCd) {
		return getPolicyLevelCoverageCDs().contains(coverageCd);
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
		StringBuilder paymentPlan = new StringBuilder("regex=^");
		String planName = term == 12 ? "Eleven Pay" : "Five Pay";
		switch (paymentPlanType) {
			case "A":
				paymentPlan.append("Quarterly");
				break;
			case "B":
				paymentPlan.append(planName).append(" - Standard");
				break;
			case "C":
				paymentPlan.append("Semi-[aA]nnual");
				break;
			case "L":
				paymentPlan.append(getRandom(planName + " - Low Down", "Monthly - Low Down"));
				break;
			case "P":
				return getPremiumAndCoveragesPaymentPlan(term);
			case "Z":
				paymentPlan.append(getRandom(planName + " - Zero Down", "Monthly - Zero Down"));
				break;
			default:
				throw new IstfException("Unknown mapping for paymentPlanType: " + paymentPlanType);
		}
		paymentPlan.append("(\\s*\\(Renewal\\))?$");
		return paymentPlan.toString();
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

	Map<String, Object> getGeneralTabAgentInceptionAndExpirationData(Integer autoInsurancePersistency, Integer aaaInsurancePersistency, LocalDate policyEffectiveDate) {
		assertThat(autoInsurancePersistency).as("\"autoInsurancePersistency\" openL field should be equal or greater than \"aaaInsurancePersistency\"")
				.isGreaterThanOrEqualTo(aaaInsurancePersistency);

		LocalDate inceptionDate = autoInsurancePersistency.equals(aaaInsurancePersistency)
				? policyEffectiveDate : policyEffectiveDate.minusYears(autoInsurancePersistency - aaaInsurancePersistency);

		int duration = Math.abs(Math.toIntExact(ChronoUnit.DAYS.between(policyEffectiveDate, TimeSetterUtil.getInstance().getCurrentTime().toLocalDate())));
		LocalDate expirationDate = duration == 0 ? policyEffectiveDate : policyEffectiveDate.plusDays(new Random().nextInt(duration));

		Map<String, Object> generalTabAgentInceptionAndExpirationData = new HashMap<>();
		generalTabAgentInceptionAndExpirationData.put(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel(), inceptionDate.format(DateTimeUtils.MM_DD_YYYY));
		generalTabAgentInceptionAndExpirationData.put(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel(), expirationDate.format(DateTimeUtils.MM_DD_YYYY));
		if (ChronoUnit.MONTHS.between(inceptionDate, policyEffectiveDate) <= 6) {
			generalTabAgentInceptionAndExpirationData.put(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.MORE_THAN_6_MONTHS_TOTAL_INSURANCE_EXPERIENCE.getLabel(), "Yes");
		}
		return generalTabAgentInceptionAndExpirationData;
	}

	String getGeneralTabPriorBILimit(String priorBILimit) {
		switch (priorBILimit) {
			case "N":
				return "None";
			case "FR":
				return getRandom(getRangedDollarValue(15_000, 30_000), getRangedDollarValue(20_000, 40_000), getRangedDollarValue(25_000, 50_000));
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

	protected <C extends AutoOpenLCoverage> String getPremiumAndCoveragesTabLimitOrDeductible(C coverage) {
		String coverageCd = coverage.getCoverageCd();
		if ("SP EQUIP".equals(coverageCd)) {
			return new Dollar(coverage.getLimit()).toString();
		}
		String[] limitRange = getLimitOrDeductibleRange(coverage);

		if ("EMB".equals(coverageCd)) {
			return "1000000".equals(limitRange[0]) ? "starts=Yes" : "starts=No";
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

	protected String[] getLimitOrDeductibleRange(AutoOpenLCoverage coverage) {
		String coverageCd = coverage.getCoverageCd();
		String limitOrDeductible;
		if ("COMP".equals(coverageCd) || "COLL".equals(coverageCd) || "MAINT".equals(coverageCd) || getState().equals(Constants.States.NY) && "PIP".equals(coverageCd)) {
			limitOrDeductible = coverage.getDeductible();
		} else {
			limitOrDeductible = coverage.getLimit();
		}

		String[] limitRange = limitOrDeductible.split("/");
		assertThat(limitRange.length).as("Unknown mapping for limit/deductible: %s", limitOrDeductible).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(2);
		return limitRange;
	}

	String getFormattedCoverageLimit(String coverageLimit, String coverageCD) {
		if ("Y".equals(coverageLimit)) {
			return AdvancedComboBox.RANDOM_EXCEPT_CONTAINS_MARK + "=No Coverage";
		}
		if ("N".equals(coverageLimit)) {
			return "starts=No Coverage";
		}
		Dollar cLimit = new Dollar(coverageLimit.replace("Y", "").replace("F", ""));
		if (isPolicyLevelCoverageCd(coverageCD) && !isFirstPartyBenefitsComboCoverage(coverageCD)) {
			cLimit = cLimit.multiply(1000);
		}

		String formattedCoverageLimit = cLimit.toString().replaceAll("\\.00", "");
		if (getState().equals(Constants.States.MD) && "PIP".equals(coverageCD)) {
			if (coverageLimit.endsWith("F")) {
				formattedCoverageLimit = formattedCoverageLimit + " Full";
			}
			if (coverageLimit.endsWith("G")) {
				formattedCoverageLimit = formattedCoverageLimit + " Guest";
			}
		}
		if (getState().equals(Constants.States.NY) && "APIP".equals(coverageCD)) {
			formattedCoverageLimit = formattedCoverageLimit.replaceAll(",", "");
		}
		return formattedCoverageLimit;
	}

	String getDbRestraintsCode(String openlAirbagCode) {
		switch (openlAirbagCode) {
			case "N":
				return "is null OR RESTRAINTSCODE = '000A'";
			case "0":
				return "= '0002' OR RESTRAINTSCODE = 'AUTOSB'";
			case "1":
				return "= '000B' OR RESTRAINTSCODE = '000D' OR RESTRAINTSCODE = '000M' OR RESTRAINTSCODE = '0001'";
			case "2":
				return "= '0002' OR RESTRAINTSCODE = '000C' OR RESTRAINTSCODE = '000E' OR RESTRAINTSCODE = '000F' OR RESTRAINTSCODE = '000L' OR RESTRAINTSCODE = '000U'";
			case "3":
				return "= '0004' OR RESTRAINTSCODE = '000G' OR RESTRAINTSCODE = '000J' OR RESTRAINTSCODE = '000K' OR RESTRAINTSCODE = '000X' OR RESTRAINTSCODE = '0003' "
						+ "OR RESTRAINTSCODE = '000R' OR RESTRAINTSCODE = '000S'";
			case "4":
				return "= '0004' OR RESTRAINTSCODE = '000H' OR RESTRAINTSCODE = '000I' OR RESTRAINTSCODE = '000Y' OR RESTRAINTSCODE = '000V' OR RESTRAINTSCODE = '000W' "
						+ "OR RESTRAINTSCODE = '0007' OR RESTRAINTSCODE = '0006' OR RESTRAINTSCODE = '000T'";
			default:
				throw new IstfException("Unknown mapping for airbagCode: " + openlAirbagCode);
		}
	}

	String covertToValidVin(String vin) {
		if (StringUtils.isBlank(vin)) {
			return null;
		}

		int vinLength = 17;
		Map<String, Integer> transliterationMap = new HashMap<>(24);
		transliterationMap.put("&", 0); // VIN"s check digit
		transliterationMap.put("A", 1);
		transliterationMap.put("B", 2);
		transliterationMap.put("C", 3);
		transliterationMap.put("D", 4);
		transliterationMap.put("E", 5);
		transliterationMap.put("F", 6);
		transliterationMap.put("G", 7);
		transliterationMap.put("H", 8);
		transliterationMap.put("J", 1);
		transliterationMap.put("K", 2);
		transliterationMap.put("L", 3);
		transliterationMap.put("M", 4);
		transliterationMap.put("N", 5);
		transliterationMap.put("P", 7);
		transliterationMap.put("R", 9);
		transliterationMap.put("S", 2);
		transliterationMap.put("T", 3);
		transliterationMap.put("U", 4);
		transliterationMap.put("V", 5);
		transliterationMap.put("W", 6);
		transliterationMap.put("X", 7);
		transliterationMap.put("Y", 8);
		transliterationMap.put("Z", 9);

		vin += RandomStringUtils.randomNumeric(7); // adding random plant digit and serial number
		assertThat(vin).as("Invalid VIN size").hasSize(vinLength);

		List<Integer> vinValues = new ArrayList<>(vinLength);
		List<Integer> vinWeights = Arrays.asList(8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2);
		for (String vinChar : vin.split("")) {
			if (transliterationMap.containsKey(vinChar)) {
				vinValues.add(transliterationMap.get(vinChar));
			} else {
				assertThat(NumberUtils.isCreatable(vinChar))
						.as("Unknown transliteration for VIN char '%1$s', only digits and these chars are allowed: %s", vinChar, transliterationMap.keySet()).isTrue();
				vinValues.add(Integer.valueOf(vinChar));
			}
		}
		int vinSum = IntStream.range(0, vinLength).map(i -> vinValues.get(i) * vinWeights.get(i)).sum();
		int checkDigit = vinSum % 11;
		return vin.replaceAll("&", checkDigit == 10 ? "X" : String.valueOf(checkDigit));
	}
}
