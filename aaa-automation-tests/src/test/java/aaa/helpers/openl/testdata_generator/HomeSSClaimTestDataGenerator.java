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
	private String state;
	private String sqlMaxValue = "";
	private String sqlClaimData = "";
	private String sqlClaimDataRiskState = "";
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
		String lookupName = isAAAClaim ? AAA_CLAIM_POINT : NOT_AAA_CLAIM_POINT;
		int claimPoints = isAAAClaim ? openLPolicy.getPolicyLossInformation().getExpClaimPoint() : openLPolicy.getPolicyLossInformation().getPriorClaimPoint();
		String sqlMaxCode = String.format("select max(code) from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and riskstatecd is null", lookupName);

		sqlMaxValue = String.format("select max(displayvalue) from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and riskstatecd is null", lookupName) + " and code=%d";
		sqlClaimData = String.format("select causeOfLoss, minPremiumOvr from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and riskstatecd is null", lookupName) + " and code=%d and displayvalue=%d";
		sqlClaimDataRiskState = String.format("select causeOfLoss, minPremiumOvr from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and riskstatecd='%s'", lookupName, state) + " and code=%d";
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
		int maxValue = Integer.parseInt(DBService.get().getValue(String.format(sqlMaxValue, code)).get());
		if (claimPoints < maxValue) {
			row = getRow(code, claimPoints);
			if (row.isEmpty()) {
				row = getRow(code, 1);
				claimList.add(getClaim(row));
				if (--claimPoints > 0) {
					claimList.addAll(getClaimList(claimPoints, code < maxCode ? ++code : code));
				}
			} else {
				claimList.add(getClaim(row));
			}
		} else {
			row = getRow(code, maxValue);
			claimList.add(getClaim(row));
			if (claimPoints > maxValue) {
				claimPoints -= maxValue;
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

	private Map<String, String> getRow(int code, int claimPoints) {
		List<Map<String, String>> rows = DBService.get().getRows(String.format(sqlClaimData, code, claimPoints));
		if (rows.isEmpty()) {
			return null;
		}
		List<Map<String, String>> rowsRiskState = DBService.get().getRows(String.format(sqlClaimDataRiskState, code));
		if (!rowsRiskState.isEmpty()) {
			for (Map<String, String> r : rows) {
				if (!rowsRiskState.contains(r)) {
					return r;
				}
			}
		}
		return rows.get(0);
	}
}