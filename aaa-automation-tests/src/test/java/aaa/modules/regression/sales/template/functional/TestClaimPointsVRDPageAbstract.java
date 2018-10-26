package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import aaa.common.Tab;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

public abstract class TestClaimPointsVRDPageAbstract extends PolicyBaseTest {
	
	protected abstract Tab getPropertyInfoTab();
	protected abstract Tab getPremiumAndCoveragesQuoteTab();
	protected abstract Table getClaimHistoryTable();
	protected abstract String getClaimHistoryLabel();
	protected abstract TextBox getClaimDateOfLossAsset();
	protected abstract RadioGroup getClaimCatastropheAsset();
	protected abstract RadioGroup getAAAClaimAsset();
	protected abstract TextBox getClaimAmountAsset();
	protected abstract void calculatePremiumAndOpenVRD();
	protected abstract void navigateToPropertyInfoTab();

	protected void testClaimsPointsVRDPage() {

		// Initialize test data, create quote, and fill to P & C tab
		List<TestData> tdClaims = getClaimsTD();
		TestData td = getPolicyTD().adjust(TestData.makeKeyPath(getPropertyInfoTab().getClass().getSimpleName(), getClaimHistoryLabel()), tdClaims);
		createQuoteAndFillUpTo(td, getPremiumAndCoveragesQuoteTab().getClass());

		// Validate claims on VRD page, CA has different rules for when first claim is under $1000
		PropertyQuoteTab.RatingDetailsView.open();
		TestData claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		if (isStateCA()) {
			assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.FIRE, "1"));
		} else {
			assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEmpty();
		}
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.THEFT, "2"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.LIABILITY, "3"));

		// Update Liability claim so it is over 3 years old (over 5 years for DP3), update Water claim to catastrophe = 'No'
		navigateToPropertyInfoTab();
		viewEditClaimByCauseOfLoss(Labels.LIABILITY);
		String newLiabilityLossDate;
		if (getPolicyType().equals(PolicyType.HOME_CA_DP3)) {
			newLiabilityLossDate = getPropertyInfoTab().getEffectiveDate().minusMonths(61).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		} else {
			newLiabilityLossDate = getPropertyInfoTab().getEffectiveDate().minusMonths(37).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		}
		getClaimDateOfLossAsset().setValue(newLiabilityLossDate);
		viewEditClaimByCauseOfLoss(Labels.WATER);
		getClaimCatastropheAsset().setValue("No");

		// Validate VRD page
		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(newLiabilityLossDate);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEmpty();
		if (isStateCA()) {
			assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.FIRE, "1"));
		} else {
			assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEmpty();
		}
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.WATER, "2"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.THEFT, "3"));

		// Update Liability claim to 2 years ago so it is now included in rating as oldest claim
		navigateToPropertyInfoTab();
		viewEditClaimByCauseOfLoss(Labels.LIABILITY);
		newLiabilityLossDate = getPropertyInfoTab().getEffectiveDate().minusYears(2).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		getClaimDateOfLossAsset().setValue(newLiabilityLossDate);

		// Validate VRD page
		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(newLiabilityLossDate);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.LIABILITY, "1"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.FIRE, "2"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.WATER, "3"));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.THEFT, "3"));

		// Update Liability claim back to original date of loss and set all claims to non-AAA
		navigateToPropertyInfoTab();
		viewEditClaimByCauseOfLoss(Labels.LIABILITY);
		getClaimDateOfLossAsset().setValue(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		getAAAClaimAsset().setValue("No");
		viewEditClaimByCauseOfLoss(Labels.FIRE);
		getAAAClaimAsset().setValue("No");
		viewEditClaimByCauseOfLoss(Labels.WATER);
		getAAAClaimAsset().setValue("No");
		viewEditClaimByCauseOfLoss(Labels.THEFT);
		getAAAClaimAsset().setValue("No");

		// Validate VRD page
		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.FIRE, "1"));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.WATER, "2"));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_3).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.THEFT, "3"));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_4).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.LIABILITY, "3"));

		// Set Fire and Water claims back to AAA claims, change theft claim to a catastrophe loss, and update value of Fire claim to $2001
		navigateToPropertyInfoTab();
		viewEditClaimByCauseOfLoss(Labels.FIRE);
		getAAAClaimAsset().setValue("Yes");
		if (isStateCA()) {
			getClaimAmountAsset().setValue("2001");
		} else {
			getClaimAmountAsset().setValue("1001");
		}
		viewEditClaimByCauseOfLoss(Labels.WATER);
		getAAAClaimAsset().setValue("Yes");
		viewEditClaimByCauseOfLoss(Labels.THEFT);
		getClaimCatastropheAsset().setValue("Yes");

		// Validate VRD page
		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEmpty();
		assertThat(claimsVRD.getTestData(Labels.PRIOR_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, Labels.LIABILITY, "1"));

		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		if (isStateCA()) {
			assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.FIRE, "2001", "1"));
		} else {
			assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_1).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.FIRE, "1001", "1"));
		}
		assertThat(claimsVRD.getTestData(Labels.AAA_CLAIMS).getTestData(Labels.CLAIM_2).getValue(Labels.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, Labels.WATER, "2"));
		PropertyQuoteTab.RatingDetailsView.close();

	}

	protected List<TestData> getClaimsTD() {
		List<TestData> tdList = testDataManager.getDefault(TestClaimPointsVRDPageAbstract.class).getTestDataList("PropertyInfo_Claims");
		if (getPolicyType().equals(PolicyType.HOME_CA_DP3)) {
			for (TestData td : tdList) {
				td.adjust(HomeCaMetaData.PropertyInfoTab.ClaimHistory.ZIP.getLabel(), "90255")
						.adjust(HomeCaMetaData.PropertyInfoTab.ClaimHistory.ADDRESS_LINE_1.getLabel(), "6586 Porcupine Way")
						.adjust(HomeCaMetaData.PropertyInfoTab.ClaimHistory.POLICY_NUMBER.getLabel(), "123456789")
						.adjust(HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM.getLabel(), "Yes");
			}
		}
		return tdList;
	}

	void viewEditClaimByCauseOfLoss(String claimType) {
		getClaimHistoryTable().getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.CAUSE_OF_LOSS, claimType)
				.getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
	}

	void viewEditClaimByLossAmount(String lossAmount) {
		getClaimHistoryTable().getRowContains(PolicyConstants.PropertyInfoClaimHistoryTable.AMOUNT_OF_LOSS, lossAmount)
				.getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
	}

	private String getExpectedClaimPointsFromDB(boolean isAAAClaim, String causeOfLoss, String claimOrder) {
		String claimMin = "";
		switch (causeOfLoss) {
			case Labels.FIRE:
				claimMin = "1";
				break;
			case Labels.WATER:
				if (isStateCA()) {
					claimMin = "2001";
				} else {
					claimMin = "4001";
				}
				break;
			case Labels.THEFT:
				claimMin = "2001";
				break;
			case Labels.LIABILITY:
				if (isStateCA()) {
					claimMin = "2001";
				} else {
					claimMin = "6001";
				}
				break;
		}
		return getExpectedClaimPointsFromDB(isAAAClaim, causeOfLoss, claimMin, claimOrder);
	}

	private String getExpectedClaimPointsFromDB(boolean isAAAClaim, String causeOfLoss, String claimMin, String claimOrder) {
		String lookupName;
		String query;
		Optional<String> result;
		String policyTypeCd = getPolicyType().getShortName().substring(getPolicyType().getShortName().length() - 3);
		if ("2001".equals(claimMin) && getPolicyType().equals(PolicyType.HOME_CA_DP3)) {
			claimMin = "1001";
		}
		if (isStateCA()) {
			if (isAAAClaim) {
				lookupName = "AAAExperienceClaimPoint";
			} else {
				lookupName = "AAAPriorClaimPoint";
			}
			query = String.format("select points from (select DTYPE,CAUSEOFLOSS,MINPREMIUMOVR,MAXPREMIUMOVR,PRODUCTCD,POLICYTYPECD,RISKSTATECD,CODE as claimorder,DISPLAYVALUE as points from lookupvalue "
					+ "where LOOKUPLIST_ID in (select id from LOOKUPLIST where lookupname = '%s')) "
					+ "where CAUSEOFLOSS = '" + causeOfLoss + "' and MINPREMIUMOVR = '" + claimMin + "' and claimorder = '" + claimOrder + "' and POLICYTYPECD = '" + policyTypeCd + "'", lookupName);
			result = DBService.get().getValue(query);
		} else {
			if (isAAAClaim) {
				lookupName = "AAAHOExperienceClaimPoint";
			} else {
				lookupName = "AAAHOPriorClaimPoint";
			}
			query = String.format("select points from (select DTYPE,CAUSEOFLOSS,MINPREMIUMOVR,MAXPREMIUMOVR,PRODUCTCD,POLICYTYPECD,RISKSTATECD,CODE as claimorder,DISPLAYVALUE as points from lookupvalue "
					+ "where LOOKUPLIST_ID in (select id from LOOKUPLIST where lookupname = '%s') and riskstatecd = '" + getState() + "') "
					+ "where CAUSEOFLOSS = '" + causeOfLoss + "' and MINPREMIUMOVR = '" + claimMin + "' and claimorder = '" + claimOrder + "'", lookupName);
			String queryNoState = String.format("select points from (select DTYPE,CAUSEOFLOSS,MINPREMIUMOVR,MAXPREMIUMOVR,PRODUCTCD,POLICYTYPECD,RISKSTATECD,CODE as claimorder,DISPLAYVALUE as points from lookupvalue "
					+ "where LOOKUPLIST_ID in (select id from LOOKUPLIST where lookupname = '%s') and riskstatecd is null) "
					+ "where CAUSEOFLOSS = '" + causeOfLoss + "' and MINPREMIUMOVR = '" + claimMin + "' and claimorder = '" + claimOrder + "'", lookupName);
			if (DBService.get().getValue(query).isPresent()) {
				result = DBService.get().getValue(query);
			} else {
				result = DBService.get().getValue(queryNoState);
			}
		}
		return result.get();
	}

	private final class Claims {
		static final int FIRE = 0;
		static final int WATER = 1;
		static final int THEFT = 2;
		static final int LIABILITY = 3;
	}
	
	final class Labels {
		static final String PRIOR_CLAIMS = "Prior claims";
		static final String AAA_CLAIMS = "AAA claims";
		static final String DATE = "Date";
		static final String HAIL = "Hail";
		static final String POINTS = "Points";
		static final String FIRE = "Fire";
		static final String WATER = "Water";
		static final String WIND = "Wind";
		static final String THEFT = "Theft";
		static final String LIABILITY = "Liability";
		static final String CLAIM_1 = "Claim 1";
		static final String CLAIM_2 = "Claim 2";
		static final String CLAIM_3 = "Claim 3";
		static final String CLAIM_4 = "Claim 4";
		static final String DATE_OF_LOSS = "Date of loss";
		static final String APPLICANT_PROPERTY = "Applicant and Property";
        static final String APPLICANT = "Applicant";
        static final String RADIO_YES = "Yes";
        static final String RADIO_NO = "No";
	}

}
