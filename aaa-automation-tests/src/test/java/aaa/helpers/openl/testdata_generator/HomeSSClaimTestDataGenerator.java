package aaa.helpers.openl.testdata_generator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;

public class HomeSSClaimTestDataGenerator {

	private final String AAA_CLAIM_POINT = "AAAHOExperienceClaimPoint";
	private final String NOT_AAA_CLAIM_POINT = "AAAHOPriorClaimPoint";
	private String lookupName = "";
	private String state;
	private String sqlMaxCode = "";
	private String sqlMaxValue = "";
	private String sqlClaimData = "";
	private String sqlNullState = "";
	private HomeSSOpenLPolicy openLPolicy;
	private LocalDate dateOfLoss;
	private int maxCode;
	private boolean isAAAClaim;

	public HomeSSClaimTestDataGenerator(HomeSSOpenLPolicy openLPolicy) {
		this.openLPolicy = openLPolicy;
		state = openLPolicy.getPolicyAddress().getState();
		dateOfLoss = openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyLossInformation().getRecentYCF());
	}

	public List<TestData> getClaimTestData(boolean isAAAClaim, boolean isFirstClaim) {
		this.isAAAClaim = isAAAClaim;
		lookupName = isAAAClaim ? AAA_CLAIM_POINT : NOT_AAA_CLAIM_POINT;
		int claimPoints = isAAAClaim ? openLPolicy.getPolicyLossInformation().getExpClaimPoint() : openLPolicy.getPolicyLossInformation().getPriorClaimPoint();

		String riskStateCd = DBService.get().getRow(String.format("select code from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and riskstatecd='%s'", lookupName, state)).isEmpty() ? " is null" : String.format("='%s'", state);

		sqlMaxCode = String.format("select max(code) from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and riskstatecd%s", lookupName, riskStateCd);
		sqlMaxValue = String.format("select max(displayvalue) from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and riskstatecd%s", lookupName, riskStateCd) + " and code=%d";
		sqlClaimData = String.format("select causeOfLoss, minPremiumOvr from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and riskstatecd%s", lookupName, riskStateCd) + " and code=%d and displayvalue=%d";
		sqlNullState = String.format("select causeOfLoss, minPremiumOvr from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and riskstatecd is null", lookupName) + " and code=%d and displayvalue=1";

		maxCode = Integer.parseInt(DBService.get().getValue(sqlMaxCode).get());
		List<TestData> claimList = getClaimList(claimPoints, 1);
		if (isFirstClaim) {
			claimList.get(0).adjust(DataProviderFactory.dataOf(HomeSSMetaData.PropertyInfoTab.ClaimHistory.ADD_A_CLAIM.getLabel(), "Yes"));
		}
		return claimList;
	}

	private List<TestData> getClaimList(int claimPoints, int code) {
		List<TestData> claimList = new ArrayList<>();
		Map<String, String> row;
		int value = Integer.parseInt(DBService.get().getValue(String.format(sqlMaxValue, code)).get());
		if (claimPoints < value) {
			row = DBService.get().getRow(String.format(sqlClaimData, code, claimPoints));
			if (row.isEmpty()) {
				row = DBService.get().getRow(String.format(sqlClaimData, code, 1));
				if (row.isEmpty()) {
					row = DBService.get().getRow(String.format(sqlNullState, code));
				}
				claimList.add(getClaim(row));
				if (--claimPoints > 0) {
					claimList.addAll(getClaimList(claimPoints, code < maxCode ? ++code : code));
				}
			} else {
				claimList.add(getClaim(row));
			}
		} else {
			row = DBService.get().getRow(String.format(sqlClaimData, code, value));
			claimList.add(getClaim(row));
			if (claimPoints > value) {
				claimPoints -= value;
				if (claimPoints > 0) {
					claimList.addAll(getClaimList(claimPoints, code < maxCode ? ++code : code));
				}
			}
		}
		return claimList;
	}

	private TestData getClaim(Map<String, String> row) {
		return DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel(), dateOfLoss.format(DateTimeUtils.MM_DD_YYYY),
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.CAUSE_OF_LOSS.getLabel(), row.get("CAUSEOFLOSS"),
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel(), String.format("%d", Integer.parseInt(row.get("MINPREMIUMOVR")) + 1),
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS.getLabel(), "Closed",
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM.getLabel(), isAAAClaim ? "Yes" : "No"
		);
	}
}
