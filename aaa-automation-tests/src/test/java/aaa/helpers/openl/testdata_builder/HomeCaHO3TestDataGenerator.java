package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLDwelling;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
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

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
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
				HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getDwellings().get(1).getAddress().get(1).getZipCode());
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
		Double coverageA = openLPolicy.getCovALimit();

		for (HomeCaHO3OpenLDwelling dwelling : openLPolicy.getDwellings()) {
			TestData dwellingAddressData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + dwelling.getNumOfFamilies());
			TestData ppcData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), dwelling.getPpcValue());
			TestData wildfireScoreData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.FireReport.WILDFIRE_SCORE.getLabel(), dwelling.getFirelineScore());
			TestData propertyValueData = DataProviderFactory.dataOf(
					HomeCaMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), coverageA,
					HomeCaMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST, coverageA);

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
}
