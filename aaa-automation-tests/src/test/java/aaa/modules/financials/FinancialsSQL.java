package aaa.modules.financials;

import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.db.DBService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
		public static final String RENEWAL = "renewal";
		public static final String PLIGA_FEE = "PLIGAFee";
		public static final String SR22_FEE = "SR22Fee";
		public static final String MVLE_FEE = "MVLEFee";
    }

}
