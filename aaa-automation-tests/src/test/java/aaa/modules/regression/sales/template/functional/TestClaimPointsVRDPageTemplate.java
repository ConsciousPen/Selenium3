package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.List;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestClaimPointsVRDPageTemplate extends PolicyBaseTest {

	private List<TestData> tdClaims;

	private PropertyInfoTab propertyInfoTabSS = new PropertyInfoTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabSS = new PremiumsAndCoveragesQuoteTab();

	private aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab propertyInfoTabCA = new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab();
	private aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabCA = new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab();

	protected void pas17772_testAAAClaimPointsVRDPageSS() {
		createQuoteAndFillUpTo(getTdWithClaims(), premiumsAndCoveragesQuoteTabSS.getClass());

		PropertyQuoteTab.RatingDetailsView.open();
		TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();

	}

	private TestData getTdWithClaims() {
		tdClaims = testDataManager.getDefault(TestClaimPointsVRDPageTemplate.class).getTestDataList("PropertyInfo_Claims");
		if (getPolicyType().isCaProduct()) {
			return getPolicyTD().adjust(TestData.makeKeyPath(propertyInfoTabCA.getClass().getSimpleName(), HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()), tdClaims);
		}
		return getPolicyTD().adjust(TestData.makeKeyPath(propertyInfoTabSS.getClass().getSimpleName(), HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()), tdClaims);
	}


}
