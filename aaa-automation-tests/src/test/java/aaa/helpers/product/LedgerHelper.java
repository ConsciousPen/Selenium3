package aaa.helpers.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;

public class LedgerHelper {

	private static final String GET_EARNED_MONTHLY_REPORTED_PREMIUM_TOTAL =
			"SELECT SUM(CASE WHEN ENTRYTYPE='DEBIT' THEN ENTRYAMT ELSE -ENTRYAMT END) " +
					"FROM LEDGERENTRY " +
					"WHERE PRODUCTNUMBER = '%s' " +
					"AND PERIODTYPE = 'MONTHLY' " +
					"AND LEDGERACCOUNTNO = '1015'";

	private static final String GET_MONTHLY_EARNED_PREMIUM_AMOUNTS =
			"select to_char(TXDATE, 'MM/DD/YYYY') as TX_DATE, "
					+ "sum(case when entrytype='DEBIT' then entryamt else -entryamt end) as EARNED_PREMIUM "
					+ "from LEDGERENTRY where PRODUCTNUMBER = '%s' "
					+ "and PERIODTYPE = 'MONTHLY' "
					+ "and LEDGERACCOUNTNO = '1015' "
					+ "group by TXDATE "
					+ "ORDER BY TXDATE";

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	public static String getEarnedMonthlyReportedPremiumTotal(@Nonnull String policyNumber) {
		String query = String.format(GET_EARNED_MONTHLY_REPORTED_PREMIUM_TOTAL, policyNumber);
		return DBService.get().getValue(query).get();
	}

	public static Map<LocalDate, BigDecimal> getMonthlyEarnedPremiumAmounts(@Nonnull String policyNumber) {
		String query = String.format(GET_MONTHLY_EARNED_PREMIUM_AMOUNTS, policyNumber);
		List<Map<String, String>> epsFromDb = DBService.get().getRows(query);
		Map<LocalDate, BigDecimal> convertedEPs = new LinkedHashMap<>();
		for (Map<String, String> epFromDb : epsFromDb) {
			LocalDate txdate = LocalDate.parse(epFromDb.get("TX_DATE"), formatter);
			BigDecimal earnedPremium = toBigDecimal(epFromDb.get("EARNED_PREMIUM"));
			convertedEPs.put(txdate, earnedPremium);
		}
		return convertedEPs;
	}

	public static BigDecimal toBigDecimal(Object sum) {
		try {
			return new BigDecimal(String.valueOf(sum).replace(" ", "").replace("$", "").replace(",", "").replace("(", "-").replace(")", ""));
		} catch (NumberFormatException nfe){
			throw new IstfException(String.format("Value '%s' can't be converted to financial value", sum), nfe);
		}
	}
}