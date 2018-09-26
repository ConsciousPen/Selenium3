package aaa.modules.financials;

public final class FinancialsSQL {

	private FinancialsSQL() {}

	public static String getMonthlyNetPremiumForPolicyQuery(String policyNum, String renewalCycle) {
		return String.format("select monthlyamt from POLICYSUMMARY p join PREMIUMENTRY e on p.id = e.POLICYSUMMARY_ID where policynumber = '%s' and premiumcd = 'NWT' and renewalcycle = %s", policyNum, renewalCycle);
	}

	public static String getMonthlyNetPremiumSumQuery() {
		return "select SUM(monthlyamt) from (select monthlyamt from POLICYSUMMARY p join PREMIUMENTRY e on p.id = e.POLICYSUMMARY_ID where premiumcd = 'NWT' and txtype = 'policy' and renewalcycle = 0)";
	}

}
