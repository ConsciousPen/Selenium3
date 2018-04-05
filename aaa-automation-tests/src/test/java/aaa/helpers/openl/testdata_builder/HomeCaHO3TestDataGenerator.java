package aaa.helpers.openl.testdata_builder;

import org.apache.commons.lang3.NotImplementedException;
import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;
//import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLDwelling;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLForm;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PersonalPropertyTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;

public class HomeCaHO3TestDataGenerator extends TestDataGenerator<HomeCaHO3OpenLPolicy> {
	public HomeCaHO3TestDataGenerator(String state) {
		super(state);
	}

	public HomeCaHO3TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeCaHO3OpenLPolicy openLPolicy) {		
		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new ApplicantTab().getMetaKey(), getApplicantTabData(openLPolicy),
				new ReportsTab().getMetaKey(), getReportsTabData(openLPolicy),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabData(openLPolicy),
				new EndorsementTab().getMetaKey(), getEndorsementTabData(openLPolicy),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy));
		
		for(HomeCaHO3OpenLForm form: openLPolicy.getForms()) {
			if (form.getFormCode().equals("HO-61C")) {
				td.adjust(DataProviderFactory.dataOf(new PersonalPropertyTab().getMetaKey(), getPersonalPropertyTabData(openLPolicy)));
			}
		}
		
		return TestDataHelper.merge(getRatingDataPattern(), td);
	}
	
	private TestData getGeneralTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		TestData policyInfo = DataProviderFactory.emptyData();
		TestData currentCarrier = DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.CurrentCarrier.CONTINUOUS_YEARS_WITH_HO_INSURANCE.getLabel(), openLPolicy.getYearsOfPriorInsurance(),
				HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel(),
					openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsWithCsaa()).format(DateTimeUtils.MM_DD_YYYY));
		return DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), policyInfo,
				HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), currentCarrier);
	}
	
	private TestData getApplicantTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		TestData namedInsured = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel(), getYesOrNo(openLPolicy.getHasEmployeeDiscount()));
		if (openLPolicy.getHasSeniorDiscount()) {
			namedInsured.adjust(HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(60).format(DateTimeUtils.MM_DD_YYYY));
		}
		TestData aaaMembership = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.getAaaMember()));
		if (openLPolicy.getAaaMember()) {
			//TODO remove hard coded values
			aaaMembership.adjust(HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "4290023667710001");
			aaaMembership.adjust(HomeCaMetaData.ApplicantTab.AAAMembership.LAST_NAME.getLabel(), "Smith");
		}
		TestData dwellingAddress = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getDwellings().get(0).getAddress().get(0).getZipCode());
		TestData otherActiveAAAPolicies = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), getYesOrNo(openLPolicy.getHasMultiPolicyDiscount()));
		if (openLPolicy.getHasMultiPolicyDiscount()) {
			//TODO add adjust	
		}
		
		return DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), namedInsured,
				HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembership,
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddress,
				HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel(), otherActiveAAAPolicies);
	}
	
	private TestData getReportsTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
	
	private TestData getPropertyInfoTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		Dollar coverageA = new Dollar(openLPolicy.getCovALimit());
		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + openLPolicy.getDwellings().get(0).getNumOfFamilies());
		
		TestData ppcData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getDwellings().get(0).getPpcValue());
		//Wildfire returns from reports
		//TestData wildfireScoreData = DataProviderFactory.dataOf(
		//		HomeCaMetaData.PropertyInfoTab.FireReport.WILDFIRE_SCORE.getLabel(), openLPolicy.getDwellings().get(0).getFirelineScore());	
		
		TestData propertyValueData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), coverageA, 
				HomeCaMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST.getLabel(), coverageA.multiply(0.85),  
				HomeCaMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), "Mortgagee requirements");
		
		TestData constructionData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), openLPolicy.getEffectiveDate().minusYears(openLPolicy.getDwellings().get(0).getAgeOfHome()).getYear(),
				HomeCaMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel(), openLPolicy.getDwellings().get(0).getConstructionType());
		
		TestData theftProtectiveDeviceData = getTheftProtectiveDevice(openLPolicy.getDwellings().get(0));
		
		TestData detachedStructures = DataProviderFactory.emptyData();
		for (HomeCaHO3OpenLForm form: openLPolicy.getForms()) {
			if (form.getFormCode().contains("HO-44")) {
				detachedStructures = DataProviderFactory.dataOf(
						HomeCaMetaData.PropertyInfoTab.DetachedStructures.ARE_THERE_ANY_DETACHED_STRUCTURES_ON_THE_PROPERTY.getLabel(), "Yes", 
						HomeCaMetaData.PropertyInfoTab.DetachedStructures.RENTED_TO_OTHERS.getLabel(), "Yes", 
						HomeCaMetaData.PropertyInfoTab.DetachedStructures.DESCRIPTION.getLabel(), "test", 
						HomeCaMetaData.PropertyInfoTab.DetachedStructures.LIMIT_OF_LIABILITY.getLabel(), "1000", 
						HomeCaMetaData.PropertyInfoTab.DetachedStructures.NUMBER_OF_FAMILY_UNITS.getLabel(), "index=1", 
						HomeCaMetaData.PropertyInfoTab.DetachedStructures.NUMBER_OF_OCCUPANTS.getLabel(), "index=2");
			}
		}
		
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcData,
				//HomeCaMetaData.PropertyInfoTab.FIRE_REPORT.getLabel(), wildfireScoreData, 
				HomeCaMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData,
				HomeCaMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), constructionData,
				HomeCaMetaData.PropertyInfoTab.DETACHED_STRUCTURES.getLabel(), detachedStructures, 
				HomeCaMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), theftProtectiveDeviceData);
	}
	
	private TestData getTheftProtectiveDevice(HomeCaHO3OpenLDwelling dwelling) {
		TestData theftProtectiveDeviceData;
		switch (dwelling.getBurglarAlarmType()) {
		case "Central": 
			theftProtectiveDeviceData = DataProviderFactory.dataOf(HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.CENTRAL_THEFT_ALARM.getLabel(), Boolean.TRUE);
			break; 
		case "Local": 
			theftProtectiveDeviceData = DataProviderFactory.dataOf(HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.LOCAL_THEFT_ALARM.getLabel(), Boolean.TRUE);
			break;
		case "None": 
			theftProtectiveDeviceData = DataProviderFactory.emptyData();
			break;
		default: 
			theftProtectiveDeviceData = DataProviderFactory.emptyData();
			break;
		}
		return theftProtectiveDeviceData;
	}
	
	private TestData getEndorsementTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		TestData endorsementData = new SimpleDataProvider();
		
		for (HomeCaHO3OpenLForm openLForm: openLPolicy.getForms()) {
			String formCode = openLForm.getFormCode();
			if (!formCode.equals("premium")) {
				if (!endorsementData.containsKey(HomeCAFormTestDataGenerator.getFormMetaKey(formCode))) {
					TestData td = HomeCAFormTestDataGenerator.getFormTestData(openLForm);
					endorsementData.adjust(td);
				}
			}
		}		
		return endorsementData;
	}
	
	private TestData getPersonalPropertyTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		TestData personalPropertyTabTestData = DataProviderFactory.emptyData();
		for(HomeCaHO3OpenLForm form: openLPolicy.getForms()) {
			if (form.getFormCode().equals("HO-61")) {
				personalPropertyTabTestData = getPersonalPropertyDataForHO61(form);						
			}
			else if (form.getFormCode().equals("HO-61C")) {
				personalPropertyTabTestData = getPersonalPropertyDataForHO61C(form);	
			}
		}

		return personalPropertyTabTestData;
	}
	
	private TestData getPersonalPropertyDataForHO61 (HomeCaHO3OpenLForm openLForm) {
		switch (openLForm.getScheduledPropertyItems().get(0).getPropertyType()){
		case "Golf Equipment": 
			TestData golfEquipmentData = DataProviderFactory.dataOf(
					HomeCaMetaData.PersonalPropertyTab.GolfEquipment.LIMIT_OF_LIABILITY.getLabel(), openLForm.getScheduledPropertyItems().get(0).getLimit(), 
					HomeCaMetaData.PersonalPropertyTab.GolfEquipment.DESCRIPTION.getLabel(), "test", 
					HomeCaMetaData.PersonalPropertyTab.GolfEquipment.LEFT_OR_RIGHT_HANDED_CLUB.getLabel(), "index=1");
			return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.GOLF_EQUIPMENT.getLabel(), golfEquipmentData);
		case "Silverware": 
			TestData silverwareData = DataProviderFactory.dataOf(
					HomeCaMetaData.PersonalPropertyTab.Silverware.LIMIT_OF_LIABILITY.getLabel(), openLForm.getScheduledPropertyItems().get(0).getLimit(), 
					HomeCaMetaData.PersonalPropertyTab.Silverware.DESCRIPTION.getLabel(), "test", 
					HomeCaMetaData.PersonalPropertyTab.Silverware.SET_OR_INDIVIDUAL_PIECE.getLabel(), "index=1");
			return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.SILVERWARE.getLabel(), silverwareData);
		case "Jewelry": 
			TestData jewerlyData = DataProviderFactory.dataOf(
					HomeCaMetaData.PersonalPropertyTab.Jewelry.LIMIT_OF_LIABILITY.getLabel(), openLForm.getScheduledPropertyItems().get(0).getLimit(), 
					HomeCaMetaData.PersonalPropertyTab.Jewelry.JEWELRY_CATEGORY.getLabel(), "index=1", 
					HomeCaMetaData.PersonalPropertyTab.Jewelry.DESCRIPTION.getLabel(), "test");
			return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.JEWELRY.getLabel(), jewerlyData); 
		case "Furs":
			TestData fursData = DataProviderFactory.dataOf(
					HomeCaMetaData.PersonalPropertyTab.Furs.LIMIT_OF_LIABILITY.getLabel(), openLForm.getScheduledPropertyItems().get(0).getLimit(), 
					HomeCaMetaData.PersonalPropertyTab.Furs.DESCRIPTION.getLabel(), "test");
			return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.FURS.getLabel(), fursData);
		case "Cameras": 
			TestData camerasData = DataProviderFactory.dataOf(
					HomeCaMetaData.PersonalPropertyTab.Cameras.LIMIT_OF_LIABILITY.getLabel(), openLForm.getScheduledPropertyItems().get(0).getLimit(), 
					HomeCaMetaData.PersonalPropertyTab.Cameras.DESCRIPTION.getLabel(), "test");
			return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.CAMERAS.getLabel(), camerasData);
		case "Stamps":
			TestData stampsData = DataProviderFactory.dataOf(
					HomeCaMetaData.PersonalPropertyTab.PostageStamps.LIMIT_OF_LIABILITY.getLabel(), openLForm.getScheduledPropertyItems().get(0).getLimit(), 
					HomeCaMetaData.PersonalPropertyTab.PostageStamps.DESCRIPTION.getLabel(), "test");
			return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.POSTAGE_STAMPS.getLabel(), stampsData);
		case "Coins":
			TestData coinsData = DataProviderFactory.dataOf(
					HomeCaMetaData.PersonalPropertyTab.Coins.LIMIT_OF_LIABILITY.getLabel(), openLForm.getScheduledPropertyItems().get(0).getLimit(), 
					HomeCaMetaData.PersonalPropertyTab.Coins.DESCRIPTION.getLabel(), "test");
			return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.COINS.getLabel(), coinsData);
		case "Musical Instruments":
			TestData musicalInstrumentsData = DataProviderFactory.dataOf(
					HomeCaMetaData.PersonalPropertyTab.MusicalInstruments.LIMIT_OF_LIABILITY.getLabel(), openLForm.getScheduledPropertyItems().get(0).getLimit(), 
					HomeCaMetaData.PersonalPropertyTab.MusicalInstruments.DESCRIPTION.getLabel(), "test");
			return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.MUSICAL_INSTRUMENTS.getLabel(), musicalInstrumentsData);
		case "Fine Art":
			TestData fineArtData = DataProviderFactory.dataOf(
					HomeCaMetaData.PersonalPropertyTab.FineArts.LIMIT_OF_LIABILITY.getLabel(), openLForm.getScheduledPropertyItems().get(0).getLimit(), 
					HomeCaMetaData.PersonalPropertyTab.FineArts.DESCRIPTION.getLabel(), "test");
			return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.FINE_ARTS.getLabel(), fineArtData);
		default: 
			return DataProviderFactory.emptyData();
		} 
	}
	
	private TestData getPersonalPropertyDataForHO61C (HomeCaHO3OpenLForm openLForm) {
		TestData boatsData = DataProviderFactory.dataOf(
				HomeCaMetaData.PersonalPropertyTab.Boats.BOAT_TYPE.getLabel(), openLForm.getType(), 
				HomeCaMetaData.PersonalPropertyTab.Boats.YEAR.getLabel(), "2015", 
				HomeCaMetaData.PersonalPropertyTab.Boats.HORSEPOWER.getLabel(), "50", 
				HomeCaMetaData.PersonalPropertyTab.Boats.LENGTH_INCHES.getLabel(), "300", 
				HomeCaMetaData.PersonalPropertyTab.Boats.DEDUCTIBLE.getLabel(), new Dollar(openLForm.getDeductible()).toString().split("\\.")[0], 
				HomeCaMetaData.PersonalPropertyTab.Boats.AMOUNT_OF_INSURANCE.getLabel(), "500");
		return DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.BOATS.getLabel(), boatsData);
	}
	
	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		//Coverage A is disabled on Premiums & Coverges Quote tab
		Double covA = openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covC = openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covD = openLPolicy.getCoverages().stream().filter(c -> "CovD".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covE = openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();

		/*
		Dollar coverageC = new Dollar(covC);
		Dollar coverageA = new Dollar(covA);
		if (coverageC.lessThan(coverageA.multiply(0.75))) {
			coverageC = coverageA.multiply(0.75);
		}*/
		
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), covC.toString().split("\\.")[0], 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), covD.toString().split("\\.")[0], 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), new Dollar(covE).toString().split("\\.")[0], 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), getDeductibleValueByForm(openLPolicy));
	}
	
	private String getDeductibleValueByForm(HomeCaHO3OpenLPolicy openLPolicy) {
		String deductible = "index=1";
		
		for(HomeCaHO3OpenLForm form: openLPolicy.getForms()) {
			if (form.getFormCode().contains("HO-57")){
				deductible = "contains=" + new Dollar(100).toString().split("\\.")[0]; 
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
