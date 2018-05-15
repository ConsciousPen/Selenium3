package aaa.helpers.openl.testdata_builder;

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
	private String lookupName;
	private String state;
	private String sqlMaxCode = "select max(code) from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and (riskstatecd is null or riskstatecd = '%s')";
	private String sqlMaxValue = "select max(displayvalue) from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and (riskstatecd is null or riskstatecd = '%s') and code = %d";
	private String sqlClaimData = "select causeOfLoss, minPremiumOvr from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and (riskstatecd is null or riskstatecd = '%s') and code = %d and displayvalue = %d";
	private HomeSSOpenLPolicy openLPolicy;
	private LocalDate dateOfLoss;
	private int maxCode;
	private boolean isAAAClaim;

	public HomeSSClaimTestDataGenerator(HomeSSOpenLPolicy openLPolicy) {
		this.openLPolicy = openLPolicy;
		state = openLPolicy.getPolicyAddress().getState();
	}

	public List<TestData> getClaimTestData(boolean isAAAClaim, boolean isFirstClaim) {
		this.isAAAClaim = isAAAClaim;
		lookupName = isAAAClaim ? AAA_CLAIM_POINT : NOT_AAA_CLAIM_POINT;
		dateOfLoss = openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyLossInformation().getRecentYCF());
		int claimPoints = isAAAClaim ? openLPolicy.getPolicyLossInformation().getExpClaimPoint() : openLPolicy.getPolicyLossInformation().getPriorClaimPoint();
		maxCode = Integer.parseInt(DBService.get().getValue(String.format(sqlMaxCode, lookupName, state)).get());
		List<TestData> claimList = getClaimList(claimPoints, 1);
		if (isFirstClaim){
			claimList.get(0).adjust(DataProviderFactory.dataOf(HomeSSMetaData.PropertyInfoTab.ClaimHistory.ADD_A_CLAIM.getLabel(), "Yes"));
		}
		return claimList;
	}

	private List<TestData> getClaimList(int claimPoints, int code) {
		List<TestData> claimList = new ArrayList<>();
		Map<String, String> row;
		String dateOfLoss = openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyLossInformation().getRecentYCF()).minusDays(code).format(DateTimeUtils.MM_DD_YYYY);
		int value = Integer.parseInt(DBService.get().getValue(String.format(sqlMaxValue, lookupName, state, code)).get());
		if (claimPoints < value) {
			row = DBService.get().getRow(String.format(sqlClaimData, lookupName, state, code, claimPoints));
			if (row.isEmpty()) {
				claimList.add(getClaim(DBService.get().getRow(String.format(sqlClaimData, lookupName, state, code, 1))));
				claimList.addAll(getClaimList(--claimPoints, code < maxCode ? ++code : code));
			} else {
				claimList.add(getClaim(row));
			}
		} else {
			row = DBService.get().getRow(String.format(sqlClaimData, lookupName, state, code, value));
			claimList.add(getClaim(row));
			if (claimPoints > value) {
				claimPoints -= value;
				claimList.addAll(getClaimList(claimPoints, code < maxCode ? ++code : code));
			}
		}
		return claimList;
	}

	private TestData getClaim(Map<String, String> row) {
		dateOfLoss = dateOfLoss.minusDays(1);
		return DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel(), dateOfLoss.format(DateTimeUtils.MM_DD_YYYY),
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.CAUSE_OF_LOSS.getLabel(), row.get("CAUSEOFLOSS"),
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel(), row.get("MINPREMIUMOVR"),
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS.getLabel(), "Closed",
				HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM.getLabel(), isAAAClaim ? "Yes" : "No"
		);
	}
}
