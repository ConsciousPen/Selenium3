package aaa.helpers.product;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import toolkit.db.DBService;

public class LedgerHelper {

	private static final String GET_EARNED_MONTHLY_REPORTED_PREMIUM_TOTAL =
			"SELECT SUM(CASE WHEN ENTRYTYPE='DEBIT' THEN ENTRYAMT ELSE -ENTRYAMT END) " +
					"FROM LEDGERENTRY " +
					"WHERE PRODUCTNUMBER = '%s' " +
					"AND PERIODTYPE = 'MONTHLY' " +
					"AND LEDGERACCOUNTNO = '1015'";

	private static final String GET_MONTHLY_EARNED_PREMIUM_AMOUNTS =
			"select TXDATE, sum(case when entrytype='DEBIT' then entryamt else -entryamt end) "
					+ "from LEDGERENTRY where PRODUCTNUMBER = '%s' "
					+ "and PERIODTYPE = 'MONTHLY' "
					+ "and LEDGERACCOUNTNO = '1015' "
					+ "group by TXDATE "
					+ "ORDER BY TXDATE";

	public static String getEarnedMonthlyReportedPremiumTotal(@Nonnull String policyNumber) {
		String query = String.format(GET_EARNED_MONTHLY_REPORTED_PREMIUM_TOTAL, policyNumber);
		return DBService.get().getValue(query).get();
	}

	protected static List<Map<String, String>> getMonthlyEarnedPremiumAmounts(@Nonnull String policyNumber) {
		String query = String.format(GET_MONTHLY_EARNED_PREMIUM_AMOUNTS, policyNumber);
		return DBService.get().getRows(query);
	}
}