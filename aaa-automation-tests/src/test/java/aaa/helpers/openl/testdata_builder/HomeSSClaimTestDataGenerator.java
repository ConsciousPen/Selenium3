package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import toolkit.datax.TestData;
import toolkit.db.DBService;

public class HomeSSClaimTestDataGenerator {

	private final String AAA_CLAIM_POINT = "AAAHOPriorClaimPoint";
	private final String NOT_AAA_CLAIM_POINT = "AAAHOExperienceClaimPoint";
	private String lookupName;
	private String state;
	private String sqlMaxCode = "select max(code) from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and (riskstatecd is null or riskstatecd = '%s')";
	private String sqlMaxValue = "select max(displayvalue) from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and (riskstatecd is null or riskstatecd = '%s') and code = %d";
	private String sqlClaimData = "select causeOfLoss, minPremiumOvr from lookupvalue where lookuplist_id in (select id from lookuplist where lookupname='%s') and productcd='AAA_HO_SS' and (riskstatecd is null or riskstatecd = '%s') and code = %d and displayvalue = %d";
	private HomeSSOpenLPolicy openLPolicy;
	private int maxCode;

	public HomeSSClaimTestDataGenerator(HomeSSOpenLPolicy openLPolicy) {
		this.openLPolicy = openLPolicy;
		state = openLPolicy.getPolicyAddress().getState();
	}

	public TestData getClaimTestData(boolean isAAAClaim) {
		lookupName = isAAAClaim ? AAA_CLAIM_POINT : NOT_AAA_CLAIM_POINT;
		int claimPoints = isAAAClaim ? openLPolicy.getPolicyLossInformation().getPriorClaimPoint() : openLPolicy.getPolicyLossInformation().getExpClaimPoint();
		maxCode = Integer.parseInt(DBService.get().getValue(String.format(sqlMaxCode, lookupName, state)).get());
		getClaimList(claimPoints, 1);
		return null;
	}

	private List<Map<String, String>> getClaimList(int claimPoints, int code) {
		List<Map<String, String>> claimList = new ArrayList<>();
		Map<String, String> row;
		int value = Integer.parseInt(DBService.get().getValue(String.format(sqlMaxValue, lookupName, state, code)).get());
		if (claimPoints < value) {
			row = DBService.get().getRow(String.format(sqlClaimData, lookupName, state, code, claimPoints));
			if (row.isEmpty()) {
				row = DBService.get().getRow(String.format(sqlClaimData, lookupName, state, code, 1));
				claimList.add(row);
				claimList.addAll(getClaimList(claimPoints--, code < maxCode ? code++ : code));
			} else {
				claimList.add(row);
			}
		} else {
			row = DBService.get().getRow(String.format(sqlClaimData, lookupName, state, code, value));
			claimList.add(row);
			if (claimPoints > value) {
				claimPoints -= value;
				claimList.addAll(getClaimList(claimPoints, code < maxCode ? code++ : code));
			}
		}
		return null;
	}
}
