package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomUtils;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLForm;
import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class HomeCaHO4TestDataGenerator extends TestDataGenerator<HomeCaHO4OpenLPolicy> {
	public HomeCaHO4TestDataGenerator(String state) {
		super(state);
	}

	public HomeCaHO4TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeCaHO4OpenLPolicy openLPolicy) {
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

	private TestData getGeneralTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		TestData policyInfo = DataProviderFactory.emptyData();
		TestData currentCarrier = DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.CurrentCarrier.CONTINUOUS_YEARS_WITH_HO_INSURANCE.getLabel(), openLPolicy.getYearsOfPriorInsurance(),
				HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel(),
					openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsWithCsaa()).format(DateTimeUtils.MM_DD_YYYY));
		return DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), policyInfo,
				HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), currentCarrier);
	}
	
	private TestData getApplicantTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		TestData namedInsured = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel(), getYesOrNo(openLPolicy.getHasEmployeeDiscount()));
		if (openLPolicy.getHasSeniorDiscount()) {
			namedInsured.adjust(HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(60).format(DateTimeUtils.MM_DD_YYYY));
		}
		TestData aaaMembership = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.getAaaMember()));
		if (Boolean.TRUE.equals(openLPolicy.getAaaMember())) {
			aaaMembership.adjust(HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "4290023667710001");
			aaaMembership.adjust(HomeCaMetaData.ApplicantTab.AAAMembership.LAST_NAME.getLabel(), "Smith");
		}		
		TestData dwellingAddress = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getDwellings().get(0).getAddress().get(0).getZipCode());
		return DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), namedInsured,
				HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembership,
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddress);
	}
	
	private TestData getReportsTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
	
	private TestData getPropertyInfoTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		TestData dwellingAddressData = DataProviderFactory.emptyData(); 
		if ("CO1".equals(openLPolicy.getConstructionGroup())) {
			dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + RandomUtils.nextInt(1, 4));
		}
		else if ("CO2".equals(openLPolicy.getConstructionGroup())) {
			dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "5-15");
		}
		TestData ppcData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getDwellings().get(0).getPpcValue());
		TestData propertyValueData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PropertyValue.PERSONAL_PROPERTY_VALUE.getLabel(), new Dollar(openLPolicy.getCovCLimit()));
		
		TestData interiorData = DataProviderFactory.emptyData();
		if ("Renter".equals(openLPolicy.getOccupancyType())) {
			interiorData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.Interior.OCCUPANCY_TYPE.getLabel(), "Tenant occupied");
		}
		if ("Owner".equals(openLPolicy.getOccupancyType())) {
			interiorData = DataProviderFactory.dataOf(
					//TODO need clarify this value
					HomeCaMetaData.PropertyInfoTab.Interior.OCCUPANCY_TYPE.getLabel(), "Tenant occupied");
		}

		List<TestData> claimHistoryData = getClaimsHistoryData(openLPolicy, openLPolicy.getExpClaimPoints(), openLPolicy.getClaimPoints());
		
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcData,
				HomeCaMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData, 
				HomeCaMetaData.PropertyInfoTab.INTERIOR.getLabel(), interiorData, 
				HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel(), claimHistoryData);
	}
	
	private List<TestData> getClaimsHistoryData(HomeCaHO4OpenLPolicy openLPolicy, Integer AaaClaimPoints, Integer notAaaClaimPoints) {
		List<TestData> claimsDataList = new ArrayList<>();
		TestData claim = DataProviderFactory.emptyData(); 
		
		int aaaPoints = AaaClaimPoints.intValue();
		int notAaaPoints = notAaaClaimPoints.intValue();
		int totalPoints = aaaPoints + notAaaPoints;
		boolean isFirstClaim = true;
		
		if (totalPoints == 0) {
			claimsDataList.add(claim); 
		}
		
		if (notAaaPoints != 0) {
			for (int i = 0; i < notAaaPoints; i++) {
				claim = addClaimData(openLPolicy, isFirstClaim);
				isFirstClaim = false;
				claimsDataList.add(claim);
			}
		}
		
		if (aaaPoints != 0) {
			for (int j = 0; j < aaaPoints; j++) {
				claim = addClaimData(openLPolicy, isFirstClaim); 
				claim.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM.getLabel(), "Yes"));
				isFirstClaim = false; 
				claimsDataList.add(claim);
			}
		}

		return claimsDataList;
	}
	
	private TestData addClaimData(HomeCaHO4OpenLPolicy openLPolicy, boolean isFirstClaim) {
		TestData claimData = DataProviderFactory.emptyData(); 
		if (isFirstClaim) {
			claimData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.ADD_A_CLAIM.getLabel(), "Yes", 
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel(), 
								openLPolicy.getEffectiveDate().minusYears(RandomUtils.nextInt(1, 3)).format(DateTimeUtils.MM_DD_YYYY), 
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.CAUSE_OF_LOSS.getLabel(), AdvancedComboBox.RANDOM_MARK, 
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel(), RandomUtils.nextInt(10000, 20000), 
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS.getLabel(), "Open"); 
		}
		else {
			claimData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel(), 
								openLPolicy.getEffectiveDate().minusYears(RandomUtils.nextInt(1, 3)).format(DateTimeUtils.MM_DD_YYYY), 
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.CAUSE_OF_LOSS.getLabel(), AdvancedComboBox.RANDOM_MARK, 
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel(), RandomUtils.nextInt(10000, 20000), 
					HomeCaMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS.getLabel(), "Open");
		}
		return claimData;
	}
	
	private TestData getEndorsementTabData(HomeCaHO4OpenLPolicy openLPolicy) {
		return null;
	}
	
	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaHO4OpenLPolicy openLPolicy) { 
		Double covD = openLPolicy.getCoverages().stream().filter(c -> "CovD".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covE = openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();

		return DataProviderFactory.dataOf( 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), covD.toString().split("\\.")[0], 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), new Dollar(covE).toString().split("\\.")[0], 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), getDeductibleValueByForm(openLPolicy));
	}
	
	private String getDeductibleValueByForm(HomeCaHO4OpenLPolicy openLPolicy) {
		String deductible = "index=1";
		
		for(HomeCaHO4OpenLForm form: openLPolicy.getForms()) {
			if (form.getFormCode().contains("HO-58")){
				deductible = "contains=" + new Dollar(250).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().contains("HO-59")) {
				deductible = "contains=" + new Dollar(500).toString().split("\\.")[0];
			}
			else if (form.getFormCode().contains("HO-60")) {
				deductible = "contains=" + new Dollar(1000).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().contains("HO-76")) {
				deductible = "contains=" + new Dollar(1500).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().contains("HO-77")) {
				deductible = "contains=" + new Dollar(2000).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().contains("HO-78")) {
				deductible = "contains=" + new Dollar(2500).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().contains("HO-79")) {
				deductible = "contains=" + new Dollar(3000).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().contains("HO-80")) {
				deductible = "contains=" + new Dollar(4000).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().contains("HO-81")) {
				deductible = "contains=" + new Dollar(5000).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().contains("HO-82")) {
				deductible = "contains=" + new Dollar(7500).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().contains("HO-177")) {
				deductible = "contains=Theft";
			}
		}
		return deductible;
	}
	
	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
