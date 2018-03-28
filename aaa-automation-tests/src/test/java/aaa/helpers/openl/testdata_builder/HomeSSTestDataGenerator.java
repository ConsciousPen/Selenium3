package aaa.helpers.openl.testdata_builder;

import aaa.helpers.TestDataHelper;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.utils.excel.io.entity.area.table.TableHeader;
import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

import java.util.Arrays;

public class HomeSSTestDataGenerator extends TestDataGenerator<HomeSSOpenLPolicy> {
	public HomeSSTestDataGenerator(String state) {
		super(state);
	}

	public HomeSSTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy) {


		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new ApplicantTab().getMetaKey(), getApplicantTabData(openLPolicy),
				new ReportsTab().getMetaKey(), getReportsTabData(),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabData(openLPolicy),
				new ProductOfferingTab().getMetaKey(), getProductOfferingTabData(openLPolicy),
				new EndorsementTab().getMetaKey(), getEndorsementTabData(openLPolicy),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy)
		);

		return TestDataHelper.merge(getRatingDataPattern(), td);
	}

	private TestData getGeneralTabData(HomeSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.dataOf(
				HomeSSMetaData.GeneralTab.STATE.getLabel(), getState(),
				HomeSSMetaData.GeneralTab.POLICY_TYPE.getLabel(), openLPolicy.getPolicyType(),
				HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY)
		);
	}

	private TestData getApplicantTabData(HomeSSOpenLPolicy openLPolicy) {

		TestData namedInsuredData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().get(0).isAAAEmployee())
		);

		TestData aaaMembershipData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().get(0).isCurrAAAMember())
		);

		int streetNumber = RandomUtils.nextInt(100, 1000);
		String streetName = RandomStringUtils.randomAlphabetic(10).toUpperCase() + " St";
		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().get(0).getZip(),
				HomeSSMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel(), streetNumber + " " + streetName,
				HomeSSMetaData.ApplicantTab.DwellingAddress.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.dataOf("Street number", streetNumber, "Street Name", streetName)

		);

		return DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), Arrays.asList(namedInsuredData),
				HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembershipData,
				HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData
		);
	}

	private TestData getReportsTabData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getPropertyInfoTabData(HomeSSOpenLPolicy openLPolicy) {

		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), openLPolicy.getPolicyDwellingRatingInfo().get(0).getFamilyUnits()
		);

		TestData publicProtectionClass = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getPolicyDwellingRatingInfo().get(0).getProtectionClass()
		);


		return DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), publicProtectionClass
		);


	}

	private TestData getProductOfferingTabData(HomeSSOpenLPolicy openLPolicy) {
		return null;
	}

	private TestData getEndorsementTabData(HomeSSOpenLPolicy openLPolicy) {
		return null;
	}

	private TestData getPremiumsAndCoveragesQuoteTabData(HomeSSOpenLPolicy openLPolicy) {
		return null;
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
