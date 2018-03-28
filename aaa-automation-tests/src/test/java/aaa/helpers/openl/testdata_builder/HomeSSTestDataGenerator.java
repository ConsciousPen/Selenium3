package aaa.helpers.openl.testdata_builder;

import aaa.helpers.TestDataHelper;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

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
				new ReportsTab().getMetaKey(), getReportsTabData(openLPolicy),
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
				HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				HomeSSMetaData.GeneralTab.LEAD_SOURCE.getLabel(), AdvancedComboBox.RANDOM_MARK,
				HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel(), AdvancedComboBox.RANDOM_MARK
		);
	}

	private TestData getApplicantTabData(HomeSSOpenLPolicy openLPolicy) {
		return null;
	}

	private TestData getReportsTabData(HomeSSOpenLPolicy openLPolicy) {
		return null;
	}

	private TestData getPropertyInfoTabData(HomeSSOpenLPolicy openLPolicy) {
		return null;
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
