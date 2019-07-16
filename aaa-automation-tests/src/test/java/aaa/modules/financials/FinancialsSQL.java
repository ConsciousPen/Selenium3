package aaa.modules.financials;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.exigen.ipb.eisa.utils.Dollar;
import toolkit.db.DBService;

public final class FinancialsSQL {

	private FinancialsSQL() {}

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
								"and TRANSACTIONTYPE like '" + txType + "%' " +
								"and LEDGERACCOUNTNO = '" + account + "' " +
								"and entrytype = 'DEBIT'";
        if (txDate != null) {
        	query += " and trunc(TXDATE) = '" + txDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy")) + "'";
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
								"and TRANSACTIONTYPE like '" + txType + "%' " +
								"and LEDGERACCOUNTNO = '" + account + "' " +
								"and entrytype = 'CREDIT'";
		if (txDate != null) {
			query += " and trunc(TXDATE) = '" + txDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy")) + "'";
		}
		query += ")";

        Optional<String> value = DBService.get().getValue(query);
        return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
	}

	public static Dollar getCreditsForAccountByTransaction(String transactionId, String txType, String account) {
		return getCreditsForAccountByTransaction(null, transactionId, txType, account);
	}

	public static Dollar getCreditsForAccountByTransaction(LocalDateTime txDate, String transactionId, String txType, String account) {
		String query =
				"select SUM(ENTRYAMT) " +
						"from (" +
						"select ENTRYAMT " +
						"from LEDGERENTRY LE " +
						"join LEDGERTRANSACTION LT on LE.ledgertransaction_id = LT.id " +
						"WHERE LT.ENTITYREF_FK = '" + transactionId + "' " +
						"and LE.TRANSACTIONTYPE like '" + txType + "%' " +
						"and LE.LEDGERACCOUNTNO = '" + account + "' " +
						"and LE.entrytype = 'CREDIT'";
		if (txDate != null) {
			query += " and trunc(TXDATE) = '" + txDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy")) + "'";
		}
		query += ")";

		Optional<String> value = DBService.get().getValue(query);
		return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
	}

	public static Dollar getDebitsForAccountByTransaction(String transactionId, String txType, String account) {
		return getDebitsForAccountByTransaction(null, transactionId, txType, account);
	}

	public static Dollar getDebitsForAccountByTransaction(LocalDateTime txDate, String transactionId, String txType, String account) {
		String query =
				"select SUM(ENTRYAMT) " +
						"from (" +
						"select ENTRYAMT " +
						"from LEDGERENTRY LE " +
						"join LEDGERTRANSACTION LT on LE.ledgertransaction_id = LT.id " +
						"WHERE LT.ENTITYREF_FK = '" + transactionId + "' " +
						"and LE.TRANSACTIONTYPE like '" + txType + "%' " +
						"and LE.LEDGERACCOUNTNO = '" + account + "' " +
						"and LE.entrytype = 'DEBIT'";
		if (txDate != null) {
			query += " and trunc(TXDATE) = '" + txDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy")) + "'";
		}
		query += ")";

		Optional<String> value = DBService.get().getValue(query);
		return value.map(Dollar::new).orElseGet(() -> new Dollar("0.00"));
	}

	public static List<String> getTransactionIdsForAccount(String accountNumber) {
		String query =
				"SELECT BT.ID FROM BILLINGTRANSACTION BT " +
						"JOIN BILLINGACCOUNT BA ON BT.ACCOUNT_ID = BA.ID " +
						"WHERE BA.ACCOUNTNUMBER = '" + accountNumber + "' " +
						"ORDER BY BT.APPLYDATE DESC ";
		List<Map<String, String>> rows = DBService.get().getRows(query);
		List<String> transactionIds = new ArrayList<>();
		for (Map<String, String> row : rows) {
			transactionIds.add(row.get("ID"));
		}
		return transactionIds;
	}

	public static final class TxType {
	    public static final String NEW_BUSINESS = "policy";
	    public static final String ENDORSEMENT = "endorsement";
        public static final String RENEWAL = "renewal";
	    public static final String MANUAL_PAYMENT = "ManualPayment";
	    public static final String CANCELLATION = "cancellation";
	    public static final String REINSTATEMENT = "reinstatement";
	    public static final String DEPOSIT_PAYMENT = "DepositPayment";
	    public static final String POLICY_FEE = "PolicyFee";
	    public static final String CA_FRAUD_ASSESSMENT_FEE = "CAFraudAssessmentFee";
	    public static final String CANCELLATION_FEE = "CancellationFee";
	    public static final String OVERPAYMENT_REALLOCATION_ADJUSTMENT = "OverPaymentReallocationAdjustment";
		public static final String SEISMIC_FEE = "SeismicFee";
		public static final String EMPLOYEE_BENEFIT = "EmployeeBenefit";
		public static final String NON_EFT_INSTALLMENT_FEE = "NonEFTInstallmentFee";
		public static final String PAYMENT_DECLINED = "PaymentDeclined";
		public static final String NSF_FEE = "NotSufficientFunds";
		public static final String NSF_FEE_WAIVED = "NSFFeeWORestriction";
		public static final String ROLL_BACK_ENDORSEMENT = "retro";
		public static final String STATE_TAX_WV = "PRMS_WV";
		public static final String STATE_TAX_KY = "PRMS_KY";
		public static final String CITY_TAX_KY = "PREMT_CITY";
		public static final String COUNTY_TAX_KY = "PREMT_COUNTY";
		public static final String PLIGA_FEE = "PLIGAFee";
		public static final String SR22_FEE = "SR22Fee";
		public static final String MVLE_FEE = "MVLEFee";
		public static final String REINSTATEMENT_FEE = "ReinstatementFee";
		public static final String RENEWAL_LAPSE_FEE = "RenewalLapseFee";
		public static final String AUTOMATED_REFUND = "AutomatedRefund";
		public static final String MANUAL_REFUND = "ManualRefund";
		public static final String REFUND_PAYMENT_VOIDED = "RefundPaymentVoided";
		public static final String ESCHEATMENT = "Escheatment";
		public static final String SMALL_BALANCE_WRITEOFF = "SmallBalanceWriteoff";
		public static final String WRITEOFF = "Writeoff";
		public static final String CROSS_POLICY_TRANSFER = "CrossPolicyTransfer";
		public static final String ACCOUNT_MONEY_TRANSFER = "AccountMoneyTransfer";
		public static final String EARNED_PREMIUM_WRITE_OFF = "EarnedPremiumWriteoff";
    }

}
