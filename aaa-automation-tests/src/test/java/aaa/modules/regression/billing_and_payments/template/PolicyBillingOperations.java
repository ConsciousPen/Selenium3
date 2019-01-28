package aaa.modules.regression.billing_and_payments.template;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.BillingPendingTransactionsVerifier;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.BillingConstants.*;
import aaa.main.enums.MyWorkConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.*;
import aaa.main.modules.mywork.MyWork;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.TextBox;

public abstract class PolicyBillingOperations extends PolicyBaseTest {

	private TestData tdBilling = testDataManager.billingAccount;

	private OtherTransactionsActionTab otherTransactionsActionTab = new OtherTransactionsActionTab();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();

	/**
	 * @author Jurij Kuznecov
	 * @name Test Policy Manual Fee Adjustment
	 * @scenario 
	 * 1.  Create new or open existent Customer
	 * 2.  Create a new policy
	 * 3.  Make a positive fee
	 * 4.  Check fee transaction appears in "Payments&Other Transactions"
	 * 5.  Check total amount due is increased on fee amount
	 * 6.  Check minimum due doesn't change
	 * 7.  Waive the fee transaction 
	 * 8.  Check waive transaction appears in "Payments&Other Transactions"
	 * 9.  Check waive link isn't present in the fee transaction row 
	 * 10. Check total amount due is decreased by fee amount
	 * 11. Check minimum due doesn't change
	 */

