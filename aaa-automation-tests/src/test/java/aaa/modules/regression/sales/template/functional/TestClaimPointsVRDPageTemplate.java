package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.List;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
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

	protected void pas17772_testAAAClaimPointsVRDPageNB_SS() {
		TestData td = getTdWithClaims();
		createQuoteAndFillUpTo(td, premiumsAndCoveragesQuoteTabSS.getClass());

		PropertyQuoteTab.RatingDetailsView.open();
		TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData("Prior claims").getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData("AAA claims").getTestData("Claim 1").getTestData("Date"))
				.isEqualTo(td.getTestData(propertyInfoTabSS.getClass().getSimpleName()).getTestDataList(HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()).get(2).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData("AAA claims").getTestData("Claim 1").getTestData("Points"))
				.isEqualTo("test");
		assertThat(claimsVRD.getTestData("AAA claims").getTestData("Claim 2").getTestData("Date"))
				.isEqualTo(td.getTestData(propertyInfoTabSS.getClass().getSimpleName()).getTestDataList(HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()).get(3).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData("AAA claims").getTestData("Claim 2").getTestData("Points"))
				.isEqualTo("test");
		PropertyQuoteTab.RatingDetailsView.close();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());

	}

	private TestData getTdWithClaims() {
		tdClaims = testDataManager.getDefault(TestClaimPointsVRDPageTemplate.class).getTestDataList("PropertyInfo_Claims");
		if (getPolicyType().isCaProduct()) {
			return getPolicyTD().adjust(TestData.makeKeyPath(propertyInfoTabCA.getClass().getSimpleName(), HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()), tdClaims);
		}
		return getPolicyTD().adjust(TestData.makeKeyPath(propertyInfoTabSS.getClass().getSimpleName(), HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()), tdClaims);
	}


}
