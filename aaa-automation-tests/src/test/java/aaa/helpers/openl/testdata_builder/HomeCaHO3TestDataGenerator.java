package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLDwelling;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLForm;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;

//import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;
//import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;

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
			if (form.getFormCode().contains("HO-61")) {
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
		
		boolean isHO44 = false; 
		for (HomeCaHO3OpenLForm form: openLPolicy.getForms()) {
			if ("HO-44".equals(form.getFormCode())) {
				isHO44 = true;	
			}
		}
		
		TestData dwellingAddressData;
		if (isHO44) {
			dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(),
					"contains=" + openLPolicy.getForms().stream().filter(n -> "HO-44".equals(n.getFormCode())).findFirst().get().getNumOfFamilies(),
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.SECTION_II_TERRITORY.getLabel(), 
					"contains=" + openLPolicy.getForms().stream().filter(n -> "HO-44".equals(n.getFormCode())).findFirst().get().getTerritoryCode()); 			
		}
		else {
			dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + openLPolicy.getDwellings().get(0).getNumOfFamilies());
		}	
	
		TestData ppcData = DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getDwellings().get(0).getPpcValue());
		
		//Wildfire score should be returned from reports, UI field is disabled
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
		
		List<TestData> detachedStructuresDataList = getDetachedStructuresData(openLPolicy);
		
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcData,
				//HomeCaMetaData.PropertyInfoTab.FIRE_REPORT.getLabel(), wildfireScoreData, 
				HomeCaMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData,
				HomeCaMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), constructionData,
				HomeCaMetaData.PropertyInfoTab.DETACHED_STRUCTURES.getLabel(), detachedStructuresDataList, 
				HomeCaMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), theftProtectiveDeviceData);
	}
	
	private TestData getTheftProtectiveDevice(HomeCaHO3OpenLDwelling dwelling) {
		switch (dwelling.getBurglarAlarmType()) {
		case "Central": 
			return DataProviderFactory.dataOf(HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.CENTRAL_THEFT_ALARM.getLabel(), Boolean.TRUE); 
		case "Local": 
			return DataProviderFactory.dataOf(HomeCaMetaData.PropertyInfoTab.TheftProtectiveTPDD.LOCAL_THEFT_ALARM.getLabel(), Boolean.TRUE);
		case "None": 
			return DataProviderFactory.emptyData();
		default: 
			return DataProviderFactory.emptyData();
		}
	}
	
	private List<TestData> getDetachedStructuresData(HomeCaHO3OpenLPolicy openLPolicy) {
		List<TestData> detachedStructuresDataList = new ArrayList<>();
		TestData detachedStructures;
		Integer dsCounter = 0;
		for (HomeCaHO3OpenLForm form: openLPolicy.getForms()) {
			if (dsCounter.equals(0)) {
				if ("HO-40".equals(form.getFormCode())) {
					dsCounter++;
					detachedStructures = DataProviderFactory.dataOf(
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.ARE_THERE_ANY_DETACHED_STRUCTURES_ON_THE_PROPERTY.getLabel(), "Yes",
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.RENTED_TO_OTHERS.getLabel(), "Yes",
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.DESCRIPTION.getLabel(), "Description" + dsCounter,
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.LIMIT_OF_LIABILITY.getLabel(), new Dollar(form.getLimit()).toString(),
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.NUMBER_OF_FAMILY_UNITS.getLabel(), form.getNumOfFamilies().toString(), 
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.NUMBER_OF_OCCUPANTS.getLabel(), "index=2");
					detachedStructuresDataList.add(detachedStructures);
				} else if ("HO-48".equals(form.getFormCode())) {
					dsCounter++;
					detachedStructures = DataProviderFactory.dataOf(
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.ARE_THERE_ANY_DETACHED_STRUCTURES_ON_THE_PROPERTY.getLabel(), "Yes",
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.RENTED_TO_OTHERS.getLabel(), "No",
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.DESCRIPTION.getLabel(), "Description" + dsCounter,
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString());
					detachedStructuresDataList.add(detachedStructures);
				}
			}
			else {
				if ("HO-40".equals(form.getFormCode())) {
					dsCounter++;
					detachedStructures = DataProviderFactory.dataOf(
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.RENTED_TO_OTHERS.getLabel(), "Yes",
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.DESCRIPTION.getLabel(), "Description" + dsCounter,
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString(),
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.NUMBER_OF_FAMILY_UNITS.getLabel(), form.getNumOfFamilies().toString(), 
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.NUMBER_OF_OCCUPANTS.getLabel(), "index=2");
					detachedStructuresDataList.add(detachedStructures);
				} else if ("HO-48".equals(form.getFormCode())) {
					dsCounter++;
					detachedStructures = DataProviderFactory.dataOf(
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.RENTED_TO_OTHERS.getLabel(), "No",
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.DESCRIPTION.getLabel(), "Description" + dsCounter,
							HomeCaMetaData.PropertyInfoTab.DetachedStructures.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString());
					detachedStructuresDataList.add(detachedStructures);
				}
			}	
		}
		return detachedStructuresDataList;
	}
	
	private TestData getEndorsementTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		TestData endorsementData = new SimpleDataProvider();
		
		for (HomeCaHO3OpenLForm openLForm: openLPolicy.getForms()) {
			String formCode = openLForm.getFormCode();
			if (!"premium".equals(formCode)) {
				if (!endorsementData.containsKey(HomeCAFormTestDataGenerator.getFormMetaKey(formCode))) {
					List<TestData> tdList = HomeCAFormTestDataGenerator.getFormTestData(openLPolicy, formCode);
					if (tdList != null) {
						TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeCAFormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeCAFormTestDataGenerator.getFormMetaKey(formCode), tdList);
						endorsementData.adjust(td);
					}
				}
			}
		}		
		return endorsementData;
	}
	
	private TestData getPersonalPropertyTabData (HomeCaHO3OpenLPolicy openLPolicy) {
		TestData personalPropertyTabData = new SimpleDataProvider();

		for (HomeCaHO3OpenLForm form: openLPolicy.getForms()) {
			if ("HO-61".equals(form.getFormCode())) {
				switch (form.getScheduledPropertyItems().get(0).getPropertyType()){
				case "Cameras": 
					TestData camerasData = DataProviderFactory.dataOf(
							HomeCaMetaData.PersonalPropertyTab.Cameras.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(), 
							HomeCaMetaData.PersonalPropertyTab.Cameras.DESCRIPTION.getLabel(), "test");
					personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.CAMERAS.getLabel(), camerasData));
					break;
				case "Coins":
					TestData coinsData = DataProviderFactory.dataOf(
							HomeCaMetaData.PersonalPropertyTab.Coins.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(), 
							HomeCaMetaData.PersonalPropertyTab.Coins.DESCRIPTION.getLabel(), "test");
					personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.COINS.getLabel(), coinsData));
					break;
				case "Fine Art":
					TestData fineArtData = DataProviderFactory.dataOf(
							HomeCaMetaData.PersonalPropertyTab.FineArts.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(), 
							HomeCaMetaData.PersonalPropertyTab.FineArts.DESCRIPTION.getLabel(), "test");
					personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.FINE_ARTS.getLabel(), fineArtData));
					break; 
				case "Furs":
					TestData fursData = DataProviderFactory.dataOf(
							HomeCaMetaData.PersonalPropertyTab.Furs.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(), 
							HomeCaMetaData.PersonalPropertyTab.Furs.DESCRIPTION.getLabel(), "test");
					personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.FURS.getLabel(), fursData));
					break;
				case "Golf Equipment": 
					TestData golfEquipmentData = DataProviderFactory.dataOf(
							HomeCaMetaData.PersonalPropertyTab.GolfEquipment.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(), 
							HomeCaMetaData.PersonalPropertyTab.GolfEquipment.DESCRIPTION.getLabel(), "test", 
							HomeCaMetaData.PersonalPropertyTab.GolfEquipment.LEFT_OR_RIGHT_HANDED_CLUB.getLabel(), "index=1");
					personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.GOLF_EQUIPMENT.getLabel(), golfEquipmentData));
					break;
				case "Jewelry": 
					TestData jewerlyData = DataProviderFactory.dataOf(
							HomeCaMetaData.PersonalPropertyTab.Jewelry.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(), 
							HomeCaMetaData.PersonalPropertyTab.Jewelry.JEWELRY_CATEGORY.getLabel(), "index=1", 
							HomeCaMetaData.PersonalPropertyTab.Jewelry.DESCRIPTION.getLabel(), "test");
					personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.JEWELRY.getLabel(), jewerlyData));
					break;
				case "Musical Instruments":
					TestData musicalInstrumentsData = DataProviderFactory.dataOf(
							HomeCaMetaData.PersonalPropertyTab.MusicalInstruments.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(), 
							HomeCaMetaData.PersonalPropertyTab.MusicalInstruments.DESCRIPTION.getLabel(), "test");
					personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.MUSICAL_INSTRUMENTS.getLabel(), musicalInstrumentsData));
					break;
				case "Stamps":
					TestData stampsData = DataProviderFactory.dataOf(
							HomeCaMetaData.PersonalPropertyTab.PostageStamps.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(), 
							HomeCaMetaData.PersonalPropertyTab.PostageStamps.DESCRIPTION.getLabel(), "test");
					personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.POSTAGE_STAMPS.getLabel(), stampsData));
					break;
				case "Silverware": 
					TestData silverwareData = DataProviderFactory.dataOf(
							HomeCaMetaData.PersonalPropertyTab.Silverware.LIMIT_OF_LIABILITY.getLabel(), form.getScheduledPropertyItems().get(0).getLimit().toString(), 
							HomeCaMetaData.PersonalPropertyTab.Silverware.DESCRIPTION.getLabel(), "test", 
							HomeCaMetaData.PersonalPropertyTab.Silverware.SET_OR_INDIVIDUAL_PIECE.getLabel(), "index=1");
					personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.SILVERWARE.getLabel(), silverwareData));
					break;
				default: 
					break;
				}
			}
			else if ("HO-61C".equals(form.getFormCode())) {
				TestData boatsData = DataProviderFactory.dataOf(
						HomeCaMetaData.PersonalPropertyTab.Boats.BOAT_TYPE.getLabel(), form.getType(), 
						HomeCaMetaData.PersonalPropertyTab.Boats.YEAR.getLabel(), "2015", 
						HomeCaMetaData.PersonalPropertyTab.Boats.HORSEPOWER.getLabel(), "50", 
						HomeCaMetaData.PersonalPropertyTab.Boats.LENGTH_INCHES.getLabel(), "300", 
						HomeCaMetaData.PersonalPropertyTab.Boats.DEDUCTIBLE.getLabel(), new Dollar(form.getDeductible()).toString().split("\\.")[0], 
						HomeCaMetaData.PersonalPropertyTab.Boats.AMOUNT_OF_INSURANCE.getLabel(), "500");
				personalPropertyTabData.adjust(DataProviderFactory.dataOf(HomeCaMetaData.PersonalPropertyTab.BOATS.getLabel(), boatsData));
			}
		}
		return personalPropertyTabData;	
		
	}
	
	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		//Coverage A is disabled on Premiums & Coverges Quote tab
		//Double covA = openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
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
