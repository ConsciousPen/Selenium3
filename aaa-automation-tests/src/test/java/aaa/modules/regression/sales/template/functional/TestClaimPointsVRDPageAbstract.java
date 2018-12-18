package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.main.enums.ClaimConstants;
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
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_4).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		if (isStateCA()) {
			assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.FIRE, "1"));
		} else {
			assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEmpty();
		}
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEmpty();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.THEFT, "2"));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_4).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.LIABILITY, "3"));

		// Update Liability claim so it is over 3 years old (over 5 years for DP3), update Water claim to catastrophe = 'No'
		navigateToPropertyInfoTab();
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.LIABILITY);
		String newLiabilityLossDate;
		if (getPolicyType().equals(PolicyType.HOME_CA_DP3)) {
			newLiabilityLossDate = getPropertyInfoTab().getEffectiveDate().minusMonths(61).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		} else {
			newLiabilityLossDate = getPropertyInfoTab().getEffectiveDate().minusMonths(37).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		}
		getClaimDateOfLossAsset().setValue(newLiabilityLossDate);
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.WATER);
		getClaimCatastropheAsset().setValue("No");

		// Validate VRD page
		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(newLiabilityLossDate);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_4).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEmpty();
		if (isStateCA()) {
			assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.FIRE, "1"));
		} else {
			assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEmpty();
		}
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.WATER, "2"));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_4).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.THEFT, "3"));

		// Update Liability claim to 2 years ago so it is now included in rating as oldest claim
		navigateToPropertyInfoTab();
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.LIABILITY);
		newLiabilityLossDate = getPropertyInfoTab().getEffectiveDate().minusYears(2).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		getClaimDateOfLossAsset().setValue(newLiabilityLossDate);

		// Validate VRD page
		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(newLiabilityLossDate);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_4).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.LIABILITY, "1"));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.FIRE, "2"));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.WATER, "3"));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_4).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.THEFT, "3"));

		// Update Liability claim back to original date of loss and set all claims to non-AAA
		navigateToPropertyInfoTab();
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.LIABILITY);
		getClaimDateOfLossAsset().setValue(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		getAAAClaimAsset().setValue("No");
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.FIRE);
		getAAAClaimAsset().setValue("No");
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.WATER);
		getAAAClaimAsset().setValue("No");
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.THEFT);
		getAAAClaimAsset().setValue("No");

		// Validate VRD page
		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getKeys()).isEmpty();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getKeys().size()).isEqualTo(4);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_4).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, ClaimConstants.CauseOfLoss.FIRE, "1"));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, ClaimConstants.CauseOfLoss.WATER, "2"));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_3).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, ClaimConstants.CauseOfLoss.THEFT, "3"));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_4).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, ClaimConstants.CauseOfLoss.LIABILITY, "3"));

		// Set Fire and Water claims back to AAA claims, change theft claim to a catastrophe loss, and update value of Fire claim to $2001
		navigateToPropertyInfoTab();
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.FIRE);
		getAAAClaimAsset().setValue("Yes");
		if (isStateCA()) {
			getClaimAmountAsset().setValue("2001");
		} else {
			getClaimAmountAsset().setValue("1001");
		}
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.WATER);
		getAAAClaimAsset().setValue("Yes");
		viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.THEFT);
		getClaimCatastropheAsset().setValue("Yes");

		// Validate VRD page
		calculatePremiumAndOpenVRD();
		claimsVRD = PropertyQuoteTab.RatingDetailsView.getClaims();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.THEFT).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.LIABILITY).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEmpty();
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.PRIOR_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(false, ClaimConstants.CauseOfLoss.LIABILITY, "1"));

		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getKeys().size()).isEqualTo(2);
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.FIRE).getValue(Labels.DATE_OF_LOSS));
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.DATE)).isEqualTo(tdClaims.get(Claims.WATER).getValue(Labels.DATE_OF_LOSS));
		if (isStateCA()) {
			assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.FIRE, "2001", "1"));
		} else {
			assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_1).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.FIRE, "1001", "1"));
		}
		assertThat(claimsVRD.getTestData(ClaimConstants.ClaimsRatingDetails.AAA_CLAIMS).getTestData(ClaimConstants.ClaimsRatingDetails.CLAIM_2).getValue(ClaimConstants.ClaimsRatingDetails.POINTS)).isEqualTo(getExpectedClaimPointsFromDB(true, ClaimConstants.CauseOfLoss.WATER, "2"));

		//PAS-6730 assert 5 years rule for Years Claim Free only for SS
		if (!isStateCA() && !getState().contains(Constants.States.OK)) {
			navigateToPropertyInfoTab();
			// Change Claim Dates to within or over 5 years
			String newLossDateWithin3Years = getPropertyInfoTab().getEffectiveDate().minusMonths(35).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
			String newLossDateWithin4Years = getPropertyInfoTab().getEffectiveDate().minusMonths(47).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
			String newLossDateWithin5Years = getPropertyInfoTab().getEffectiveDate().minusMonths(59).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
			String newLossDateOver5Years = getPropertyInfoTab().getEffectiveDate().minusMonths(61).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

			//Over 5 years YCF = 5
			viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.FIRE);
			getClaimDateOfLossAsset().setValue(newLossDateOver5Years);
			viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.WATER);
			getClaimDateOfLossAsset().setValue(newLossDateOver5Years);
			viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.THEFT);
			getClaimDateOfLossAsset().setValue(newLossDateOver5Years);
			viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.LIABILITY);
			getClaimDateOfLossAsset().setValue(newLossDateOver5Years);
			calculatePremiumAndOpenVRD();
			assertThat(PropertyQuoteTab.RatingDetailsView.discounts.getValueByKey(ClaimConstants.ClaimsRatingDetails.NUMBER_OF_YEARS_CLAIMS_FREE)).contains("5");
			// Within 5 years YCF=4
			navigateToPropertyInfoTab();
			viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.FIRE);
			getClaimDateOfLossAsset().setValue(newLossDateWithin5Years);
			calculatePremiumAndOpenVRD();
			assertThat(PropertyQuoteTab.RatingDetailsView.discounts.getValueByKey(ClaimConstants.ClaimsRatingDetails.NUMBER_OF_YEARS_CLAIMS_FREE)).contains("4");
			// Within 4 years YCF=3
			navigateToPropertyInfoTab();
			viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.FIRE);
			getClaimDateOfLossAsset().setValue(newLossDateWithin4Years);
			calculatePremiumAndOpenVRD();
			assertThat(PropertyQuoteTab.RatingDetailsView.discounts.getValueByKey(ClaimConstants.ClaimsRatingDetails.NUMBER_OF_YEARS_CLAIMS_FREE)).contains("3");
			// Within 3 years YCF=2
			navigateToPropertyInfoTab();
			viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.FIRE);
			getClaimDateOfLossAsset().setValue(newLossDateWithin3Years);
			calculatePremiumAndOpenVRD();
			assertThat(PropertyQuoteTab.RatingDetailsView.discounts.getValueByKey(ClaimConstants.ClaimsRatingDetails.NUMBER_OF_YEARS_CLAIMS_FREE)).contains("2");
		}
	}

	//TODO Change some claims statuses to 'Open' and 'Subrogated' in mock data. The test should work the same PAS-6730
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
		getClaimHistoryTable().getRow(PolicyConstants.PropertyInfoClaimHistoryTable.AMOUNT_OF_LOSS, lossAmount)
				.getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
	}

	private String getExpectedClaimPointsFromDB(boolean isAAAClaim, String causeOfLoss, String claimOrder) {
		String claimMin = "";
		switch (causeOfLoss) {
			case ClaimConstants.CauseOfLoss.FIRE:
				claimMin = "1";
				break;
			case ClaimConstants.CauseOfLoss.WATER:
				if (isStateCA()) {
					claimMin = "2001";
				} else {
					claimMin = "4001";
				}
				break;
			case ClaimConstants.CauseOfLoss.THEFT:
				claimMin = "2001";
				break;
			case ClaimConstants.CauseOfLoss.LIABILITY:
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
					+ "where CAUSEOFLOSS = '" + causeOfLoss
					+ "' and MINPREMIUMOVR = '" + claimMin
					+ "' and claimorder = '" + claimOrder
					+ "' and POLICYTYPECD = '" + policyTypeCd
					+ "'", lookupName);

			result = DBService.get().getValue(query);
		} else {
			if (isAAAClaim) {
				lookupName = "AAAHOExperienceClaimPoint";
			} else {
				lookupName = "AAAHOPriorClaimPoint";
			}
			query = String.format("select points from (select DTYPE,CAUSEOFLOSS,MINPREMIUMOVR,MAXPREMIUMOVR,PRODUCTCD,POLICYTYPECD,RISKSTATECD,CODE as claimorder,DISPLAYVALUE as points from lookupvalue "
					+ "where LOOKUPLIST_ID in (select id from LOOKUPLIST where lookupname = '%s') and riskstatecd = '" + getState() + "') "
					+ "where CAUSEOFLOSS = '" + causeOfLoss
					+ "' and MINPREMIUMOVR = '" + claimMin
					+ "' and claimorder = '" + claimOrder
					+ "'", lookupName);

			String queryNoState = String.format("select points from (select DTYPE,CAUSEOFLOSS,MINPREMIUMOVR,MAXPREMIUMOVR,PRODUCTCD,POLICYTYPECD,RISKSTATECD,CODE as claimorder,DISPLAYVALUE as points from lookupvalue "
					+ "where LOOKUPLIST_ID in (select id from LOOKUPLIST where lookupname = '%s') and riskstatecd is null) "
					+ "where CAUSEOFLOSS = '" + causeOfLoss
					+ "' and MINPREMIUMOVR = '" + claimMin
					+ "' and claimorder = '" + claimOrder
					+ "'", lookupName);

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
		static final String DATE_OF_LOSS = "Date of loss";
        static final String RADIO_YES = "Yes";
        static final String RADIO_NO = "No";
	}

}