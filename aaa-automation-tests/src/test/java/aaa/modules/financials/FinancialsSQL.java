package aaa.modules.financials;

import java.util.Optional;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.db.DBService;

public final class FinancialsSQL {

	private FinancialsSQL() {}

	public static String getMonthlyNetPremiumForPolicyQuery(String policyNum, String renewalCycle) {
		return String.format("select monthlyamt from POLICYSUMMARY p join PREMIUMENTRY e on p.id = e.POLICYSUMMARY_ID where policynumber = '%s' and premiumcd = 'NWT' and renewalcycle = %s", policyNum, renewalCycle);
	}

	public static String getMonthlyNetPremiumSumQuery() {
		return "select SUM(monthlyamt) from (select monthlyamt from POLICYSUMMARY p join PREMIUMENTRY e on p.id = e.POLICYSUMMARY_ID where premiumcd = 'NWT' and txtype = 'policy' and renewalcycle = 0)";
	}

	public static String getTotalEntryAmtForAcct(String account) {
		return String.format("select SUM(ENTRYAMT) from LEDGERENTRY where LEDGERACCOUNTNO = '%s'", account);
	}

    public static Dollar getDebitsForAccountByPolicyNB(String policyNumber, String account) {
        String query = String.format("select SUM(ENTRYAMT) from (select ENTRYAMT from LEDGERENTRY WHERE PRODUCTNUMBER = '%s' and TRANSACTIONTYPE = 'policy' and LEDGERACCOUNTNO = '%s' and entrytype = 'DEBIT')", policyNumber, account);
        Optional<String> value = DBService.get().getValue(query);
        return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
    }

	public static Dollar getCreditsForAccountByPolicyNB(String policyNumber, String account) {
	    String query = String.format("select SUM(ENTRYAMT) from (select ENTRYAMT from LEDGERENTRY WHERE PRODUCTNUMBER = '%s' and TRANSACTIONTYPE = 'policy' and LEDGERACCOUNTNO = '%s' and entrytype = 'CREDIT')", policyNumber, account);
        Optional<String> value = DBService.get().getValue(query);
        return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
	}

	public static Dollar getDebitsForAccountByPolicyEndorsement(String policyNumber, String account) {
		String query = String.format("select SUM(ENTRYAMT) from (select ENTRYAMT from LEDGERENTRY WHERE PRODUCTNUMBER = '%s' and TRANSACTIONTYPE = 'endorsement' and LEDGERACCOUNTNO = '%s' and entrytype = 'DEBIT')", policyNumber, account);
		Optional<String> value = DBService.get().getValue(query);
		return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
	}

	public static Dollar getCreditsForAccountByPolicyEndorsement(String policyNumber, String account) {
		String query = String.format("select SUM(ENTRYAMT) from (select ENTRYAMT from LEDGERENTRY WHERE PRODUCTNUMBER = '%s' and TRANSACTIONTYPE = 'endorsement' and LEDGERACCOUNTNO = '%s' and entrytype = 'CREDIT')", policyNumber, account);
		Optional<String> value = DBService.get().getValue(query);
		return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
	}

	public static Dollar getDebitsForAccountByPolicyManualPayment(String policyNumber, String account) {
		String query = String.format("select SUM(ENTRYAMT) from (select ENTRYAMT from LEDGERENTRY WHERE PRODUCTNUMBER = '%s' and TRANSACTIONTYPE = 'ManualPayment' and LEDGERACCOUNTNO = '%s' and entrytype = 'DEBIT')", policyNumber, account);
		Optional<String> value = DBService.get().getValue(query);
		return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
	}

	public static Dollar getCreditsForAccountByPolicyManualPayment(String policyNumber, String account) {
		String query = String.format("select SUM(ENTRYAMT) from (select ENTRYAMT from LEDGERENTRY WHERE PRODUCTNUMBER = '%s' and TRANSACTIONTYPE = 'ManualPayment' and LEDGERACCOUNTNO = '%s' and entrytype = 'CREDIT')", policyNumber, account);
		Optional<String> value = DBService.get().getValue(query);
		return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
	}

}
