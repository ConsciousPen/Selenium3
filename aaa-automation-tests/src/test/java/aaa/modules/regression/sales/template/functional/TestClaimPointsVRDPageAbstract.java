package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import aaa.common.Tab;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.table.Table;

public abstract class TestClaimPointsVRDPageAbstract extends PolicyBaseTest {
	
	protected abstract Tab getPropertyInfoTab();
	protected abstract Table getClaimHistoryTable();
	protected abstract MultiInstanceAfterAssetList getClaimHistoryAssetList();
	protected abstract String getClaimHistoryLabel();
	protected abstract AssetDescriptor<TextBox> getClaimDateOfLossAsset();
	protected abstract AssetDescriptor<RadioGroup> getClaimCatastropheAsset();
	protected abstract AssetDescriptor<RadioGroup> getAAAClaimAsset();
	protected abstract AssetDescriptor<TextBox> getClaimAmountAsset();
	protected abstract void calculatePremiumAndOpenVRD();
	protected abstract void navigateToPropertyInfoTab();

	protected void testClaimsPointsVRDPage() {

		List<TestData> tdClaims = getClaimsTD();

		PropertyQuoteTab.RatingDetailsView.open();
		TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.THEFT, "2"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.LIABILITY, "3"));

		navigateToPropertyInfoTab();
		viewEditClaim(Labels.LIABILITY);
		getClaimHistoryAssetList().getAsset(getClaimDateOfLossAsset()).setValue(getPropertyInfoTab().getEffectiveDate().minusMonths(37).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		viewEditClaim(Labels.WATER);
		getClaimHistoryAssetList().getAsset(getClaimCatastropheAsset()).setValue("No");

		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.WATER, "2"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.THEFT, "3"));

		navigateToPropertyInfoTab();
		viewEditClaim(Labels.LIABILITY);
		String newLiabilityLossDate = getPropertyInfoTab().getEffectiveDate().minusYears(2).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		getClaimHistoryAssetList().getAsset(getClaimDateOfLossAsset()).setValue(newLiabilityLossDate);

		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(newLiabilityLossDate);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.LIABILITY, "1"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.FIRE, "2"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.WATER, "3"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.THEFT, "3"));

		navigateToPropertyInfoTab();
		viewEditClaim(Labels.LIABILITY);
		getClaimHistoryAssetList().getAsset(getClaimDateOfLossAsset()).setValue(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		getClaimHistoryAssetList().getAsset(getAAAClaimAsset()).setValue("No");
		viewEditClaim(Labels.FIRE);
		getClaimHistoryAssetList().getAsset(getAAAClaimAsset()).setValue("No");
		viewEditClaim(Labels.WATER);
		getClaimHistoryAssetList().getAsset(getAAAClaimAsset()).setValue("No");
		viewEditClaim(Labels.THEFT);
		getClaimHistoryAssetList().getAsset(getAAAClaimAsset()).setValue("No");

		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.FIRE, "1"));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.WATER, "2"));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.THEFT, "3"));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.LIABILITY, "3"));

		navigateToPropertyInfoTab();
		viewEditClaim(Labels.FIRE);
		getClaimHistoryAssetList().getAsset(getAAAClaimAsset()).setValue("Yes");
		getClaimHistoryAssetList().getAsset(getClaimAmountAsset()).setValue("1001");
		viewEditClaim(Labels.WATER);
		getClaimHistoryAssetList().getAsset(getAAAClaimAsset()).setValue("Yes");
		viewEditClaim(Labels.THEFT);
		getClaimHistoryAssetList().getAsset(getClaimCatastropheAsset()).setValue("Yes");

		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys().size()).isEqualTo(1);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.FIRE, "1"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.FIRE, "1"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.WATER, "2"));
		PropertyQuoteTab.RatingDetailsView.close();

	}

	protected TestData adjustTdWithClaims(TestData tdPolicy) {
		return tdPolicy.adjust(TestData.makeKeyPath(getPropertyInfoTab().getClass().getSimpleName(), getClaimHistoryLabel()), getClaimsTD());
	}

	private List<TestData> getClaimsTD() {
		return testDataManager.getDefault(TestClaimPointsVRDPageAbstract.class).getTestDataList("PropertyInfo_Claims");
	}

	private void viewEditClaim(String claimType) {
		getClaimHistoryTable().getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, claimType)
				.getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
	}

	private String getExpectedClaimPointsFromDB(boolean isAAAClaim, String causeOfLoss, String claimOrder) {
		String claimMin = "";
		switch (causeOfLoss) {
			case Labels.FIRE:
				claimMin = "1";
				break;
			case Labels.WATER:
				claimMin = "4001";
				break;
			case Labels.THEFT:
				claimMin = "2001";
				break;
			case Labels.LIABILITY:
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
	
	private final class Labels {
		static final String PRIOR_CLAIMS = "Prior claims";
		static final String AAA_CLAIMS = "AAA claims";
		static final String DATE = "Date";
		static final String POINTS = "Points";
		static final String FIRE = "Fire";
		static final String WATER = "Water";
		static final String THEFT = "Theft";
		static final String LIABILITY = "Liability";
		static final String CLAIM_1 = "Claim 1";
		static final String CLAIM_2 = "Claim 2";
		static final String CLAIM_3 = "Claim 3";
		static final String CLAIM_4 = "Claim 4";
		static final String DATE_OF_LOSS = "Date of loss";
	}

}
