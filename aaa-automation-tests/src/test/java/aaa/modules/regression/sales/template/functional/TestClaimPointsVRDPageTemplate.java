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
import toolkit.db.DBService;

public class TestClaimPointsVRDPageTemplate extends PolicyBaseTest {

	private List<TestData> tdClaims;
	private final String priorClaims = "Prior claims";
	private final String aaaClaims = "AAA claims";
	private final String date = "Date";
	private final String points = "Points";
	private final String fire = "Fire";
	private final String water = "Water";
	private final String theft = "Theft";
	private final String liability = "Liability";
	private final String claim1 = "Claim 1";
	private final String claim2 = "Claim 2";
	private final String claim3 = "Claim 3";
	private final String claim4 = "Claim 4";

	private PropertyInfoTab propertyInfoTabSS = new PropertyInfoTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabSS = new PremiumsAndCoveragesQuoteTab();

	private aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab propertyInfoTabCA = new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab();
	private aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabCA = new aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab();

	protected void pas17772_testAAAClaimPointsVRDPageNB_SS() {
		TestData td = getTdWithClaims();
		createQuoteAndFillUpTo(td, premiumsAndCoveragesQuoteTabSS.getClass());

		PropertyQuoteTab.RatingDetailsView.open();
		TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(priorClaims).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(aaaClaims).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(date))
				.isEqualTo(td.getTestData(propertyInfoTabSS.getClass().getSimpleName()).getTestDataList(HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()).get(2).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(points))
				.isEqualTo(getExpectedClaimPointsFromDB(true, theft, "2001", "2"));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(date))
				.isEqualTo(td.getTestData(propertyInfoTabSS.getClass().getSimpleName()).getTestDataList(HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()).get(3).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(points))
				.isEqualTo(getExpectedClaimPointsFromDB(true, liability, "6001", "3"));
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

	private String getExpectedClaimPointsFromDB(boolean isAAAClaim, String causeOfLoss, String claimMin, String claimOrder) {
		String lookupName;
		if (isAAAClaim) {
			lookupName = "AAAHOExperienceClaimPoint";
		} else {
			lookupName = "AAAHOPriorClaimPoint";
		}
		String query = String.format("select points from (select DTYPE,CAUSEOFLOSS,MINPREMIUMOVR,MAXPREMIUMOVR,PRODUCTCD,POLICYTYPECD,RISKSTATECD,CODE as claimorder,DISPLAYVALUE as points from lookupvalue "
				+ "where LOOKUPLIST_ID in (select id from LOOKUPLIST where lookupname = '%s') and riskstatecd = '" + getState() + "') "
				+ "where CAUSEOFLOSS = '" + causeOfLoss + "' and MINPREMIUMOVR = '" + claimMin + "' and claimorder = '" + claimOrder + "'", lookupName);

		return DBService.get().getValue(query).get();
	}



}