	public void testManualFeeAdjustment() {
		TestData fee_transaction = tdBilling.getTestData("Transaction", "TestData_Fee");

		mainApp().open();
		getCopiedPolicy();

		BillingSummaryPage.open();

		Dollar initialMinimumDue = BillingSummaryPage.getMinimumDue();
		Dollar initialTotalDue = BillingSummaryPage.getTotalDue();
		Dollar feeAmount = new Dollar(fee_transaction.getTestData(BillingAccountMetaData.OtherTransactionsActionTab.class.getSimpleName()).getValue(
			BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel()));

		// 3. Make a positive fee
		BillingSummaryPage.linkOtherTransactions.click();
		otherTransactionsActionTab.fillTab(fee_transaction);
		OtherTransactionsActionTab.buttonOk.click();

		// 4. Check fee transaction appears in "Payments&Other Transactions"
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.TYPE)).valueContains(PaymentsAndOtherTransactionType.FEE);

		// 5. Check total amount due is increased on fee amount
		BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue.add(feeAmount));

		// 6. Check minimum due doesn't change
		BillingSummaryPage.getMinimumDue().verify.equals(initialMinimumDue);

		// 7. Waive the fee transaction
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
			ActionConstants.BillingPaymentsAndOtherTransactionAction.WAIVE).click();
		Page.dialogConfirmation.confirm();

		// 8. Check waive transaction appears in "Payments&Other Transactions"
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON))
			.valueContains(ActionConstants.BillingPaymentsAndOtherTransactionAction.WAIVE + "d");

		// 9. Check waive link isn't present in the fee transaction row
		HashMap<String, String> query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.FEE);
		query.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, feeAmount.toString());
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION))
				.doesNotHaveValue(ActionConstants.BillingPaymentsAndOtherTransactionAction.WAIVE);

		// 10. Check total amount due is decreased by fee amount
		BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue);

		// 11. Check minimum due doesn't change
		BillingSummaryPage.getMinimumDue().verify.equals(initialMinimumDue);
	}

	/**
	 * @author Jurij Kuznecov
	 * @name Test Policy Manual Refund
	 * @scenario 
	 * 1.  Create new or open existent Customer
	 * 2.  Create a new policy
	 * 3.  Make a payment 1000$
	 * 4.  Check for an error message if Refund Amount is empty
	 * 5.  Check for an error message if Refund Amount > Total Paid Amount
	 * 6.  Check for an error message if Refund Amount is "0"
	 * 7.  Make a refund of 1000$
	 * 8.  Check presence of the refund transaction in Pending transactions on billing tab
	 * 9.  Check that System creates an Approval task for the Refund transaction
	 * 10. Approve the refund transaction
	 * 11. Check presence of the refund transaction in Payments & Other Transactions on billing tab
	 * 12. Check Total Paid Amount value after refunding
	 */

	public void testManualRefund() {
		RefundActionTab refundActionTab = new RefundActionTab();

		Dollar paymentAmount = new Dollar(1000);
		TestData payment = tdBilling.getTestData("AcceptPayment", "TestData_Check").adjust(
			TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()), paymentAmount.toString());
		TestData refund = tdBilling.getTestData("Refund", "TestData_Check");
		String keyPathRefundAmount = TestData.makeKeyPath(BillingAccountMetaData.RefundActionTab.class.getSimpleName(), BillingAccountMetaData.RefundActionTab.AMOUNT.getLabel());

		mainApp().open();
		getCopiedPolicy();

		BillingSummaryPage.open();

		Dollar initiateTotalPaid = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_PAID).getValue());

		// 3. Make a payment 1000$
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.fillTab(payment);
		AcceptPaymentActionTab.buttonOk.click();
		Dollar totalPaid = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_PAID).getValue());

		// 4. Check for an error message if Refund Amount is empty
		new BillingAccount().refund().start();
		refundActionTab.fillTab(refund.mask(keyPathRefundAmount));
		RefundActionTab.buttonOk.click();
		assertThat(refundActionTab.getAssetList().getWarning(BillingAccountMetaData.RefundActionTab.AMOUNT.getLabel())).valueContains("Value is required");

		// 5. Check for an error message if Refund Amount > Total Paid Amount
		refundActionTab.fillTab(refund.adjust(keyPathRefundAmount, totalPaid.add(1).toString()));
		RefundActionTab.buttonOk.click();
		assertThat(refundActionTab.getAssetList().getWarning(BillingAccountMetaData.RefundActionTab.TOTAL_AMOUNT.getLabel())).valueContains("Sum of subtotal amounts do not match total amount");

		// 6. Check for an error message if Refund Amount is "0"
		refundActionTab.fillTab(refund.adjust(keyPathRefundAmount, "0"));
		RefundActionTab.buttonOk.click();
		assertThat(refundActionTab.getAssetList().getWarning(BillingAccountMetaData.RefundActionTab.AMOUNT.getLabel())).valueContains("Value is required");

		// 7. Make a refund of 1000$
		refundActionTab.fillTab(tdBilling.getTestData("Refund", "TestData_Check"));
		RefundActionTab.buttonOk.click();

		// 8. Check presence of the refund transaction in Pending transactions on billing tab
		new BillingPendingTransactionsVerifier().setType(BillingPendingTransactionsType.REFUND).setAmount(paymentAmount).verifyPresent();
		// LocalDateTime tDate =
		// TimeSetterUtil.getInstance().parse(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(BillingPendingTransactionsTable.TRANSACTION_DATE).getValue(),
		// DateTimeUtils.MM_DD_YYYY);

		// 9. Check that System creates an Approval task for the Refund transaction
		String referenceID = BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.ID).getValue();
		NavigationPage.toMainTab(AppMainTabs.MY_WORK.get());
		new MyWork().filterTask().performByReferenceId(referenceID);
		MyWorkSummaryPage.linkAllQueues.click();
		assertThat(MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID)).valueContains(referenceID);
		MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID).controls.links.getFirst().click();

		// 10. Approve the refund transaction
		BillingHelper.approvePendingTransaction(TimeSetterUtil.getInstance().getCurrentTime(), BillingPendingTransactionsType.REFUND);

		// 11. Check presence of the refund transaction in Payments & Other Transactions on billing tab
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.REFUND).setAmount(paymentAmount).setStatus(PaymentsAndOtherTransactionStatus.APPROVED).verifyPresent();

		// 12. Check Total Paid Amount value after refunding
		assertThat(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_PAID)).valueContains(initiateTotalPaid.toString());
	}

	/**
	 * @author Jurij Kuznecov
	 * @name Test Policy Manual Write-Off
	 * @scenario
	 * 1.  Create new or open existent Customer
	 * 2.  Create a new policy
	 * 3.  Write Off 100$
	 * 4.  Check write-off transaction appears in "Payments and other transactions" section on billing tab
	 * 5.  Reversal Write Off 100$ 
	 * 6.  Check reversal write-off transaction appears in "Payments and other transactions" section on billing tab
	 * 7.  Check Total Due value after write-off/reversal write-off
	 *  // Manual Adjustment(Advanced Allocations)
	 * 8.  Enter payments amounts > Sub Total in advanced allocation dialog
	 * 9.  Check error appears
	 * 10. Make a positive adjustment using advanced allocation dialog
	 * 11. Check that System defaults 'Total Amount' with the value entered by user in 'Amount' field on 'Other Transactions' tab
	 * 12. Check that System defaults 'Product Sub total' with the value entered by user in 'Amount' field on 'Other Transactions' tab
	 * 13. Check positive adjustment transaction appears in "Payments and other transactions" section on billing tab
	 * 14. Check Total Due value is increased
	 * 15. Check Minimum Due Amount doesn't change
	 * 16. Make a negative adjustment using advanced allocation dialog
	 * 17. Check negative adjustment transaction appears in "Payments and other transactions" section on billing tab
	 * 18. Check Total Due value is decreased
	 * 19. Check Minimum Due Amount doesn't change
	 */
	public void testManualWriteOff() {
		AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();

		TestData writeoff = tdBilling.getTestData("Transaction", "TestData_WriteOff");
		Dollar writeoffAmount = new Dollar(writeoff.getTestData(BillingAccountMetaData.OtherTransactionsActionTab.class.getSimpleName()).getValue(
			BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel()));
		String keyPathAmount = TestData.makeKeyPath(BillingAccountMetaData.OtherTransactionsActionTab.class.getSimpleName(), BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel());
		String allocationError = "Allocation amounts for %s do not match product subtotal";
		HashMap<String, String> query;

		mainApp().open();
		String policyNumber = getCopiedPolicy();

		BillingSummaryPage.open();

		Dollar initialTotalDue = BillingSummaryPage.getTotalDue();
		Dollar initialMinimumDue = BillingSummaryPage.getMinimumDue();

		// 3. Write Off 100$
		BillingSummaryPage.linkOtherTransactions.click();
		otherTransactionsActionTab.fillTab(writeoff);
		OtherTransactionsActionTab.buttonOk.click();
		OtherTransactionsActionTab.btnContinue.click();

		// 4. Check write-off transaction appears in "Payments and other transactions" section on billing tab
		query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.ADJUSTMENT);
		query.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, writeoffAmount.toString());
		query.put(BillingPaymentsAndOtherTransactionsTable.STATUS, PaymentsAndOtherTransactionStatus.APPLIED);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query)).exists();

		// 5. Reversal Write Off 100$
		BillingSummaryPage.linkOtherTransactions.click();
		writeoffAmount = writeoffAmount.negate();
		otherTransactionsActionTab.fillTab(writeoff.adjust(keyPathAmount, writeoffAmount.toString()));
		OtherTransactionsActionTab.buttonOk.click();

		// 6. Check reversal write-off transaction appears in "Payments and other transactions" section on billing tab
		query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.ADJUSTMENT);
		query.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, writeoffAmount.toString());
		query.put(BillingPaymentsAndOtherTransactionsTable.STATUS, PaymentsAndOtherTransactionStatus.APPLIED);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query)).exists();

		// 7. Check Total Due value after write-off/reversal write-off
		BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue);

		// 8. Enter payments amounts > Sub Total in advanced allocation dialog
		BillingSummaryPage.linkOtherTransactions.click();
		writeoffAmount = writeoffAmount.multiply(2);
		otherTransactionsActionTab.fillTab(writeoff.adjust(keyPathAmount, writeoffAmount.toString()));
		OtherTransactionsActionTab.linkAdvancedAllocation.click();
		advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER.getLabel(), TextBox.class).setValue(writeoffAmount.toString());
		// advancedAllocationsActionTab.fillTab(new
		// SimpleDataProvider().adjust(BillingAccountMetaData.AdvancedAllocationsActionTab.class.getSimpleName(),
		// new SimpleDataProvider().adjust(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER.getLabel(),
		// writeoffAmount.toString())));
		AdvancedAllocationsActionTab.buttonOk.click();

		// 9. Check error appears
		assertThat(advancedAllocationsActionTab.getBottomWarning()).valueContains(String.format(allocationError, policyNumber));
		AdvancedAllocationsActionTab.buttonCancel.click();
		OtherTransactionsActionTab.buttonCancel.click();

		// 10. Make a positive adjustment using advanced allocation dialog
		BillingSummaryPage.linkOtherTransactions.click();
		otherTransactionsActionTab.fillTab(writeoff.adjust(keyPathAmount, writeoffAmount.toString()));

		// 11. Check that System defaults 'Total Amount' with the value entered by user in 'Amount' field on 'Other Transactions' tab
		OtherTransactionsActionTab.linkAdvancedAllocation.click();
		assertThat(advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.TOTAL_AMOUNT)).hasValue(writeoffAmount.toString());

		// 12. Check that System defaults 'Product Sub total' with the value entered by user in 'Amount' field on 'Other Transactions' tab
		assertThat(advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.PRODUCT_SUB_TOTAL)).hasValue(writeoffAmount.toString());
		AdvancedAllocationsActionTab.buttonOk.click();

		// 13. Check positive adjustment transaction appears in "Payments and other transactions" section on billing tab
		query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.ADJUSTMENT);
		query.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, writeoffAmount.toString());
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query)).exists();

		// 14. Check Total Due value is increased
		BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue.add(writeoffAmount));

		// 15. Check Minimum Due Amount doesn't change
		BillingSummaryPage.getMinimumDue().verify.equals(initialMinimumDue);

		// 16. Make a negative adjustment using advanced allocation dialog
		BillingSummaryPage.linkOtherTransactions.click();
		writeoffAmount = writeoffAmount.negate();
		otherTransactionsActionTab.fillTab(writeoff.adjust(keyPathAmount, writeoffAmount.toString()));
		OtherTransactionsActionTab.linkAdvancedAllocation.click();
		AdvancedAllocationsActionTab.buttonOk.click();

		// 17. Check negative adjustment transaction appears in "Payments and other transactions" section on billing tab
		query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.ADJUSTMENT);
		query.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, writeoffAmount.toString());
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query)).exists();

		// 18. Check Total Due value is decreased
		BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue);

		// 19. Check Minimum Due Amount doesn't change
		BillingSummaryPage.getMinimumDue().verify.equals(initialMinimumDue);
	}

	/**
	 * @author Jurij Kuznecov
	 * @name Test Policy Manual Returned Payments
	 * @scenario
	 * 1.  Create a policy
	 * 2.  Add 4 payment methods(Cash, Check, Credit Card, EFT)
	 * 3.  Make 5 different payments(Cash, Check, Credit Card, EFT)
	 * 4.  Decline Credit Card payment, with reason "NSF fee - with restriction"
	 * 5.  Check Payment Decline Transaction appears in Payment and Other Transaction section
	 * 6.  Check Fee Transaction appears in Payment and Other Transaction section
	 * 7.  Check status of the payment transaction changes to "Decline"
	 * 8.  Check Total Due is increased
	 * 9.  Decline EFT payment, with reason "NSF fee - without restriction"
	 * 10. Check Payment Decline Transaction appears in Payment and Other Transaction section
	 * 11. Check Fee Transaction appears in Payment and Other Transaction section
	 * 12. Check status of the payment transaction changes to "Decline"
	 * 13. Check Total Due is increased
	 * 14. Decline Check payment, with reason "No Fee + No Restriction"
	 * 15. Check Payment Decline Transaction appears in Payment and Other Transaction section
	 * 16. Check status of the payment transaction changes to "Decline"
	 * 17. Check Total Due is increased
	 * 18. Decline Check payment, with reason "No Fee + No Restriction + No Letter"
	 * 19. Check Payment Decline Transaction appears in Payment and Other Transaction section
	 * 20. Check status of the payment transaction changes to "Decline"
	 * 21. Check Total Due is increased
	 * 22. Decline Cash payment.
	 * 23. Check Payment Decline Transaction appears in Payment and Other Transaction section.
	 * 24. Check status of the payment transaction changes to "Decline"
	 * 25. Check Total Due is increased.
	 * 26. Make a payment = Deposit amount + 2 * Fee Amount 
	 *     (We add Fee Amount to compensate Fees due that was generated before)
	 * 27. Decline Deposit payment.
	 * 28. Check original installments dues stays the same(As it was on step 25)
	 * 29. Check that Prepaid is decreased.
	 */
	Dollar expectedTotalDue;
	Dollar feeAmountTotal = new Dollar(0);

	public void testManualReturnedPayments() {
		// TestData paymentMethods = tdBilling.getTestData("PaymentMethods", "TestData_AddPaymentMethods");
		HashMap<String, String> query;
		Dollar checkAmount1 = new Dollar(50);
		Dollar cardAmount = new Dollar(60);
		Dollar eftAmount = new Dollar(70);
		Dollar checkAmount2 = new Dollar(80);
		Dollar cashAmount = new Dollar(90);

		mainApp().open();
		getCopiedPolicy();

		BillingSummaryPage.open();

		Dollar initialTotalDue = BillingSummaryPage.getTotalDue();
		expectedTotalDue = new Dollar(0);
		expectedTotalDue.add(initialTotalDue);

		// 3. Make 5 different payments(Cash, Check, Credit Card, EFT)
		payment(tdBilling.getTestData("AcceptPayment", "TestData_Check"), checkAmount1);
		expectedTotalDue = expectedTotalDue.add(checkAmount1);

		payment(tdBilling.getTestData("AcceptPayment", "TestData_CC"), cardAmount);
		expectedTotalDue = expectedTotalDue.add(cardAmount);

		payment(tdBilling.getTestData("AcceptPayment", "TestData_EFT"), eftAmount);
		expectedTotalDue = expectedTotalDue.add(eftAmount);

		payment(tdBilling.getTestData("AcceptPayment", "TestData_Check"), checkAmount2);
		expectedTotalDue = expectedTotalDue.add(checkAmount2);

		payment(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), cashAmount);
		expectedTotalDue = expectedTotalDue.add(cashAmount);

		// 4. Decline Credit Card payment, with reason "NSF fee - with restriction" and verify
		declinePaymentAndVerify(cardAmount, PaymentsAndOtherTransactionReason.FEE_PLUS_RESTRICTION);

		// 9. Decline EFT payment, with reason "NSF fee - without restriction"
		declinePaymentAndVerify(eftAmount, PaymentsAndOtherTransactionReason.FEE_PLUS_RESTRICTION);

		// 14. Decline Check payment, with reason "No Fee + No Restriction"
		declinePaymentAndVerify(checkAmount1, PaymentsAndOtherTransactionReason.NO_FEE_NO_RESTRICTION);

		// 18. Decline Check payment, with reason "No Fee + No Restriction + No Letter"
		declinePaymentAndVerify(checkAmount2, PaymentsAndOtherTransactionReason.NO_FEE_NO_RESTRICTION_NO_LETTER);

		// 22. Decline Cash payment
		declinePaymentAndVerify(cashAmount, "");

		// 26. Make a payment = Deposit amount + 2 * Fee Amount
		// (We add Fee Amount to compensate Fees due that was generated before)
		query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PAYMENT);
		query.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.DEPOSIT_PAYMENT);
		Dollar depositPayment = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue()).negate();
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.fillTab(tdBilling.getTestData("AcceptPayment", "TestData_Cash").adjust(
			TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()),
			depositPayment.add(feeAmountTotal).toString()).resolveLinks());
		AcceptPaymentActionTab.buttonOk.click();

		// 27. Decline Deposit payment
		query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PAYMENT);
		query.put(BillingPaymentsAndOtherTransactionsTable.ACTION, PaymentsAndOtherTransactionAction.DECLINE);
		query.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, depositPayment.add(feeAmountTotal).negate().toString());
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(PaymentsAndOtherTransactionAction.DECLINE).click();

		// 28. Check original installments dues stays the same(As it was on step 26)
		BillingSummaryPage.getTotalDue().verify.equals(expectedTotalDue.negate());

		// 29. Check that Prepaid is decreased.
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingAccountPoliciesTable.PREPAID)).valueContains(new Dollar(0).toString());
	}

	/*
	 * Execute payment and return payed value
	 */

	private void payment(TestData td, Dollar amount) {
		BillingSummaryPage.linkAcceptPayment.click();
		acceptPaymentActionTab.fillTab(td.adjust(TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT
			.getLabel()), amount.toString()));
		AcceptPaymentActionTab.buttonOk.click();
	}

	private void declinePaymentAndVerify(Dollar amount, String reason) {
		HashMap<String, String> query;
		Dollar feeAmount = new Dollar(0);

		query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PAYMENT);
		query.put(BillingPaymentsAndOtherTransactionsTable.ACTION, PaymentsAndOtherTransactionAction.DECLINE);
		query.put(BillingPaymentsAndOtherTransactionsTable.AMOUNT, amount.negate().toString());
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(PaymentsAndOtherTransactionAction.DECLINE)
			.click();
		if (!reason.isEmpty()) {
			new DeclinePaymentActionTab().getAssetList().getAsset(BillingAccountMetaData.DeclinePaymentActionTab.DECLINE_REASON).setValue(reason); // new
																																												// DeclineActionTab().getAssetList().getAsset(assetName).fillTab(new
																																												// SimpleDataProvider().adjust(HomeCaMetaData.DeclineActionTab.class.getSimpleName(),
			DeclinePaymentActionTab.buttonOk.click();

			if (reason.equals(PaymentsAndOtherTransactionReason.FEE_PLUS_RESTRICTION)) {
				assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.TYPE)).valueContains(PaymentsAndOtherTransactionType.FEE);

				Dollar fee = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
				feeAmount = feeAmount.add(fee);
				feeAmountTotal = feeAmountTotal.add(fee);
			}
		}
		query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.ADJUSTMENT);
		query.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED);
		query.put(BillingPaymentsAndOtherTransactionsTable.REASON, reason);
		query.put(BillingPendingTransactionsTable.AMOUNT, amount.toString());
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query)).exists();

		query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PAYMENT);
		query.put(BillingPaymentsAndOtherTransactionsTable.REASON, reason);
		query.put(BillingPendingTransactionsTable.AMOUNT, amount.negate().toString());
		query.put(BillingPendingTransactionsTable.STATUS, PaymentsAndOtherTransactionStatus.DECLINED);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query)).exists();

		expectedTotalDue = expectedTotalDue.subtract(amount.add(feeAmount));
		assertThat(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE)).valueContains(expectedTotalDue.negate().toString());
	}

}
