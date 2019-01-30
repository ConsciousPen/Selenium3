package aaa.modules.financials;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.db.DBService;

public final class FinancialsSQL {

	private FinancialsSQL() {}

	public static String getMonthlyNetPremiumForPolicyQuery(String policyNum, String renewalCycle) {
		return String.format(
				"select monthlyamt " +
						"from POLICYSUMMARY p " +
						"join PREMIUMENTRY e on p.id = e.POLICYSUMMARY_ID " +
						"where policynumber = '%s' " +
							"and premiumcd = 'NWT' " +
							"and renewalcycle = %s",
				policyNum,
				renewalCycle);
	}

	public static String getMonthlyNetPremiumSumQuery() {
		return "select SUM(monthlyamt) " +
					"from (select monthlyamt " +
					"from POLICYSUMMARY p join PREMIUMENTRY e on p.id = e.POLICYSUMMARY_ID " +
					"where premiumcd = 'NWT' " +
						"and txtype = 'policy' " +
						"and renewalcycle = 0)";
	}

	public static String getTotalEntryAmtForAcct(String account) {
		return String.format("select SUM(ENTRYAMT) from LEDGERENTRY where LEDGERACCOUNTNO = '%s'", account);
	}

	public static Dollar getDebitsForAccountByPolicy(String policyNumber, String txType, String account) {
		return getDebitsForAccountByPolicy(null, policyNumber, txType, account);
	}

    public static Dollar getDebitsForAccountByPolicy(LocalDateTime txDate, String policyNumber, String txType, String account) {
        String query =
        		"select SUM(ENTRYAMT) " +
						"from (" +
							"select ENTRYAMT " +
				            "from LEDGERENTRY " +
				            "WHERE PRODUCTNUMBER like '%" + policyNumber + "' " +
								"and TRANSACTIONTYPE = '" + txType + "' " +
								"and LEDGERACCOUNTNO = '" + account + "' " +
								"and entrytype = 'DEBIT'";
        if (txDate != null) {
        	query += " and trunc(TXDATE) = " + txDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
        }
		query += ")";
        Optional<String> value = DBService.get().getValue(query);
        return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
    }

    public static Dollar getCreditsForAccountByPolicy(String policyNumber, String txType, String account) {
		return getCreditsForAccountByPolicy(null, policyNumber, txType, account);
    }

	public static Dollar getCreditsForAccountByPolicy(LocalDateTime txDate, String policyNumber, String txType, String account) {
	    String query =
	    		"select SUM(ENTRYAMT) " +
						"from (" +
							"select ENTRYAMT " +
							"from LEDGERENTRY " +
							"WHERE PRODUCTNUMBER  like '%" + policyNumber + "' " +
								"and TRANSACTIONTYPE = '" + txType + "' " +
								"and LEDGERACCOUNTNO = '" + account + "' " +
								"and entrytype = 'CREDIT'";
		if (txDate != null) {
			query += " and trunc(TXDATE) = " + txDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
		}
		query += ")";

        Optional<String> value = DBService.get().getValue(query);
        return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
	}

	public static final class TxType {
	    public static final String NEW_BUSINESS = "policy";
	    public static final String ENDORSEMENT = "endorsement";
	    public static final String MANUAL_PAYMENT = "ManualPayment";
	    public static final String CANCELLATION = "cancellation";
	    public static final String REINSTATEMENT = "reinstatement";
	    public static final String DEPOSIT_PAYMENT = "DepositPayment";
	    public static final String POLICY_FEE = "PolicyFee";
	    public static final String CA_FRAUD_ASSESSMENT_FEE = "CAFraudAssessmentFee";
	    public static final String CANCELLATION_FEE = "CancellationFee";
	    public static final String OVERPAYMENT_REALLOCATION_ADJUSTMENT = "OverPaymentReallocationAdjustment";
    }

}
