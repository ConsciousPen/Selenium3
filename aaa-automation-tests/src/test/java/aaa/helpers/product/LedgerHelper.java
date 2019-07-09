package aaa.helpers.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import com.exigen.ipb.eisa.utils.Dollar;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;

public class LedgerHelper {

	private static final String GET_EARNED_MONTHLY_REPORTED_PREMIUM_TOTAL =
			"SELECT SUM(CASE WHEN ENTRYTYPE='DEBIT' THEN ENTRYAMT ELSE -ENTRYAMT END) " +
					"FROM LEDGERENTRY " +
					"WHERE PRODUCTNUMBER = '%s' " +
					"AND PERIODTYPE = 'MONTHLY' " +
					"AND TRANSACTIONTYPE %s in ('%s') " +
					"AND LEDGERACCOUNTNO = '1015'";

	private static final String GET_MONTHLY_EARNED_PREMIUM_AMOUNTS =
			"select to_char(TXDATE, 'MM/DD/YYYY') as TX_DATE, "
					+ "sum(case when entrytype='DEBIT' then entryamt else -entryamt end) as EARNED_PREMIUM "
					+ "from LEDGERENTRY where PRODUCTNUMBER = '%s' "
					+ "and PERIODTYPE = 'MONTHLY' "
					+ "and LEDGERACCOUNTNO = '1015' "
					+ "group by TXDATE "
					+ "ORDER BY TXDATE";

	private static final String GET_TERM_AND_ACTUAL_PREMIUMS_UPDATED =
			"Select TO_CHAR(TRANSACTIONDATE, 'MM/DD/YYYY') as TRANSACTION_DATE, TO_CHAR(TRANSACTIONEFFECTIVEDATE, 'MM/DD/YYYY') as TRANSACTION_EFFECTIVE_DATE, txtype, sum(PERIODAMT) as TERM_PREMIUM, sum(PREMIUMAMT) as ACTUAL_PREMIUM from "+
				"(select p.id, p.TRANSACTIONDATE, p.TRANSACTIONEFFECTIVEDATE, p.txType, "+
				"case when pe.premiumType = 'ENDORSEMENT' then to_char(fo.formcd) else to_char(c.coverageCd) end, "+
				"pe.PREMIUMAMT, "+
				"pe.PERIODAMT, "+
				"pe.CHANGEAMT, "+
				"pe.FACTOR, "+
				"pe.MONTHLYAMT, "+
				"pe.PREMIUMCD, "+
				"pe.PREMIUMTYPE, "+
				"pe.ANNUALAMT, "+
				"pe.REMOVEDAMT "+
				"from PremiumEntry pe "+
				"left join Form fo on pe.FORM_ID = fo.id "+
				"left join Coverage c on pe.Coverage_ID = c.id "+
				"left join RiskItem ri on c.RiskItem_ID = ri.id "+
				"inner join PolicySummary p on (fo.POLICYDETAIL_ID = p.POLICYDETAIL_ID OR ri.POLICYDETAIL_ID = p.POLICYDETAIL_ID) "+
				"where p.policyNumber = '%s' "+
				"and pe.premiumtype in ('NET_PREMIUM', 'ENDORSEMENT') "+
				"and pe.PREMIUMCD='NWT') "+
				"where TXTYPE not in ('renewal')" +
				"group by TRANSACTIONDATE, TRANSACTIONEFFECTIVEDATE, txtype "+
				"order by TRANSACTIONDATE ";

	public static final String TERM_PREMIUM = "TERM_PREMIUM";
	public static final String ACTUAL_PREMIUM = "ACTUAL_PREMIUM";
	public static final String TRANSACTION_DATE = "TRANSACTION_DATE";
	public static final String TRANSACTION_EFFECTIVE_DATE = "TRANSACTION_EFFECTIVE_DATE";
	public static final String TXTYPE = "TXTYPE";

	private static final String GET_TERM_AND_ACTUAL_PREMIUMS_DESC = GET_TERM_AND_ACTUAL_PREMIUMS_UPDATED + "DESC";

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	public static String getEarnedMonthlyReportedPremiumTotal(@Nonnull String policyNumber) {
		return getEarnedMonthlyReportedPremiumTotal(policyNumber, "renewal", false);
	}

	public static String getEarnedMonthlyReportedPremiumTotal(@Nonnull String policyNumber, @Nonnull String txTypes, @Nonnull boolean includeOrExclude) {
		String inOrEx = includeOrExclude ? "" : "not";
		String query = String.format(GET_EARNED_MONTHLY_REPORTED_PREMIUM_TOTAL, policyNumber, inOrEx, txTypes);
		return DBService.get().getValue(query).get();
	}

	public static Map<LocalDate, BigDecimal> getMonthlyEarnedPremiumAmounts(@Nonnull String policyNumber) {
		String query = String.format(GET_MONTHLY_EARNED_PREMIUM_AMOUNTS, policyNumber);
		List<Map<String, String>> epsFromDb = DBService.get().getRows(query);
		Map<LocalDate, BigDecimal> convertedEPs = new LinkedHashMap<>();
		for (Map<String, String> epFromDb : epsFromDb) {
			LocalDate txdate = LocalDate.parse(epFromDb.get("TX_DATE"), DATE_TIME_FORMATTER);
			BigDecimal earnedPremium = toBigDecimal(epFromDb.get("EARNED_PREMIUM"));
			convertedEPs.put(txdate, earnedPremium);
		}
		return convertedEPs;
	}

	public static List<Map<String, String>> getTermAndActualPremiums(@Nonnull String policyNumber) {
		String query = String.format(GET_TERM_AND_ACTUAL_PREMIUMS_UPDATED, policyNumber);
		return DBService.get().getRows(query);
	}

	public static Dollar getEndingActualPremium(@Nonnull String policyNumber) {
		String query = String.format(GET_TERM_AND_ACTUAL_PREMIUMS_DESC, policyNumber);
		Map<String, String> premiums = DBService.get().getRow(query);
		return new Dollar(premiums.get(ACTUAL_PREMIUM));
	}

	public static BigDecimal toBigDecimal(Object sum) {
		try {
			return new BigDecimal(String.valueOf(sum).replace(" ", "").replace("$", "").replace(",", "").replace("(", "-").replace(")", ""));
		} catch (NumberFormatException nfe){
			throw new IstfException(String.format("Value '%s' can't be converted to financial value", sum), nfe);
		}
	}
}
