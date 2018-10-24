package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.verification.CustomSoftAssertions;

public class DeclinePaymentTest extends BackwardCompatibilityBaseTest {
	private BillingAccountPoliciesVerifier billingAccountPoliciesVerifier = new BillingAccountPoliciesVerifier();

	/**
	 * @author Deloite
	 * @name Decline Payment with Fee and Restriction
	 * @scenario
	 * 1. Retrieve a Policy and navigate to billing page
	 * 2. Make a payment of $100 and decline the payment by clicking on decline payment Link
	 * 3. Validate if Check payment  be returned from bank is NSF by rejecting the payment
	 * that was made through check by declining in payments & Transactions section with the reason : "Fee+ No Restriction "
	 *
	 * Check:
	 * 1. Payment should be declined by selecting "Fee+ No Restriction " decline reason
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {UT, NJ, PA})
	public void BCT_ONL_076_Decline_Payment(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);
		//TODO Test moved from Deloite's code as is, probably some additional steps should be added
		SearchPage.openBilling(policyNumber);
		billingAccountPoliciesVerifier.setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);

		Dollar initialMinDue = BillingSummaryPage.getMinimumDue();
		Dollar initialTotalDue = BillingSummaryPage.getTotalDue();
		Dollar feeAmount = new Dollar(10);

		billingAccount.otherTransactions().perform(getTestSpecificTD("OtherTransaction_076"), feeAmount);

		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NSF_FEE__WITHOUT_RESTRICTION)
				.setAmount(feeAmount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();

		BillingSummaryPage.getMinimumDue().verify.equals(initialMinDue);
		BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue.add(feeAmount));
	}

	/**
	 * @author Deloite
	 * @name Decline-Fee+ No Restriction
	 * @scenario
	 * 1. Retrieve a Policy and navigate to billing page
	 * 2. Make a payment of $100 and decline the payment by clicking on decline payment Link
	 * 3. Validate if Check payment  be returned from bank is NSF by rejecting the payment
	 * that was made through check by declining in payments & Transactions section with the reason : "Fee+ No Restriction "
	 *
	 * Check:
	 * 1. Payment should be declined by selecting "Fee+ No Restriction " decline reason
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = AZ)
	public void BCT_ONL_120_Payments(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openBilling(policyNumber);
		billingAccountPoliciesVerifier.setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);

		Dollar initialTotalDue = BillingSummaryPage.getTotalDue();
		Dollar initialTotalPaid = BillingSummaryPage.getTotalPaid();
		Dollar amount = new Dollar(100);
		Dollar feeAmount = new Dollar(20);

		billingAccount.acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Check"), amount);
		billingAccount.declinePayment().perform(testDataManager.billingAccount.getTestData("DeclinePayment", "TestData_FeeNoRestriction"), "(" + amount + ")");

		CustomSoftAssertions.assertSoftly(softly -> {
			new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
					.setAmount(amount.negate()).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.DECLINED)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
					.setReason(BillingConstants.PaymentsAndOtherTransactionReason.FEE_NO_RESTRICTION).verifyPresent();
			new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED)
					.setAmount(amount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
					.setReason(BillingConstants.PaymentsAndOtherTransactionReason.FEE_NO_RESTRICTION).verifyPresent();
			new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NSF_FEE__WITHOUT_RESTRICTION)
					.setAmount(feeAmount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();

			BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue.add(new Dollar(feeAmount)));
			BillingSummaryPage.getTotalPaid().verify.equals(initialTotalPaid);
		});
	}

	/**
	 * @author Deloite
	 * @name Decline Fee+Restriction
	 * @scenario
	 * 1. Retrieve a Policy and navigate to billing page
	 * 2. Make a payment of $100 and decline the payment by clicking on decline payment Link
	 * 3. Validate if Check payment  be returned from bank as Rejected by rejecting the payment that was made through check by declining in payments & Transactions section with the reason : Fee+Restriction:
	 * 4. Verify the declined reason by selecting  declined payment link
	 * 5. Validate  e_folder and review the Payment Restriction Notice_ check(60 5000)
	 * Check:
	 * 1. Payment should be declined by selecting "Fee+ Restriction " decline reason
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = AZ)
	public void BCT_ONL_121_Payments(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openBilling(policyNumber);
		billingAccountPoliciesVerifier.setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);

		Dollar initialTotalDue = BillingSummaryPage.getTotalDue();
		Dollar initialTotalPaid = BillingSummaryPage.getTotalPaid();
		Dollar amount = new Dollar(100);
		Dollar feeAmount = new Dollar(20);

		billingAccount.acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Check"), amount);
		billingAccount.declinePayment().perform(testDataManager.billingAccount.getTestData("DeclinePayment", "TestData_FeeRestriction"), "(" + amount + ")");

		CustomSoftAssertions.assertSoftly(softly -> {
			new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
					.setAmount(amount.negate()).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.DECLINED)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
					.setReason(BillingConstants.PaymentsAndOtherTransactionReason.FEE_PLUS_RESTRICTION).verifyPresent();
			new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED)
					.setAmount(amount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
					.setReason(BillingConstants.PaymentsAndOtherTransactionReason.FEE_PLUS_RESTRICTION).verifyPresent();
			new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NSF_FEE__WITH_RESTRICTION)
					.setAmount(feeAmount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();

			BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue.add(feeAmount));
			BillingSummaryPage.getTotalPaid().verify.equals(initialTotalPaid);

			//Efolder.isDocumentExist("Invoice Bills Statements", "RETURNED PAYMNT");
		});
	}

	/**
	 * @author Deloite
	 * @name Decline Fee+Restriction
	 * @scenario
	 * 1. Retrieve a Policy and navigate to billing page
	 * 2. Make a payment of $100 and decline the payment by clicking on decline payment Link
	 * 3. Validate if Check payment  be returned from bank as Rejected by rejecting the payment that was made through check by declining in payments & Transactions section with the reason :NO Fee+ NO Restriction:
	 * 4. Verify the declined reason by selecting  declined payment link
	 * 5. Validate  e_folder and review the NSF notice in e_folder(605002)
	 * Check:
	 * 1. Payment should be declined by selecting "NO Fee+NO  Restriction " decline reason
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = AZ)
	public void BCT_ONL_122_Payments(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openBilling(policyNumber);
		billingAccountPoliciesVerifier.setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);

		Dollar initialTotalDue = BillingSummaryPage.getTotalDue();
		Dollar initialTotalPaid = BillingSummaryPage.getTotalPaid();
		Dollar amount = new Dollar(100);

		billingAccount.acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Check"), amount);
		billingAccount.declinePayment().perform(testDataManager.billingAccount.getTestData("DeclinePayment", "TestData_NoFeeNoRestriction"), "(" + amount + ")");

		CustomSoftAssertions.assertSoftly(softly -> {
			new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
					.setAmount(amount.negate()).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.DECLINED)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
					.setReason(BillingConstants.PaymentsAndOtherTransactionReason.NO_FEE_NO_RESTRICTION).verifyPresent();
			new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED)
					.setAmount(amount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
					.setReason(BillingConstants.PaymentsAndOtherTransactionReason.NO_FEE_NO_RESTRICTION).verifyPresent();

			BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue);
			BillingSummaryPage.getTotalPaid().verify.equals(initialTotalPaid);

			//Efolder.isDocumentExist("Invoice Bills Statements", "RETURNED PAYMNT");
		});
	}
}
