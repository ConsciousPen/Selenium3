package aaa.helpers.product;

import toolkit.db.DBService;

import javax.annotation.Nonnull;

public class LedgerHelper {

	private static final String GET_EARNED_MONTHLY_REPORTED_PREMIUM_TOTAL =
			"SELECT SUM(CASE WHEN ENTRYTYPE='DEBIT' THEN ENTRYAMT ELSE -ENTRYAMT END) " +
			"FROM LEDGERENTRY " +
			"WHERE PRODUCTNUMBER = '%s' " +
			"AND PERIODTYPE = 'MONTHLY' " +
			"AND LEDGERACCOUNTNO = '1015';";

	public static String getEarnedMonthlyReportedPremiumTotal(@Nonnull String policyNumber) {
		String query = String.format(GET_EARNED_MONTHLY_REPORTED_PREMIUM_TOTAL, policyNumber);
		return DBService.get().getValue(query).get();
	}
}