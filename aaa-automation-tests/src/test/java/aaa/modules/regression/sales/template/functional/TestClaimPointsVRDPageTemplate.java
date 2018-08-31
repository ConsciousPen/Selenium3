package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.PolicyConstants;
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
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(date)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, theft, "2"));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(date)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, liability, "3"));

		navigateToPropertyInfoTab();
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, liability).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS).setValue(propertyInfoTabSS.getEffectiveDate().minusMonths(37).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, water).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CATASTROPHE_LOSS).setValue("No");

		premiumsAndCoveragesQuoteTabSS.calculatePremium();
		PropertyQuoteTab.RatingDetailsView.open();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(priorClaims).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(aaaClaims).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(date)).isEqualTo(tdClaims.get(Claims.WATER).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, water, "2"));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(date)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, theft, "3"));

		navigateToPropertyInfoTab();
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, liability).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		String newLiabilityLossDate = propertyInfoTabSS.getEffectiveDate().minusYears(2).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS).setValue(newLiabilityLossDate);

		premiumsAndCoveragesQuoteTabSS.calculatePremium();
		PropertyQuoteTab.RatingDetailsView.open();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(priorClaims).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(aaaClaims).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(date)).isEqualTo(newLiabilityLossDate);
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, liability, "1"));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(date)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, fire, "2"));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim3).getValue(date)).isEqualTo(tdClaims.get(Claims.WATER).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim3).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, water, "3"));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim4).getValue(date)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim4).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, theft, "4"));

		navigateToPropertyInfoTab();
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, liability).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS).setValue(tdClaims.get(Claims.LIABILITY).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM).setValue("No");
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, fire).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM).setValue("No");
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, water).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM).setValue("No");
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, theft).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM).setValue("No");

		premiumsAndCoveragesQuoteTabSS.calculatePremium();
		PropertyQuoteTab.RatingDetailsView.open();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(aaaClaims).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(priorClaims).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim1).getValue(date)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim1).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(false, fire, "1"));
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim2).getValue(date)).isEqualTo(tdClaims.get(Claims.WATER).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim2).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(false, water, "2"));
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim3).getValue(date)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim3).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(false, theft, "3"));
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim4).getValue(date)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim4).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(false, liability, "4"));

		navigateToPropertyInfoTab();
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, fire).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM).setValue("Yes");
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS).setValue("1001");
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, water).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM).setValue("Yes");
		propertyInfoTabSS.tblClaimsList.getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, theft).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
		propertyInfoTabSS.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CATASTROPHE_LOSS).setValue("Yes");

		premiumsAndCoveragesQuoteTabSS.calculatePremium();
		PropertyQuoteTab.RatingDetailsView.open();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(priorClaims).getKeys().size()).isEqualTo(1);
		assertThat(claimsVRD.getTestData(aaaClaims).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim1).getValue(date)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(priorClaims).getTestData(claim1).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(false, fire, "1"));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(date)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim1).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, fire, "1"));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(date)).isEqualTo(tdClaims.get(Claims.WATER).getValue(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()));
		assertThat(claimsVRD.getTestData(aaaClaims).getTestData(claim2).getValue(points)).isEqualTo(getExpectedClaimPointsFromDB(true, water, "2"));
		PropertyQuoteTab.RatingDetailsView.close();

	}

	private TestData getTdWithClaims() {
		tdClaims = testDataManager.getDefault(TestClaimPointsVRDPageTemplate.class).getTestDataList("PropertyInfo_Claims");
		if (isStateCA()) {
			return getPolicyTD().adjust(TestData.makeKeyPath(propertyInfoTabCA.getClass().getSimpleName(), HomeCaMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()), tdClaims);
		}
		return getPolicyTD().adjust(TestData.makeKeyPath(propertyInfoTabSS.getClass().getSimpleName(), HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel()), tdClaims);
	}

	private void navigateToPropertyInfoTab() {
		PropertyQuoteTab.RatingDetailsView.close();
		if (isStateCA()) {
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		} else {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		}
	}

	private String getExpectedClaimPointsFromDB(boolean isAAAClaim, String causeOfLoss, String claimOrder) {
		String claimMin = "";
		switch (causeOfLoss) {
			case fire:
				claimMin = "1";
				break;
			case water:
				claimMin = "4001";
				break;
			case theft:
				claimMin = "2001";
				break;
			case liability:
				claimMin = "6001";
				break;
		}
		return getExpectedClaimPointsFromDB(isAAAClaim, causeOfLoss, claimMin, claimOrder);
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

	private final class Claims {
		static final int FIRE = 0;
		static final int WATER = 1;
		static final int THEFT = 2;
		static final int LIABILITY = 3;
	}

}
