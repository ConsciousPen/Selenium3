package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLDwelling;
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
				new ReportsTab().getMetaKey(), getReportsTabData(),
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
		return DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.CurrentCarrier.CONTINUOUS_YEARS_WITH_HO_INSURANCE.getLabel(), openLPolicy.getYearsOfPriorInsurance(), 
				HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel(), 
					openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsWithCsaa()).format(DateTimeUtils.MM_DD_YYYY));
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
		TestData otherActiveAAAPolicies = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), getYesOrNo(openLPolicy.getHasMultiPolicyDiscount()));
		if (openLPolicy.getHasMultiPolicyDiscount()) {
			//TODO add adjust		
		}
		
		return DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), namedInsured, 
				HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembership, 
				HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel(), otherActiveAAAPolicies); 
	}
	
	private TestData getReportsTabData() {
		return DataProviderFactory.emptyData();
	}
	
	private List<TestData> getPropertyInfoTabData(HomeCaHO3OpenLPolicy openLPolicy) { 
		List<TestData> dwellingTestDataList = new ArrayList<>(openLPolicy.getDwellings().size()); 
		
		for (HomeCaHO3OpenLDwelling dwelling: openLPolicy.getDwellings()) {
			TestData dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), dwelling.getNumOfFamilies());			
			TestData ppcData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), dwelling.getPpcValue());			
			TestData wildfireScoreData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.FireReport.WILDFIRE_SCORE.getLabel(), dwelling.getFirelineScore());			
			TestData constructionData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), dwelling.getAgeOfHome(), 
					HomeCaMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel(), dwelling.getConstructionType());			
		TestData fireProtectiveDeviceData = DataProviderFactory.dataOf( 
					HomeCaMetaData.PropertyInfoTab.FIRE_PROTECTIVE_DD.getLabel(), dwelling.getBurglarAlarmType());
			
			dwellingTestDataList.add(DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData, 
					HomeCaMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), ppcData, 
					HomeCaMetaData.PropertyInfoTab.FIRE_PROTECTIVE_DD.getLabel(), wildfireScoreData, 
					HomeCaMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), constructionData, 
					HomeCaMetaData.PropertyInfoTab.FIRE_PROTECTIVE_DD.getLabel(), fireProtectiveDeviceData));
		
		}
		return dwellingTestDataList;
	}
	
	private TestData getEndorsementTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		return null;
	}
	
	private TestData getPersonalPropertyTabData() {
		return null;
	}
	
	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaHO3OpenLPolicy openLPolicy) {
		return null;
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
