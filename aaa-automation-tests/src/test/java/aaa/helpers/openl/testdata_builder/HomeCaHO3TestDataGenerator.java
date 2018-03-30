package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLDwelling;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLForm;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PersonalPropertyTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.UnderwritingAndApprovalTab;
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
				new PersonalPropertyTab().getMetaKey(), getPersonalPropertyTabData(),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy),
				new MortgageesTab().getMetaKey(), getMortgageesTabData(),
				new UnderwritingAndApprovalTab().getMetaKey(), getUnderwritingAndApprovalTabData(),
				new DocumentsTab().getMetaKey(), getDocumentsTabData(),
				new BindTab().getMetaKey(), getBindTabData());
		
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
	
	private List<TestData> getPropertyInfoTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		List<TestData> dwellingTestDataList = new ArrayList<>(openLPolicy.getDwellings().size()); 
		Dollar coverageA = new Dollar(openLPolicy.getCovALimit());
		
		for (HomeCaHO3OpenLDwelling dwelling: openLPolicy.getDwellings()) {
			TestData dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + dwelling.getNumOfFamilies());
			TestData ppcData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), dwelling.getPpcValue());
			TestData wildfireScoreData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.FireReport.WILDFIRE_SCORE.getLabel(), dwelling.getFirelineScore());	
			
			TestData propertyValueData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), coverageA, 
					HomeCaMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST, coverageA.multiply(0.85), 
					HomeCaMetaData.PropertyInfoTab.PropertyValue.REASON_REPLACEMENT_COST_DIFFERS_FROM_THE_TOOL_VALUE.getLabel(), "Mortgagee requirements");
			
			TestData constructionData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), openLPolicy.getEffectiveDate().minusYears(dwelling.getAgeOfHome()).getYear(),
					HomeCaMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel(), dwelling.getConstructionType());
			
			TestData theftProtectiveDeviceData = getTheftProtectiveDevice(dwelling);
			
			dwellingTestDataList.add(DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
					HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcData,
					HomeCaMetaData.PropertyInfoTab.FIRE_REPORT.getLabel(), wildfireScoreData, 
					HomeCaMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData,
					HomeCaMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), constructionData,
					HomeCaMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), theftProtectiveDeviceData));
		
		}
		return dwellingTestDataList;
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
	
	private List<TestData> getEndorsementTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		List<TestData> formsTestDataList = new ArrayList<>(openLPolicy.getForms().size()); 
		for(HomeCaHO3OpenLForm form: openLPolicy.getForms()) {
			if (!form.getFormCode().equals("premium")) {
				formsTestDataList.add(getFormData(form));
			}
		}
		return formsTestDataList;
	}
	
	private TestData getFormData(HomeCaHO3OpenLForm form) {
		switch (form.getFormCode()){
		case "HO-61C": 
			TestData form_HO61C = DataProviderFactory.dataOf("Action", "Add");
			return DataProviderFactory.dataOf(HomeCaMetaData.EndorsementTab.HO_61C.getLabel(), form_HO61C);
		case "HO-61": 
			TestData form_HO61 = DataProviderFactory.dataOf("Action", "Add");
			return DataProviderFactory.dataOf(HomeCaMetaData.EndorsementTab.HO_61.getLabel(), form_HO61);
		default: 
			return null;
		}
	}
	
	private TestData getPersonalPropertyTabData() {
		return null;
	}
	
	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		Map<String, Object> premiumAndCoveragesTabTestData = new HashMap<>();
		for(HomeCaOpenLCoverage coverage: openLPolicy.getCoverages()) {
			switch (coverage.getCoverageCd()) {
			case "CovA": 
				premiumAndCoveragesTabTestData.put(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_A.getLabel(), coverage.getLimitAmount());
				break;
			case "CovC": 
				premiumAndCoveragesTabTestData.put(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), coverage.getLimitAmount());
				break; 
			case "CovD": 
				premiumAndCoveragesTabTestData.put(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), coverage.getLimitAmount());
				break; 
			case "CovE": 
				premiumAndCoveragesTabTestData.put(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), coverage.getLimitAmount());
				break;
			default: 
				break;
			}
		}
		return new SimpleDataProvider(premiumAndCoveragesTabTestData);
	}
	
	private TestData getMortgageesTabData() {
		return null;
	}
	
	private TestData getUnderwritingAndApprovalTabData() {
		return null;
	}

	private TestData getDocumentsTabData() {
		return null; 
	}
	
	private TestData getBindTabData() {
		return null;
	}
	
	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	} 
}
