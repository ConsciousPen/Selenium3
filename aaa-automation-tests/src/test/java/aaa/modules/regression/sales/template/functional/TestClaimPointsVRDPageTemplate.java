package aaa.modules.regression.sales.template.functional;

import java.util.List;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestClaimPointsVRDPageTemplate extends PolicyBaseTest {

	private PropertyInfoTab propertyInfoTabSS = new PropertyInfoTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabSS = new PremiumsAndCoveragesQuoteTab();

	aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab propertyInfoTabCA = new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab();
	aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabCA = new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab();

	protected void pas17772_testClaimPointsVRDPageSS() {

		createQuoteAndFillUpTo(getTdWithClaims(), premiumsAndCoveragesQuoteTabSS.getClass());
		PropertyQuoteTab.RatingDetailsView.open();



	}

	private TestData getTdWithClaims() {
		List<TestData> tdClaims = testDataManager.getDefault(TestClaimPointsVRDPageTemplate.class).getTestDataList("PropertyInfo_Claims");
		return getPolicyTD().adjust(TestData.makeKeyPath(propertyInfoTabSS.getClass().getSimpleName(), HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()), tdClaims);
	}


}
