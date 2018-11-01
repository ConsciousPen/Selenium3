package aaa.helpers.openl.testdata_generator;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.dp3.HomeCaDP3OpenLForm;
import aaa.helpers.openl.model.home_ca.dp3.HomeCaDP3OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class HomeCaDP3TestDataGenerator extends TestDataGenerator<HomeCaDP3OpenLPolicy> {
	public HomeCaDP3TestDataGenerator(String state) {
		super(state);
	}

	public HomeCaDP3TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeCaDP3OpenLPolicy openLPolicy) {
		TestData ratingDataPattern = getRatingDataPattern().resolveLinks();
		TestData maskedMembershipData = ratingDataPattern.getTestData(new ApplicantTab().getMetaKey()).mask(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
		TestData maskedReportsData = ratingDataPattern.getTestData(new ReportsTab().getMetaKey()).mask(HomeCaMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());
		if (Boolean.FALSE.equals(openLPolicy.getAaaMember())) {
			ratingDataPattern.adjust(new ApplicantTab().getMetaKey(), maskedMembershipData);
			ratingDataPattern.adjust(new ReportsTab().getMetaKey(), maskedReportsData);
		}
		
		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new ApplicantTab().getMetaKey(), getApplicantTabData(openLPolicy),
				new ReportsTab().getMetaKey(), getReportsTabData(openLPolicy),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabData(openLPolicy),
				new EndorsementTab().getMetaKey(), getEndorsementTabData(openLPolicy),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy));
		
		return TestDataHelper.merge(ratingDataPattern, td);
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
	
	private TestData getGeneralTabData(HomeCaDP3OpenLPolicy openLPolicy) {
		TestData policyInfo = DataProviderFactory.emptyData();
		TestData currentCarrier = DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.CurrentCarrier.CONTINUOUS_YEARS_WITH_HO_INSURANCE.getLabel(), openLPolicy.getYearsOfPriorInsurance(),
				HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsWithCsaa()).format(DateTimeUtils.MM_DD_YYYY));
		return DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), policyInfo,
				HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), currentCarrier);
	}
	
	private TestData getApplicantTabData(HomeCaDP3OpenLPolicy openLPolicy) {
		TestData namedInsured = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), 
					openLPolicy.getEffectiveDate().minusYears(openLPolicy.getAgeOfOldestInsured()).format(DateTimeUtils.MM_DD_YYYY));
		TestData aaaMembership = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.getAaaMember()));
		if (Boolean.TRUE.equals(openLPolicy.getAaaMember())) {
			aaaMembership.adjust(HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "4290023667710001");
		}
		TestData dwellingAddress = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getDwelling().getAddress().getZipCode());
		
		TestData otherActiveAAAPolicies = DataProviderFactory.emptyData();
		if (openLPolicy.getHasAutoPolicy() || openLPolicy.getHasCeaPolicy()) {
			otherActiveAAAPolicies = DataProviderFactory.dataOf(
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), "Yes",
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), "click",
					HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData());
			if (openLPolicy.getHasAutoPolicy()) {
				otherActiveAAAPolicies.adjust(
						HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "Auto",
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COMPANION_AUTO_PENDING_WITH_DISCOUNT.getLabel(), "No",
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), "345345345",
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_STATE.getLabel(), "CA",
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().minusYears(1).format(DateTimeUtils.MM_DD_YYYY),
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_BI_LIMIT.getLabel(), "index=2"));
			}
			if (openLPolicy.getHasCeaPolicy()) {
				otherActiveAAAPolicies.adjust(
						HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
								HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "CEA"));
			}
		}
		
		return DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), namedInsured,
				HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembership,
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddress, 
				HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel(), otherActiveAAAPolicies);
	}
	
	private TestData getReportsTabData(HomeCaDP3OpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
	
	private TestData getPropertyInfoTabData(HomeCaDP3OpenLPolicy openLPolicy) {
		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + openLPolicy.getDwelling().getNumOfFamilies());
		TestData ppcData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getDwelling().getPpcValue());

		Dollar coverageA = new Dollar(openLPolicy.getCovALimit());
		TestData propertyValueData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), coverageA,
				HomeCaMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST.getLabel(), coverageA.multiply(0.85),
				HomeCaMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), "Mortgagee requirements");
		
		TestData constructionData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), openLPolicy.getEffectiveDate().minusYears(openLPolicy.getDwelling().getAgeOfHome()).getYear(),
				HomeCaMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel(), openLPolicy.getDwelling().getRoofType(), 
				HomeCaMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel(), "contains=" + openLPolicy.getDwelling().getConstructionType().split(" ")[0], 
				HomeCaMetaData.PropertyInfoTab.Construction.MASONRY_VENEER.getLabel(), "Masonry Veneer".equals(openLPolicy.getDwelling().getConstructionType()) ? "Yes" : "No", 
				HomeCaMetaData.PropertyInfoTab.Construction.IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR.getLabel(), "Log Home".equals(openLPolicy.getDwelling().getConstructionType()) ? "Yes" : null);
		
		TestData rentalInformationData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.RentalInformation.YEAR_FIRST_RENTED.getLabel(), openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsOwned()).getYear(), 
				HomeCaMetaData.PropertyInfoTab.RentalInformation.PROPERTY_MANAGER.getLabel(), getPropertyManagerType(openLPolicy), 
				HomeCaMetaData.PropertyInfoTab.RentalInformation.ARE_THERE_ANY_ADDITIONAL_RENTAL_DWELLINGS.getLabel(), "No");
		
		TestData theftProtectiveDeviceData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.LOCAL_THEFT_ALARM.getLabel(), "Local".equals(openLPolicy.getDwelling().getBurglarAlarmType()), 
				HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.CENTRAL_THEFT_ALARM.getLabel(), "Central".equals(openLPolicy.getDwelling().getBurglarAlarmType()), 
				HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.GATED_COMMUNITY.getLabel(), openLPolicy.getDwelling().getGatedCommunity());
		
		TestData fireProtectiveDeviceData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.FireProtectiveDD.LOCAL_FIRE_ALARM.getLabel(), "Local".equals(openLPolicy.getDwelling().getFireAlarmType()), 
				HomeCaMetaData.PropertyInfoTab.FireProtectiveDD.CENTRAL_FIRE_ALARM.getLabel(), "Central".equals(openLPolicy.getDwelling().getFireAlarmType()), 
				HomeCaMetaData.PropertyInfoTab.FireProtectiveDD.FULL_RESIDENTIAL_SPRINKLERS.getLabel(), openLPolicy.getDwelling().getHasSprinklers());
		
		TestData homeRenovationData; 
		Integer yearsSinceRenovation = openLPolicy.getDwelling().getYearsSinceRenovation();
		if (yearsSinceRenovation.equals(0)) {
			homeRenovationData = DataProviderFactory.emptyData();
		}
		else {
			homeRenovationData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_RENOVATION.getLabel(), "100% Copper", 
					HomeCaMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_PERCENT_COMPLETE.getLabel(), "100", 
					HomeCaMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_MONTH_OF_COMPLECTION.getLabel(), "1", 
					HomeCaMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_YEAR_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusYears(yearsSinceRenovation).getYear());
		}
		
		TestData petsOrAnimalsData;
		Integer numberOfLivestock = openLPolicy.getDwelling().getNumOfLivestock();
		if (numberOfLivestock.equals(0)) {
			petsOrAnimalsData = DataProviderFactory.emptyData();
		}
		else {
			petsOrAnimalsData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.PetsOrAnimals.ARE_ANY_PETS_OR_ANIMALS_KEPT_ON_THE_PROPERTY.getLabel(), "Yes", 
					HomeCaMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_TYPE.getLabel(), "Livestock - Cow", 
					HomeCaMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_COUNT.getLabel(), numberOfLivestock.toString());
		}
		
		TestData stovesData = DataProviderFactory.emptyData();
		if (openLPolicy.getDwelling().getHasWoodStove()) {
			stovesData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_PROPERTY_HAVE_A_WOOD_BURNING_STOVE.getLabel(), "Yes", 
					HomeCaMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT.getLabel(), "No", 
					HomeCaMetaData.PropertyInfoTab.Stoves.WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR.getLabel(), "Yes", 
					HomeCaMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY.getLabel(), "Yes");
		}
		
		TestData recreationalEquipmentData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), getSwimmingPoolType(openLPolicy));
		
		List<TestData> claimHistoryData = getClaimsHistoryData(openLPolicy);
		
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcData,
				HomeCaMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData,
				HomeCaMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), constructionData,
				//HomeCaMetaData.PropertyInfoTab.INTERIOR.getLabel(), interiorData,
				HomeCaMetaData.PropertyInfoTab.RENTAL_INFORMATION.getLabel(), rentalInformationData,
				HomeCaMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), theftProtectiveDeviceData,
				HomeCaMetaData.PropertyInfoTab.FIRE_PROTECTIVE_DD.getLabel(), fireProtectiveDeviceData, 
				HomeCaMetaData.PropertyInfoTab.HOME_RENOVATION.getLabel(), homeRenovationData, 
				HomeCaMetaData.PropertyInfoTab.PETS_OR_ANIMALS.getLabel(), petsOrAnimalsData, 
				HomeCaMetaData.PropertyInfoTab.STOVES.getLabel(), stovesData, 
				HomeCaMetaData.PropertyInfoTab.RECREATIONAL_EQUIPMENT.getLabel(), recreationalEquipmentData,
				HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel(), claimHistoryData);
	}
	
	private String getPropertyManagerType(HomeCaDP3OpenLPolicy openLPolicy) {
		String propertyManagerType;
		switch (openLPolicy.getPropertyManagerType()) {
			case "Professional / full-time": 
				propertyManagerType = "Professional / Full-time";
				break;
			case "None": 
				propertyManagerType = "None";
				break;
			default: 
				throw new IstfException("Unknown mapping for PropertyManagerType = " + openLPolicy.getPropertyManagerType());				
		}
		return propertyManagerType;
	}
	
	private String getSwimmingPoolType(HomeCaDP3OpenLPolicy openLPolicy) {
		String swimmingPoolType; 
		if (StringUtils.isBlank(openLPolicy.getDwelling().getSwimmingPoolType())) {
			swimmingPoolType = "None";
		}
		else {
			switch (openLPolicy.getDwelling().getSwimmingPoolType()) {
				case "Fenced with no accessories": 
					swimmingPoolType = "Restricted access with no accessories";
					break;
				case "Fenced with slide and diving board": 
					swimmingPoolType = "Restricted access with slide and diving board"; 
					break;
				case "Fenced with slide only":
					swimmingPoolType = "Restricted access with slide only";
					break;
				case "Fenced with diving board only":
					swimmingPoolType = "Restricted access with diving board only";
					break;
				case "Not Fenced with no Accessories": 
					swimmingPoolType = "Unrestricted access";
					break;
				case "None": 
					swimmingPoolType = "None";
					break;
				default: 
					throw new IstfException("Unknown mapping for SwimmingPoolType = " + openLPolicy.getDwelling().getSwimmingPoolType());
			}
		}
		return swimmingPoolType;
	}
	
	private List<TestData> getClaimsHistoryData(HomeCaDP3OpenLPolicy openLPolicy) {
		List<TestData> claimsDataList = new ArrayList<>();
		TestData claim = DataProviderFactory.emptyData();

		double frequency = openLPolicy.getFrequencyOfDwellingLoss();
		Integer aaaClaimPoints = openLPolicy.getExpClaimPoints();
		Integer notAaaClaimPoints = openLPolicy.getClaimPoints();
		int totalPoints = aaaClaimPoints + notAaaClaimPoints;
		boolean isFirstClaim = true;

		if (totalPoints == 0 && frequency == 0.0) {
			claimsDataList.add(claim);
		}

		if (notAaaClaimPoints != 0) {
			for (int i = 0; i < notAaaClaimPoints; i++) {
				claim = addClaimData(openLPolicy, isFirstClaim, "Fire", 1, 3, "Open", getYesOrNo(false), getYesOrNo(false));
				isFirstClaim = false;
				claimsDataList.add(claim);
			}
		}

		if (aaaClaimPoints != 0) {
			for (int j = 0; j < aaaClaimPoints; j++) {
				claim = addClaimData(openLPolicy, isFirstClaim, "Fire", 1, 3, "Open", getYesOrNo(false), getYesOrNo(true));
				isFirstClaim = false;
				claimsDataList.add(claim);
			}
		}
	
		if (frequency != 0.0) {
			Integer years = openLPolicy.getYearsOwned();
			Double num_claims = new Double(frequency*years);			
			if (num_claims >= 1) {
				for (int i = 0; i < num_claims.intValue(); i++) {
					claim = addClaimData(openLPolicy, isFirstClaim, AdvancedComboBox.RANDOM_MARK, 4, 5, "Closed", getYesOrNo(true), getYesOrNo(false));
					isFirstClaim = false;
					claimsDataList.add(claim);				
				}
			}
			else {
				claimsDataList.add(claim);	
			}
		}
		
		return claimsDataList;
	}

	private TestData addClaimData(HomeCaDP3OpenLPolicy openLPolicy, boolean isFirstClaim, 
			String causeOfLoss, int year1, int year2, String claimStatus, String rentalClaim, String isAaaClaim) {
		TestData claimData;
		if (isFirstClaim) {
			claimData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.BTN_ADD.getLabel(), "Click");
			claimData.adjust(getClaimData(openLPolicy, causeOfLoss, year1, year2, claimStatus, rentalClaim, isAaaClaim));
		} else {
			claimData = getClaimData(openLPolicy, causeOfLoss, year1, year2, claimStatus, rentalClaim, isAaaClaim);
		}
		return claimData;
	}
	
	private TestData getClaimData(HomeCaDP3OpenLPolicy openLPolicy, String causeOfLoss, int year1, int year2, String claimStatus, String rentalClaim, String isAaaClaim) {
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.ClaimHistory.ZIP.getLabel(), openLPolicy.getDwelling().getAddress().getZipCode(), 
				HomeCaMetaData.PropertyInfoTab.ClaimHistory.ADDRESS_LINE_1.getLabel(), "6586 PORCUPINE WAY",
				HomeCaMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel(),
						openLPolicy.getEffectiveDate().minusYears(RandomUtils.nextInt(year1, year2)).format(DateTimeUtils.MM_DD_YYYY),
				HomeCaMetaData.PropertyInfoTab.ClaimHistory.CAUSE_OF_LOSS.getLabel(), causeOfLoss, 
				HomeCaMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel(), RandomUtils.nextInt(5000, 10000),
				HomeCaMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS.getLabel(), claimStatus, 
				HomeCaMetaData.PropertyInfoTab.ClaimHistory.POLICY_NUMBER.getLabel(), "345345345", 
				HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM.getLabel(), rentalClaim, 
				HomeCaMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM.getLabel(), isAaaClaim);
	}
	
	
	private TestData getEndorsementTabData(HomeCaDP3OpenLPolicy openLPolicy) {
		TestData endorsementData = DataProviderFactory.emptyData();
		for (HomeCaDP3OpenLForm openLForm : openLPolicy.getForms()) {
			String formCode = openLForm.getFormCode();
			if (!"premium".equals(formCode)) {
				if (!endorsementData.containsKey(HomeCaDP3FormTestDataGenerator.getFormMetaKey(formCode))) {
					List<TestData> tdList = HomeCaDP3FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
					if (tdList != null) {
						TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeCaDP3FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeCaDP3FormTestDataGenerator.getFormMetaKey(formCode), tdList);
						endorsementData.adjust(td);
					}
				}
			}
		}
		return endorsementData;
	}
	
	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaDP3OpenLPolicy openLPolicy) {
		//Coverage A and Coverage B is disabled on Premiums & Coverages Quote tab
		//Double covA = openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		//Double covB = openLPolicy.getCoverages().stream().filter(c -> "CovB".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covC = openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covD = openLPolicy.getCoverages().stream().filter(c -> "CovD".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covE = openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covF = openLPolicy.getCoverages().stream().filter(c -> "CovF".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), covC.toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), covD.toString().split("\\.")[0], 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), "contains=" + new Dollar(covE).toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_F.getLabel(), "contains=" + new Dollar(covF).toString().split("\\.")[0],
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), "contains=" + new Dollar(openLPolicy.getDeductible()).toString().split("\\.")[0]);
	}
}
