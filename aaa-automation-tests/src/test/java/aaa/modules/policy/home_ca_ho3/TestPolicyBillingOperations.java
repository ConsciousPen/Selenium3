package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.table.Table;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.BillingConstants.BillingAccountPoliciesTable;
import aaa.main.enums.BillingConstants.BillingGeneralInformationTable;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsActions;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsTable;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsType;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionAction;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.MyWorkConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.AdvancedAllocationsActionTab;
import aaa.main.modules.billing.account.actiontabs.OtherTransactionsActionTab;
import aaa.main.modules.billing.account.actiontabs.RefundActionTab;
import aaa.main.modules.mywork.MyWork;
import aaa.main.modules.policy.home_ca.actiontabs.DeclineActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;

public class TestPolicyBillingOperations extends HomeCaHO3BaseTest {

    OtherTransactionsActionTab otherTransactionsActionTab = new OtherTransactionsActionTab();
    AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
    DeclineActionTab declineActionTab = new DeclineActionTab();

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Manual Fee Adjustment
     * @scenario 
     * 1.  Create new or open existent Customer
     * 2.  Create a new HO3 policy
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

    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testManualFeeAdjustment() {

        mainApp().open();
        getCopiedPolicy();

        NavigationPage.toMainTab(AppMainTabs.BILLING.get());

        Dollar initialMinimumDue = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.MINIMUM_DUE).getValue());
        Dollar initialTotalDue = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).getValue());
        Dollar feeAmount = new Dollar(tdSpecific.getTestData("TestData_Fee")
                .getTestData(BillingAccountMetaData.OtherTransactionsActionTab.class.getSimpleName()).getValue(BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel()));

        // 3.  Make a positive fee
        BillingSummaryPage.linkOtherTransactions.click();
        otherTransactionsActionTab.fillTab(tdSpecific.getTestData("TestData_Fee"));
        otherTransactionsActionTab.submitTab();

        // 4.  Check fee transaction appears in "Payments&Other Transactions"
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.TYPE).verify
                .contains(PaymentsAndOtherTransactionType.FEE);

        // 5.  Check total amount due is increased on fee amount
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).verify.contains(initialTotalDue.add(feeAmount).toString());

        // 6.  Check minimum due doesn't change
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.MINIMUM_DUE).verify.contains(initialMinimumDue.toString());

        // 7.  Waive the fee transaction
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(
                ActionConstants.BillingPaymentsAndOtherTransactionAction.WAIVE).click();
        Page.dialogConfirmation.confirm();

        // 8.  Check waive transaction appears in "Payments&Other Transactions"
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON).verify
                .contains(ActionConstants.BillingPaymentsAndOtherTransactionAction.WAIVE + "d");

        // 9.  Check waive link isn't present in the fee transaction row
        CustomAssert.assertFalse(
                "Waive link is presented in the fee transaction row: ",
                BillingSummaryPage.tablePaymentsOtherTransactions.getRow(Table.buildQuery(String.format("%s->%s|%s->%s", BillingPaymentsAndOtherTransactionsTable.TYPE,
                        PaymentsAndOtherTransactionType.FEE,
                        BillingPaymentsAndOtherTransactionsTable.AMOUNT, feeAmount.toString()))).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).getValue()
                        .equals(ActionConstants.BillingPaymentsAndOtherTransactionAction.WAIVE));

        // 10. Check total amount due is decreased by fee amount
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).verify.contains(initialTotalDue.toString());

        // 11. Check minimum due doesn't change
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.MINIMUM_DUE).verify.contains(initialMinimumDue.toString());
    }

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Manual Refund
     * @scenario 
     * 1.  Create new or open existent Customer
     * 2.  Create a new HO3 policy
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

    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testManualRefund() {

        Dollar paymentAmount = new Dollar(tdSpecific.getTestData("TestData_Payment_1000").getTestData(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName())
                .getValue(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()));
        String keyPathRefund = TestData.makeKeyPath(BillingAccountMetaData.RefundActionTab.class.getSimpleName(), BillingAccountMetaData.RefundActionTab.AMOUNT.getLabel());

        mainApp().open();
        getCopiedPolicy();

        NavigationPage.toMainTab(AppMainTabs.BILLING.get());
        Dollar initiateTotalPaid = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_PAID).getValue());

        // 3.  Make a payment 1000$
        BillingSummaryPage.linkAcceptPayment.click();
        AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
        acceptPaymentActionTab.fillTab(tdSpecific.getTestData("TestData_Payment_1000"));
        acceptPaymentActionTab.submitTab();
        Dollar totalPaid = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_PAID).getValue());

        // 4. Check for an error message if Refund Amount is empty
        new BillingAccount().refund().start();
        RefundActionTab refundActionTab = new RefundActionTab();
        refundActionTab.fillTab(tdSpecific.getTestData("TestData_Refund_1000").mask(keyPathRefund));
        refundActionTab.submitTab();
        refundActionTab.getAssetList().getWarning(BillingAccountMetaData.RefundActionTab.AMOUNT.getLabel()).verify.contains("Amount is required");

        // 5. Check for an error message if Refund Amount > Total Paid Amount
        refundActionTab.fillTab(tdSpecific.getTestData("TestData_Refund_1000").adjust(keyPathRefund, totalPaid.add(1).toString()));
        refundActionTab.submitTab();
        refundActionTab.getAssetList().getWarning(BillingAccountMetaData.RefundActionTab.TOTAL_AMOUNT.getLabel()).verify.contains("Sum of subtotal amounts do not match total amount");

        // 6. Check for an error message if Refund Amount is "0"
        refundActionTab.fillTab(tdSpecific.getTestData("TestData_Refund_1000").adjust(keyPathRefund, "0"));
        refundActionTab.submitTab();
        refundActionTab.getAssetList().getWarning(BillingAccountMetaData.RefundActionTab.AMOUNT.getLabel()).verify.contains("Amount is required");

        // 7. Make a refund of 1000$
        refundActionTab.fillTab(tdSpecific.getTestData("TestData_Refund_1000"));
        refundActionTab.submitTab();

        // 8. Check presence of the refund transaction in Pending transactions on billing tab
        BillingSummaryPage.tablePendingTransactions.getRow(Table.buildQuery(String.format("%s->%s|%s->%s", BillingPendingTransactionsTable.TYPE,
                BillingPendingTransactionsType.REFUND,
                BillingPendingTransactionsTable.AMOUNT, paymentAmount.toString()))).verify.present();

        // 9. Check that System creates an Approval task for the Refund transaction
        String referenceID = BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.ID).getValue();
        NavigationPage.toMainTab(AppMainTabs.MY_WORK.get());
        new MyWork().filterTask().performByReferenceId(referenceID);
        MyWorkSummaryPage.linkAllQueues.click();
        MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID).verify.contains(referenceID);
        MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID).controls.links.getFirst().click();

        // 10. Approve the refund transaction
        BillingSummaryPage.tablePendingTransactions.getRowContains(Table.buildQuery(String.format("%s->%s|%s->%s", BillingPendingTransactionsTable.TYPE,
                BillingPendingTransactionsType.REFUND, BillingPendingTransactionsTable.AMOUNT, paymentAmount.toString())))
                .getCell(BillingPendingTransactionsTable.ACTION).controls.links.get(BillingPendingTransactionsActions.APPROVE).click();
        Page.dialogConfirmation.confirm();

        // 11. Check presence of the refund transaction in Payments & Other Transactions on billing tab
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(Table.buildQuery(String.format("%s->%s|%s->%s|%s->%s", BillingPaymentsAndOtherTransactionsTable.TYPE,
                PaymentsAndOtherTransactionType.REFUND, BillingPaymentsAndOtherTransactionsTable.AMOUNT, paymentAmount.toString(),
                BillingPaymentsAndOtherTransactionsTable.STATUS, PaymentsAndOtherTransactionStatus.APPROVED))).verify.present();

        // 12. Check Total Paid Amount value after refunding
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_PAID).verify.contains(initiateTotalPaid.toString());
    }

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Manual Write-Off
     * @scenario
     * 1.  Create new or open existent Customer
     * 2.  Create a new HO3 policy
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

    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testManualWriteOff() {

        Dollar writeoffAmount = new Dollar(tdSpecific.getTestData("TestData_WriteOff").getTestData(BillingAccountMetaData.OtherTransactionsActionTab.class.getSimpleName())
                .getValue(BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel()));
        String keyPathAmount = TestData.makeKeyPath(BillingAccountMetaData.OtherTransactionsActionTab.class.getSimpleName(), BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel());
        String allocationError = "Allocation amounts for %s do not match product subtotal";

        mainApp().open();
        getCopiedPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        NavigationPage.toMainTab(AppMainTabs.BILLING.get());

        Dollar initialTotalDue = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).getValue());
        Dollar initialMinimumDue = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.MINIMUM_DUE).getValue());

        // 3.  Write Off 100$
        BillingSummaryPage.linkOtherTransactions.click();
        otherTransactionsActionTab.fillTab(tdSpecific.getTestData("TestData_WriteOff"));
        otherTransactionsActionTab.submitTab();
        OtherTransactionsActionTab.btnContinue.click();

        // 4.  Check write-off transaction appears in "Payments and other transactions" section on billing tab
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(Table.buildQuery(String.format("%s->%s|%s->%s|%s->%s", BillingPaymentsAndOtherTransactionsTable.TYPE,
                PaymentsAndOtherTransactionType.ADJUSTMENT, BillingPaymentsAndOtherTransactionsTable.AMOUNT, writeoffAmount.toString(),
                BillingPaymentsAndOtherTransactionsTable.STATUS, PaymentsAndOtherTransactionStatus.APPLIED))).verify.present();

        // 5.  Reversal Write Off 100$
        BillingSummaryPage.linkOtherTransactions.click();
        writeoffAmount = writeoffAmount.negate();
        otherTransactionsActionTab.fillTab(tdSpecific.getTestData("TestData_WriteOff").adjust(keyPathAmount, writeoffAmount.toString()));
        otherTransactionsActionTab.submitTab();

        // 6.  Check reversal write-off transaction appears in "Payments and other transactions" section on billing tab
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(Table.buildQuery(String.format("%s->%s|%s->%s|%s->%s", BillingPaymentsAndOtherTransactionsTable.TYPE,
                PaymentsAndOtherTransactionType.ADJUSTMENT, BillingPaymentsAndOtherTransactionsTable.AMOUNT, writeoffAmount.toString(),
                BillingPaymentsAndOtherTransactionsTable.STATUS, PaymentsAndOtherTransactionStatus.APPLIED))).verify.present();

        // 7.  Check Total Due value after write-off/reversal write-off
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).verify.contains(initialTotalDue.toString());

        // 8.  Enter payments amounts > Sub Total in advanced allocation dialog
        BillingSummaryPage.linkOtherTransactions.click();
        writeoffAmount = writeoffAmount.multiply(2);
        otherTransactionsActionTab.fillTab(tdSpecific.getTestData("TestData_WriteOff").adjust(keyPathAmount, writeoffAmount.toString()));
        OtherTransactionsActionTab.linkAdvancedAllocation.click();
        AdvancedAllocationsActionTab advancedAllocationsActionTab = new AdvancedAllocationsActionTab();
        advancedAllocationsActionTab.fillTab(new SimpleDataProvider().adjust(BillingAccountMetaData.AdvancedAllocationsActionTab.class.getSimpleName(),
                new SimpleDataProvider().adjust(BillingAccountMetaData.AdvancedAllocationsActionTab.OTHER.getLabel(), writeoffAmount.toString())));
        advancedAllocationsActionTab.submitTab();

        // 9.  Check error appears
        advancedAllocationsActionTab.getBottomWarning().verify.contains(String.format(allocationError, policyNumber));
        AdvancedAllocationsActionTab.buttonCancel.click();
        OtherTransactionsActionTab.buttonCancel.click();

        // 10. Make a positive adjustment using advanced allocation dialog
        BillingSummaryPage.linkOtherTransactions.click();
        otherTransactionsActionTab.fillTab(tdSpecific.getTestData("TestData_WriteOff").adjust(keyPathAmount, writeoffAmount.toString()));

        // 11. Check that System defaults 'Total Amount' with the value entered by user in 'Amount' field on 'Other Transactions' tab
        OtherTransactionsActionTab.linkAdvancedAllocation.click();
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.TOTAL_AMOUNT.getLabel()).verify.present(writeoffAmount.toString());

        // 12. Check that System defaults 'Product Sub total' with the value entered by user in 'Amount' field on 'Other Transactions' tab
        advancedAllocationsActionTab.getAssetList().getAsset(BillingAccountMetaData.AdvancedAllocationsActionTab.PRODUCT_SUB_TOTAL.getLabel()).verify.present(writeoffAmount.toString());
        advancedAllocationsActionTab.submitTab();

        // 13. Check positive adjustment transaction appears in "Payments and other transactions" section on billing tab
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(Table.buildQuery(String.format("%s->%s|%s->%s", BillingPaymentsAndOtherTransactionsTable.TYPE,
                PaymentsAndOtherTransactionType.ADJUSTMENT, BillingPaymentsAndOtherTransactionsTable.AMOUNT, writeoffAmount.toString()))).verify.present();

        //14. Check Total Due value is increased
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).verify.contains(initialTotalDue.add(writeoffAmount).toString());

        // 15. Check Minimum Due Amount doesn't change
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.MINIMUM_DUE).verify.contains(initialMinimumDue.toString());

        // 16. Make a negative adjustment using advanced allocation dialog
        BillingSummaryPage.linkOtherTransactions.click();
        writeoffAmount = writeoffAmount.negate();
        otherTransactionsActionTab.fillTab(tdSpecific.getTestData("TestData_WriteOff").adjust(keyPathAmount, writeoffAmount.toString()));
        OtherTransactionsActionTab.linkAdvancedAllocation.click();
        advancedAllocationsActionTab.submitTab();

        // 17. Check negative adjustment transaction appears in "Payments and other transactions" section on billing tab
        BillingSummaryPage.tablePaymentsOtherTransactions.getRow(Table.buildQuery(String.format("%s->%s|%s->%s", BillingPaymentsAndOtherTransactionsTable.TYPE,
                PaymentsAndOtherTransactionType.ADJUSTMENT, BillingPaymentsAndOtherTransactionsTable.AMOUNT, writeoffAmount.toString()))).verify.present();

        // 18. Check Total Due value is decreased
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).verify.contains(initialTotalDue.toString());

        // 19. Check Minimum Due Amount doesn't change
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.MINIMUM_DUE).verify.contains(initialMinimumDue.toString());
    }

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Manual Returned Payments
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

    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testManualReturnedPayments() {

        mainApp().open();
        getCopiedPolicy();

        NavigationPage.toMainTab(AppMainTabs.BILLING.get());
        Dollar initialTotalDue = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).getValue());
        expectedTotalDue = new Dollar(0);
        expectedTotalDue.add(initialTotalDue);

        // 2.  Add 4 payment methods(Cash, Check, Credit Card, EFT)
        BillingSummaryPage.linkAcceptPayment.click();
        acceptPaymentActionTab.fillTab(tdSpecific.getTestData("TestData_AddPaymentMethods"));
        OtherTransactionsActionTab.buttonCancel.click();

        // 3.  Make 5 different payments(Cash, Check, Credit Card, EFT)
        Dollar checkAmount1 = payment("Payment_Check");
        expectedTotalDue = expectedTotalDue.add(checkAmount1);

        Dollar cardAmount = payment("Payment_CreditCard");
        expectedTotalDue = expectedTotalDue.add(cardAmount);

        Dollar eftAmount = payment("Payment_EFT");
        expectedTotalDue = expectedTotalDue.add(eftAmount);

        Dollar checkAmount2 = payment("Payment_Check");
        expectedTotalDue = expectedTotalDue.add(checkAmount2);

        Dollar cashAmount = payment("Payment_Cash");
        expectedTotalDue = expectedTotalDue.add(cashAmount);

        // 4.  Decline Credit Card payment, with reason "NSF fee - with restriction" and verify
        declinePaymentAndVerify(cardAmount, PaymentsAndOtherTransactionReason.FEE_PLUS_RESTRICTION);

        // 9.  Decline EFT payment, with reason "NSF fee - without restriction"
        declinePaymentAndVerify(eftAmount, PaymentsAndOtherTransactionReason.FEE_PLUS_RESTRICTION);

        // 14. Decline Check payment, with reason "No Fee + No Restriction"
        declinePaymentAndVerify(checkAmount1, PaymentsAndOtherTransactionReason.NO_FEE_NO_RESTRICTION);

        // 18. Decline Check payment, with reason "No Fee + No Restriction + No Letter"
        declinePaymentAndVerify(checkAmount2, PaymentsAndOtherTransactionReason.NO_FEE_NO_RESTRICTION_NO_LETTER);

        // 22. Decline Cash payment
        declinePaymentAndVerify(cashAmount, "");

        // 26. Make a payment = Deposit amount + 2 * Fee Amount 
        //    (We add Fee Amount to compensate Fees due that was generated before)
        Dollar depositPayment =
                new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions
                        .getRowContains(Table.buildQuery(String.format("%s->%s|%s->%s", BillingPaymentsAndOtherTransactionsTable.TYPE,
                                PaymentsAndOtherTransactionType.PAYMENT, BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,
                                PaymentsAndOtherTransactionSubtypeReason.DEPOSIT_PAYMENT))).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue())
                        .negate();
        BillingSummaryPage.linkAcceptPayment.click();
        acceptPaymentActionTab.fillTab((tdSpecific.getTestData("Payment_Cash")).adjust(
                TestData.makeKeyPath(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName(), BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()),
                depositPayment.add(feeAmountTotal).toString()).resolveLinks());
        acceptPaymentActionTab.submitTab();

        // 27. Decline Deposit payment
        BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(
                Table.buildQuery(String.format("%s->%s|%s->%s|%s->%s", BillingPaymentsAndOtherTransactionsTable.TYPE,
                        PaymentsAndOtherTransactionType.PAYMENT, BillingPaymentsAndOtherTransactionsTable.ACTION,
                        PaymentsAndOtherTransactionAction.DECLINE, BillingPaymentsAndOtherTransactionsTable.AMOUNT, depositPayment.add(feeAmountTotal).negate()
                                .toString())))
                .getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(PaymentsAndOtherTransactionAction.DECLINE).click();

        // 28. Check original installments dues stays the same(As it was on step 26)
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).verify.contains(expectedTotalDue.negate().toString());

        // 29. Check that Prepaid is decreased.
        BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingAccountPoliciesTable.PREPAID).verify.contains(new Dollar(0).toString());
    }

    /*
     * Execute payment and return payed value
     */

    private Dollar payment(String paymentMethod) {
        BillingSummaryPage.linkAcceptPayment.click();
        acceptPaymentActionTab.fillTab((tdSpecific.getTestData(paymentMethod)));
        acceptPaymentActionTab.submitTab();
        return new Dollar(tdSpecific.getTestData(paymentMethod).getTestData(BillingAccountMetaData.AcceptPaymentActionTab.class.getSimpleName())
                .getValue(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()));
    }

    private void declinePaymentAndVerify(Dollar amount, String reason) {
        Dollar feeAmount = new Dollar(0);

        BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(
                Table.buildQuery(String.format("%s->%s|%s->%s|%s->%s", BillingPaymentsAndOtherTransactionsTable.TYPE,
                        PaymentsAndOtherTransactionType.PAYMENT, BillingPaymentsAndOtherTransactionsTable.ACTION,
                        PaymentsAndOtherTransactionAction.DECLINE, BillingPaymentsAndOtherTransactionsTable.AMOUNT, amount.negate().toString())))
                .getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(PaymentsAndOtherTransactionAction.DECLINE).click();

        if (!reason.isEmpty()) {
            declineActionTab.fillTab(new SimpleDataProvider().adjust(HomeCaMetaData.DeclineActionTab.class.getSimpleName(),
                    new SimpleDataProvider().adjust(HomeCaMetaData.DeclineActionTab.DECLINE_REASON.getLabel(), reason)));
            declineActionTab.submitTab();

            if (reason.equals(PaymentsAndOtherTransactionReason.FEE_PLUS_RESTRICTION)) {
                BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.TYPE).verify
                        .contains(PaymentsAndOtherTransactionType.FEE);

                Dollar fee = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
                feeAmount = feeAmount.add(fee);
                feeAmountTotal = feeAmountTotal.add(fee);
            }
        }

        BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(Table.buildQuery(String.format("%s->%s|%s->%s|%s->%s|%s->%s",
                BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.ADJUSTMENT,
                BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED,
                BillingPaymentsAndOtherTransactionsTable.REASON, reason,
                BillingPendingTransactionsTable.AMOUNT, amount.toString()))).verify.present();

        BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(Table.buildQuery(String.format("%s->%s|%s->%s|%s->%s|%s->%s",
                BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PAYMENT,
                BillingPaymentsAndOtherTransactionsTable.REASON, reason,
                BillingPendingTransactionsTable.AMOUNT, amount.negate().toString(),
                BillingPendingTransactionsTable.STATUS, PaymentsAndOtherTransactionStatus.DECLINED))).verify.present();

        expectedTotalDue = expectedTotalDue.subtract(amount.add(feeAmount));
        BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.TOTAL_DUE).verify.contains(expectedTotalDue.negate().toString());
    }

}
